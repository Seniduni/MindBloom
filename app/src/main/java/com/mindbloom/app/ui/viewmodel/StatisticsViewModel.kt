package com.mindbloom.app.ui.viewmodel

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Settings
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindbloom.app.data.DayCompletion
import com.mindbloom.app.data.InsightEngine
import com.mindbloom.app.data.Mood
import com.mindbloom.app.data.StatsSummary
import com.mindbloom.app.data.WellnessReport
import com.mindbloom.app.data.local.HabitEntity
import com.mindbloom.app.data.local.HabitLogEntity
import com.mindbloom.app.data.local.UserEntity
import com.mindbloom.app.data.prefs.AppPreferences
import com.mindbloom.app.data.repository.HabitRepository
import com.mindbloom.app.data.repository.MoodRepository
import com.mindbloom.app.data.repository.UserRepository
import com.mindbloom.app.notifications.ReminderScheduler
import com.mindbloom.app.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/* =============================== Statistics ============================== */

enum class StatsRange(val label: String, val days: Int) {
    WEEK("Week", 7),
    MONTH("Month", 30),
    YEAR("Year", 365)
}

class StatisticsViewModel(
    private val habitRepository: HabitRepository,
    private val moodRepository: MoodRepository
) : ViewModel() {

    private val _range = MutableStateFlow(StatsRange.WEEK)
    val range: StateFlow<StatsRange> = _range.asStateFlow()

    val summary: StateFlow<StatsSummary> = combine(
        habitRepository.observeHabits(),
        habitRepository.observeAllLogs(),
        moodRepository.observeAll(),
        _range
    ) { habits, logs, moods, range ->
        buildSummary(habits, logs, moods.associate { it.date to it.mood }, range)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySummary())

    fun selectRange(range: StatsRange) {
        _range.value = range
    }

    private fun emptySummary() = StatsSummary(
        moodScores = List(7) { null },
        energyScores = List(7) { null },
        axisLabels = DateUtils.weekDayInitials,
        completed = 0,
        missed = 0,
        partial = 0,
        averagePercent = 0,
        monthDays = emptyList(),
        monthLabel = DateUtils.monthYear(YearMonth.now())
    )

    private fun buildSummary(
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>,
        moodByDate: Map<String, String>,
        range: StatsRange
    ): StatsSummary {
        val today = DateUtils.today()

        // ---- Line chart: mood, plus an "energy" proxy from habit activity.
        val points: List<LocalDate>
        val labels: List<String>
        when (range) {
            StatsRange.WEEK -> {
                points = DateUtils.currentWeek(today)
                labels = DateUtils.weekDayInitials
            }

            StatsRange.MONTH -> {
                points = (0..3).map { today.minusWeeks((3 - it).toLong()) }
                labels = listOf("W1", "W2", "W3", "W4")
            }

            StatsRange.YEAR -> {
                points = (0..5).map { today.minusMonths((5 - it).toLong()) }
                labels = points.map { it.month.name.take(1) }
            }
        }

        val moodScores = points.map { day ->
            when (range) {
                StatsRange.WEEK -> moodByDate[DateUtils.iso(day)]
                    ?.let { Mood.fromLabel(it)?.score?.toFloat() }

                StatsRange.MONTH -> averageMood(moodByDate, day, day.plusDays(6))
                StatsRange.YEAR -> averageMood(
                    moodByDate,
                    day.withDayOfMonth(1),
                    day.withDayOfMonth(day.lengthOfMonth())
                )
            }
        }

        val energyScores = points.map { day ->
            val window = when (range) {
                StatsRange.WEEK -> listOf(day)
                StatsRange.MONTH -> (0..6).map { day.plusDays(it.toLong()) }
                StatsRange.YEAR -> (0 until day.lengthOfMonth())
                    .map { day.withDayOfMonth(1).plusDays(it.toLong()) }
            }
            val values = window.mapNotNull { d ->
                if (d.isAfter(today) || habits.isEmpty()) null
                else completionRatio(d, habits, logs) * 4f + 1f
            }
            if (values.isEmpty()) null else values.average().toFloat()
        }

        // ---- Habit completion ring over the selected window.
        val windowDays = (0 until range.days)
            .map { today.minusDays(it.toLong()) }
            .filterNot { it.isAfter(today) }

        var completed = 0
        var missed = 0
        var partial = 0
        var ratioSum = 0f
        var counted = 0
        habits.forEach { habit ->
            windowDays.forEach { day ->
                val log = logs.firstOrNull {
                    it.habitId == habit.id && it.date == DateUtils.iso(day)
                }
                val ratio = ((log?.progress ?: 0).toFloat() /
                    habit.target.coerceAtLeast(1)).coerceIn(0f, 1f)
                ratioSum += ratio
                counted++
                when {
                    ratio >= 1f -> completed++
                    ratio <= 0f -> missed++
                    else -> partial++
                }
            }
        }
        val average = if (counted == 0) 0 else ((ratioSum / counted) * 100).roundToInt()

        // ---- Month grid.
        val month = YearMonth.from(today)
        val monthDays = (0 until month.lengthOfMonth()).map { offset ->
            val day = month.atDay(1).plusDays(offset.toLong())
            when {
                day.isAfter(today) || habits.isEmpty() -> DayCompletion.NONE
                else -> when (val ratio = completionRatio(day, habits, logs)) {
                    0f -> DayCompletion.MISSED
                    in 0f..0.45f -> DayCompletion.PARTIAL
                    in 0.45f..0.85f -> DayCompletion.MOSTLY
                    else -> if (ratio > 0.85f) DayCompletion.COMPLETE else DayCompletion.MOSTLY
                }
            }
        }

        return StatsSummary(
            moodScores = moodScores,
            energyScores = energyScores,
            axisLabels = labels,
            completed = completed,
            missed = missed,
            partial = partial,
            averagePercent = average,
            monthDays = monthDays,
            monthLabel = DateUtils.monthYear(month)
        )
    }

    private fun averageMood(
        moodByDate: Map<String, String>,
        from: LocalDate,
        to: LocalDate
    ): Float? {
        var day = from
        val values = mutableListOf<Int>()
        while (!day.isAfter(to)) {
            moodByDate[DateUtils.iso(day)]?.let { label ->
                Mood.fromLabel(label)?.let { values += it.score }
            }
            day = day.plusDays(1)
        }
        return if (values.isEmpty()) null else values.average().toFloat()
    }

    private fun completionRatio(
        day: LocalDate,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Float {
        if (habits.isEmpty()) return 0f
        val iso = DateUtils.iso(day)
        val total = habits.sumOf { habit ->
            val progress = logs.firstOrNull { it.habitId == habit.id && it.date == iso }?.progress ?: 0
            (progress.toDouble() / habit.target.coerceAtLeast(1)).coerceIn(0.0, 1.0)
        }
        return (total / habits.size).toFloat()
    }
}

/* ================================ Insights =============================== */

class InsightsViewModel(
    habitRepository: HabitRepository,
    moodRepository: MoodRepository
) : ViewModel() {

    val report: StateFlow<WellnessReport?> = combine(
        moodRepository.observeAll(),
        habitRepository.observeHabits(),
        habitRepository.observeAllLogs()
    ) { moods, habits, logs ->
        InsightEngine.build(moods, habits, logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

/* ================================= Profile =============================== */

data class ProfileUiState(
    val user: UserEntity? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val loading: Boolean = true
) {
    val initials: String
        get() = user?.name.orEmpty().trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "MB" }

    val xpToNextLevel: Int get() = (2000 - (user?.experience ?: 0)).coerceAtLeast(0)
    val xpFraction: Float get() = ((user?.experience ?: 0) / 2000f).coerceIn(0f, 1f)
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    habitRepository: HabitRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.observeCurrentUser(),
        habitRepository.observeOverallStreaks()
    ) { user, streaks ->
        ProfileUiState(
            user = user,
            currentStreak = streaks.first,
            longestStreak = streaks.second,
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun updateProfile(name: String, goal: String) = viewModelScope.launch {
        userRepository.updateProfile(name, goal)
    }
}

/* ================================ Settings =============================== */

data class SettingsUiState(
    val darkMode: Boolean = false,
    val notifications: Boolean = true,
    val reminderTime: String = "09:00 AM",
    val language: String = "English"
)

class SettingsViewModel(
    private val preferences: AppPreferences,
    private val userRepository: UserRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            darkMode = preferences.darkMode,
            notifications = preferences.notificationsEnabled,
            reminderTime = preferences.reminderTime,
            language = preferences.language
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        preferences.darkMode = enabled
        _uiState.value = _uiState.value.copy(darkMode = enabled)
    }

    fun setNotifications(enabled: Boolean) {
        preferences.notificationsEnabled = enabled
        _uiState.value = _uiState.value.copy(notifications = enabled)
        applyReminderSchedule()
    }

    fun setReminderTime(value: String) {
        preferences.reminderTime = value
        _uiState.value = _uiState.value.copy(reminderTime = value)
        applyReminderSchedule()
    }

    /** Keeps the background worker in step with the two settings above. */
    private fun applyReminderSchedule() {
        ReminderScheduler.apply(
            context = appContext,
            enabled = preferences.notificationsEnabled,
            timeLabel = preferences.reminderTime
        )
    }

    fun setLanguage(value: String) {
        preferences.language = value
        _uiState.value = _uiState.value.copy(language = value)
    }

    fun signOut(onSignedOut: () -> Unit) {
        userRepository.signOut()
        onSignedOut()
    }
}

/* ============================ Theme controller =========================== */

/**
 * Owns the dark mode flag at the activity level so the Settings toggle
 * repaints the whole app immediately.
 */
class ThemeViewModel(private val preferences: AppPreferences) : ViewModel() {

    val darkMode: StateFlow<Boolean> = preferences.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, preferences.darkMode)
}
