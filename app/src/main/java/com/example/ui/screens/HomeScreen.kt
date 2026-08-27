package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WORKOUT_TEMPLATES
import com.example.ui.FitTrackUiState
import com.example.ui.components.FitTrackButton
import com.example.ui.components.FitTrackCard
import com.example.ui.components.ProgressBarWithMarker
import com.example.ui.theme.Ember
import com.example.ui.theme.EmberDim
import com.example.ui.theme.SurfaceHi
import com.example.ui.theme.Teal
import com.example.ui.theme.TealDim
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HomeScreen(
  uiState: FitTrackUiState,
  onStartWorkout: (String) -> Unit,
  onOpenIntro: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val nextTemplate = WORKOUT_TEMPLATES[uiState.nextWorkoutType] ?: WORKOUT_TEMPLATES["A"]!!
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Greeting
    Column {
      Text(
        text = if (uiState.profile.name.isNotBlank()) "Olá, ${uiState.profile.name}! 👋" else "Olá! 👋",
        style = MaterialTheme.typography.displayMedium,
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Vamos manter a consistência hoje?",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
      )
    }

    // Progress Card (Weight vs Goal)
    FitTrackCard {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "PROGRESSO",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "📈",
            fontSize = 16.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = String.format("%.1f", uiState.currentWeight),
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
              )
              Text(
                text = " kg",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
              )
            }
            Text(
              text = "peso atual",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${uiState.profile.goalWeight} kg",
              style = MaterialTheme.typography.displaySmall,
              color = Teal
            )
            Text(
              text = "objetivo",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ProgressBarWithMarker(progressPct = uiState.goalProgressPct)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${uiState.profile.startWeight} kg",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary
          )
          Text(
            text = "${uiState.profile.goalWeight} kg",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Você já percorreu ${String.format("%.0f", uiState.goalProgressPct)}% do caminho 🔥",
          style = MaterialTheme.typography.titleMedium,
          color = Ember,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Next Workout Action Card
    FitTrackCard(
      borderColor = Ember.copy(alpha = 0.6f)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "PRÓXIMO TREINO",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "🔥",
            fontSize = 16.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(EmberDim),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = nextTemplate.id,
              style = MaterialTheme.typography.displaySmall,
              color = Ember,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = nextTemplate.name,
              style = MaterialTheme.typography.titleLarge,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = nextTemplate.subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = "⏱️ ~${nextTemplate.estTime} min",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
          Text(
            text = "🏋️ ${nextTemplate.exercises.size} exercícios",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FitTrackButton(
          text = if (uiState.activeSession != null) "CONTINUAR TREINO" else "COMEÇAR TREINO",
          onClick = { onOpenIntro(nextTemplate.id) },
          icon = Icons.Default.PlayArrow,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // Weekly consistency & Total workouts grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      FitTrackCard(
        modifier = Modifier.weight(1f)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🔥 ", fontSize = 14.sp)
            Text(
              text = "CONSISTÊNCIA",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${uiState.weekWorkoutCount}/3",
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "treinos esta semana",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }

      FitTrackCard(
        modifier = Modifier.weight(1f)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🏆 ", fontSize = 14.sp)
            Text(
              text = "TOTAL",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${uiState.totalCompletedWorkouts}",
            style = MaterialTheme.typography.displaySmall,
            color = Teal,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "treinos concluídos",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }
    }

    // Motivational Quote Card
    FitTrackCard(
      backgroundColor = TealDim,
      borderColor = Teal.copy(alpha = 0.3f)
    ) {
      Row(verticalAlignment = Alignment.Top) {
        Text(text = "💬 ", fontSize = 16.sp)
        Text(
          text = "Seu primeiro objetivo não é chegar aos ${uiState.profile.goalWeight} kg. É se tornar alguém que treina consistentemente 3x por semana.",
          style = MaterialTheme.typography.bodyMedium,
          color = Teal,
          fontWeight = FontWeight.Medium
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
