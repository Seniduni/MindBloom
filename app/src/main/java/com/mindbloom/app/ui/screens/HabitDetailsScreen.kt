package com.mindbloom.app.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.step
import com.mindbloom.app.ui.components.BackHeader
import com.mindbloom.app.ui.components.CardDivider
import com.mindbloom.app.ui.components.DangerOutlineButton
import com.mindbloom.app.ui.components.LoadingScreen
import com.mindbloom.app.ui.components.OutlineButton
import com.mindbloom.app.ui.components.ProgressTrack
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.components.WeekTickRow
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.HabitDetailsViewModel
import com.mindbloom.app.ui.viewmodel.HabitsViewModel

/** Screen 10. */
@Composable
fun HabitDetailsScreen(
    detailsViewModel: HabitDetailsViewModel,
    habitsViewModel: HabitsViewModel,
    onBack: () -> Unit
) {
    val state by detailsViewModel.uiState.collectAsStateWithLifecycle()
    val showForm by habitsViewModel.showForm.collectAsStateWithLifecycle()
    val colors = MB.colors
    var confirmDelete by remember { mutableStateOf(false) }

    if (state.loading) {
        LoadingScreen()
        return
    }

    val habit = state.habit
    if (habit == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            BackHeader(title = "Habit Details", onBack = onBack)
            Spacer(Modifier.height(40.dp))
            Text(
                text = "This habit has been deleted.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }

    val accent = Color(habit.accentColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        BackHeader(title = "Habit Details", onBack = onBack)

        Spacer(Modifier.height(24.dp))

        // ---- Icon, name, description
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(92.dp)
                .clip(CircleShape)
                .background(Color(habit.iconBackground)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = habit.emoji, fontSize = 40.sp)
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = habit.name,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = habit.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // ---- Facts card
        SurfaceCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${state.progress}/${habit.target}",
                    style = MaterialTheme.typography.titleMedium,
                    color = accent
                )
            }
            Spacer(Modifier.height(12.dp))
            ProgressTrack(
                fraction = if (habit.target == 0) 0f else state.progress / habit.target.toFloat(),
                color = accent,
                height = 8
            )
            Spacer(Modifier.height(16.dp))

            // Quick adjust controls keep the screen useful, not just a readout.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AdjustButton(
                    icon = Icons.Default.Remove,
                    description = "Decrease progress",
                    onClick = {
                        habitsViewModel.setProgress(
                            habit.id,
                            (state.progress - habit.step).coerceAtLeast(0)
                        )
                    }
                )
                Text(
                    text = "Log progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                AdjustButton(
                    icon = Icons.Default.Add,
                    description = "Increase progress",
                    onClick = { habitsViewModel.increment(habit.id) }
                )
            }

            Spacer(Modifier.height(16.dp))
            CardDivider()
            DetailRow(label = "Streak", value = "${state.streak} Days", valueColor = Orange)
            CardDivider()
            DetailRow(label = "Frequency", value = habit.frequency)
            CardDivider()
            DetailRow(label = "Reminder", value = habit.reminderTime)
        }

        Spacer(Modifier.height(16.dp))

        // ---- This week
        SurfaceCard {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(16.dp))
            WeekTickRow(completed = state.weekTicks)
        }

        Spacer(Modifier.height(24.dp))
        OutlineButton(text = "Edit Habit", onClick = { habitsViewModel.openEditForm(habit) })
        Spacer(Modifier.height(14.dp))
        DangerOutlineButton(text = "Delete Habit", onClick = { confirmDelete = true })
        Spacer(Modifier.height(28.dp))
    }

    if (showForm) {
        HabitFormSheet(habitsViewModel)
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    detailsViewModel.delete(onBack)
                }) {
                    Text("Delete", color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Keep", color = Purple)
                }
            },
            title = { Text("Delete ${habit.name}?", color = colors.textPrimary) },
            text = {
                Text(
                    "This removes the habit and its history. Your streaks for other " +
                        "habits are not affected.",
                    color = colors.textSecondary
                )
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MB.colors.textPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MB.colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun AdjustButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MB.colors.field)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MB.colors.textPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}
