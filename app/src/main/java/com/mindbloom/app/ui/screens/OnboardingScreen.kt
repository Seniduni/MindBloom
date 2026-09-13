package com.mindbloom.app.ui.screens

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.ui.components.AiInsightsIllustration
import com.mindbloom.app.ui.components.MeditationIllustration
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.WateringIllustration
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val body: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Take care of your\nmental wellbeing",
        body = "Track your mood, understand your feelings and build a calmer mind " +
            "— a few minutes each day is all it takes."
    ),
    OnboardingPage(
        title = "Build healthy habits",
        body = "Small daily actions grow into lasting change. Set your habits, keep " +
            "your streak alive and watch yourself bloom."
    ),
    OnboardingPage(
        title = "Get AI insights\njust for you",
        body = "Your personal wellness companion spots patterns in your mood and " +
            "habits, then suggests what to try next."
    )
)

/**
 * Screens 2–4. A three page pager: the first two show Skip / Next, the last
 * swaps to a full width Get Started button.
 */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val colors = MB.colors
    val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(0.20f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.15f),
                    contentAlignment = Alignment.Center
                ) {
                    when (page) {
                        0 -> MeditationIllustration(modifier = Modifier.fillMaxSize())
                        1 -> WateringIllustration(modifier = Modifier.fillMaxSize())
                        else -> AiInsightsIllustration(modifier = Modifier.fillMaxSize())
                    }
                }
                Spacer(Modifier.weight(0.22f))
                Text(
                    text = onboardingPages[page].title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 26.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = onboardingPages[page].body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                Spacer(Modifier.weight(0.55f))
            }
        }

        // Bottom controls. The first two pages share a row of Skip / dots /
        // Next; the last page swaps to a full width Get Started button.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            if (isLastPage) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PageIndicator(
                        pageCount = onboardingPages.size,
                        currentPage = pagerState.currentPage
                    )
                    Spacer(Modifier.height(30.dp))
                    PrimaryButton(text = "Get Started", onClick = onFinished)
                }
            } else {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onFinished)
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                )

                PageIndicator(
                    pageCount = onboardingPages.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(88.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Purple)
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/** Dots that stretch into a pill for the active page. */
@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (selected) 22.dp else 8.dp,
                label = "indicator"
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(if (selected) Purple else MB.colors.border)
            )
        }
    }
}
