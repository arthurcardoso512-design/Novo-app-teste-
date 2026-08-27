package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SessionExercise
import com.example.data.models.WORKOUT_ORDER
import com.example.data.models.WORKOUT_TEMPLATES
import com.example.data.models.WorkoutTemplate
import com.example.ui.FitTrackUiState
import com.example.ui.FitTrackViewModel
import com.example.ui.components.FitTrackButton
import com.example.ui.components.FitTrackButtonVariant
import com.example.ui.components.FitTrackCard
import com.example.ui.theme.Amber
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Ember
import com.example.ui.theme.EmberDim
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHi
import com.example.ui.theme.Teal
import com.example.ui.theme.TealDim
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun WorkoutScreen(
  uiState: FitTrackUiState,
  viewModel: FitTrackViewModel,
  modifier: Modifier = Modifier
) {
  val activeSession = uiState.activeSession

  // Determine current screen state
  when {
    activeSession != null && uiState.workoutScreen == "exercise" -> {
      ExerciseDetailView(
        uiState = uiState,
        viewModel = viewModel,
        modifier = modifier
      )
    }
    activeSession != null && uiState.workoutScreen == "complete" -> {
      SessionCompleteView(
        uiState = uiState,
        viewModel = viewModel,
        modifier = modifier
      )
    }
    activeSession != null -> {
      SessionOverviewView(
        uiState = uiState,
        viewModel = viewModel,
        modifier = modifier
      )
    }
    uiState.workoutScreen == "intro" -> {
      WorkoutIntroView(
        workoutType = uiState.introWorkoutType,
        onBack = { viewModel.setWorkoutScreen("workoutHome") },
        onStart = { viewModel.startWorkout(uiState.introWorkoutType) },
        modifier = modifier
      )
    }
    else -> {
      WorkoutHomeListView(
        nextWorkoutType = uiState.nextWorkoutType,
        onSelectWorkout = { type -> viewModel.openWorkoutIntro(type) },
        modifier = modifier
      )
    }
  }
}

@Composable
private fun WorkoutHomeListView(
  nextWorkoutType: String,
  onSelectWorkout: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Treino 🏋️",
      style = MaterialTheme.typography.displayMedium,
      color = TextPrimary
    )

    WORKOUT_ORDER.forEach { key ->
      val template = WORKOUT_TEMPLATES[key] ?: return@forEach
      val isNext = key == nextWorkoutType

      FitTrackCard(
        borderColor = if (isNext) Ember else BorderDark,
        onClick = { onSelectWorkout(key) }
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isNext) EmberDim else SurfaceHi),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = template.id,
                style = MaterialTheme.typography.titleLarge,
                color = if (isNext) Ember else TextSecondary,
                fontWeight = FontWeight.Black
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = template.name,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = template.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }

            if (isNext) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(EmberDim)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "PRÓXIMO",
                  style = MaterialTheme.typography.labelSmall,
                  color = Ember,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Text(
              text = "⏱️ ~${template.estTime} min",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
            Text(
              text = "🏋️ ${template.exercises.size} exercícios",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          FitTrackButton(
            text = if (isNext) "COMEÇAR AGORA" else "VER TREINO",
            onClick = { onSelectWorkout(key) },
            variant = if (isNext) FitTrackButtonVariant.PRIMARY else FitTrackButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // Locked Workout D
    FitTrackCard(
      modifier = Modifier.padding(bottom = 16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceHi),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "Treino D — Em breve",
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Depois de 4–8 semanas podemos avaliar a inclusão de um quarto treino.",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary
          )
        }
      }
    }
  }
}

@Composable
private fun WorkoutIntroView(
  workoutType: String,
  onBack: () -> Unit,
  onStart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val template = WORKOUT_TEMPLATES[workoutType] ?: WORKOUT_TEMPLATES["A"]!!
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable(onClick = onBack)
        .padding(vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Voltar",
        tint = TextSecondary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Voltar",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
      )
    }

    FitTrackCard {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "🏋️", fontSize = 42.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Treino ${template.id}",
          style = MaterialTheme.typography.displaySmall,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = template.subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = "⏱️ ${template.estTime} minutos",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
          Text(
            text = "🏋️ ${template.exercises.size} exercícios",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }
      }
    }

    // Warmup Card
    FitTrackCard {
      Column {
        Text(
          text = "AQUECIMENTO",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = template.warmup,
          style = MaterialTheme.typography.bodyMedium,
          color = TextPrimary
        )
      }
    }

    // Exercises Overview
    Text(
      text = "Exercícios da Sessão",
      style = MaterialTheme.typography.titleMedium,
      color = TextPrimary,
      fontWeight = FontWeight.Bold
    )

    template.exercises.forEachIndexed { i, ex ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(SurfaceDark)
          .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceHi),
          contentAlignment = Alignment.Center
        ) {
          Text(text = ex.icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${i + 1}. ${ex.name}",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "${ex.sets}× ${ex.reps} · RIR ${ex.rir}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    FitTrackButton(
      text = "FAZER CHECK-IN",
      onClick = onStart,
      icon = Icons.Default.PlayArrow,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun SessionOverviewView(
  uiState: FitTrackUiState,
  viewModel: FitTrackViewModel,
  modifier: Modifier = Modifier
) {
  val session = uiState.activeSession ?: return
  val template = WORKOUT_TEMPLATES[session.workoutType] ?: WORKOUT_TEMPLATES["A"]!!
  val scrollState = rememberScrollState()

  val doneCount = session.exercises.count { it.skipped || (it.sets.isNotEmpty() && it.sets.all { s -> s.done }) }
  val totalCount = template.exercises.size
  val allFinished = doneCount == totalCount

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Column {
      Text(
        text = "TREINO ${template.id}",
        style = MaterialTheme.typography.labelSmall,
        color = Ember,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = template.subtitle,
        style = MaterialTheme.typography.displaySmall,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "$doneCount de $totalCount exercícios concluídos",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(SurfaceHi)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(fraction = (doneCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f))
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Ember)
        )
      }
    }

    template.exercises.forEachIndexed { i, ex ->
      val exState = session.exercises.getOrNull(i) ?: SessionExercise(exerciseId = ex.id, sets = emptyList())
      val isDone = exState.skipped || (exState.sets.isNotEmpty() && exState.sets.all { it.done })

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(if (isDone) SurfaceDark.copy(alpha = 0.6f) else SurfaceDark)
          .border(
            1.dp,
            if (isDone) Teal.copy(alpha = 0.4f) else BorderDark,
            RoundedCornerShape(18.dp)
          )
          .clickable { viewModel.setExerciseIndex(i) }
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDone) TealDim else SurfaceHi),
          contentAlignment = Alignment.Center
        ) {
          if (isDone && !exState.skipped) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Concluído",
              tint = Teal,
              modifier = Modifier.size(22.dp)
            )
          } else {
            Text(text = ex.icon, fontSize = 20.sp)
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = ex.name,
            style = MaterialTheme.typography.titleMedium,
            color = if (isDone) TextSecondary else TextPrimary,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = if (exState.skipped) "Pulado (${exState.skipReason.ifBlank { "Sem motivo" }})" else "${ex.sets}× ${ex.reps} · RIR ${ex.rir}",
            style = MaterialTheme.typography.bodySmall,
            color = if (exState.skipped) Amber else TextTertiary
          )
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Ir",
          tint = if (isDone) TextTertiary else Ember,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    if (allFinished) {
      FitTrackButton(
        text = "FINALIZAR TREINO",
        onClick = { viewModel.setWorkoutScreen("complete") },
        icon = Icons.Default.Check,
        variant = FitTrackButtonVariant.TEAL,
        modifier = Modifier.fillMaxWidth()
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun ExerciseDetailView(
  uiState: FitTrackUiState,
  viewModel: FitTrackViewModel,
  modifier: Modifier = Modifier
) {
  val session = uiState.activeSession ?: return
  val template = WORKOUT_TEMPLATES[session.workoutType] ?: WORKOUT_TEMPLATES["A"]!!
  val exIndex = uiState.currentExerciseIndex.coerceIn(0, template.exercises.size - 1)
  val exTemplate = template.exercises[exIndex]
  val exState = session.exercises.getOrNull(exIndex) ?: SessionExercise(exerciseId = exTemplate.id, sets = emptyList())
  val previous = viewModel.findLastCompletedForExercise(exTemplate.id, session.id)

  val isLastExercise = exIndex == template.exercises.size - 1
  val allDone = exState.skipped || (exState.sets.isNotEmpty() && exState.sets.all { it.done })

  var showSkipDialog by remember { mutableStateOf(false) }
  var skipReasonInput by remember { mutableStateOf("") }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Navigation back to session list
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable { viewModel.setWorkoutScreen("session") }
        .padding(vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Ver treino completo",
        tint = TextSecondary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Ver treino completo",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
      )
    }

    // Exercise Hero Header
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(SurfaceHi),
        contentAlignment = Alignment.Center
      ) {
        Text(text = exTemplate.icon, fontSize = 42.sp)
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = exTemplate.name,
        style = MaterialTheme.typography.displaySmall,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = exTemplate.focus,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )
    }

    // Execution Cue Description
    FitTrackCard {
      Text(
        text = exTemplate.desc,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimary
      )
    }

    // Alert / Warning if available
    if (exTemplate.alert != null) {
      FitTrackCard(
        backgroundColor = Amber.copy(alpha = 0.12f),
        borderColor = Amber.copy(alpha = 0.4f)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = exTemplate.alert,
            style = MaterialTheme.typography.bodySmall,
            color = Amber,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Target RIR & Rest tags
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { viewModel.setShowRIRDialog(true) }
          .padding(4.dp)
      ) {
        Text(
          text = "RIR ${exTemplate.rir}",
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "O que é RIR?",
          tint = TextSecondary,
          modifier = Modifier.size(14.dp)
        )
      }

      Text(
        text = "Descanso: ${exTemplate.rest}s",
        style = MaterialTheme.typography.bodySmall,
        color = TextTertiary
      )
    }

    // Previous session stats
    if (previous != null && previous.sets.isNotEmpty()) {
      FitTrackCard(
        backgroundColor = SurfaceHi.copy(alpha = 0.7f)
      ) {
        Column {
          Text(
            text = "SESSÃO ANTERIOR",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = previous.sets.joinToString("  /  ") { "${it.load.ifBlank { "0" }}kg × ${it.reps.ifBlank { "0" }} reps" },
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Sets inputs
    if (!exState.skipped) {
      exState.sets.forEachIndexed { setI, s ->
        FitTrackCard(
          backgroundColor = if (s.done) SurfaceDark.copy(alpha = 0.5f) else SurfaceDark,
          borderColor = if (s.done) Teal.copy(alpha = 0.4f) else BorderDark
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "SÉRIE ${setI + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
              )
              if (s.done) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Feita",
                    style = MaterialTheme.typography.labelSmall,
                    color = Teal,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Load input
              OutlinedTextField(
                value = s.load,
                onValueChange = { viewModel.updateSetField(exIndex, setI, "load", it) },
                enabled = !s.done,
                label = { Text("CARGA (KG)", style = MaterialTheme.typography.labelSmall) },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Ember,
                  unfocusedBorderColor = BorderDark,
                  disabledBorderColor = BorderDark.copy(alpha = 0.4f),
                  focusedContainerColor = SurfaceHi,
                  unfocusedContainerColor = SurfaceHi,
                  disabledContainerColor = SurfaceHi.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp)
              )

              // Reps input
              OutlinedTextField(
                value = s.reps,
                onValueChange = { viewModel.updateSetField(exIndex, setI, "reps", it) },
                enabled = !s.done,
                label = { Text("REPETIÇÕES", style = MaterialTheme.typography.labelSmall) },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Ember,
                  unfocusedBorderColor = BorderDark,
                  disabledBorderColor = BorderDark.copy(alpha = 0.4f),
                  focusedContainerColor = SurfaceHi,
                  unfocusedContainerColor = SurfaceHi,
                  disabledContainerColor = SurfaceHi.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp)
              )
            }

            if (!s.done) {
              Spacer(modifier = Modifier.height(10.dp))
              FitTrackButton(
                text = "MARCAR SÉRIE COMO FEITA",
                onClick = {
                  viewModel.markSetDone(
                    exerciseIndex = exIndex,
                    setIndex = setI,
                    load = s.load,
                    reps = s.reps,
                    restSeconds = exTemplate.rest
                  )
                },
                enabled = s.load.isNotBlank() && s.reps.isNotBlank(),
                icon = Icons.Default.Check,
                variant = FitTrackButtonVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      }
    }

    if (exState.skipped) {
      FitTrackCard(backgroundColor = SurfaceHi) {
        Text(
          text = "Exercício pulado: ${exState.skipReason.ifBlank { "Sem motivo informado" }}",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )
      }
    }

    // Bottom Actions: Next / Finish or Skip
    if (allDone) {
      FitTrackCard(
        backgroundColor = EmberDim,
        borderColor = Ember.copy(alpha = 0.5f)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (exState.skipped) "Exercício pulado" else "Exercício concluído! 🔥",
            style = MaterialTheme.typography.titleMedium,
            color = Ember,
            fontWeight = FontWeight.Bold
          )
        }
      }

      FitTrackButton(
        text = if (isLastExercise) "FINALIZAR TREINO" else "PRÓXIMO EXERCÍCIO",
        onClick = {
          if (isLastExercise) {
            viewModel.setWorkoutScreen("complete")
          } else {
            viewModel.setExerciseIndex(exIndex + 1)
          }
        },
        icon = if (isLastExercise) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
        modifier = Modifier.fillMaxWidth()
      )
    } else {
      if (!showSkipDialog) {
        Text(
          text = "Pular exercício",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .clip(RoundedCornerShape(8.dp))
            .clickable { showSkipDialog = true }
            .padding(8.dp)
        )
      } else {
        FitTrackCard {
          Column {
            Text(
              text = "Motivo para pular (opcional):",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = skipReasonInput,
              onValueChange = { skipReasonInput = it },
              placeholder = { Text("Ex: Equipamento ocupado, dor...") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              FitTrackButton(
                text = "Cancelar",
                onClick = { showSkipDialog = false },
                variant = FitTrackButtonVariant.SECONDARY,
                modifier = Modifier.weight(1f)
              )
              FitTrackButton(
                text = "Confirmar",
                onClick = {
                  viewModel.skipExercise(exIndex, skipReasonInput)
                  showSkipDialog = false
                },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun SessionCompleteView(
  uiState: FitTrackUiState,
  viewModel: FitTrackViewModel,
  modifier: Modifier = Modifier
) {
  val session = uiState.activeSession ?: return
  val template = WORKOUT_TEMPLATES[session.workoutType] ?: WORKOUT_TEMPLATES["A"]!!
  val isAlreadyCompleted = session.status == "completed"

  var cardioInput by remember { mutableStateOf(session.cardioMin) }
  val scrollState = rememberScrollState()

  val totalSets = session.exercises.sumOf { it.sets.count { s -> s.done } }
  val plannedSets = session.exercises.sumOf { it.sets.size }
  val exercisesDone = session.exercises.count { it.skipped || it.sets.all { s -> s.done } }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    if (!isAlreadyCompleted) {
      Text(text = "🎉", fontSize = 52.sp)
      Text(
        text = "Quase lá!",
        style = MaterialTheme.typography.displayMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Black
      )
      Text(
        text = "Registre o cardio (opcional) para finalizar o treino.",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      FitTrackCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = template.cardio,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = cardioInput,
            onValueChange = { cardioInput = it },
            placeholder = { Text("Minutos de cardio (ex: 10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
          )
        }
      }

      FitTrackButton(
        text = "FINALIZAR E SALVAR",
        onClick = { viewModel.finishWorkout(cardioInput) },
        icon = Icons.Default.Check,
        variant = FitTrackButtonVariant.TEAL,
        modifier = Modifier.fillMaxWidth()
      )
    } else {
      Text(text = "🎉", fontSize = 52.sp)
      Text(
        text = "TREINO CONCLUÍDO!",
        style = MaterialTheme.typography.displayMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Black
      )
      Text(
        text = "Treino ${template.id} — ${template.subtitle}",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
      )

      // Summary 4-grid stats
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        FitTrackCard(modifier = Modifier.weight(1f)) {
          Column {
            Text(
              text = "$exercisesDone/${template.exercises.size}",
              style = MaterialTheme.typography.displaySmall,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Exercícios",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        FitTrackCard(modifier = Modifier.weight(1f)) {
          Column {
            Text(
              text = "$totalSets/$plannedSets",
              style = MaterialTheme.typography.displaySmall,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Séries",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        FitTrackCard(modifier = Modifier.weight(1f)) {
          Column {
            Text(
              text = "${session.cardioMin.ifBlank { "0" }} min",
              style = MaterialTheme.typography.displaySmall,
              color = Teal,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Cardio",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        FitTrackCard(modifier = Modifier.weight(1f)) {
          Column {
            Text(
              text = "~${template.estTime} min",
              style = MaterialTheme.typography.displaySmall,
              color = Ember,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Duração",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }

      FitTrackCard(
        backgroundColor = TealDim,
        borderColor = Teal.copy(alpha = 0.35f)
      ) {
        Text(
          text = "Mais um treino concluído. A consistência está construindo seu resultado. 💪",
          style = MaterialTheme.typography.bodyMedium,
          color = Teal,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      FitTrackButton(
        text = "Voltar para Início",
        onClick = { viewModel.goHomeFromComplete() },
        modifier = Modifier.fillMaxWidth()
      )

      FitTrackButton(
        text = "Registrar check-in semanal",
        onClick = {
          viewModel.goHomeFromComplete()
          viewModel.selectTab("profile")
        },
        variant = FitTrackButtonVariant.SECONDARY,
        modifier = Modifier.fillMaxWidth()
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
