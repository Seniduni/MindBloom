package com.mindbloom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindbloom.app.data.StaticContent
import com.mindbloom.app.ui.components.AppSelectField
import com.mindbloom.app.ui.components.AppTextField
import com.mindbloom.app.ui.components.PrimaryButton
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Purple
import com.mindbloom.app.ui.theme.Red
import com.mindbloom.app.ui.viewmodel.AuthViewModel

/** Screen 6. */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegistered: () -> Unit,
    onLogIn: () -> Unit
) {
    val state by viewModel.register.collectAsStateWithLifecycle()
    val colors = MB.colors
    var showGoalPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.consumeRegisterSuccess()
            onRegistered()
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
        Spacer(Modifier.height(44.dp))
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Start growing better, one day at a time.",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(28.dp))
        AppTextField(
            label = "Full Name",
            value = state.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "Your name",
            errorText = state.nameError
        )

        Spacer(Modifier.height(16.dp))
        AppTextField(
            label = "Email",
            value = state.email,
            onValueChange = viewModel::onRegisterEmailChange,
            placeholder = "you@email.com",
            keyboardType = KeyboardType.Email,
            errorText = state.emailError
        )

        Spacer(Modifier.height(16.dp))
        AppTextField(
            label = "Password",
            value = state.password,
            onValueChange = viewModel::onRegisterPasswordChange,
            placeholder = "At least 6 characters",
            isPassword = true,
            errorText = state.passwordError
        )

        Spacer(Modifier.height(16.dp))
        AppTextField(
            label = "Confirm Password",
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            placeholder = "Repeat your password",
            isPassword = true,
            errorText = state.confirmError
        )

        Spacer(Modifier.height(16.dp))
        AppSelectField(
            label = "My Wellness Goal",
            value = state.wellnessGoal,
            onClick = { showGoalPicker = true }
        )

        if (state.formError != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = state.formError.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = Red
            )
        }

        Spacer(Modifier.height(28.dp))
        PrimaryButton(
            text = "Create Account",
            loading = state.loading,
            onClick = viewModel::submitRegister
        )

        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "Log In",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Purple,
                modifier = Modifier.clickable(onClick = onLogIn)
            )
        }
        Spacer(Modifier.height(32.dp))
    }

    if (showGoalPicker) {
        OptionPickerSheet(
            title = "My Wellness Goal",
            options = StaticContent.wellnessGoals,
            selected = state.wellnessGoal,
            onSelect = {
                viewModel.onGoalChange(it)
                showGoalPicker = false
            },
            onDismiss = { showGoalPicker = false }
        )
    }
}

/** Simple bottom sheet used by every "pick one of these" field. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionPickerSheet(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MB.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(12.dp))
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(option) }
                        .padding(vertical = 14.dp)
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (option == selected) Purple else colors.textPrimary,
                        fontWeight = if (option == selected) FontWeight.SemiBold
                        else FontWeight.Normal
                    )
                }
            }
        }
    }
}
