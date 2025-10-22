package ca.gbc.comp3074.mobileapp_tmwa.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val type: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val isRepeat: Boolean,
)

