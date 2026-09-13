package com.mindbloom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.HabitWithProgress
import com.mindbloom.app.data.StaticContent
import com.mindbloom.app.ui.components.AppTextField
import com.mindbloom.app.ui.components.EmptyState
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.ProgressTrack
import com.mindbloom.app.ui.components.ScreenTitle
import com.mindbloom.app.ui.components.StreakPill
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.viewmodel.HabitsViewModel

/** Screen 9. */
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    onOpenHabit: (Long) -> Unit
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val showForm by viewModel.showForm.collectAsStateWithLifecycle()
    val colors = MB.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 18.dp, bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ScreenTitle(
                    text = "My Habits",
                    subtitle = "Keep going, you're doing great!"
                )
                Spacer(Modifier.height(6.dp))
            }

            if (habits.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "\uD83C\uDF31",
                        title = "No habits yet",
                        message = "Add your first habit and MindBloom will start tracking " +
                            "your streak from today.",
                        actionLabel = "Add a habit",
                        onAction = viewModel::openCreateForm
                    )
                }
            }

            items(items = habits, key = { it.habit.id }) { item ->
                HabitRow(
                    item = item,
                    onOpen = { onOpenHabit(item.habit.id) },
                    onQuickAdd = { viewModel.increment(item.habit.id) }
                )
            }
        }

        // Floating add button.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .size(58.dp)
                .shadow(8.dp, CircleShape, spotColor = Purple.copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(Purple)
                .clickable(onClick = viewModel::openCreateForm),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add habit",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showForm) {
        HabitFormSheet(viewModel = viewModel)
    }
}

/** One row of the habit list. */
@Composable
fun HabitRow(
    item: HabitWithProgress,
    onOpen: () -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MB.colors
    val accent = Color(item.habit.accentColor)

    SurfaceCard(modifier = modifier, onClick = onOpen, padding = 16) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(item.habit.iconBackground))
                    .clickable(onClick = onQuickAdd),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.habit.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = item.habit.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                if (item.streak > 0) {
                    StreakPill(days = item.streak)
                    Spacer(Modifier.height(8.dp))
                }
                Text(
                    text = "${item.progress}/${item.target}",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        ProgressTrack(fraction = item.fraction, color = accent, height = 7)
    }
}

/** Bottom sheet used for both creating and editing a habit. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormSheet(viewModel: HabitsViewModel) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val colors = MB.colors
    var showFrequency by remember { mutableStateOf(false) }
    var showReminder by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = viewModel::dismissForm,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = if (form.isEditing) "Edit Habit" else "New Habit",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(18.dp))

            AppTextField(
                label = "Habit name",
                value = form.name,
                onValueChange = { value -> viewModel.updateForm { it.copy(name = value, nameError = null) } },
                placeholder = "Drink Water",
                errorText = form.nameError
            )
            Spacer(Modifier.height(14.dp))
            AppTextField(
                label = "Description",
                value = form.subtitle,
                onValueChange = { value -> viewModel.updateForm { it.copy(subtitle = value) } },
                placeholder = "8 glasses a day"
            )
            Spacer(Modifier.height(14.dp))
            AppTextField(
                label = "Daily target",
                value = form.target,
                onValueChange = { value ->
                    viewModel.updateForm { it.copy(target = value.filter(Char::isDigit), targetError = null) }
                },
                placeholder = "8",
                keyboardType = KeyboardType.Number,
                errorText = form.targetError
            )

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Icon",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StaticContent.habitEmojis.forEach { emoji ->
                    val selected = emoji == form.emoji
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Purple.copy(alpha = 0.12f) else colors.field)
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) Purple else colors.border,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.updateForm { it.copy(emoji = emoji) } },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Colour",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StaticContent.habitPalettes.forEach { (accent, background) ->
                    val selected = accent == form.accentColor
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(accent))
                            .border(
                                width = if (selected) 3.dp else 0.dp,
                                color = if (selected) colors.textPrimary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.updateForm {
                                    it.copy(accentColor = accent, iconBackground = background)
                                }
                            }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            SettingRowSimple(
                label = "Frequency",
                value = form.frequency,
                onClick = { showFrequency = true }
            )
            SettingRowSimple(
                label = "Reminder",
                value = form.reminderTime,
                onClick = { showReminder = true }
            )

            Spacer(Modifier.height(22.dp))
            PrimaryButton(
                text = if (form.isEditing) "Save Changes" else "Create Habit",
                onClick = { viewModel.saveForm() }
            )
        }
    }

    if (showFrequency) {
        ChoiceDialog(
            title = "Frequency",
            options = listOf("Daily", "Weekdays", "Weekends", "Weekly"),
            selected = form.frequency,
            onSelect = { value ->
                viewModel.updateForm { it.copy(frequency = value) }
                showFrequency = false
            },
            onDismiss = { showFrequency = false }
        )
    }
    if (showReminder) {
        ChoiceDialog(
            title = "Reminder time",
            options = listOf(
                "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM",
                "12:00 PM", "05:30 PM", "08:30 PM", "10:30 PM"
            ),
            selected = form.reminderTime,
            onSelect = { value ->
                viewModel.updateForm { it.copy(reminderTime = value) }
                showReminder = false
            },
            onDismiss = { showReminder = false }
        )
    }
}

@Composable
private fun SettingRowSimple(label: String, value: String, onClick: () -> Unit) {
    val colors = MB.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
    }
}

/** Small single-choice dialog reused by the habit form and settings. */
@Composable
fun ChoiceDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MB.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Purple) }
        },
        title = { Text(title, color = colors.textPrimary) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (option == selected) Purple else colors.textPrimary,
                            fontWeight = if (option == selected) FontWeight.SemiBold
                            else FontWeight.Normal
                        )
                    }
                }
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
