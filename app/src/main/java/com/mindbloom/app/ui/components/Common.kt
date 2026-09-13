package com.mindbloom.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Orange
import com.mindbloom.app.ui.theme.OrangeSoft
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.PurpleDeep
import com.mindbloom.app.ui.theme.Red

/* ------------------------------------------------------------------ Buttons */

/** The large pill button used for Login, Create Account, Save Entry, etc. */
@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    containerColor: Color = Purple,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(29.dp))
            .background(if (enabled) containerColor else containerColor.copy(alpha = 0.45f))
            .clickable(enabled = enabled && !loading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 17.sp,
                color = Color.White
            )
        }
    }
}

/** Outlined pill button — "Edit Habit", "Back to Home", "Continue with Google". */
@Composable
fun OutlineButton(
    text: String,
    modifier: Modifier = Modifier,
    contentColor: Color = Purple,
    borderColor: Color = Purple,
    leading: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(29.dp))
            .background(MB.colors.surface)
            .border(1.5.dp, borderColor, RoundedCornerShape(29.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 17.sp,
                color = contentColor
            )
        }
    }
}

/** Destructive variant used by "Delete Habit" and "Log Out". */
@Composable
fun DangerOutlineButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlineButton(
        text = text,
        modifier = modifier,
        contentColor = Red,
        borderColor = Red,
        onClick = onClick
    )
}

/* --------------------------------------------------------------- Text input */

/**
 * The filled input used across Login, Register and the habit editor: a soft
 * grey rounded rectangle with the label sitting above it.
 */
@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    minHeight: Int = 56,
    errorText: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val colors = MB.colors
    val hasError = errorText != null

    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(8.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.field)
                .border(
                    width = 1.dp,
                    color = if (hasError) Red else colors.border,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = singleLine,
                        textStyle = LocalTextStyle.current.merge(
                            TextStyle(
                                color = colors.textPrimary,
                                fontSize = 16.sp,
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                            )
                        ),
                        cursorBrush = SolidColor(Purple),
                        visualTransformation = if (isPassword && !passwordVisible) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textTertiary
                        )
                    }
                }
                when {
                    isPassword -> {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility
                            else Icons.Outlined.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password",
                            tint = colors.textSecondary,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    }

                    trailing != null -> trailing()
                }
            }
        }
        if (errorText != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = Red
            )
        }
    }
}

/** Read-only field that opens a picker, e.g. "My Wellness Goal". */
@Composable
fun AppSelectField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    leading: String? = null,
    onClick: () -> Unit
) {
    val colors = MB.colors
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.field)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                Text(text = leading, fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colors.textSecondary
            )
        }
    }
}

/* ---------------------------------------------------------------- Container */

/** The white rounded card used almost everywhere in the design. */
@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    corner: Int = 18,
    padding: Int = 18,
    borderStroke: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val colors = MB.colors
    val shape = RoundedCornerShape(corner.dp)
    val stroke = borderStroke ?: BorderStroke(1.dp, colors.border)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 0.dp else 3.dp,
                shape = shape,
                ambientColor = Color(0x14000000),
                spotColor = Color(0x14000000)
            )
            .clip(shape)
            .background(colors.surface)
            .border(stroke, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding.dp),
        content = content
    )
}

/* ----------------------------------------------------------------- Progress */

/** Rounded progress track. Animates whenever the value changes. */
@Composable
fun ProgressTrack(
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Int = 8,
    trackColor: Color = MB.colors.track,
    animate: Boolean = true
) {
    val target = fraction.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        label = "progress"
    )
    val shown = if (animate) animated else target
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(shown)
                .height(height.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

/** Progress track filled with a gradient, used on the profile XP bar. */
@Composable
fun GradientProgressTrack(
    fraction: Float,
    brush: Brush,
    modifier: Modifier = Modifier,
    height: Int = 10
) {
    val animated by animateFloatAsState(fraction.coerceIn(0f, 1f), label = "xp")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(CircleShape)
            .background(MB.colors.track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .height(height.dp)
                .clip(CircleShape)
                .background(brush)
        )
    }
}

/* -------------------------------------------------------------------- Misc. */

/** Orange "🔥 7 day streak" chip on the habit rows. */
@Composable
fun StreakPill(days: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OrangeSoft)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "\uD83D\uDD25", fontSize = 11.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$days day streak",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = Orange
        )
    }
}

/** Large screen title, e.g. "My Habits", "Statistics". */
@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            color = MB.colors.textPrimary
        )
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MB.colors.textSecondary
            )
        }
    }
}

/** Back chevron plus title, used on Habit Details, Meditation and Settings. */
@Composable
fun BackHeader(
    title: String,
    modifier: Modifier = Modifier,
    tint: Color = MB.colors.textPrimary,
    trailing: (@Composable () -> Unit)? = null,
    onBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Back",
            tint = tint,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack)
                .padding(2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 22.sp,
            color = tint,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

/** Small uppercase-free section caption, e.g. "Preferences". */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 13.sp,
        color = MB.colors.textTertiary,
        modifier = modifier
    )
}

/** Shown wherever a list has nothing in it yet. */
@Composable
fun EmptyState(
    emoji: String,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 40.sp)
        Spacer(Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MB.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MB.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = actionLabel,
                modifier = Modifier.width(220.dp),
                onClick = onAction
            )
        }
    }
}

/** Full-screen translucent loader used while the database is being read. */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MB.colors.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Purple, strokeWidth = 3.dp)
    }
}

/** Thin divider matching the hairlines inside the design's cards. */
@Composable
fun CardDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MB.colors.border)
    )
}

/** Small solid circular action button, e.g. the meditation play control. */
@Composable
fun CircleIconButton(
    modifier: Modifier = Modifier,
    size: Int = 44,
    background: Color = Purple,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
        content = { content() }
    )
}

internal val ButtonPressColor = PurpleDeep
