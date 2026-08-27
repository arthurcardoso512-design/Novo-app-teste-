package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BodyEntryEntity
import com.example.data.local.CheckinEntity
import com.example.data.local.FitTrackDatabase
import com.example.data.local.PhotoEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutSessionEntity
import com.example.data.models.WORKOUT_TEMPLATES
import com.example.data.models.WorkoutSet
import com.example.data.repository.FitTrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FitTrackUiState(
  val profile: UserProfileEntity = UserProfileEntity(startDate = FitTrackRepository.getTodayIso()),
  val currentWeight: Double = 122.0,
  val goalProgressPct: Float = 0f,
  val nextWorkoutType: String = "A",
  val weekWorkoutCount: Int = 0,
  val totalCompletedWorkouts: Int = 0,
  val completedSessions: List<WorkoutSessionEntity> = emptyList(),
  val activeSession: WorkoutSessionEntity? = null,
  val bodyEntries: List<BodyEntryEntity> = emptyList(),
  val checkins: List<CheckinEntity> = emptyList(),
  val photos: List<PhotoEntity> = emptyList(),
  val selectedTab: String = "home", // "home", "workout", "progress", "history", "profile"
  val workoutScreen: String = "workoutHome", // "workoutHome", "intro", "session", "exercise", "complete"
  val introWorkoutType: String = "A",
  val currentExerciseIndex: Int = 0,
  val restTimerSeconds: Int? = null,
  val restTimerTotal: Int = 90,
  val showRIRDialog: Boolean = false,
  val toastMessage: String? = null
)

class FitTrackViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: FitTrackRepository
  private var timerJob: Job? = null

  private val _navigationState = MutableStateFlow(
    NavigationState(
      selectedTab = "home",
      workoutScreen = "workoutHome",
      introWorkoutType = "A",
      currentExerciseIndex = 0,
      restTimerSeconds = null,
      restTimerTotal = 90,
      showRIRDialog = false,
      toastMessage = null
    )
  )

  data class NavigationState(
    val selectedTab: String,
    val workoutScreen: String,
    val introWorkoutType: String,
    val currentExerciseIndex: Int,
    val restTimerSeconds: Int?,
    val restTimerTotal: Int,
    val showRIRDialog: Boolean,
    val toastMessage: String?
  )

  init {
    val db = FitTrackDatabase.getDatabase(application)
    repository = FitTrackRepository(db)
    viewModelScope.launch {
      repository.ensureInitialData()
    }
  }

  private data class RepoData(
    val profileOpt: UserProfileEntity?,
    val sessions: List<WorkoutSessionEntity>,
    val activeSession: WorkoutSessionEntity?,
    val bodyEntries: List<BodyEntryEntity>,
    val checkins: List<CheckinEntity>,
    val photos: List<PhotoEntity>
  )

  private val repoDataFlow = combine(
    combine(
      repository.userProfileFlow,
      repository.allSessions,
      repository.activeSessionFlow
    ) { profile, sessions, active -> Triple(profile, sessions, active) },
    combine(
      repository.allBodyEntries,
      repository.allCheckins,
      repository.allPhotos
    ) { body, checkins, photos -> Triple(body, checkins, photos) }
  ) { (profile, sessions, active), (body, checkins, photos) ->
    RepoData(profile, sessions, active, body, checkins, photos)
  }

  val uiState: StateFlow<FitTrackUiState> = combine(
    repoDataFlow,
    _navigationState
  ) { repo, nav ->
    val profile = repo.profileOpt ?: UserProfileEntity(startDate = FitTrackRepository.getTodayIso())
    val completed = repo.sessions.filter { it.status == "completed" }
    val currentWeight = repo.bodyEntries.lastOrNull { it.weight != null }?.weight ?: profile.startWeight
    val goalWeight = profile.goalWeight
    val startWeight = profile.startWeight

    val progressPct = if (startWeight == goalWeight) {
      100f
    } else {
      val raw = ((startWeight - currentWeight) / (startWeight - goalWeight)) * 100f
      raw.toFloat().coerceIn(0f, 100f)
    }

    val todayWeekKey = FitTrackRepository.getIsoWeekKey(FitTrackRepository.getTodayIso())
    val weekCount = completed.count { FitTrackRepository.getIsoWeekKey(it.date) == todayWeekKey }
    val nextType = FitTrackRepository.calculateNextWorkoutType(completed)

    val active = repo.activeSession

    FitTrackUiState(
      profile = profile,
      currentWeight = currentWeight,
      goalProgressPct = progressPct,
      nextWorkoutType = nextType,
      weekWorkoutCount = weekCount,
      totalCompletedWorkouts = completed.size,
      completedSessions = completed,
      activeSession = active,
      bodyEntries = repo.bodyEntries,
      checkins = repo.checkins,
      photos = repo.photos,
      selectedTab = nav.selectedTab,
      workoutScreen = if (active != null && nav.workoutScreen == "workoutHome") "session" else nav.workoutScreen,
      introWorkoutType = nav.introWorkoutType,
      currentExerciseIndex = nav.currentExerciseIndex,
      restTimerSeconds = nav.restTimerSeconds,
      restTimerTotal = nav.restTimerTotal,
      showRIRDialog = nav.showRIRDialog,
      toastMessage = nav.toastMessage
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = FitTrackUiState()
  )

  fun selectTab(tab: String) {
    _navigationState.update { current ->
      var screen = current.workoutScreen
      if (tab == "workout") {
        val active = uiState.value.activeSession
        if (active == null && screen != "intro" && screen != "complete") {
          screen = "workoutHome"
        }
      }
      current.copy(selectedTab = tab, workoutScreen = screen)
    }
  }

  fun openWorkoutIntro(type: String) {
    _navigationState.update {
      it.copy(
        selectedTab = "workout",
        workoutScreen = "intro",
        introWorkoutType = type
      )
    }
  }

  fun setWorkoutScreen(screen: String) {
    _navigationState.update { it.copy(workoutScreen = screen) }
  }

  fun setExerciseIndex(index: Int) {
    _navigationState.update { it.copy(currentExerciseIndex = index, workoutScreen = "exercise") }
  }

  fun setShowRIRDialog(show: Boolean) {
    _navigationState.update { it.copy(showRIRDialog = show) }
  }

  fun showToast(message: String) {
    _navigationState.update { it.copy(toastMessage = message) }
    viewModelScope.launch {
      delay(2800)
      _navigationState.update { if (it.toastMessage == message) it.copy(toastMessage = null) else it }
    }
  }

  fun startWorkout(workoutType: String) {
    viewModelScope.launch {
      repository.startWorkout(workoutType)
      _navigationState.update {
        it.copy(
          selectedTab = "workout",
          workoutScreen = "session",
          currentExerciseIndex = 0
        )
      }
    }
  }

  fun updateSetField(exerciseIndex: Int, setIndex: Int, field: String, value: String) {
    val active = uiState.value.activeSession ?: return
    val updatedExercises = active.exercises.toMutableList()
    if (exerciseIndex >= updatedExercises.size) return
    val ex = updatedExercises[exerciseIndex]
    val updatedSets = ex.sets.toMutableList()
    if (setIndex >= updatedSets.size) return

    val currentSet = updatedSets[setIndex]
    val newSet = when (field) {
      "load" -> currentSet.copy(load = value)
      "reps" -> currentSet.copy(reps = value)
      else -> currentSet
    }
    updatedSets[setIndex] = newSet
    updatedExercises[exerciseIndex] = ex.copy(sets = updatedSets)

    val updatedSession = active.copy(exercises = updatedExercises)
    viewModelScope.launch {
      repository.updateSession(updatedSession)
    }
  }

  fun markSetDone(exerciseIndex: Int, setIndex: Int, load: String, reps: String, restSeconds: Int) {
    val active = uiState.value.activeSession ?: return
    val updatedExercises = active.exercises.toMutableList()
    if (exerciseIndex >= updatedExercises.size) return
    val ex = updatedExercises[exerciseIndex]
    val updatedSets = ex.sets.toMutableList()
    if (setIndex >= updatedSets.size) return

    // Check progressive overload warning
    val previousSession = findLastCompletedForExercise(ex.exerciseId, active.id)
    if (previousSession != null && setIndex < previousSession.sets.size) {
      val prevLoad = previousSession.sets[setIndex].load.toDoubleOrNull() ?: 0.0
      val newLoadVal = load.toDoubleOrNull() ?: 0.0
      if (prevLoad > 0 && newLoadVal > prevLoad * 1.15) {
        showToast("Você aumentou bastante a carga. Confirme se a execução continua confortável. ⚠️")
      }
    }

    updatedSets[setIndex] = WorkoutSet(load = load, reps = reps, done = true)
    updatedExercises[exerciseIndex] = ex.copy(sets = updatedSets)
    val updatedSession = active.copy(exercises = updatedExercises)

    viewModelScope.launch {
      repository.updateSession(updatedSession)
    }

    // Trigger Rest Timer if not last set
    val isLastSet = setIndex == updatedSets.size - 1
    if (!isLastSet) {
      startRestTimer(restSeconds)
    }
  }

  fun skipExercise(exerciseIndex: Int, reason: String) {
    val active = uiState.value.activeSession ?: return
    val updatedExercises = active.exercises.toMutableList()
    if (exerciseIndex >= updatedExercises.size) return
    val ex = updatedExercises[exerciseIndex]
    updatedExercises[exerciseIndex] = ex.copy(skipped = true, skipReason = reason)
    val updatedSession = active.copy(exercises = updatedExercises)
    viewModelScope.launch {
      repository.updateSession(updatedSession)
    }
  }

  fun startRestTimer(seconds: Int) {
    timerJob?.cancel()
    _navigationState.update { it.copy(restTimerSeconds = seconds, restTimerTotal = seconds) }
    timerJob = viewModelScope.launch {
      var remaining = seconds
      while (remaining > 0) {
        delay(1000)
        remaining--
        _navigationState.update { it.copy(restTimerSeconds = remaining) }
      }
    }
  }

  fun addRestTimerSeconds(extra: Int) {
    val current = _navigationState.value.restTimerSeconds ?: return
    val newSeconds = current + extra
    startRestTimer(newSeconds)
  }

  fun cancelRestTimer() {
    timerJob?.cancel()
    _navigationState.update { it.copy(restTimerSeconds = null) }
  }

  fun finishWorkout(cardioMin: String) {
    val active = uiState.value.activeSession ?: return
    cancelRestTimer()
    viewModelScope.launch {
      repository.finishWorkout(active.id, cardioMin)
      _navigationState.update { it.copy(workoutScreen = "complete") }
    }
  }

  fun goHomeFromComplete() {
    cancelRestTimer()
    _navigationState.update {
      it.copy(
        selectedTab = "home",
        workoutScreen = "workoutHome"
      )
    }
  }

  fun findLastCompletedForExercise(exerciseId: String, currentSessionId: String): com.example.data.models.SessionExercise? {
    val list = uiState.value.completedSessions.filter { it.id != currentSessionId }
    for (s in list) {
      val found = s.exercises.find { it.exerciseId == exerciseId && !it.skipped }
      if (found != null) return found
    }
    return null
  }

  fun addBodyEntry(entry: BodyEntryEntity) {
    viewModelScope.launch {
      repository.insertBodyEntry(entry)
      showToast("Peso / Medidas registradas ✅")
    }
  }

  fun addCheckin(checkin: CheckinEntity) {
    viewModelScope.launch {
      repository.insertCheckin(checkin)
      showToast("Check-in semanal salvo ✅")
    }
  }

  fun addPhoto(photo: PhotoEntity) {
    viewModelScope.launch {
      repository.insertPhoto(photo)
      showToast("Foto adicionada 📸")
    }
  }

  fun deletePhoto(photo: PhotoEntity) {
    viewModelScope.launch {
      repository.deletePhoto(photo)
      showToast("Foto removida")
    }
  }

  fun updateProfile(profile: UserProfileEntity) {
    viewModelScope.launch {
      repository.updateProfile(profile)
      showToast("Perfil atualizado ✅")
    }
  }
}
