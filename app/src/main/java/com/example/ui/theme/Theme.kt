package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FitTrackColorScheme = darkColorScheme(
  primary = Ember,
  onPrimary = DarkBg,
  primaryContainer = EmberDim,
  onPrimaryContainer = EmberLight,
  secondary = Teal,
  onSecondary = DarkBg,
  secondaryContainer = TealDim,
  onSecondaryContainer = TealLight,
  tertiary = Amber,
  onTertiary = DarkBg,
  tertiaryContainer = AmberDim,
  onTertiaryContainer = Amber,
  error = Danger,
  onError = DarkBg,
  errorContainer = DangerDim,
  onErrorContainer = Danger,
  background = DarkBg,
  onBackground = TextPrimary,
  surface = SurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceHi,
  onSurfaceVariant = TextSecondary,
  outline = BorderDark,
  outlineVariant = SurfaceHi
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = FitTrackColorScheme,
    typography = Typography,
    content = content
  )
}
