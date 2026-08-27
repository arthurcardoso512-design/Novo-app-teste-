package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.RIRInfoDialog
import com.example.ui.components.RestTimerOverlay
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Ember
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHi
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

data class NavTabItem(
  val id: String,
  val label: String,
  val icon: ImageVector
)

val NAV_ITEMS = listOf(
  NavTabItem("home", "Início", Icons.Default.Home),
  NavTabItem("workout", "Treino", Icons.Default.FitnessCenter),
  NavTabItem("progress", "Progresso", Icons.Default.TrendingUp),
  NavTabItem("history", "Histórico", Icons.Default.History),
  NavTabItem("profile", "Perfil", Icons.Default.Person)
)

@Composable
fun FitTrackApp(
  viewModel: FitTrackViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    containerColor = DarkBg,
    bottomBar = {
      FitTrackBottomNav(
        selectedTab = uiState.selectedTab,
        hasActiveSession = uiState.activeSession != null,
        onSelectTab = { tab -> viewModel.selectTab(tab) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(DarkBg)
        .padding(innerPadding)
        .statusBarsPadding()
    ) {
      // Screen Content
      when (uiState.selectedTab) {
        "home" -> {
          HomeScreen(
            uiState = uiState,
            onStartWorkout = { type -> viewModel.startWorkout(type) },
            onOpenIntro = { type -> viewModel.openWorkoutIntro(type) }
          )
        }
        "workout" -> {
          WorkoutScreen(
            uiState = uiState,
            viewModel = viewModel
          )
        }
        "progress" -> {
          ProgressScreen(
            uiState = uiState,
            viewModel = viewModel
          )
        }
        "history" -> {
          HistoryScreen(
            uiState = uiState
          )
        }
        "profile" -> {
          ProfileScreen(
            uiState = uiState,
            viewModel = viewModel
          )
        }
      }

      // Toast Notification Banner (Animated)
      AnimatedVisibility(
        visible = uiState.toastMessage != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = SurfaceHi,
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
          shadowElevation = 8.dp
        ) {
          Text(
            text = uiState.toastMessage ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
          )
        }
      }

      // Rest Countdown Timer Overlay
      if (uiState.restTimerSeconds != null) {
        RestTimerOverlay(
          seconds = uiState.restTimerSeconds ?: 0,
          onAddSeconds = { extra -> viewModel.addRestTimerSeconds(extra) },
          onSkip = { viewModel.cancelRestTimer() }
        )
      }

      // RIR Info Dialog
      if (uiState.showRIRDialog) {
        RIRInfoDialog(
          onDismiss = { viewModel.setShowRIRDialog(false) }
        )
      }
    }
  }
}

@Composable
private fun FitTrackBottomNav(
  selectedTab: String,
  hasActiveSession: Boolean,
  onSelectTab: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(SurfaceDark)
      .navigationBarsPadding()
  ) {
    HorizontalDivider(color = BorderDark, thickness = 1.dp)

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NAV_ITEMS.forEach { item ->
        val isSelected = selectedTab == item.id
        val activeColor = Ember
        val inactiveColor = TextSecondary

        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectTab(item.id) }
            .padding(vertical = 6.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(contentAlignment = Alignment.TopEnd) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.label,
              tint = if (isSelected) activeColor else inactiveColor,
              modifier = Modifier.size(22.dp)
            )

            // Active workout indicator dot
            if (item.id == "workout" && hasActiveSession) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Ember)
              )
            }
          }

          Spacer(modifier = Modifier.height(3.dp))

          Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) activeColor else inactiveColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
