package com.mindbloom.app.data.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

/**
 * Small key/value store for things that do not belong in the database:
 * onboarding state, the signed-in session and the Settings toggles.
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("mindbloom_prefs", Context.MODE_PRIVATE)

    /** Emits every time any preference changes so the UI can react. */
    private val changes: Flow<SharedPreferences> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, _ -> trySend(sp) }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    // ---- Onboarding ------------------------------------------------------

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()

    // ---- Session ---------------------------------------------------------

    var signedInEmail: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var rememberMe: Boolean
        get() = prefs.getBoolean(KEY_REMEMBER, true)
        set(value) = prefs.edit().putBoolean(KEY_REMEMBER, value).apply()

    val isSignedIn: Boolean get() = !signedInEmail.isNullOrBlank()

    fun signOut() {
        prefs.edit().remove(KEY_EMAIL).apply()
    }

    // ---- Settings --------------------------------------------------------

    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK, value).apply()

    val darkModeFlow: Flow<Boolean> = changes.map { it.getBoolean(KEY_DARK, false) }

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply()

    var reminderTime: String
        get() = prefs.getString(KEY_REMINDER, "09:00 AM") ?: "09:00 AM"
        set(value) = prefs.edit().putString(KEY_REMINDER, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "English") ?: "English"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    // ---- Seed guard ------------------------------------------------------

    var demoDataSeeded: Boolean
        get() = prefs.getBoolean(KEY_SEEDED, false)
        set(value) = prefs.edit().putBoolean(KEY_SEEDED, value).apply()

    private companion object {
        const val KEY_ONBOARDING = "onboarding_completed"
        const val KEY_EMAIL = "signed_in_email"
        const val KEY_REMEMBER = "remember_me"
        const val KEY_DARK = "dark_mode"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_REMINDER = "reminder_time"
        const val KEY_LANGUAGE = "language"
        const val KEY_SEEDED = "demo_data_seeded"
    }
}
