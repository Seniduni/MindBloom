package com.mindbloom.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.data.MeditationSession
import com.mindbloom.app.data.StaticContent
import com.mindbloom.app.ui.components.BackHeader
import com.mindbloom.app.ui.components.ProgressTrack
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.PurpleSoft
import kotlinx.coroutines.delay

/** Screen 14. */
@Composable
fun MeditationScreen(onBack: () -> Unit) {
    val colors = MB.colors
    var selectedCategory by remember { mutableStateOf("All") }
    var playingSession by remember { mutableStateOf<MeditationSession?>(null) }

    val sessions = remember(selectedCategory) {
        if (selectedCategory == "All") StaticContent.meditationSessions
        else StaticContent.meditationSessions.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(14.dp))
        BackHeader(
            title = "Meditation",
            modifier = Modifier.padding(horizontal = 20.dp),
            onBack = onBack
        )

        Spacer(Modifier.height(20.dp))

        // ---- Category chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StaticContent.meditationCategories.forEach { category ->
                val selected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) Purple else colors.surface)
                        .border(
                            width = 1.dp,
                            color = if (selected) Purple else colors.border,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 14.sp,
                        color = if (selected) Color.White else colors.textSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        if (sessions.isEmpty()) {
            Text(
                text = "No sessions in this category yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            sessions.forEach { session ->
                SessionRow(session = session, onPlay = { playingSession = session })
            }
        }

        Spacer(Modifier.height(28.dp))
    }

    playingSession?.let { session ->
        SessionPlayerSheet(session = session, onDismiss = { playingSession = null })
    }
}

@Composable
private fun SessionRow(session: MeditationSession, onPlay: () -> Unit) {
    val colors = MB.colors
    SurfaceCard(padding = 14, onClick = onPlay) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(session.gradientStart), Color(session.gradientEnd))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = session.emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PurpleSoft)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = session.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Purple
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "${session.minutes} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Purple)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play ${session.title}",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * A working timer rather than a stub: the session counts down in real time,
 * with a breathing circle that expands and contracts on a four second cycle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionPlayerSheet(session: MeditationSession, onDismiss: () -> Unit) {
    val colors = MB.colors
    val totalSeconds = session.minutes * 60
    var elapsed by remember(session.id) { mutableIntStateOf(0) }
    var playing by remember(session.id) { mutableStateOf(true) }
    var inhale by remember(session.id) { mutableStateOf(true) }

    LaunchedEffect(session.id, playing) {
        while (playing && elapsed < totalSeconds) {
            delay(1000)
            elapsed++
            if (elapsed % 4 == 0) inhale = !inhale
        }
    }

    val circleScale by animateFloatAsState(
        targetValue = if (playing && inhale) 1f else 0.78f,
        animationSpec = tween(4000),
        label = "breath"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${session.category} · ${session.minutes} min",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(28.dp))
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(circleScale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(session.gradientStart), Color(session.gradientEnd))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (elapsed >= totalSeconds) "Done" else if (inhale) "Breathe in"
                    else "Breathe out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(28.dp))
            ProgressTrack(
                fraction = if (totalSeconds == 0) 0f else elapsed / totalSeconds.toFloat(),
                color = Purple,
                height = 8
            )
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = formatSeconds(elapsed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatSeconds(totalSeconds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Purple)
                    .clickable { playing = !playing },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Resume",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

private fun formatSeconds(value: Int): String {
    val minutes = value / 60
    val seconds = value % 60
    return "%d:%02d".format(minutes, seconds)
}
