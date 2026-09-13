package com.mindbloom.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindbloom.app.data.prefs.AppPreferences
import com.mindbloom.app.data.repository.AuthResult
import com.mindbloom.app.data.repository.UserRepository
import com.mindbloom.app.util.Validators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val loading: Boolean = false,
    val success: Boolean = false
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val wellnessGoal: String = "Reduce Stress",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val formError: String? = null,
    val loading: Boolean = false,
    val success: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register.asStateFlow()

    /* ------------------------------- Login ------------------------------ */

    fun onLoginEmailChange(value: String) =
        _login.update { it.copy(email = value, emailError = null, formError = null) }

    fun onLoginPasswordChange(value: String) =
        _login.update { it.copy(password = value, passwordError = null, formError = null) }

    fun onRememberMeChange(value: Boolean) = _login.update { it.copy(rememberMe = value) }

    fun submitLogin() {
        val state = _login.value
        val emailError = Validators.emailError(state.email)
        val passwordError = Validators.passwordError(state.password)
        if (emailError != null || passwordError != null) {
            _login.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        _login.update { it.copy(loading = true, formError = null) }
        viewModelScope.launch {
            delay(400) // keeps the loading state visible on fast devices
            when (val result = userRepository.login(state.email, state.password, state.rememberMe)) {
                is AuthResult.Success -> _login.update { it.copy(loading = false, success = true) }
                is AuthResult.Failure ->
                    _login.update { it.copy(loading = false, formError = result.message) }
            }
        }
    }

    fun continueWithGoogle() {
        _login.update { it.copy(loading = true, formError = null) }
        viewModelScope.launch {
            delay(500)
            when (val result = userRepository.continueWithGoogle()) {
                is AuthResult.Success -> _login.update { it.copy(loading = false, success = true) }
                is AuthResult.Failure ->
                    _login.update { it.copy(loading = false, formError = result.message) }
            }
        }
    }

    fun consumeLoginSuccess() = _login.update { it.copy(success = false) }

    /* ----------------------------- Register ----------------------------- */

    fun onNameChange(value: String) =
        _register.update { it.copy(name = value, nameError = null, formError = null) }

    fun onRegisterEmailChange(value: String) =
        _register.update { it.copy(email = value, emailError = null, formError = null) }

    fun onRegisterPasswordChange(value: String) =
        _register.update { it.copy(password = value, passwordError = null, formError = null) }

    fun onConfirmPasswordChange(value: String) =
        _register.update { it.copy(confirmPassword = value, confirmError = null) }

    fun onGoalChange(value: String) = _register.update { it.copy(wellnessGoal = value) }

    fun submitRegister() {
        val state = _register.value
        val nameError = Validators.nameError(state.name)
        val emailError = Validators.emailError(state.email)
        val passwordError = Validators.passwordError(state.password)
        val confirmError = Validators.confirmPasswordError(state.password, state.confirmPassword)
        if (nameError != null || emailError != null ||
            passwordError != null || confirmError != null
        ) {
            _register.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmError = confirmError
                )
            }
            return
        }
        _register.update { it.copy(loading = true, formError = null) }
        viewModelScope.launch {
            delay(400)
            val result = userRepository.register(
                name = state.name,
                email = state.email,
                password = state.password,
                wellnessGoal = state.wellnessGoal
            )
            when (result) {
                is AuthResult.Success ->
                    _register.update { it.copy(loading = false, success = true) }

                is AuthResult.Failure ->
                    _register.update { it.copy(loading = false, formError = result.message) }
            }
        }
    }

    fun consumeRegisterSuccess() = _register.update { it.copy(success = false) }

    /* ------------------------------ Session ----------------------------- */

    val isSignedIn: Boolean get() = preferences.isSignedIn
    val onboardingCompleted: Boolean get() = preferences.onboardingCompleted

    fun completeOnboarding() {
        preferences.onboardingCompleted = true
    }

    /** Pre-fills the demo account so the app can be reviewed in one tap. */
    fun prefillDemoAccount(email: String, password: String) {
        if (_login.value.email.isEmpty()) {
            _login.update { it.copy(email = email, password = password) }
        }
    }
}
