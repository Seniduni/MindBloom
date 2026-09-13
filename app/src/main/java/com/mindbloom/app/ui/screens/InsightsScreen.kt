package com.mindbloom.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.Insight
import com.mindbloom.app.ui.components.BloomBotBadge
import com.mindbloom.app.ui.components.DonutChart
import com.mindbloom.app.ui.components.DonutSegment
import com.mindbloom.app.ui.components.LoadingScreen
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.GradientEnd
import com.mindbloom.app.ui.theme.GradientStart
import com.mindbloom.app.ui.theme.Green
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.InsightsViewModel

/** Screen 13. */
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    onViewWeeklyReport: () -> Unit,
    onOpenMeditation: () -> Unit
) {
    val report by viewModel.report.collectAsStateWithLifecycle()
    val colors = MB.colors

    val current = report
    if (current == null) {
        LoadingScreen()
        return
    }

    val scoreColor = when {
        current.score >= 70 -> Green
        current.score >= 45 -> Orange
        else -> Red
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ---- Gradient header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BloomBotBadge(size = 62)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "AI Insights",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Bloom Bot has been reading your week.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // ---- Wellness score
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            SurfaceCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(
                        segments = listOf(
                            DonutSegment(current.score, scoreColor),
                            DonutSegment(100 - current.score, Color.Transparent)
                        ),
                        centerValue = current.score.toString(),
                        centerLabel = "/ 100",
                        diameter = 104,
                        strokeWidth = 13
                    )
                    Spacer(Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Mental Wellness Score",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${current.label} ${current.emoji}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = deltaMessage(current.delta),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Insights for You",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 21.sp,
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(14.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            current.insights.forEach { insight ->
                InsightCard(
                    insight = insight,
                    onClick = if (insight.title.contains("breathing", ignoreCase = true)) {
                        onOpenMeditation
                    } else {
                        null
                    }
                )
            }
        }

        Spacer(Modifier.height(26.dp))
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            PrimaryButton(text = "View Weekly Report", onClick = onViewWeeklyReport)
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun InsightCard(insight: Insight, onClick: (() -> Unit)?) {
    val colors = MB.colors
    SurfaceCard(padding = 16, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(insight.tintColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = insight.emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = insight.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

private fun deltaMessage(delta: Int): String = when {
    delta > 0 -> "Keep going! You're $delta points above last week."
    delta < 0 -> "You're ${-delta} points below last week. Small steps count."
    else -> "You're holding steady against last week."
}
