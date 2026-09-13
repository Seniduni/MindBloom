package com.mindbloom.app.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.Mood
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.ScreenTitle
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.Green
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.MoodViewModel
import java.time.LocalDate
import java.time.YearMonth

/** Screen 8. */
@Composable
fun MoodScreen(
    viewModel: MoodViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MB.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        ScreenTitle(text = "How are you feeling?")

        Spacer(Modifier.height(20.dp))

        // ---- Mood selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Mood.entries.forEach { mood ->
                val selected = state.selectedMood == mood
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) Purple else colors.field)
                        .border(
                            width = 1.dp,
                            color = if (selected) Purple else colors.border,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.selectMood(mood) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = mood.emoji, fontSize = 24.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = mood.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = if (selected) Color.White else colors.textSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // ---- Optional note
        Text(
            text = "Write your thoughts (optional)",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 116.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.field)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            BasicTextField(
                value = state.note,
                onValueChange = viewModel::updateNote,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                ),
                cursorBrush = SolidColor(Purple),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
            )
            if (state.note.isEmpty()) {
                Text(
                    text = "What made you feel this way today?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 15.sp,
                    color = colors.textTertiary
                )
            }
            Text(
                text = "${state.noteLength}/${MoodViewModel.NOTE_LIMIT}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        if (state.error != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = state.error.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = Red
            )
        }

        Spacer(Modifier.height(20.dp))

        // ---- Mood calendar
        SurfaceCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { viewModel.previousMonth() }
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = com.mindbloom.app.util.DateUtils.monthYear(state.month),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { viewModel.nextMonth() }
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Mood Calendar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Purple
                )
            }
            Spacer(Modifier.height(16.dp))
            MoodCalendar(
                month = state.month,
                moods = state.monthMoods
            )
        }

        Spacer(Modifier.height(22.dp))
        PrimaryButton(
            text = "Save Mood",
            containerColor = Green,
            onClick = { viewModel.save(onSaved) }
        )
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Month grid, Monday first. Days with a logged mood show its emoji; today is
 * outlined in purple and future days are dimmed.
 */
@Composable
private fun MoodCalendar(
    month: YearMonth,
    moods: Map<Int, Mood>,
    modifier: Modifier = Modifier
) {
    val colors = MB.colors
    val firstDay = month.atDay(1)
    val leadingBlanks = (firstDay.dayOfWeek.value + 6) % 7
    val today = LocalDate.now()
    val cells = leadingBlanks + month.lengthOfMonth()
    val rows = (cells + 6) / 7

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(10.dp))

        repeat(rows) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                repeat(7) { column ->
                    val index = row * 7 + column
                    val dayNumber = index - leadingBlanks + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.92f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNumber in 1..month.lengthOfMonth()) {
                            val date = month.atDay(dayNumber)
                            val isToday = date == today
                            val isFuture = date.isAfter(today)
                            val mood = moods[dayNumber]
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isToday -> Purple.copy(alpha = 0.10f)
                                            mood != null -> colors.field
                                            else -> Color.Transparent
                                        }
                                    )
                                    .then(
                                        if (isToday) {
                                            Modifier.border(
                                                1.5.dp, Purple, RoundedCornerShape(10.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .padding(vertical = 5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = if (isToday) FontWeight.SemiBold
                                    else FontWeight.Normal,
                                    color = if (isFuture) colors.textTertiary
                                    else colors.textSecondary
                                )
                                Text(
                                    text = mood?.emoji ?: "\u00B7",
                                    fontSize = if (mood != null) 12.sp else 11.sp,
                                    color = colors.textTertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
