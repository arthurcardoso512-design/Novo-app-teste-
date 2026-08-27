package com.example.data.repository

import com.example.data.local.BodyEntryEntity
import com.example.data.local.CheckinEntity
import com.example.data.local.FitTrackDatabase
import com.example.data.local.PhotoEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutSessionEntity
import com.example.data.models.SessionExercise
import com.example.data.models.WORKOUT_ORDER
import com.example.data.models.WORKOUT_TEMPLATES
import com.example.data.models.WorkoutSet
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FitTrackRepository(private val database: FitTrackDatabase) {
  private val workoutDao = database.workoutSessionDao()
  private val bodyDao = database.bodyEntryDao()
  private val checkinDao = database.checkinDao()
  private val photoDao = database.photoDao()
  private val profileDao = database.userProfileDao()

  val allSessions: Flow<List<WorkoutSessionEntity>> = workoutDao.getAllSessions()
  val activeSessionFlow: Flow<WorkoutSessionEntity?> = workoutDao.getActiveSessionFlow()
  val allBodyEntries: Flow<List<BodyEntryEntity>> = bodyDao.getAllBodyEntries()
  val allCheckins: Flow<List<CheckinEntity>> = checkinDao.getAllCheckins()
  val allPhotos: Flow<List<PhotoEntity>> = photoDao.getAllPhotos()
  val userProfileFlow: Flow<UserProfileEntity?> = profileDao.getProfileFlow()

  suspend fun ensureInitialData() {
    val existingProfile = profileDao.getProfile()
    if (existingProfile == null) {
      val today = getTodayIso()
      profileDao.insertOrUpdateProfile(
        UserProfileEntity(
          id = 1,
          name = "",
          startWeight = 122.0,
          goalWeight = 100.0,
          startDate = today
        )
      )
      // Add initial body entry for start weight if none exists
      bodyDao.insertEntry(
        BodyEntryEntity(
          date = today,
          weight = 122.0,
          note = "Peso inicial"
        )
      )
    }
  }

  suspend fun startWorkout(workoutType: String): WorkoutSessionEntity {
    val template = WORKOUT_TEMPLATES[workoutType] ?: WORKOUT_TEMPLATES["A"]!!
    val now = Date()
    val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    val session = WorkoutSessionEntity(
      id = System.currentTimeMillis().toString(),
      workoutType = workoutType,
      date = isoDateFormat.format(now),
      startTime = isoDateTimeFormat.format(now),
      endTime = null,
      status = "in_progress",
      cardioMin = "",
      exercises = template.exercises.map { ex ->
        SessionExercise(
          exerciseId = ex.id,
          sets = List(ex.sets) { WorkoutSet(load = "", reps = "", done = false) },
          skipped = false,
          skipReason = ""
        )
      }
    )
    workoutDao.insertSession(session)
    return session
  }

  suspend fun updateSession(session: WorkoutSessionEntity) {
    workoutDao.updateSession(session)
  }

  suspend fun finishWorkout(sessionId: String, cardioMin: String) {
    val current = workoutDao.getSessionById(sessionId) ?: return
    val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val updated = current.copy(
      status = "completed",
      endTime = isoDateTimeFormat.format(Date()),
      cardioMin = cardioMin
    )
    workoutDao.updateSession(updated)
  }

  suspend fun deleteSession(id: String) {
    workoutDao.deleteSessionById(id)
  }

  suspend fun insertBodyEntry(entry: BodyEntryEntity) {
    bodyDao.insertEntry(entry)
  }

  suspend fun insertCheckin(checkin: CheckinEntity) {
    checkinDao.insertCheckin(checkin)
  }

  suspend fun insertPhoto(photo: PhotoEntity) {
    photoDao.insertPhoto(photo)
  }

  suspend fun deletePhoto(photo: PhotoEntity) {
    photoDao.deletePhoto(photo)
  }

  suspend fun updateProfile(profile: UserProfileEntity) {
    profileDao.insertOrUpdateProfile(profile)
  }

  suspend fun exportAllDataJson(): String {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val map = mapOf(
      "profile" to profileDao.getProfile(),
      "exportDate" to getTodayIso()
    )
    val adapter = moshi.adapter(Map::class.java)
    return adapter.toJson(map)
  }

  companion object {
    fun getTodayIso(): String {
      return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun formatDisplayDate(iso: String): String {
      return try {
        val parts = iso.split("-")
        if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
      } catch (e: Exception) {
        iso
      }
    }

    fun formatShortDate(iso: String): String {
      return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(iso) ?: return iso
        val out = SimpleDateFormat("dd MMM", Locale("pt", "BR"))
        out.format(date).uppercase().replace(".", "")
      } catch (e: Exception) {
        iso
      }
    }

    fun getIsoWeekKey(dateStr: String): String {
      return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.time = sdf.parse(dateStr) ?: Date()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        "$year-W$week"
      } catch (e: Exception) {
        dateStr
      }
    }

    fun calculateNextWorkoutType(completedSessions: List<WorkoutSessionEntity>): String {
      if (completedSessions.isEmpty()) return "A"
      val last = completedSessions.first() // sorted DESC by date/time
      val idx = WORKOUT_ORDER.indexOf(last.workoutType)
      return if (idx >= 0) WORKOUT_ORDER[(idx + 1) % WORKOUT_ORDER.size] else "A"
    }
  }
}
