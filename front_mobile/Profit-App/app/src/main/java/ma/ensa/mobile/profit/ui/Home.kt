package ma.ensa.mobile.profit.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import kotlinx.coroutines.launch
import ma.ensa.mobile.profit.R
import ma.ensa.mobile.profit.models.*
import ma.ensa.mobile.profit.retrofit.RetrofitClient
import ma.ensa.mobile.profit.services.AuthService
import ma.ensa.mobile.profit.services.StepCounterService
import ma.ensa.mobile.profit.ui.bmi.BmicalcActivity
import ma.ensa.mobile.profit.viewmodels.StepCounterViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Home : Fragment() {

    private lateinit var textViewStepsBig: TextView
    private lateinit var textViewCalories: TextView
    private lateinit var textViewDistance: TextView
    private lateinit var stepsProgressBar: ProgressBar
    private lateinit var fitnessChatbotIcon: ImageView
    private lateinit var textObjectif: TextView
    private lateinit var textCalories: TextView
    private lateinit var bmiIcon: ImageView
    private lateinit var objectivProgressBar: ProgressBar
    private lateinit var caloriesProgressBar: ProgressBar
    private lateinit var textDuration: TextView

    private lateinit var dailyCalorieRepository: DailyCalorieRepository
    private var userId: Long = -1
    private val stepCounterViewModel by lazy { StepCounterViewModel.getInstance() }
    private var stepsGoal = 2000
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100
    private lateinit var calorieChart: LineChart
    private lateinit var timeRangeSpinner: Spinner



    private var doneObjectifs: Int = 0
    private var allObjectifs: Int = 0
    private var allBurnedCalories: Int = 0
    private var burnedCalories: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database and repository
        val database = AppDatabase.getDatabase(requireContext())
        dailyCalorieRepository = DailyCalorieRepository(database.dailyCalorieDao())


        userId = requireActivity().intent.getLongExtra("USER_ID", -1)
        if (userId == -1L) {
            Toast.makeText(requireContext(), "Invalid User ID", Toast.LENGTH_SHORT).show()
        }

        stepCounterViewModel.init(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        } else {
            startStepCounterService()
        }

        freshStart()

        setupChart(7)

        fetchObjectifs()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        initializeViews(rootView)
        setupClickListeners()
        setupObservers()

        return rootView
    }

    private fun initializeViews(rootView: View) {
        textDuration = rootView.findViewById(R.id.workout_duration)
        textViewStepsBig = rootView.findViewById(R.id.steps_big)
        textViewCalories = rootView.findViewById(R.id.burned_calories)
        textViewDistance = rootView.findViewById(R.id.distance)
        stepsProgressBar = rootView.findViewById(R.id.stepsProgressBar)
        fitnessChatbotIcon = rootView.findViewById(R.id.chatbot_icon)
        bmiIcon = rootView.findViewById(R.id.bmi_icon)
        textObjectif = rootView.findViewById(R.id.objective_text)
        objectivProgressBar = rootView.findViewById(R.id.objectivProgressBar)
        caloriesProgressBar = rootView.findViewById(R.id.caloriesProgressBar)
        textCalories = rootView.findViewById(R.id.calories_text)
        calorieChart = rootView.findViewById(R.id.calorieChart)
        timeRangeSpinner = rootView.findViewById(R.id.time_ranges)

        stepsProgressBar.max = stepsGoal

        timeRangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val timeRange = when (position) {
                    0 -> 7  // Last 7 Days
                    1 -> 30 // Last 30 Days
                    else -> 7
                }
                setupChart(timeRange)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun setupClickListeners() {
        fitnessChatbotIcon.setOnClickListener {
            val intent = Intent(requireContext(), ChatMainActivity::class.java)
            startActivity(intent)
        }

        bmiIcon.setOnClickListener {
            val intent = Intent(requireContext(), BmicalcActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        stepCounterViewModel.currentStepCount.observe(viewLifecycleOwner) { currentStepCount ->
            updateUI(currentStepCount)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(currentStepCount: Int) {
        textViewStepsBig.text = currentStepCount.toString()
        stepsProgressBar.progress = currentStepCount

        val currentCaloriesBurned = calculateCalories(currentStepCount) + burnedCalories
        val distance = calculateDistance(currentStepCount)

        textViewCalories.text = currentCaloriesBurned.toString()
        textViewDistance.text = String.format("%.2f", distance)

        // Update daily calories in Room database
        lifecycleScope.launch {
            dailyCalorieRepository.updateDailyCalories(userId,currentCaloriesBurned)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChart(timeRange: Int) {
        lifecycleScope.launch {
            dailyCalorieRepository.getSortedCalories().collect { calories ->
                // Filter data based on the time range
                val filteredCalories = calories.takeLast(timeRange)

                val entries = mutableListOf<Entry>()
                val labels = mutableListOf<String>()

                // Preparing data for the chart
                filteredCalories.forEachIndexed { index, dailyCalorie ->
                    val xValue = index.toFloat()
                    val yValue = dailyCalorie.value.toFloat()
                    entries.add(Entry(xValue, yValue))
                    labels.add(dailyCalorie.date.split("-").last()) // Use only the day (e.g., "23")
                }

                // Creating dataset for the chart
                val dataSet = LineDataSet(entries, "Calories Burned")
                dataSet.color = resources.getColor(R.color.lavender)
                dataSet.valueTextColor = resources.getColor(R.color.lavender)
                dataSet.lineWidth = 2f
                dataSet.setCircleColor(resources.getColor(R.color.primary_light))
                dataSet.circleRadius = 4f
                dataSet.valueTextSize = 10f

                // Create LineData object
                val lineData = LineData(dataSet)
                calorieChart.data = lineData

                // Customize X-axis
                val xAxis = calorieChart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.textSize = 12f
                xAxis.textColor = resources.getColor(R.color.black)
                xAxis.axisLineColor = resources.getColor(R.color.black)
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -45f
                xAxis.axisMinimum = 0f

                // Customize Y-axis (left)
                val yAxisLeft = calorieChart.axisLeft
                yAxisLeft.textSize = 12f
                yAxisLeft.textColor = resources.getColor(R.color.black)
                yAxisLeft.setDrawGridLines(true)
                yAxisLeft.gridColor = resources.getColor(R.color.black)
                yAxisLeft.axisLineColor = resources.getColor(R.color.black)
                yAxisLeft.axisMinimum = 0f

                // Disable right Y-axis
                calorieChart.axisRight.isEnabled = false

                // Add chart description
                calorieChart.description.isEnabled = true
                calorieChart.description.text = "Calories Burned ($timeRange Days)"
                calorieChart.description.textSize = 12f
                calorieChart.description.textColor = resources.getColor(R.color.black)

                calorieChart.invalidate() // Refresh chart
            }
        }
    }






    private fun fetchObjectifs() {
        val authService = RetrofitClient.getRetrofitInstance().create(AuthService::class.java)
        val call = authService.getObjectifs(userId)

        call.enqueue(object : Callback<List<Objectif>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<Objectif>>,
                response: Response<List<Objectif>>
            ) {
                if (response.isSuccessful) {
                    val objectifs = response.body()
                    if (objectifs != null) {
                        handleObjectives(objectifs)
                    }
                } else {
                    resetProgressBars()
                }
            }

            override fun onFailure(call: Call<List<Objectif>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error fetching objectives: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleObjectives(objectifs: List<Objectif>) {
        // Handle Objectives ProgressBar
        allObjectifs = objectifs.size
        doneObjectifs = objectifs.count { it.isDone }
        objectivProgressBar.max = if (allObjectifs > 0) allObjectifs else 1
        objectivProgressBar.progress = doneObjectifs
        textObjectif.text = "$doneObjectifs/$allObjectifs"

        // Handle Calories ProgressBar
        val countObjectives = objectifs.filter { it.type == "COUNT" }
        allBurnedCalories = countObjectives.sumOf {
            (it.value * 0.23).toInt()
        }
        burnedCalories = countObjectives.filter { it.isDone }
            .sumOf {
                (it.value * 0.23).toInt()
            }

        val durationObjectives = objectifs.filter { it.type == "DURATION" && it.isDone }.sumOf {
            it.value.toInt()
        }

        caloriesProgressBar.max = if (allBurnedCalories > 0) allBurnedCalories else 1
        caloriesProgressBar.progress = burnedCalories
        textCalories.text = "$burnedCalories/$allBurnedCalories"
        textDuration.text = "$durationObjectives"

        // Update daily calories with initial burned calories
        lifecycleScope.launch {
            dailyCalorieRepository.updateDailyCalories(userId,burnedCalories)
        }
    }

    private fun resetProgressBars() {
        objectivProgressBar.progress = 0
        textObjectif.text = "0/0"
        caloriesProgressBar.progress = 0
        textCalories.text = "0/0"
    }

    private fun calculateCalories(steps: Int): Int {
        val caloriesPerStep = 0.04
        return (steps * caloriesPerStep).toInt()
    }

    private fun calculateDistance(steps: Int): Double {
        val stepLengthMeters = 0.762
        return (steps * stepLengthMeters) / 1000
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStepCounterService() {
        val serviceIntent = Intent(requireContext(), StepCounterService::class.java)
        requireContext().startForegroundService(serviceIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService()
                Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission is required to count steps",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkDailyCalories() {
        lifecycleScope.launch {
            dailyCalorieRepository.getAllCalories().collect { calories ->
                Log.d("DailyCalories", "All records: ${calories.joinToString {
                    "Date: ${it.date}, Value: ${it.value}"
                }}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupHistoricalData() {
        lifecycleScope.launch {
            // First check if we already have data for the past week

            dailyCalorieRepository.populateDaysData(userId)

            // You can use this to verify the data
            dailyCalorieRepository.getAllCalories().collect { calories ->
                Log.d("DailyCalories", "History: ${calories.joinToString {
                    "\nDate: ${it.date}, Value: ${it.value}"
                }}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun freshStart() {
        lifecycleScope.launch {
            // First check if we already have data for the past week

            dailyCalorieRepository.clearAllCalories(userId)

            // You can use this to verify the data
            dailyCalorieRepository.getAllCalories().collect { calories ->
                Log.d("DailyCalories", "History: ${calories.joinToString {
                    "\nDate: ${it.date}, Value: ${it.value}"
                }}")
            }
        }
    }
}