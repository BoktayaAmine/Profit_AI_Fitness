package ma.ensa.mobile.profit.models

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class DailyCalorieRepository(private val dailyCalorieDao: DailyCalorieDao) {

    fun getAllCalories(): Flow<List<DailyCalorie>> = dailyCalorieDao.getAllCalories()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateDailyCalories(userId: Long, burnedCalories: Int) {
        val today = LocalDate.now().toString()
        val existingRecord = dailyCalorieDao.getCaloriesForDate(today, userId)

        if (existingRecord == null || burnedCalories > existingRecord.value) {
            val dailyCalorie = DailyCalorie(today, burnedCalories, userId)
            dailyCalorieDao.insert(dailyCalorie)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSortedCalories(): Flow<List<DailyCalorie>> {
        return dailyCalorieDao.getAllCalories().map { list ->
            list.sortedBy { LocalDate.parse(it.date) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun populateDaysData(userId: Long) {
        val today = LocalDate.now()

        val pastCalories = listOf(
            1250, 1400, 1100, 1600, 1300, 1450, 1500, 1200, 1350, 1550, 1400,
            1250, 1300, 1450, 1550, 1600, 1200, 1100, 1400, 1300, 1500, 1450
        )

        for (i in 1..22) {
            val date = today.minusDays(i.toLong()).toString()
            val calories = pastCalories[i - 1]
            dailyCalorieDao.insert(DailyCalorie(date, calories, userId))
        }
    }

    // Clear all calories records for a specific user
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun clearAllCalories(userId: Long) {
        dailyCalorieDao.clearAllForUser(userId)
    }
}
