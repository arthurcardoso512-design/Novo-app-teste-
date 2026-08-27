package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WorkoutSessionEntity
import com.example.data.models.WORKOUT_TEMPLATES
import com.example.data.repository.FitTrackRepository
import com.example.ui.FitTrackUiState
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FitTrackCard
import com.example.ui.theme.Amber
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHi
import com.example.ui.theme.Teal
import com.example.ui.theme.TealDim
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  uiState: FitTrackUiState,
  modifier: Modifier = Modifier
) {
  val currentCal = remember { Calendar.getInstance() }
  var viewYear by remember { mutableIntStateOf(currentCal.get(Calendar.YEAR)) }
  var viewMonth by remember { mutableIntStateOf(currentCal.get(Calendar.MONTH)) } // 0-based
  var expandedSessionId by remember { mutableStateOf<String?>(null) }

  val scrollState = rememberScrollState()

  // Map workouts by ISO date
  val workoutsByDate = remember(uiState.completedSessions) {
    uiState.completedSessions.associateBy { it.date }
  }

  // Generate calendar matrix for month
  val daysInMonth = remember(viewYear, viewMonth) {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, viewYear)
    cal.set(Calendar.MONTH, viewMonth)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val list = mutableListOf<Int?>()
    for (i in 0 until firstDayOfWeek) list.add(null)
    for (d in 1..maxDays) list.add(d)
    while (list.size % 7 != 0) list.add(null)
    list
  }

  val monthLabel = remember(viewYear, viewMonth) {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, viewYear)
    cal.set(Calendar.MONTH, viewMonth)
    SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(cal.time).replaceFirstChar { it.uppercase() }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Histórico 📊",
      style = MaterialTheme.typography.displayMedium,
      color = TextPrimary
    )

    // Interactive Monthly Calendar Card
    FitTrackCard {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              if (viewMonth == 0) {
                viewMonth = 11
                viewYear--
              } else {
                viewMonth--
              }
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
              contentDescription = "Mês anterior",
              tint = TextSecondary
            )
          }

          Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )

          IconButton(
            onClick = {
              if (viewMonth == 11) {
                viewMonth = 0
                viewYear++
              } else {
                viewMonth++
              }
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
              contentDescription = "Próximo mês",
              tint = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday header (S T Q Q S S D)
        Row(modifier = Modifier.fillMaxWidth()) {
          listOf("S", "T", "Q", "Q", "S", "S", "D").forEach { dayLabel ->
            Text(
              text = dayLabel,
              style = MaterialTheme.typography.labelSmall,
              color = TextTertiary,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.weight(1f)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Day Cells
        val rows = daysInMonth.chunked(7)
        rows.forEach { weekRow ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            weekRow.forEach { dayNumber ->
              if (dayNumber != null) {
                val dateIso = String.format(Locale.US, "%04d-%02d-%02d", viewYear, viewMonth + 1, dayNumber)
                val session = workoutsByDate[dateIso]
                val hasWorkout = session != null

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (hasWorkout) TealDim else Color.Transparent)
                    .border(
                      1.dp,
                      if (hasWorkout) Teal.copy(alpha = 0.5f) else Color.Transparent,
                      RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = hasWorkout) {
                      expandedSessionId = if (expandedSessionId == session?.id) null else session?.id
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$dayNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasWorkout) Teal else TextSecondary,
                    fontWeight = if (hasWorkout) FontWeight.Bold else FontWeight.Normal
                  )
                }
              } else {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
        }
      }
    }

    // Sessions List
    Text(
      text = "Treinos Realizados",
      style = MaterialTheme.typography.titleMedium,
      color = TextPrimary,
      fontWeight = FontWeight.Bold
    )

    if (uiState.completedSessions.isEmpty()) {
      EmptyStateView(
        title = "Nenhum treino concluído ainda",
        subtitle = "Seu histórico completo de treinos aparecerá aqui."
      )
    } else {
      uiState.completedSessions.forEach { session ->
        val template = WORKOUT_TEMPLATES[session.workoutType]
        val isExpanded = expandedSessionId == session.id
        val totalSetsDone = session.exercises.sumOf { it.sets.count { s -> s.done } }

        FitTrackCard(
          onClick = { expandedSessionId = if (isExpanded) null else session.id }
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = FitTrackRepository.formatShortDate(session.date),
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondary,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Treino ${session.workoutType} ✓",
                  style = MaterialTheme.typography.titleLarge,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${template?.exercises?.size ?: 0} exercícios · $totalSetsDone séries",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextTertiary
                )
              }

              Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextSecondary
              )
            }

            AnimatedVisibility(visible = isExpanded) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 12.dp)
              ) {
                HorizontalDivider(color = BorderDark, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                session.exercises.forEachIndexed { i, ex ->
                  val exName = template?.exercises?.getOrNull(i)?.name ?: ex.exerciseId
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = exName,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimary,
                      modifier = Modifier.weight(1f)
                    )

                    Text(
                      text = if (ex.skipped) "Pulado" else ex.sets.filter { it.done }.joinToString(" / ") { "${it.load}×${it.reps}" },
                      style = MaterialTheme.typography.bodySmall,
                      color = if (ex.skipped) Amber else TextSecondary,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }

                if (session.cardioMin.isNotBlank()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Cardio: ${session.cardioMin} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = Teal,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
