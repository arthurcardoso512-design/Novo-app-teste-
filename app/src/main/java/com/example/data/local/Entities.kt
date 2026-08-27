package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.SessionExercise

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
  @PrimaryKey val id: String,
  val workoutType: String,
  val date: String, // YYYY-MM-DD
  val startTime: String,
  val endTime: String? = null,
  val status: String = "in_progress", // "in_progress" | "completed"
  val cardioMin: String = "",
  val exercises: List<SessionExercise> = emptyList()
)

@Entity(tableName = "body_entries")
data class BodyEntryEntity(
  @PrimaryKey val date: String, // YYYY-MM-DD (one per date)
  val weight: Double? = null,
  val waist: Double? = null,
  val abdomen: Double? = null,
  val chest: Double? = null,
  val arm: Double? = null,
  val thigh: Double? = null,
  val note: String = ""
)

@Entity(tableName = "checkins")
data class CheckinEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String,
  val weight: String = "",
  val steps: String = "",
  val workouts: Int = 0,
  val energy: Int = 5,
  val sleep: Int = 5,
  val hunger: Int = 5,
  val pain: String = "Não",
  val notes: String = ""
)

@Entity(tableName = "photos")
data class PhotoEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: String,
  val category: String, // "Frente" | "Lateral" | "Costas"
  val imageUri: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: Int = 1,
  val name: String = "",
  val startWeight: Double = 122.0,
  val goalWeight: Double = 100.0,
  val startDate: String
)
