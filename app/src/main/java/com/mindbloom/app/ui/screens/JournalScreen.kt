package com.mindbloom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.Mood
import com.mindbloom.app.ui.components.AppSelectField
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.components.ScreenTitle
import com.mindbloom.app.ui.components.SurfaceCard
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.JournalViewModel
import com.mindbloom.app.util.DateUtils

/** Tags map to the small gradient thumbnails shown in the design. */
private val photoTags = listOf("leaf", "city", "sunset", "coffee", "book", "walk")

private fun gradientForTag(tag: String): Brush = when (tag) {
    "leaf" -> Brush.linearGradient(listOf(Color(0xFF5FE3CE), Color(0xFF2DD4BF)))
    "city" -> Brush.linearGradient(listOf(Color(0xFFB79BF5), Color(0xFF8B6FF0)))
    "sunset" -> Brush.linearGradient(listOf(Color(0xFFF7B267), Color(0xFFF4845F)))
    "coffee" -> Brush.linearGradient(listOf(Color(0xFFC9A27E), Color(0xFF8D6748)))
    "book" -> Brush.linearGradient(listOf(Color(0xFF7FB3F5), Color(0xFF4F8DF5)))
    else -> Brush.linearGradient(listOf(Color(0xFF9FE6B8), Color(0xFF22B85C)))
}

private fun emojiForTag(tag: String): String = when (tag) {
    "leaf" -> "\uD83C\uDF3F"
    "city" -> "\uD83C\uDF06"
    "sunset" -> "\uD83C\uDF05"
    "coffee" -> "\u2615"
    "book" -> "\uD83D\uDCD6"
    else -> "\uD83D\uDEB6"
}

/** Screen 11. */
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MB.colors
    var showMoodPicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        ScreenTitle(
            text = "How was your day?",
            subtitle = DateUtils.longDateWithYear()
        )

        Spacer(Modifier.height(18.dp))

        // ---- Entry editor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 168.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.field)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            BasicTextField(
                value = state.content,
                onValueChange = viewModel::updateContent,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                ),
                cursorBrush = SolidColor(Purple),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 118.dp)
            )
            if (state.content.isEmpty()) {
                Text(
                    text = "Write about your day, what went well and what did not.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 15.sp,
                    color = colors.textTertiary
                )
            }
            Text(
                text = "${state.characterCount}/${JournalViewModel.CONTENT_LIMIT}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        Spacer(Modifier.height(18.dp))

        // ---- Mood for this entry
        AppSelectField(
            label = "Mood",
            value = state.mood.label,
            leading = state.mood.emoji,
            onClick = { showMoodPicker = true }
        )

        Spacer(Modifier.height(18.dp))

        // ---- Photos
        Text(
            text = "Add Photos",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            state.photos.forEachIndexed { index, tag ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(gradientForTag(tag))
                        .clickable { viewModel.removePhoto(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emojiForTag(tag), fontSize = 26.sp)
                }
            }
            if (state.photos.size < 6) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.field)
                        .border(
                            width = 1.5.dp,
                            color = Purple.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showPhotoPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add photo",
                        tint = Purple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.error.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = Red
            )
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = "Save Entry", onClick = { viewModel.save(onSaved) })

        Spacer(Modifier.height(26.dp))

        // ---- Recent entries
        Text(
            text = "Recent Entries",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 17.sp,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(12.dp))

        if (state.recentEntries.isEmpty()) {
            SurfaceCard {
                Text(
                    text = "Nothing here yet. Entries you save on other days will " +
                        "appear in this list.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        } else {
            state.recentEntries.forEach { entry ->
                SurfaceCard(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(
                        text = DateUtils.mediumDate(DateUtils.parse(entry.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = Mood.fromLabel(entry.mood)?.emoji ?: "",
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    if (showMoodPicker) {
        ChoiceDialog(
            title = "Mood",
            options = Mood.entries.map { "${it.emoji}  ${it.label}" },
            selected = "${state.mood.emoji}  ${state.mood.label}",
            onSelect = { value ->
                Mood.entries.firstOrNull { value.endsWith(it.label) }?.let(viewModel::updateMood)
                showMoodPicker = false
            },
            onDismiss = { showMoodPicker = false }
        )
    }

    if (showPhotoPicker) {
        ChoiceDialog(
            title = "Add a photo",
            options = photoTags.map { "${emojiForTag(it)}  ${it.replaceFirstChar(Char::uppercase)}" },
            selected = "",
            onSelect = { value ->
                photoTags.firstOrNull { value.endsWith(it, ignoreCase = true) }
                    ?.let(viewModel::addPhoto)
                showPhotoPicker = false
            },
            onDismiss = { showPhotoPicker = false }
        )
    }
}
