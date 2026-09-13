package com.mindbloom.app.util

/** Form validation shared by the Login and Register screens. */
object Validators {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun emailError(value: String): String? = when {
        value.isBlank() -> "Enter your email address"
        !emailRegex.matches(value.trim()) -> "Enter a valid email address"
        else -> null
    }

    fun passwordError(value: String): String? = when {
        value.isEmpty() -> "Enter your password"
        value.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }

    fun nameError(value: String): String? = when {
        value.isBlank() -> "Enter your full name"
        value.trim().length < 2 -> "Name looks too short"
        else -> null
    }

    fun confirmPasswordError(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Re-enter your password"
        confirm != password -> "Passwords do not match"
        else -> null
    }
}
