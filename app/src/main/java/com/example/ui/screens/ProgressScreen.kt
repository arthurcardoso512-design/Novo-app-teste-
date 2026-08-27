package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.BodyEntryEntity
import com.example.data.local.PhotoEntity
import com.example.data.repository.FitTrackRepository
import com.example.ui.FitTrackUiState
import com.example.ui.FitTrackViewModel
import com.example.ui.components.ChartPoint
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FitTrackButton
import com.example.ui.components.FitTrackButtonVariant
import com.example.ui.components.FitTrackCard
import com.example.ui.components.SimpleLineChart
import com.example.ui.theme.Amber
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Ember
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceHi
import com.example.ui.theme.Teal
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MeasureFieldDef(val key: String, val label: String, val icon: String)

val MEASURE_FIELDS = listOf(
  MeasureFieldDef("waist", "Cintura", "📏"),
  MeasureFieldDef("abdomen", "Abdômen", "📏"),
  MeasureFieldDef("chest", "Peito", "📏"),
  MeasureFieldDef("arm", "Braço", "💪"),
  MeasureFieldDef("thigh", "Coxa", "🦵")
)

val PERIOD_FILTERS = listOf("7" to "7d", "30" to "30d", "90" to "3m", "180" to "6m", "all" to "Tudo")

@Composable
fun ProgressScreen(
  uiState: FitTrackUiState,
  viewModel: FitTrackViewModel,
  modifier: Modifier = Modifier
) {
  var showAddWeightDialog by remember { mutableStateOf(false) }
  var showAddMeasureDialog by remember { mutableStateOf(false) }
  var showAddPhotoDialog by remember { mutableStateOf(false) }
  var selectedPeriod by remember { mutableStateOf("30") }

  val scrollState = rememberScrollState()

  val lostWeight = uiState.profile.startWeight - uiState.currentWeight
  val lostFormatted = String.format(Locale.US, "%.1f", lostWeight)

  // Filter weight entries by period
  val filteredEntries = remember(uiState.bodyEntries, selectedPeriod) {
    val sorted = uiState.bodyEntries.sortedBy { it.date }
    if (selectedPeriod == "all") sorted
    else {
      val days = selectedPeriod.toIntOrNull() ?: 30
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_YEAR, -days)
      val cutoffDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
      sorted.filter { it.date >= cutoffDate }
    }
  }

  val chartPoints = remember(filteredEntries) {
    filteredEntries.filter { it.weight != null }.map {
      ChartPoint(
        label = FitTrackRepository.formatShortDate(it.date),
        value = it.weight ?: 0.0
      )
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Meu Progresso 📈",
      style = MaterialTheme.typography.displayMedium,
      color = TextPrimary
    )

    // 4 Key Metrics Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FitTrackCard(modifier = Modifier.weight(1f)) {
        Column {
          Text(
            text = "${String.format("%.1f", uiState.currentWeight)} kg",
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Peso atual",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }

      FitTrackCard(modifier = Modifier.weight(1f)) {
        Column {
          Text(
            text = "${String.format("%.1f", uiState.profile.startWeight)} kg",
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Peso inicial",
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
            text = if (lostWeight > 0) "-$lostFormatted kg" else "$lostFormatted kg",
            style = MaterialTheme.typography.displaySmall,
            color = Teal,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Perdido",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }

      FitTrackCard(modifier = Modifier.weight(1f)) {
        Column {
          Text(
            text = "${String.format("%.0f", uiState.goalProgressPct)}%",
            style = MaterialTheme.typography.displaySmall,
            color = Ember,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Do objetivo",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }
    }

    // Weight Progression Card
    FitTrackCard {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "📉 EVOLUÇÃO DO PESO",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { showAddWeightDialog = true }
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Registrar",
              tint = Ember,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Registrar",
              style = MaterialTheme.typography.labelMedium,
              color = Ember,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Period filter buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          PERIOD_FILTERS.forEach { (key, label) ->
            val isSelected = selectedPeriod == key
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Ember else SurfaceHi)
                .clickable { selectedPeriod = key }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) DarkBg else TextSecondary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        SimpleLineChart(points = chartPoints)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Observe a tendência da média semanal, não pesagens isoladas.",
          style = MaterialTheme.typography.bodySmall,
          color = TextTertiary,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // Body Measurements Card
    FitTrackCard {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "📏 MEDIDAS CORPORAIS",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { showAddMeasureDialog = true }
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Registrar",
              tint = Ember,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Registrar",
              style = MaterialTheme.typography.labelMedium,
              color = Ember,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val entriesWithMeasures = uiState.bodyEntries.filter {
          it.waist != null || it.abdomen != null || it.chest != null || it.arm != null || it.thigh != null
        }

        if (entriesWithMeasures.isEmpty()) {
          EmptyStateView(
            title = "Nenhuma medida ainda",
            subtitle = "Toque em Registrar para cadastrar suas medidas."
          )
        } else {
          MEASURE_FIELDS.forEach { field ->
            val firstLog = entriesWithMeasures.firstOrNull { getFieldValue(it, field.key) != null }
            val lastLog = entriesWithMeasures.lastOrNull { getFieldValue(it, field.key) != null }

            if (lastLog != null) {
              val lastVal = getFieldValue(lastLog, field.key) ?: 0.0
              val firstVal = if (firstLog != null) getFieldValue(firstLog, field.key) ?: lastVal else lastVal
              val diff = lastVal - firstVal

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(SurfaceHi)
                  .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = field.icon, fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${String.format("%.1f", lastVal)} cm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (diff >= 0) "+${String.format("%.1f", diff)} cm" else "${String.format("%.1f", diff)} cm",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (diff <= 0) Teal else Amber,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Evolution Photos Card
    FitTrackCard {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "📸 FOTOS DE EVOLUÇÃO",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { showAddPhotoDialog = true }
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Adicionar",
              tint = Ember,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Adicionar",
              style = MaterialTheme.typography.labelMedium,
              color = Ember,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.photos.isEmpty()) {
          EmptyStateView(
            title = "Nenhuma foto ainda",
            subtitle = "Registre fotos para comparar sua evolução visual."
          )
        } else {
          // Photo Grid (3 columns)
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val chunkedPhotos = uiState.photos.chunked(3)
            chunkedPhotos.forEach { rowPhotos ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                rowPhotos.forEach { photo ->
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .aspectRatio(0.75f)
                      .clip(RoundedCornerShape(14.dp))
                      .background(SurfaceHi)
                  ) {
                    AsyncImage(
                      model = photo.imageUri,
                      contentDescription = photo.category,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )

                    // Overlay bottom bar
                    Box(
                      modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = FitTrackRepository.formatShortDate(photo.date),
                          style = MaterialTheme.typography.labelSmall,
                          color = Color.White,
                          fontSize = 9.sp
                        )
                        Text(
                          text = photo.category,
                          style = MaterialTheme.typography.labelSmall,
                          color = Ember,
                          fontSize = 9.sp
                        )
                      }
                    }

                    // Delete button
                    IconButton(
                      onClick = { viewModel.deletePhoto(photo) },
                      modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }

                // Fill remaining empty columns if row has < 3 items
                if (rowPhotos.size < 3) {
                  for (i in 0 until (3 - rowPhotos.size)) {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }

  // Modals
  if (showAddWeightDialog) {
    AddWeightDialog(
      onDismiss = { showAddWeightDialog = false },
      onSave = { date, weight, note ->
        viewModel.addBodyEntry(BodyEntryEntity(date = date, weight = weight, note = note))
        showAddWeightDialog = false
      }
    )
  }

  if (showAddMeasureDialog) {
    AddMeasureDialog(
      onDismiss = { showAddMeasureDialog = false },
      onSave = { date, waist, abdomen, chest, arm, thigh ->
        val current = uiState.bodyEntries.find { it.date == date }
        val updated = (current ?: BodyEntryEntity(date = date)).copy(
          waist = waist,
          abdomen = abdomen,
          chest = chest,
          arm = arm,
          thigh = thigh
        )
        viewModel.addBodyEntry(updated)
        showAddMeasureDialog = false
      }
    )
  }

  if (showAddPhotoDialog) {
    AddPhotoDialog(
      onDismiss = { showAddPhotoDialog = false },
      onSave = { date, category, uri ->
        viewModel.addPhoto(PhotoEntity(date = date, category = category, imageUri = uri))
        showAddPhotoDialog = false
      }
    )
  }
}

private fun getFieldValue(entry: BodyEntryEntity, key: String): Double? {
  return when (key) {
    "waist" -> entry.waist
    "abdomen" -> entry.abdomen
    "chest" -> entry.chest
    "arm" -> entry.arm
    "thigh" -> entry.thigh
    else -> null
  }
}

@Composable
private fun AddWeightDialog(
  onDismiss: () -> Unit,
  onSave: (date: String, weight: Double, note: String) -> Unit
) {
  var date by remember { mutableStateOf(FitTrackRepository.getTodayIso()) }
  var weightText by remember { mutableStateOf("") }
  var note by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = SurfaceDark,
      border = BorderStroke(1.dp, BorderDark),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Registrar Peso ⚖️",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
          }
        }

        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("DATA (AAAA-MM-DD)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
          value = weightText,
          onValueChange = { weightText = it },
          label = { Text("PESO (KG)") },
          placeholder = { Text("Ex: 121.5") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("OBSERVAÇÃO (OPCIONAL)") },
          placeholder = { Text("Ex: Pesagem em jejum") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        FitTrackButton(
          text = "Salvar Peso",
          onClick = {
            val parsed = weightText.replace(",", ".").toDoubleOrNull()
            if (parsed != null && parsed > 0) {
              onSave(date, parsed, note)
            }
          },
          enabled = weightText.isNotBlank(),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
private fun AddMeasureDialog(
  onDismiss: () -> Unit,
  onSave: (date: String, waist: Double?, abdomen: Double?, chest: Double?, arm: Double?, thigh: Double?) -> Unit
) {
  var date by remember { mutableStateOf(FitTrackRepository.getTodayIso()) }
  var waistText by remember { mutableStateOf("") }
  var abdomenText by remember { mutableStateOf("") }
  var chestText by remember { mutableStateOf("") }
  var armText by remember { mutableStateOf("") }
  var thighText by remember { mutableStateOf("") }

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
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Registrar Medidas 📏",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
          }
        }

        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("DATA") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = waistText,
          onValueChange = { waistText = it },
          label = { Text("📏 CINTURA (CM)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = abdomenText,
          onValueChange = { abdomenText = it },
          label = { Text("📏 ABDÔMEN (CM)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = chestText,
          onValueChange = { chestText = it },
          label = { Text("📏 PEITO (CM)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = armText,
          onValueChange = { armText = it },
          label = { Text("💪 BRAÇO (CM)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = thighText,
          onValueChange = { thighText = it },
          label = { Text("🦵 COXA (CM)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        FitTrackButton(
          text = "Salvar Medidas",
          onClick = {
            onSave(
              date,
              waistText.replace(",", ".").toDoubleOrNull(),
              abdomenText.replace(",", ".").toDoubleOrNull(),
              chestText.replace(",", ".").toDoubleOrNull(),
              armText.replace(",", ".").toDoubleOrNull(),
              thighText.replace(",", ".").toDoubleOrNull()
            )
          },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
private fun AddPhotoDialog(
  onDismiss: () -> Unit,
  onSave: (date: String, category: String, uri: String) -> Unit
) {
  var date by remember { mutableStateOf(FitTrackRepository.getTodayIso()) }
  var category by remember { mutableStateOf("Frente") }
  var selectedUriString by remember { mutableStateOf<String?>(null) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedUriString = uri.toString()
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = SurfaceDark,
      border = BorderStroke(1.dp, BorderDark),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Adicionar Foto 📸",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
          }
        }

        // Image selector box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceHi)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable { photoPickerLauncher.launch("image/*") },
          contentAlignment = Alignment.Center
        ) {
          if (selectedUriString != null) {
            AsyncImage(
              model = selectedUriString,
              contentDescription = "Preview",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(32.dp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Toque para escolher uma foto",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }
        }

        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("DATA") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Column {
          Text(
            text = "ÂNGULO",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Frente", "Lateral", "Costas").forEach { angle ->
              val isSelected = category == angle
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Ember else SurfaceHi)
                  .clickable { category = angle }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = angle,
                  style = MaterialTheme.typography.titleMedium,
                  color = if (isSelected) DarkBg else TextSecondary,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        FitTrackButton(
          text = "Salvar Foto",
          onClick = {
            val uri = selectedUriString
            if (uri != null) {
              onSave(date, category, uri)
            }
          },
          enabled = selectedUriString != null,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
