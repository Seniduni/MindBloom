package com.mindbloom.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.ui.components.CheckMark
import com.mindbloom.app.ui.components.OutlineButton
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.successConfetti
import com.mindbloom.app.ui.theme.GreenBright
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.OrangeSoft

/** Screen 17 — shown the moment a habit is finished for the day. */
@Composable
fun SuccessScreen(
    habitName: String,
    streak: Int,
    onViewProgress: () -> Unit,
    onBackToHome: () -> Unit
) {
    val colors = MB.colors
    var appeared by remember { mutableStateOf(false) }

    val circleScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.4f,
        animationSpec = tween(520),
        label = "successCircle"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(600),
        label = "successContent"
    )

    val drift = rememberInfiniteTransition(label = "confetti")
    val driftValue by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "confettiDrift"
    )

    LaunchedEffect(Unit) { appeared = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ---- Confetti field
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val fieldWidth = maxWidth
            val fieldHeight = maxHeight * 0.72f
            successConfetti.forEachIndexed { index, piece ->
                val bob = if (index % 2 == 0) driftValue else 1f - driftValue
                Box(
                    modifier = Modifier
                        .offset(
                            x = fieldWidth * piece.xFraction,
                            y = fieldHeight * piece.yFraction + (bob * 10).dp
                        )
                        .rotate(piece.rotation + bob * 24f)
                        .size(
                            width = if (piece.round) 8.dp else 8.dp,
                            height = if (piece.round) 8.dp else 16.dp
                        )
                        .clip(if (piece.round) CircleShape else RoundedCornerShape(4.dp))
                        .background(piece.color.copy(alpha = contentAlpha))
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.28f))

            Box(
                modifier = Modifier
                    .scale(circleScale)
                    .size(136.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = GreenBright.copy(alpha = 0.7f)
                    )
                    .clip(CircleShape)
                    .background(GreenBright),
                contentAlignment = Alignment.Center
            ) {
                CheckMark(modifier = Modifier.size(62.dp), color = Color.White, stroke = 6f)
            }

            Spacer(Modifier.height(30.dp))
            Text(
                text = "Great Job! \uD83C\uDF89",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 27.sp,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "You've completed $habitName. Keep up the amazing work!",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            if (streak > 0) {
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(OrangeSoft)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\uD83D\uDD25", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$streak day streak unlocked",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 14.sp,
                        color = Orange
                    )
                }
            }

            Spacer(Modifier.weight(0.5f))

            PrimaryButton(text = "View Progress", onClick = onViewProgress)
            Spacer(Modifier.height(14.dp))
            OutlineButton(text = "Back to Home", onClick = onBackToHome)
            Spacer(Modifier.height(32.dp))
        }
    }
}
