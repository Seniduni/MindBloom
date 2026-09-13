package com.mindbloom.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.ui.components.AppTextField
import com.mindbloom.app.ui.components.CheckMark
import com.mindbloom.app.ui.components.OutlineButton
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.AuthViewModel

/** Screen 5. */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoggedIn: () -> Unit,
    onSignUp: () -> Unit
) {
    val state by viewModel.login.collectAsStateWithLifecycle()
    val colors = MB.colors
    var showForgotPassword by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.consumeLoginSuccess()
            onLoggedIn()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            text = "Welcome Back \uD83C\uDF38",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Log in to continue your wellness journey.",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(34.dp))
        AppTextField(
            label = "Email",
            value = state.email,
            onValueChange = viewModel::onLoginEmailChange,
            placeholder = "you@email.com",
            keyboardType = KeyboardType.Email,
            errorText = state.emailError
        )

        Spacer(Modifier.height(18.dp))
        AppTextField(
            label = "Password",
            value = state.password,
            onValueChange = viewModel::onLoginPasswordChange,
            placeholder = "Your password",
            isPassword = true,
            errorText = state.passwordError
        )

        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppCheckbox(
                checked = state.rememberMe,
                onCheckedChange = viewModel::onRememberMeChange
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Remember me",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Purple,
                modifier = Modifier.clickable { showForgotPassword = true }
            )
        }

        if (state.formError != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = state.formError.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = Red
            )
        }

        Spacer(Modifier.height(22.dp))
        PrimaryButton(
            text = "Login",
            loading = state.loading,
            onClick = viewModel::submitLogin
        )

        Spacer(Modifier.height(22.dp))
        DividerWithLabel("or continue with")

        Spacer(Modifier.height(20.dp))
        OutlineButton(
            text = "Continue with Google",
            contentColor = colors.textPrimary,
            borderColor = colors.border,
            leading = { GoogleLogo() },
            onClick = viewModel::continueWithGoogle
        )

        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Purple,
                modifier = Modifier.clickable(onClick = onSignUp)
            )
        }
        Spacer(Modifier.height(32.dp))
    }

    if (showForgotPassword) {
        AlertDialog(
            onDismissRequest = { showForgotPassword = false },
            confirmButton = {
                TextButton(onClick = { showForgotPassword = false }) {
                    Text("Got it", color = Purple)
                }
            },
            title = { Text("Reset your password", color = colors.textPrimary) },
            text = {
                Text(
                    "MindBloom stores your account on this device only, so there is no " +
                        "reset email to send. Create a new account to start again, or use " +
                        "Continue with Google.",
                    color = colors.textSecondary
                )
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/** Purple square checkbox with a white tick, as drawn in the design. */
@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (checked) Purple else MB.colors.surface)
            .border(
                width = 1.5.dp,
                color = if (checked) Purple else MB.colors.border,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            CheckMark(modifier = Modifier.size(13.dp), color = Color.White, stroke = 2.2f)
        }
    }
}

/** A hairline on either side of a short caption. */
@Composable
fun DividerWithLabel(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(MB.colors.border)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp,
            color = MB.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(MB.colors.border)
        )
    }
}

/** The four-colour Google mark, drawn as arcs so no bitmap asset is needed. */
@Composable
fun GoogleLogo(modifier: Modifier = Modifier, size: Int = 20) {
    Canvas(modifier = modifier.size(size.dp)) {
        val stroke = this.size.minDimension * 0.26f
        val inset = stroke / 2f
        val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
        val topLeft = Offset(inset, inset)

        drawArc(
            color = Color(0xFF4285F4), startAngle = -20f, sweepAngle = 70f, useCenter = false,
            topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFF34A853), startAngle = 50f, sweepAngle = 70f, useCenter = false,
            topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFFFBBC05), startAngle = 120f, sweepAngle = 70f, useCenter = false,
            topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFFEA4335), startAngle = 190f, sweepAngle = 80f, useCenter = false,
            topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Butt)
        )
        // The horizontal bar of the G.
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(this.size.width * 0.52f, this.size.height * 0.52f),
            end = Offset(this.size.width * 0.98f, this.size.height * 0.52f),
            strokeWidth = stroke
        )
    }
}
