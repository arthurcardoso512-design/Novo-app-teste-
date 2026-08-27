package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun FitTrackCard(
  modifier: Modifier = Modifier,
  borderColor: Color = BorderDark,
  backgroundColor: Color = SurfaceDark,
  shapeRadius: Dp = 24.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val cardShape = RoundedCornerShape(shapeRadius)
  Card(
    modifier = modifier
      .fillMaxWidth()
      .then(
        if (onClick != null) {
          Modifier.clip(cardShape).clickable(onClick = onClick)
        } else {
          Modifier
        }
      ),
    shape = cardShape,
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = BorderStroke(1.dp, borderColor)
  ) {
    Box(modifier = Modifier.padding(18.dp)) {
      content()
    }
  }
}

enum class FitTrackButtonVariant {
  PRIMARY, SECONDARY, TEAL, GHOST, DANGER
}

@Composable
fun FitTrackButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  variant: FitTrackButtonVariant = FitTrackButtonVariant.PRIMARY,
  enabled: Boolean = true,
  icon: ImageVector? = null,
  testTag: String = "fit_track_button"
) {
  val containerColor = when (variant) {
    FitTrackButtonVariant.PRIMARY -> Ember
    FitTrackButtonVariant.SECONDARY -> SurfaceHi
    FitTrackButtonVariant.TEAL -> Teal
    FitTrackButtonVariant.DANGER -> Color(0xFFFF5468)
    FitTrackButtonVariant.GHOST -> Color.Transparent
  }

  val contentColor = when (variant) {
    FitTrackButtonVariant.PRIMARY -> DarkBg
    FitTrackButtonVariant.SECONDARY -> TextPrimary
    FitTrackButtonVariant.TEAL -> DarkBg
    FitTrackButtonVariant.DANGER -> Color.White
    FitTrackButtonVariant.GHOST -> TextSecondary
  }

  val border = when (variant) {
    FitTrackButtonVariant.SECONDARY -> BorderStroke(1.dp, BorderDark)
    else -> null
  }

  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .height(52.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = containerColor,
      contentColor = contentColor,
      disabledContainerColor = containerColor.copy(alpha = 0.4f),
      disabledContentColor = contentColor.copy(alpha = 0.4f)
    ),
    border = border
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun ProgressBarWithMarker(
  progressPct: Float,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = (progressPct / 100f).coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 600),
    label = "progress_anim"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(28.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    // Track background
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(SurfaceHi)
    )

    // Gradient Progress fill
    Box(
      modifier = Modifier
        .fillMaxWidth(fraction = animatedProgress)
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(Ember, Teal)
          )
        )
    )

    // Floating indicator dot
    Box(
      modifier = Modifier
        .fillMaxWidth(fraction = animatedProgress.coerceAtLeast(0.04f))
        .padding(end = 0.dp),
      contentAlignment = Alignment.CenterEnd
    ) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(Teal)
          .padding(3.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(DarkBg)
        )
      }
    }
  }
}

data class ChartPoint(val label: String, val value: Double)

@Composable
fun SimpleLineChart(
  points: List<ChartPoint>,
  modifier: Modifier = Modifier,
  lineColorStart: Color = Ember,
  lineColorEnd: Color = Teal
) {
  if (points.size < 2) {
    EmptyStateView(
      title = "Sem dados suficientes",
      subtitle = "Registre pelo menos 2 pesagens para ver o gráfico."
    )
    return
  }

  val values = points.map { it.value }
  val minVal = values.minOrNull() ?: 0.0
  val maxVal = values.maxOrNull() ?: 100.0
  val range = if (maxVal - minVal == 0.0) 1.0 else maxVal - minVal

  Column(modifier = modifier.fillMaxWidth()) {
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
    ) {
      val padX = 24.dp.toPx()
      val padY = 24.dp.toPx()
      val drawW = size.width - padX * 2
      val drawH = size.height - padY * 2

      // Draw grid horizontal lines
      val gridLines = 4
      for (i in 0..gridLines) {
        val y = padY + (drawH / gridLines) * i
        drawLine(
          color = BorderDark,
          start = Offset(padX, y),
          end = Offset(size.width - padX, y),
          strokeWidth = 1f
        )
      }

      val path = Path()
      val stepX = drawW / (points.size - 1)
      val calculatedPoints = mutableListOf<Offset>()

      points.forEachIndexed { i, p ->
        val x = padX + i * stepX
        val y = padY + drawH * (1.0 - (p.value - minVal) / range).toFloat()
        val offset = Offset(x, y)
        calculatedPoints.add(offset)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
      }

      // Draw path line
      drawPath(
        path = path,
        brush = Brush.horizontalGradient(
          colors = listOf(lineColorStart, lineColorEnd),
          startX = padX,
          endX = size.width - padX
        ),
        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
      )

      // Draw point markers
      calculatedPoints.forEach { pt ->
        drawCircle(
          color = lineColorStart,
          radius = 4.dp.toPx(),
          center = pt
        )
        drawCircle(
          color = DarkBg,
          radius = 2.dp.toPx(),
          center = pt
        )
      }
    }

    // Chart labels
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = points.first().label,
        style = MaterialTheme.typography.bodySmall,
        color = TextTertiary
      )
      Text(
        text = "Mín: ${String.format("%.1f", minVal)} kg  ·  Máx: ${String.format("%.1f", maxVal)} kg",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary
      )
      Text(
        text = points.last().label,
        style = MaterialTheme.typography.bodySmall,
        color = TextTertiary
      )
    }
  }
}

@Composable
fun EmptyStateView(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "📊",
      fontSize = 32.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = TextPrimary,
      fontWeight = FontWeight.SemiBold
    )
    if (subtitle != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
fun RIRInfoDialog(onDismiss: () -> Unit) {
  val rows = listOf(
    "3" to "Você conseguiria fazer mais 3 repetições.",
    "2" to "Você conseguiria fazer mais 2 repetições.",
    "1" to "Você conseguiria fazer mais 1 repetição.",
    "0" to "Falha — não conseguiria fazer mais nenhuma."
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = SurfaceDark,
      border = BorderStroke(1.dp, BorderDark),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(22.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "O que é RIR? 🎯",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "RIR significa Repetições em Reserva — quantas repetições a mais você conseguiria fazer antes de atingir a falha concêntrica.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        rows.forEach { (n, desc) ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(SurfaceHi)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(EmberDim),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = n,
                style = MaterialTheme.typography.titleMedium,
                color = Ember,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = desc,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TealDim)
            .padding(12.dp)
        ) {
          Text(
            text = "Não precisa treinar até a falha. Priorize a técnica e consistência. 🎯",
            style = MaterialTheme.typography.bodySmall,
            color = Teal,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        FitTrackButton(
          text = "Entendi",
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
fun RestTimerOverlay(
  seconds: Int,
  onAddSeconds: (Int) -> Unit,
  onSkip: () -> Unit
) {
  Dialog(onDismissRequest = onSkip) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = DarkBg.copy(alpha = 0.96f),
      border = BorderStroke(1.5.dp, Ember),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Hora de descansar 😮‍💨",
          style = MaterialTheme.typography.headlineMedium,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        val mins = seconds / 60
        val secs = seconds % 60
        val timeString = String.format("%02d:%02d", mins, secs)

        Text(
          text = timeString,
          fontSize = 64.sp,
          fontWeight = FontWeight.Black,
          color = if (seconds > 0) Ember else Teal,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FitTrackButton(
            text = "+15s",
            onClick = { onAddSeconds(15) },
            variant = FitTrackButtonVariant.SECONDARY,
            modifier = Modifier.weight(1f)
          )
          FitTrackButton(
            text = "+30s",
            onClick = { onAddSeconds(30) },
            variant = FitTrackButtonVariant.SECONDARY,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FitTrackButton(
          text = if (seconds > 0) "Pular descanso" else "Próxima série 💪",
          onClick = onSkip,
          variant = if (seconds > 0) FitTrackButtonVariant.GHOST else FitTrackButtonVariant.TEAL,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
