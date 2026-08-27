package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.CheckinEntity
import com.example.data.local.UserProfileEntity
import com.example.data.models.INTERMEDIATE_GOALS
import com.example.data.repository.FitTrackRepository
import com.example.ui.FitTrackUiState
import com.example.ui.FitTrackViewModel
import com.example.ui.components.FitTrackButton
import com.example.ui.components.FitTrackButtonVariant
import com.example.ui.components.FitTrackCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.Danger
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Ember
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHi
import com.example.ui.theme.Teal
import com.example.ui.theme.TealDim
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProfileScreen(
  uiState: FitTrackUiState,
  viewModel: FitTrackViewModel,
  modifier: Modifier = Modifier
) {
  var isEditingProfile by remember { mutableStateOf(false) }
  var showCheckinDialog by remember { mutableStateOf(false) }
  val scrollState = rememberScrollState()

  // Profile Edit form states
  var editName by remember(uiState.profile) { mutableStateOf(uiState.profile.name) }
  var editStartWeight by remember(uiState.profile) { mutableStateOf(uiState.profile.startWeight.toString()) }
  var editGoalWeight by remember(uiState.profile) { mutableStateOf(uiState.profile.goalWeight.toString()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Perfil 👤",
      style = MaterialTheme.typography.displayMedium,
      color = TextPrimary
    )

    // Goals & Profile Info Card
    FitTrackCard {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "OBJETIVOS",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )

          IconButton(
            onClick = { isEditingProfile = !isEditingProfile },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Editar",
              tint = Ember,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (!isEditingProfile) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileRow(label = "Nome", value = uiState.profile.name.ifBlank { "—" })
            ProfileRow(label = "Peso inicial", value = "${String.format("%.1f", uiState.profile.startWeight)} kg")
            ProfileRow(label = "Peso atual", value = "${String.format("%.1f", uiState.currentWeight)} kg")
            ProfileRow(label = "Meta", value = "${String.format("%.1f", uiState.profile.goalWeight)} kg")
            ProfileRow(label = "Início", value = FitTrackRepository.formatDisplayDate(uiState.profile.startDate))
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = editName,
              onValueChange = { editName = it },
              label = { Text("NOME") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editStartWeight,
              onValueChange = { editStartWeight = it },
              label = { Text("PESO INICIAL (KG)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editGoalWeight,
              onValueChange = { editGoalWeight = it },
              label = { Text("META (KG)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            FitTrackButton(
              text = "Salvar Perfil",
              onClick = {
                val startW = editStartWeight.replace(",", ".").toDoubleOrNull() ?: uiState.profile.startWeight
                val goalW = editGoalWeight.replace(",", ".").toDoubleOrNull() ?: uiState.profile.goalWeight
                viewModel.updateProfile(
                  uiState.profile.copy(
                    name = editName,
                    startWeight = startW,
                    goalWeight = goalW
                  )
                )
                isEditingProfile = false
              },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // Intermediate Milestones Card
    FitTrackCard {
      Column {
        Text(
          text = "METAS INTERMEDIÁRIAS 🎯",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          INTERMEDIATE_GOALS.forEach { goal ->
            val reached = uiState.currentWeight <= goal
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(if (reached) Teal else SurfaceHi),
                contentAlignment = Alignment.Center
              ) {
                if (reached) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = DarkBg,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Text(
                text = "${goal.toInt()} kg ${if (goal == 100.0) "🎯" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (reached) Teal else TextSecondary,
                fontWeight = if (reached) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // Weekly Check-in Card
    FitTrackCard {
      Column {
        Text(
          text = "CHECK-IN SEMANAL",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Esta semana: ${uiState.weekWorkoutCount}/3 treinos concluídos.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        FitTrackButton(
          text = "Fazer check-in desta semana",
          onClick = { showCheckinDialog = true },
          variant = FitTrackButtonVariant.SECONDARY,
          modifier = Modifier.fillMaxWidth()
        )

        if (uiState.checkins.isNotEmpty()) {
          Spacer(modifier = Modifier.height(12.dp))
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            uiState.checkins.take(3).forEach { c ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(SurfaceHi)
                  .padding(10.dp)
              ) {
                Text(
                  text = "${FitTrackRepository.formatDisplayDate(c.date)} · Energia ${c.energy}/10 · Sono ${c.sleep}/10",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
              }
            }
          }
        }
      }
    }

    // Habits Guidelines
    FitTrackCard {
      Column {
        Text(
          text = "🥗 HÁBITOS FUNDAMENTAIS",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(text = "🥩 Proteína nas principais refeições.", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Text(text = "🥗 Verduras e legumes com frequência.", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Text(text = "🍚 Carboidratos inteligentes fazem parte da dieta.", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
          Text(text = "💧 Hidratação constante ao longo do dia.", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Seu objetivo é consistência diária, não perfeição inatingível.",
          style = MaterialTheme.typography.bodySmall,
          color = Ember,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Medical Safety Alert Card
    FitTrackCard(
      backgroundColor = Danger.copy(alpha = 0.1f),
      borderColor = Danger.copy(alpha = 0.4f)
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Danger,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "SINAIS DE ALERTA",
            style = MaterialTheme.typography.labelSmall,
            color = Danger,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Dor no peito, falta de ar desproporcional, tontura importante, desmaio, palpitações incomuns ou dor aguda durante um movimento exigem interrupção imediata do exercício e avaliação médica adequada.",
          style = MaterialTheme.typography.bodySmall,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Este aplicativo não substitui diagnóstico ou acompanhamento médico.",
          style = MaterialTheme.typography.bodySmall,
          color = TextTertiary
        )
      }
    }

    // Data Backup Info
    FitTrackCard {
      Column {
        Text(
          text = "💾 DADOS E BACKUP",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Seus dados ficam 100% salvos localmente neste aparelho.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        FitTrackButton(
          text = "Dados salvos e sincronizados com sucesso",
          onClick = { viewModel.showToast("Histórico sincronizado com o banco de dados Room ✅") },
          variant = FitTrackButtonVariant.SECONDARY,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }

  if (showCheckinDialog) {
    WeeklyCheckinDialog(
      weekCount = uiState.weekWorkoutCount,
      onDismiss = { showCheckinDialog = false },
      onSave = { checkin ->
        viewModel.addCheckin(checkin)
        showCheckinDialog = false
      }
    )
  }
}

@Composable
private fun ProfileRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(SurfaceHi)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = TextPrimary,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
private fun WeeklyCheckinDialog(
  weekCount: Int,
  onDismiss: () -> Unit,
  onSave: (CheckinEntity) -> Unit
) {
  var weightText by remember { mutableStateOf("") }
  var stepsText by remember { mutableStateOf("") }
  var energyVal by remember { mutableIntStateOf(5) }
  var sleepVal by remember { mutableIntStateOf(5) }
  var hungerVal by remember { mutableIntStateOf(5) }
  var painOption by remember { mutableStateOf("Não") }
  var notesText by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = SurfaceDark,
      border = BorderStroke(1.dp, BorderDark),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Check-in Semanal 📝",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it },
            label = { Text("PESO MÉDIO") },
            placeholder = { Text("121.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = stepsText,
            onValueChange = { stepsText = it },
            label = { Text("PASSOS MÉDIOS") },
            placeholder = { Text("8000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          )
        }

        // Energy Slider
        Column {
          Text(
            text = "⚡ ENERGIA: $energyVal/10",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Slider(
            value = energyVal.toFloat(),
            onValueChange = { energyVal = it.toInt() },
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = Ember,
              activeTrackColor = Ember,
              inactiveTrackColor = SurfaceHi
            )
          )
        }

        // Sleep Slider
        Column {
          Text(
            text = "🌙 QUALIDADE DO SONO: $sleepVal/10",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Slider(
            value = sleepVal.toFloat(),
            onValueChange = { sleepVal = it.toInt() },
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = Teal,
              activeTrackColor = Teal,
              inactiveTrackColor = SurfaceHi
            )
          )
        }

        // Hunger Slider
        Column {
          Text(
            text = "🔥 CONTROLE DA FOME: $hungerVal/10",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Slider(
            value = hungerVal.toFloat(),
            onValueChange = { hungerVal = it.toInt() },
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = Ember,
              activeTrackColor = Ember,
              inactiveTrackColor = SurfaceHi
            )
          )
        }

        // Pain toggle
        Column {
          Text(
            text = "DORES ARTICULARES OU MUSCULARES?",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Não", "Sim").forEach { opt ->
              val isSelected = painOption == opt
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Ember else SurfaceHi)
                  .clickable { painOption = opt }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = opt,
                  style = MaterialTheme.typography.titleMedium,
                  color = if (isSelected) DarkBg else TextSecondary,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = notesText,
          onValueChange = { notesText = it },
          label = { Text("OBSERVAÇÕES DA SEMANA") },
          placeholder = { Text("Como se sentiu nos treinos...") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        FitTrackButton(
          text = "Salvar Check-in",
          onClick = {
            onSave(
              CheckinEntity(
                date = FitTrackRepository.getTodayIso(),
                weight = weightText,
                steps = stepsText,
                workouts = weekCount,
                energy = energyVal,
                sleep = sleepVal,
                hunger = hungerVal,
                pain = painOption,
                notes = notesText
              )
            )
          },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
