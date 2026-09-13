package com.mindbloom.app.ui.viewmodel

import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindbloom.app.data.HabitWithProgress
import com.mindbloom.app.data.Mood
import com.mindbloom.app.data.repository.HabitRepository
import com.mindbloom.app.data.repository.MoodRepository
import com.mindbloom.app.data.repository.UserRepository
import com.mindbloom.app.util.DateUtils
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val greeting: String = DateUtils.greeting(),
    val userName: String = "there",
    val dateLabel: String = DateUtils.longDate(),
    val todayMood: Mood? = null,
    val habitsDone: Int = 0,
    val habitsTotal: Int = 0,
    val habitFraction: Float = 0f,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val weekMoodScores: List<Float?> = List(7) { null },
    val loading: Boolean = true
) {
    val initials: String
        get() = userName.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "MB" }
}

class HomeViewModel(
    habitRepository: HabitRepository,
    moodRepository: MoodRepository,
    userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        habitRepository.observeHabitsWithProgress(),
        habitRepository.observeOverallStreaks(),
        moodRepository.observeForDate(),
        moodRepository.observeWeekScores(),
        userRepository.observeCurrentUser()
    ) { habits, streaks, todayMood, weekScores, user ->
        HomeUiState(
            greeting = DateUtils.greeting(),
            userName = user?.name?.substringBefore(' ') ?: "there",
            dateLabel = DateUtils.longDate(),
            todayMood = Mood.fromLabel(todayMood?.mood),
            habitsDone = roundedCompletion(habits),
            habitsTotal = habits.size,
            habitFraction = averageFraction(habits),
            currentStreak = streaks.first,
            longestStreak = streaks.second,
            weekMoodScores = weekScores,
            loading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    /**
     * "4/6" on the dashboard is partial credit: every habit contributes the
     * share of its target that has been reached, and the total is rounded.
     * A day that is 69% done therefore reads as 4 of 6 rather than 2 of 6.
     */
    private fun roundedCompletion(habits: List<HabitWithProgress>): Int =
        habits.sumOf { it.fraction.toDouble() }.roundToInt()

    private fun averageFraction(habits: List<HabitWithProgress>): Float =
        if (habits.isEmpty()) 0f
        else (habits.sumOf { it.fraction.toDouble() } / habits.size).toFloat()
}
