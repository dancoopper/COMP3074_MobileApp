package ca.gbc.comp3074.mobileapp_tmwa.dao

import androidx.room.*
import ca.gbc.comp3074.mobileapp_tmwa.domain.model.EventEntity

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY startDateTime ASC")
    suspend fun getAllEvents(): List<EventEntity>

    @Delete
    suspend fun deleteEvent(event: EventEntity)
}
