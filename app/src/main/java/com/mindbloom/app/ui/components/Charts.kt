package com.mindbloom.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.data.DayCompletion
import com.mindbloom.app.data.Mood
import com.mindbloom.app.ui.theme.Green
import com.mindbloom.app.ui.theme.GreenPale
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.theme.Teal

/* ============================== Mood line chart ========================== */

/**
 * The weekly mood chart. [scores] holds one value per weekday on the 1..5 mood
 * scale, with null for days that have not been logged yet.
 */
@Composable
fun MoodLineChart(
    scores: List<Float?>,
    modifier: Modifier = Modifier,
    xLabels: List<String> = listOf("M", "T", "W", "T", "F", "S", "S"),
    secondarySeries: List<Float?>? = null,
    chartHeight: Int = 150,
    highlightLast: Boolean = true
) {
    val colors = MB.colors
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700),
        label = "chartReveal"
    )

    Row(modifier = modifier.fillMaxWidth()) {
        // Emoji axis
        Column(
            modifier = Modifier.height(chartHeight.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Mood.axisEmojis.forEach { emoji ->
                Text(text = emoji, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight.dp)
            ) {
                val w = size.width
                val h = size.height
                val minScore = 1f
                val maxScore = 5f

                // Horizontal guide lines.
                repeat(4) { index ->
                    val y = h * index / 3f
                    drawLine(
                        color = colors.border,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                fun pointFor(index: Int, value: Float): Offset {
                    val x = if (scores.size <= 1) w / 2f
                    else w * index / (scores.size - 1).toFloat()
                    val normalised = ((value - minScore) / (maxScore - minScore)).coerceIn(0f, 1f)
                    return Offset(x, h - normalised * h * 0.9f - h * 0.05f)
                }

                val points = scores.mapIndexedNotNull { index, value ->
                    value?.let { index to pointFor(index, it) }
                }

                if (points.size >= 2) {
                    // Shaded area under the primary line.
                    val area = Path().apply {
                        moveTo(points.first().second.x, h)
                        points.forEach { lineTo(it.second.x, it.second.y) }
                        lineTo(points.last().second.x, h)
                        close()
                    }
                    drawPath(
                        area,
                        Brush.verticalGradient(
                            listOf(Purple.copy(alpha = 0.14f), Purple.copy(alpha = 0.01f))
                        )
                    )

                    val line = Path().apply {
                        moveTo(points.first().second.x, points.first().second.y)
                        points.drop(1).forEach { lineTo(it.second.x, it.second.y) }
                    }
                    drawPath(
                        line,
                        color = Purple,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Optional dashed comparison series (energy on the statistics screen).
                if (secondarySeries != null) {
                    val secondary = secondarySeries.mapIndexedNotNull { index, value ->
                        value?.let { pointFor(index, it) }
                    }
                    if (secondary.size >= 2) {
                        val path = Path().apply {
                            moveTo(secondary.first().x, secondary.first().y)
                            secondary.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(
                            path,
                            color = Teal,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(9f, 9f), 0f
                                )
                            )
                        )
                    }
                }

                // Data points: hollow rings, with the highest day filled in.
                val peakIndex = points.maxByOrNull { pair ->
                    scores.getOrNull(pair.first) ?: 0f
                }?.first
                points.forEach { (index, point) ->
                    val filled = highlightLast && index == peakIndex
                    drawCircle(colors.surface, 4.5.dp.toPx() * progress, point)
                    drawCircle(
                        color = Purple,
                        radius = 4.5.dp.toPx() * progress,
                        center = point,
                        style = if (filled) Stroke(width = 4.5.dp.toPx()) else Stroke(
                            width = 2.dp.toPx()
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                xLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textTertiary
                    )
                }
            }
        }
    }
}

/* ================================ Donut =================================== */

data class DonutSegment(val value: Int, val color: Color)

/** The habit completion ring on the Statistics screen. */
@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    centerValue: String,
    centerLabel: String,
    modifier: Modifier = Modifier,
    diameter: Int = 120,
    strokeWidth: Int = 18
) {
    val total = segments.sumOf { it.value }.coerceAtLeast(1)
    val sweep by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "donut"
    )
    val colors = MB.colors

    Box(modifier = modifier.size(diameter.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter.dp)) {
            val stroke = strokeWidth.dp.toPx()
            val inset = stroke / 2f
            drawCircle(
                color = colors.track,
                radius = (size.minDimension - stroke) / 2f,
                style = Stroke(width = stroke)
            )
            var startAngle = -90f
            segments.forEach { segment ->
                val angle = 360f * segment.value / total * sweep
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = angle,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - stroke, size.height - stroke),
                    style = Stroke(width = stroke, cap = StrokeCap.Butt)
                )
                startAngle += angle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerValue,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary
            )
        }
    }
}

/** Legend row: coloured dot, label, then the count on the right. */
@Composable
fun LegendRow(color: Color, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MB.colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MB.colors.textPrimary
        )
    }
}

/* ============================ Month completion grid ====================== */

/** Seven columns of dots, one per day, coloured by how much was completed. */
@Composable
fun MonthCompletionGrid(
    days: List<DayCompletion>,
    modifier: Modifier = Modifier,
    dotSize: Int = 22
) {
    val colors = MB.colors
    val rows = days.chunked(7)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    Box(
                        Modifier
                            .size(dotSize.dp)
                            .clip(CircleShape)
                            .background(
                                when (day) {
                                    DayCompletion.COMPLETE -> Green
                                    DayCompletion.MOSTLY -> GreenPale
                                    DayCompletion.PARTIAL -> Orange
                                    DayCompletion.MISSED -> Red
                                    DayCompletion.NONE -> colors.track
                                }
                            )
                    )
                }
                repeat(7 - week.size) {
                    Box(
                        Modifier
                            .size(dotSize.dp)
                            .clip(CircleShape)
                            .background(colors.track)
                    )
                }
            }
        }
    }
}

/* ============================== Week tick row ============================ */

/** The "This Week" tick row on the habit details screen. */
@Composable
fun WeekTickRow(
    completed: List<Boolean>,
    modifier: Modifier = Modifier,
    labels: List<String> = listOf("M", "T", "W", "T", "F", "S", "S")
) {
    val colors = MB.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (completed.getOrElse(index) { false }) Green else colors.field
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (completed.getOrElse(index) { false }) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.15f, size.height * 0.55f)
                                lineTo(size.width * 0.40f, size.height * 0.80f)
                                lineTo(size.width * 0.88f, size.height * 0.22f)
                            }
                            drawPath(
                                path,
                                color = Color.White,
                                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** A single tick mark, reused by the success screen. */
@Composable
fun CheckMark(modifier: Modifier = Modifier, color: Color = Color.White, stroke: Float = 5f) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.16f, size.height * 0.54f)
            lineTo(size.width * 0.40f, size.height * 0.78f)
            lineTo(size.width * 0.86f, size.height * 0.24f)
        }
        drawPath(
            path,
            color = color,
            style = Stroke(width = stroke.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ChartLegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MB.colors.textSecondary,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}
