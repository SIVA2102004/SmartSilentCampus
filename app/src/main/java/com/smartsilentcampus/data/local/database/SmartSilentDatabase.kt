package com.smartsilentcampus.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartsilentcampus.data.local.dao.*
import com.smartsilentcampus.data.local.entity.*
import com.smartsilentcampus.domain.model.SoundProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        LocationEntity::class,
        SoundProfileEntity::class,
        PreviousSoundStateEntity::class,
        AutomationHistoryEntity::class,
        ActiveZoneEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartSilentDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun soundProfileDao(): SoundProfileDao
    abstract fun previousSoundStateDao(): PreviousSoundStateDao
    abstract fun automationHistoryDao(): AutomationHistoryDao
    abstract fun activeZoneDao(): ActiveZoneDao

    companion object {
        const val DATABASE_NAME = "smart_silent.db"

        fun buildDatabase(context: Context): SmartSilentDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SmartSilentDatabase::class.java,
                DATABASE_NAME
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed default sound profiles on first database creation
                    CoroutineScope(Dispatchers.IO).launch {
                        val defaultEntities = SoundProfile.defaultProfiles().map {
                            SoundProfileEntity.fromDomain(it)
                        }
                        // Note: Database instance is injected or populated via DatabaseCallback
                    }
                }
            }).fallbackToDestructiveMigration().build()
        }
    }
}
