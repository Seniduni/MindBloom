package com.mindbloom.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.notifications.NotificationHelper
import com.mindbloom.app.ui.components.BackHeader
import com.mindbloom.app.ui.components.CardDivider
import com.mindbloom.app.ui.components.DangerOutlineButton
import com.mindbloom.app.ui.components.SectionLabel
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.SettingsViewModel

/** Screen 16. */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onSignedOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MB.colors
    val context = LocalContext.current
    var showReminderPicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var confirmLogout by remember { mutableStateOf(false) }
    var showPermissionNotice by remember { mutableStateOf(false) }

    // Android 13+ needs an explicit grant before anything can be posted.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setNotifications(granted)
        if (!granted) showPermissionNotice = true
    }

    fun onNotificationsToggled(enabled: Boolean) {
        val needsPermission = enabled &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificationHelper.canPostNotifications(context)

        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.setNotifications(enabled)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        BackHeader(title = "Settings", onBack = onBack)

        Spacer(Modifier.height(24.dp))
        SectionLabel("Preferences")
        Spacer(Modifier.height(10.dp))

        SurfaceCard(padding = 4) {
            SwitchRow(
                emoji = "\uD83C\uDF19",
                label = "Dark Mode",
                checked = state.darkMode,
                onCheckedChange = viewModel::setDarkMode
            )
            CardDivider(Modifier.padding(horizontal = 14.dp))
            SwitchRow(
                emoji = "\uD83D\uDD14",
                label = "Notifications",
                checked = state.notifications,
                onCheckedChange = { onNotificationsToggled(it) }
            )
            CardDivider(Modifier.padding(horizontal = 14.dp))
            NavigationRow(
                emoji = "\u23F0",
                label = "Reminder Time",
                value = state.reminderTime,
                enabled = state.notifications,
                onClick = { showReminderPicker = true }
            )
            CardDivider(Modifier.padding(horizontal = 14.dp))
            NavigationRow(
                emoji = "\uD83C\uDF10",
                label = "Language",
                value = state.language,
                onClick = { showLanguagePicker = true }
            )
        }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Account & Support")
        Spacer(Modifier.height(10.dp))

        SurfaceCard(padding = 4) {
            NavigationRow(
                emoji = "\uD83D\uDD12",
                label = "Privacy & Security",
                onClick = { showPrivacy = true }
            )
            CardDivider(Modifier.padding(horizontal = 14.dp))
            NavigationRow(
                emoji = "\uD83D\uDCAC",
                label = "Help & Support",
                onClick = { showHelp = true }
            )
            CardDivider(Modifier.padding(horizontal = 14.dp))
            NavigationRow(
                emoji = "\uD83C\uDF38",
                label = "About MindBloom",
                value = "v1.0.0",
                onClick = { showAbout = true }
            )
        }

        Spacer(Modifier.height(26.dp))
        DangerOutlineButton(text = "Log Out", onClick = { confirmLogout = true })
        Spacer(Modifier.height(28.dp))
    }

    if (showReminderPicker) {
        ChoiceDialog(
            title = "Reminder time",
            options = listOf(
                "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM",
                "12:00 PM", "06:00 PM", "08:00 PM", "10:00 PM"
            ),
            selected = state.reminderTime,
            onSelect = {
                viewModel.setReminderTime(it)
                showReminderPicker = false
            },
            onDismiss = { showReminderPicker = false }
        )
    }

    if (showLanguagePicker) {
        ChoiceDialog(
            title = "Language",
            options = listOf("English", "සිංහල", "தமிழ்"),
            selected = state.language,
            onSelect = {
                viewModel.setLanguage(it)
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false }
        )
    }

    if (showPrivacy) {
        InfoDialog(
            title = "Privacy & Security",
            body = "Everything you log stays on this device. Moods, habits and journal " +
                "entries are written to a local database and are never uploaded. " +
                "Passwords are stored as a one-way hash.",
            onDismiss = { showPrivacy = false }
        )
    }

    if (showHelp) {
        InfoDialog(
            title = "Help & Support",
            body = "Tap a habit to open its details, or tap its icon on the list to log " +
                "progress straight away. Long lists scroll, and the + button on the " +
                "habits screen creates a new habit.",
            onDismiss = { showHelp = false }
        )
    }

    if (showAbout) {
        InfoDialog(
            title = "About MindBloom",
            body = "MindBloom v1.0.0. A mood, habit and journalling companion built with " +
                "Kotlin, Jetpack Compose and Room.",
            onDismiss = { showAbout = false }
        )
    }

    if (showPermissionNotice) {
        InfoDialog(
            title = "Reminders need permission",
            body = "Android blocks notifications until you allow them. You can turn " +
                "them on later from Settings \u203A Apps \u203A MindBloom \u203A " +
                "Notifications.",
            onDismiss = { showPermissionNotice = false }
        )
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    viewModel.signOut(onSignedOut)
                }) {
                    Text("Log Out", color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) {
                    Text("Stay", color = Purple)
                }
            },
            title = { Text("Log out of MindBloom?", color = colors.textPrimary) },
            text = {
                Text(
                    "Your data stays on this device. You can log back in with the same " +
                        "email and password.",
                    color = colors.textSecondary
                )
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun SwitchRow(
    emoji: String,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MB.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Purple,
                checkedBorderColor = Purple,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colors.border,
                uncheckedBorderColor = colors.border
            )
        )
    }
}

@Composable
private fun NavigationRow(
    emoji: String,
    label: String,
    value: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = MB.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 19.sp)
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) colors.textPrimary else colors.textTertiary,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Spacer(Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
    val colors = MB.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it", color = Purple) }
        },
        title = { Text(title, color = colors.textPrimary) },
        text = { Text(body, color = colors.textSecondary) },
        containerColor = colors.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
