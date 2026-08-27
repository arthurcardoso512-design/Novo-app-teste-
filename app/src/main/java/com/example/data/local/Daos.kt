package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
  @Query("SELECT * FROM workout_sessions ORDER BY date DESC, startTime DESC")
  fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

  @Query("SELECT * FROM workout_sessions WHERE id = :id LIMIT 1")
  suspend fun getSessionById(id: String): WorkoutSessionEntity?

  @Query("SELECT * FROM workout_sessions WHERE status = 'in_progress' LIMIT 1")
  fun getActiveSessionFlow(): Flow<WorkoutSessionEntity?>

  @Query("SELECT * FROM workout_sessions WHERE status = 'in_progress' LIMIT 1")
  suspend fun getActiveSession(): WorkoutSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: WorkoutSessionEntity)

  @Update
  suspend fun updateSession(session: WorkoutSessionEntity)

  @Query("DELETE FROM workout_sessions WHERE id = :id")
  suspend fun deleteSessionById(id: String)

  @Query("DELETE FROM workout_sessions")
  suspend fun clearAll()
}

@Dao
interface BodyEntryDao {
  @Query("SELECT * FROM body_entries ORDER BY date ASC")
  fun getAllBodyEntries(): Flow<List<BodyEntryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEntry(entry: BodyEntryEntity)

  @Query("DELETE FROM body_entries WHERE date = :date")
  suspend fun deleteEntryByDate(date: String)

  @Query("DELETE FROM body_entries")
  suspend fun clearAll()
}

@Dao
interface CheckinDao {
  @Query("SELECT * FROM checkins ORDER BY date DESC, id DESC")
  fun getAllCheckins(): Flow<List<CheckinEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCheckin(checkin: CheckinEntity)

  @Query("DELETE FROM checkins")
  suspend fun clearAll()
}

@Dao
interface PhotoDao {
  @Query("SELECT * FROM photos ORDER BY date DESC, id DESC")
  fun getAllPhotos(): Flow<List<PhotoEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPhoto(photo: PhotoEntity)

  @Delete
  suspend fun deletePhoto(photo: PhotoEntity)

  @Query("DELETE FROM photos")
  suspend fun clearAll()
}

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getProfileFlow(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getProfile(): UserProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
