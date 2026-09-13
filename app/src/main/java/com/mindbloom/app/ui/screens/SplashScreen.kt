package com.mindbloom.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.ui.components.MindBloomLogo
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Poppins
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.PurpleSoft
import com.mindbloom.app.ui.theme.Teal
import kotlinx.coroutines.delay

/**
 * Screen 1. Holds for a moment while the database opens, then hands over to
 * onboarding, login or the dashboard depending on saved state.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (started) 1f else 0.82f,
        animationSpec = tween(700),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(600),
        label = "logoAlpha"
    )
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(1600, easing = LinearEasing),
        label = "splashProgress"
    )

    LaunchedEffect(Unit) {
        started = true
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MB.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MindBloomLogo(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha),
                size = 190
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "MIND BLOOM",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Purple, Teal)),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(logoAlpha)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Grow Better Every Day",
                style = MaterialTheme.typography.bodyLarge,
                color = MB.colors.textSecondary,
                modifier = Modifier.alpha(logoAlpha)
            )
        }

        // Loading bar sitting low on the screen, as in the design.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
                .width(180.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(PurpleSoft)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Purple)
            )
        }
    }
}
