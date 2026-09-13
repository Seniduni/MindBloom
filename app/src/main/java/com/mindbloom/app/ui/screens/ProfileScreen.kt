package com.mindbloom.app.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.Achievement
import com.mindbloom.app.data.StaticContent
import com.mindbloom.app.ui.components.AppSelectField
import com.mindbloom.app.ui.components.AppTextField
import com.mindbloom.app.ui.components.CardDivider
import com.mindbloom.app.ui.components.GradientProgressTrack
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.GradientEnd
import com.mindbloom.app.ui.theme.GradientStart
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Teal
import com.mindbloom.app.ui.viewmodel.ProfileViewModel

/** Screen 15. */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onOpenSettings: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenMeditation: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MB.colors
    var showGoals by remember { mutableStateOf(false) }
    var showBadges by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }

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
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 26.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 21.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable(onClick = onOpenSettings)
                )
            }

            Spacer(Modifier.height(18.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Purple, Teal)))
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.initials,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = state.user?.name ?: "MindBloom user",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 21.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "\u2B50 Level ${state.user?.level ?: 1}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Purple
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // ---- Experience
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Experience",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${state.user?.experience ?: 0} / 2000 XP",
                        style = MaterialTheme.typography.titleSmall,
                        color = Purple
                    )
                }
                Spacer(Modifier.height(14.dp))
                GradientProgressTrack(
                    fraction = state.xpFraction,
                    brush = Brush.horizontalGradient(listOf(Purple, Teal))
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "${state.xpToNextLevel} XP to reach Level " +
                        "${(state.user?.level ?: 1) + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Achievements",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 20.sp,
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StaticContent.achievements.forEach { achievement ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedAchievement = achievement }
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(achievement.backgroundColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = achievement.emoji, fontSize = 26.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = achievement.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // ---- Menu
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            SurfaceCard(padding = 4) {
                ProfileMenuRow("\uD83C\uDFAF", "My Goals") { showGoals = true }
                CardDivider(Modifier.padding(horizontal = 14.dp))
                ProfileMenuRow("\uD83D\uDCC8", "My Progress", onClick = onOpenProgress)
                CardDivider(Modifier.padding(horizontal = 14.dp))
                ProfileMenuRow("\uD83C\uDFC5", "Badges") { showBadges = true }
                CardDivider(Modifier.padding(horizontal = 14.dp))
                ProfileMenuRow("\u270F\uFE0F", "Edit Profile") { showEditProfile = true }
            }
        }

        Spacer(Modifier.height(26.dp))
    }

    /* ------------------------------ Dialogs ----------------------------- */

    if (showGoals) {
        AlertDialog(
            onDismissRequest = { showGoals = false },
            confirmButton = {
                TextButton(onClick = { showGoals = false }) { Text("Close", color = Purple) }
            },
            title = { Text("My Goals", color = colors.textPrimary) },
            text = {
                Column {
                    Text(
                        "Wellness goal: ${state.user?.wellnessGoal ?: "Not set"}",
                        color = colors.textSecondary
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Current streak: ${state.currentStreak} days", color = colors.textSecondary)
                    Spacer(Modifier.height(6.dp))
                    Text("Personal best: ${state.longestStreak} days", color = colors.textSecondary)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Guided sessions can help with this goal.",
                        color = colors.textSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(text = "Open Meditation") {
                        showGoals = false
                        onOpenMeditation()
                    }
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showBadges) {
        AlertDialog(
            onDismissRequest = { showBadges = false },
            confirmButton = {
                TextButton(onClick = { showBadges = false }) { Text("Close", color = Purple) }
            },
            title = { Text("Badges", color = colors.textPrimary) },
            text = {
                Column {
                    StaticContent.achievements.forEach { achievement ->
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = achievement.emoji, fontSize = 22.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    achievement.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.textPrimary
                                )
                                Text(
                                    achievement.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    selectedAchievement?.let { achievement ->
        AlertDialog(
            onDismissRequest = { selectedAchievement = null },
            confirmButton = {
                TextButton(onClick = { selectedAchievement = null }) {
                    Text("Nice", color = Purple)
                }
            },
            title = { Text("${achievement.emoji}  ${achievement.label}", color = colors.textPrimary) },
            text = { Text(achievement.description, color = colors.textSecondary) },
            containerColor = colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showEditProfile) {
        EditProfileDialog(
            initialName = state.user?.name.orEmpty(),
            initialGoal = state.user?.wellnessGoal ?: StaticContent.wellnessGoals.first(),
            onSave = { name, goal ->
                viewModel.updateProfile(name, goal)
                showEditProfile = false
            },
            onDismiss = { showEditProfile = false }
        )
    }
}

@Composable
private fun ProfileMenuRow(emoji: String, label: String, onClick: () -> Unit) {
    val colors = MB.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 19.sp)
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialGoal: String,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MB.colors
    var name by remember { mutableStateOf(initialName) }
    var goal by remember { mutableStateOf(initialGoal) }
    var showGoalPicker by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(initialName) { name = initialName }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) nameError = "Enter your name" else onSave(name, goal)
            }) {
                Text("Save", color = Purple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = colors.textSecondary) }
        },
        title = { Text("Edit Profile", color = colors.textPrimary) },
        text = {
            Column(modifier = Modifier.imePadding()) {
                AppTextField(
                    label = "Full Name",
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    errorText = nameError
                )
                Spacer(Modifier.height(14.dp))
                AppSelectField(
                    label = "My Wellness Goal",
                    value = goal,
                    onClick = { showGoalPicker = true }
                )
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(20.dp)
    )

    if (showGoalPicker) {
        ChoiceDialog(
            title = "My Wellness Goal",
            options = StaticContent.wellnessGoals,
            selected = goal,
            onSelect = {
                goal = it
                showGoalPicker = false
            },
            onDismiss = { showGoalPicker = false }
        )
    }
}
