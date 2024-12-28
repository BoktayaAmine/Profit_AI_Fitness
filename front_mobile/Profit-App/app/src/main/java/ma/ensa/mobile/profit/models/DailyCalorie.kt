package ma.ensa.mobile.profit.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_calories")
data class DailyCalorie(
    @PrimaryKey
    val date: String,
    var value: Int,
    val user_id: Long,
)