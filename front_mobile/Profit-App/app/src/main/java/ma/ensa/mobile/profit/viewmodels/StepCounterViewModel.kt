package ma.ensa.mobile.profit.viewmodels


import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepCounterViewModel : ViewModel() {
    companion object {
        private lateinit var instance: StepCounterViewModel

        fun getInstance(): StepCounterViewModel {
            if (!::instance.isInitialized) {
                instance = StepCounterViewModel()
            }
            return instance
        }
    }

    private val _currentStepCount = MutableLiveData<Int>(0)
    val currentStepCount: LiveData<Int> get() = _currentStepCount

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences("step_counter", Context.MODE_PRIVATE)
            _currentStepCount.value = sharedPreferences.getInt("current_steps", 0)
        }
    }

    fun updateStepCount(newStepCount: Int) {
        _currentStepCount.postValue(newStepCount)  // Use postValue for background thread
        if (::sharedPreferences.isInitialized) {
            sharedPreferences.edit().putInt("current_steps", newStepCount).apply()
        }
    }
}
