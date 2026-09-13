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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.ui.components.ChartLegendDot
import com.mindbloom.app.ui.components.DonutChart
import com.mindbloom.app.ui.components.DonutSegment
import com.mindbloom.app.ui.components.LegendRow
import com.mindbloom.app.ui.components.MonthCompletionGrid
import com.mindbloom.app.ui.components.MoodLineChart
import com.mindbloom.app.ui.components.ScreenTitle
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.Green
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.PurpleSoft
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.theme.Teal
import com.mindbloom.app.ui.viewmodel.StatisticsViewModel
import com.mindbloom.app.ui.viewmodel.StatsRange

/** Screen 12. */
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val range by viewModel.range.collectAsStateWithLifecycle()
    val colors = MB.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        ScreenTitle(text = "Statistics")

        Spacer(Modifier.height(18.dp))

        // ---- Range switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PurpleSoft)
                .padding(4.dp)
        ) {
            StatsRange.entries.forEach { option ->
                val selected = option == range
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .then(
                            if (selected) {
                                Modifier
                                    .shadow(2.dp, RoundedCornerShape(11.dp))
                                    .background(colors.surface)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { viewModel.selectRange(option) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 15.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) Purple else colors.textSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // ---- Mood + energy overview
        SurfaceCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                ChartLegendDot(color = Purple, label = "Mood")
                Spacer(Modifier.width(4.dp))
                ChartLegendDot(color = Teal, label = "Energy")
            }
            Spacer(Modifier.height(18.dp))
            MoodLineChart(
                scores = summary.moodScores,
                secondarySeries = summary.energyScores,
                xLabels = summary.axisLabels,
                chartHeight = 140,
                highlightLast = false
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---- Habit completion ring
        SurfaceCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DonutChart(
                    segments = listOf(
                        DonutSegment(summary.completed, Green),
                        DonutSegment(summary.partial, Orange),
                        DonutSegment(summary.missed, Red)
                    ),
                    centerValue = "${summary.averagePercent}%",
                    centerLabel = "Average",
                    diameter = 118,
                    strokeWidth = 17
                )
                Spacer(Modifier.width(20.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Habit Completion",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    LegendRow(Green, "Completed", summary.completed.toString())
                    LegendRow(Red, "Missed", summary.missed.toString())
                    LegendRow(Orange, "Partially", summary.partial.toString())
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Daily completion grid
        SurfaceCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = summary.monthLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Daily completion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            Spacer(Modifier.height(18.dp))
            MonthCompletionGrid(days = summary.monthDays)
        }

        Spacer(Modifier.height(24.dp))
    }
}
