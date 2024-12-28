package ma.ensa.mobile.profit.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ma.ensa.mobile.profit.R
import ma.ensa.mobile.profit.viewmodels.StepCounterViewModel

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var initialStepCount: Int = 0
    private var currentStepCount: Int = 0

    private lateinit var stepCounterViewModel: StepCounterViewModel

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        // Initialize ViewModel with singleton instance
        stepCounterViewModel = StepCounterViewModel.getInstance()
        stepCounterViewModel.init(applicationContext)

        // Initialize SensorManager and Step Counter Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Register the step sensor listener
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?: run {
            // Handle case where step sensor is not available
            stopSelf()
        }

        // Start the foreground service with a notification
        createNotificationChannel()
        val notification = createNotification("Step Counter is Active")
        startForeground(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == 0) {
                initialStepCount = event.values[0].toInt()
            }
            currentStepCount = event.values[0].toInt() - initialStepCount

            // Update the step count in the ViewModel
            stepCounterViewModel.updateStepCount(currentStepCount)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelId = "step_counter_channel"
        val channel = NotificationChannel(
            channelId,
            "Step Counter Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        val channelId = "step_counter_channel"
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Profit")
            .setContentText(content)
            .setSmallIcon(R.drawable.steps) // Replace with your icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
