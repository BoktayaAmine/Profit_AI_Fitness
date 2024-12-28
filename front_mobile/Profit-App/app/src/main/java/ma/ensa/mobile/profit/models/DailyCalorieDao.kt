package ma.ensa.mobile.profit.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCalorieDao {

    // Get the daily calories for a specific date
    @Query("SELECT * FROM daily_calories WHERE date = :date AND user_id = :userId")
    suspend fun getCaloriesForDate(date: String, userId: Long): DailyCalorie?

    // Get all daily calories for a specific user, ordered by date (most recent first)
    @Query("SELECT * FROM daily_calories WHERE user_id = :userId ORDER BY date DESC")
    suspend fun getCaloriesForUser(userId: Long): List<DailyCalorie>

    // Get all daily calories for all users, returns as a Flow for observing changes
    @Query("SELECT * FROM daily_calories")
    fun getAllCalories(): Flow<List<DailyCalorie>>

    // Clear all records from the daily_calories table
    @Query("DELETE FROM daily_calories WHERE user_id = :userId")
    suspend fun clearAllForUser(userId: Long)

    // Insert or update daily calorie record for a specific user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyCalorie: DailyCalorie)

    // Update an existing daily calorie record
    @Update
    suspend fun update(dailyCalorie: DailyCalorie)

    // Delete a specific daily calorie record
    @Delete
    suspend fun delete(dailyCalorie: DailyCalorie)
}
