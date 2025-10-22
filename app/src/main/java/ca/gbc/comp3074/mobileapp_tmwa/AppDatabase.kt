package ca.gbc.comp3074.mobileapp_tmwa

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import ca.gbc.comp3074.mobileapp_tmwa.dao.EventDao
import ca.gbc.comp3074.mobileapp_tmwa.domain.model.EventEntity
import ca.gbc.comp3074.mobileapp_tmwa.util.Converters

@Database(entities = [EventEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "events_db"
                ).build().also { INSTANCE = it }
            }
    }
}
