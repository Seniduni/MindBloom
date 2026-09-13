package com.mindbloom.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple

/** Every destination in the app. */
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOME = "home"
    const val MOOD = "mood"
    const val HABITS = "habits"
    const val JOURNAL = "journal"
    const val PROFILE = "profile"

    const val STATISTICS = "statistics"
    const val INSIGHTS = "insights"
    const val MEDITATION = "meditation"
    const val SETTINGS = "settings"

    const val HABIT_DETAILS = "habit_details/{habitId}"
    fun habitDetails(habitId: Long) = "habit_details/$habitId"

    const val SUCCESS = "success/{habitName}/{streak}"
    fun success(habitName: String, streak: Int) =
        "success/${habitName.ifBlank { "habit" }}/$streak"

    /** Destinations that keep the bottom navigation bar visible. */
    val bottomBarRoutes = setOf(
        HOME, MOOD, HABITS, JOURNAL, PROFILE, STATISTICS, INSIGHTS, MEDITATION
    )
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(
        Routes.MOOD, "Mood",
        Icons.Filled.SentimentSatisfiedAlt, Icons.Outlined.SentimentSatisfiedAlt
    ),
    BottomNavItem(Routes.HABITS, "Habits", Icons.Filled.CheckBox, Icons.Outlined.CheckBox),
    BottomNavItem(
        Routes.JOURNAL, "Journal",
        Icons.Outlined.Description, Icons.Outlined.Description
    ),
    BottomNavItem(Routes.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
)

/**
 * Five-tab bar matching the design: a hairline across the top, outlined icons
 * in grey and the active tab in purple.
 */
@Composable
fun MindBloomBottomBar(
    currentRoute: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MB.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.border)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .navigationBarsPadding()
                .padding(top = 10.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                val interaction = remember { MutableInteractionSource() }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = { onSelect(item.route) }
                        )
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (selected) Purple else colors.textTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) Purple else colors.textTertiary
                    )
                }
            }
        }
    }
}
