package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [
    WorkoutSessionEntity::class,
    BodyEntryEntity::class,
    CheckinEntity::class,
    PhotoEntity::class,
    UserProfileEntity::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {
  abstract fun workoutSessionDao(): WorkoutSessionDao
  abstract fun bodyEntryDao(): BodyEntryDao
  abstract fun checkinDao(): CheckinDao
  abstract fun photoDao(): PhotoDao
  abstract fun userProfileDao(): UserProfileDao

  companion object {
    @Volatile
    private var INSTANCE: FitTrackDatabase? = null

    fun getDatabase(context: Context): FitTrackDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          FitTrackDatabase::class.java,
          "fittrack_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
