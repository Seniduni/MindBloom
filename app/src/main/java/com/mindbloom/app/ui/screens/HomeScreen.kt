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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.ui.components.BloomBotBadge
import com.mindbloom.app.ui.components.MoodLineChart
import com.mindbloom.app.ui.components.ProgressTrack
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.GradientEnd
import com.mindbloom.app.ui.theme.GradientStart
import com.mindbloom.app.ui.theme.Green
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Teal
import com.mindbloom.app.ui.viewmodel.HomeViewModel

/** Screen 7 — the dashboard. */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenMood: () -> Unit,
    onOpenHabits: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MB.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // ---- Greeting + avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${state.greeting}, ${state.userName} \uD83C\uDF38",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 21.sp,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.dateLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Purple, Teal)))
                    .clickable(onClick = onOpenProfile),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- Mood banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
                .clickable(onClick = onOpenMood)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = state.todayMood?.let { "${it.label} ${it.emoji}" } ?: "Tap to log",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
            Text(
                text = state.todayMood?.emoji ?: "\uD83D\uDE42",
                fontSize = 40.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---- Habit progress
        SurfaceCard(onClick = onOpenHabits) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Habit Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${state.habitsDone}/${state.habitsTotal}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Green
                )
            }
            Spacer(Modifier.height(14.dp))
            ProgressTrack(
                fraction = state.habitFraction,
                color = Green,
                height = 9
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---- Streak cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StreakCard(
                emoji = "\uD83D\uDD25",
                label = "Current Streak",
                value = "${state.currentStreak} Days",
                valueColor = Orange,
                modifier = Modifier.weight(1f),
                onClick = onOpenStatistics
            )
            StreakCard(
                emoji = "\uD83C\uDFC6",
                label = "Longest Streak",
                value = "${state.longestStreak} Days",
                valueColor = Purple,
                modifier = Modifier.weight(1f),
                onClick = onOpenStatistics
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---- Weekly mood chart
        SurfaceCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Mood",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "This week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Purple,
                    modifier = Modifier.clickable(onClick = onOpenStatistics)
                )
            }
            Spacer(Modifier.height(18.dp))
            MoodLineChart(scores = state.weekMoodScores, chartHeight = 130)
        }

        Spacer(Modifier.height(16.dp))

        // ---- AI insights banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
                .clickable(onClick = onOpenInsights)
                .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Insights",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 21.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Bloom Bot has been reading your week.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            BloomBotBadge(size = 54)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StreakCard(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    SurfaceCard(modifier = modifier, onClick = onClick, padding = 16) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MB.colors.textSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
