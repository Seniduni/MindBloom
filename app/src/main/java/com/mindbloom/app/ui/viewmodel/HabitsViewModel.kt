package com.mindbloom.app.ui.viewmodel

import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindbloom.app.data.HabitWithProgress
import com.mindbloom.app.data.local.HabitEntity
import com.mindbloom.app.data.repository.HabitRepository
import com.mindbloom.app.data.repository.UserRepository
import com.mindbloom.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Emitted once when a habit is finished, so the screen can celebrate. */
data class HabitCelebration(val habitName: String, val streak: Int)

data class HabitFormState(
    val id: Long = 0,
    val name: String = "",
    val subtitle: String = "",
    val emoji: String = "\uD83D\uDCA7",
    val target: String = "1",
    val frequency: String = "Daily",
    val reminderTime: String = "09:00 AM",
    val accentColor: Long = 0xFF3B82F6,
    val iconBackground: Long = 0xFFDCEAFE,
    val nameError: String? = null,
    val targetError: String? = null
) {
    val isEditing: Boolean get() = id != 0L
}

class HabitsViewModel(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val habits: StateFlow<List<HabitWithProgress>> =
        habitRepository.observeHabitsWithProgress()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _celebration = MutableStateFlow<HabitCelebration?>(null)
    val celebration: StateFlow<HabitCelebration?> = _celebration.asStateFlow()

    private val _form = MutableStateFlow(HabitFormState())
    val form: StateFlow<HabitFormState> = _form.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    /* --------------------------- Progress edits -------------------------- */

    fun increment(habitId: Long) = viewModelScope.launch {
        if (habitRepository.incrementProgress(habitId)) celebrate(habitId)
    }

    fun toggle(habitId: Long) = viewModelScope.launch {
        if (habitRepository.toggleComplete(habitId)) celebrate(habitId)
    }

    fun setProgress(habitId: Long, value: Int) = viewModelScope.launch {
        if (habitRepository.setProgress(habitId, DateUtils.todayIso(), value)) celebrate(habitId)
    }

    private suspend fun celebrate(habitId: Long) {
        userRepository.addExperience(50)
        val habit = habitRepository.getHabit(habitId) ?: return
        val streak = habits.value.firstOrNull { it.habit.id == habitId }?.streak ?: 0
        _celebration.value = HabitCelebration(habit.name, maxOf(streak + 1, 1))
    }

    fun consumeCelebration() {
        _celebration.value = null
    }

    /* ------------------------------ Habit form --------------------------- */

    fun openCreateForm() {
        _form.value = HabitFormState()
        _showForm.value = true
    }

    fun openEditForm(habit: HabitEntity) {
        _form.value = HabitFormState(
            id = habit.id,
            name = habit.name,
            subtitle = habit.subtitle,
            emoji = habit.emoji,
            target = habit.target.toString(),
            frequency = habit.frequency,
            reminderTime = habit.reminderTime,
            accentColor = habit.accentColor,
            iconBackground = habit.iconBackground
        )
        _showForm.value = true
    }

    fun dismissForm() {
        _showForm.value = false
    }

    fun updateForm(transform: (HabitFormState) -> HabitFormState) {
        _form.value = transform(_form.value)
    }

    /** Returns true when the habit was valid and has been written. */
    fun saveForm(onSaved: () -> Unit = {}) {
        val state = _form.value
        val nameError = if (state.name.isBlank()) "Give the habit a name" else null
        val targetValue = state.target.toIntOrNull()
        val targetError = when {
            targetValue == null -> "Enter a number"
            targetValue <= 0 -> "Target must be at least 1"
            else -> null
        }
        if (nameError != null || targetError != null) {
            _form.value = state.copy(nameError = nameError, targetError = targetError)
            return
        }
        viewModelScope.launch {
            val entity = HabitEntity(
                id = state.id,
                name = state.name.trim(),
                subtitle = state.subtitle.trim().ifBlank { "$targetValue per day" },
                emoji = state.emoji,
                target = targetValue!!,
                frequency = state.frequency,
                reminderTime = state.reminderTime,
                accentColor = state.accentColor,
                iconBackground = state.iconBackground,
                sortOrder = habits.value.size
            )
            if (state.isEditing) habitRepository.updateHabit(entity)
            else habitRepository.addHabit(entity)
            _showForm.value = false
            onSaved()
        }
    }

    fun deleteHabit(habitId: Long, onDeleted: () -> Unit = {}) = viewModelScope.launch {
        habitRepository.deleteHabit(habitId)
        onDeleted()
    }
}

data class HabitDetailsUiState(
    val habit: HabitEntity? = null,
    val progress: Int = 0,
    val streak: Int = 0,
    val weekTicks: List<Boolean> = List(7) { false },
    val loading: Boolean = true,
    val notFound: Boolean = false
)

class HabitDetailsViewModel(
    private val habitId: Long,
    private val habitRepository: HabitRepository
) : ViewModel() {

    val uiState: StateFlow<HabitDetailsUiState> = combine(
        habitRepository.observeHabit(habitId),
        habitRepository.observeHabitsWithProgress(),
        habitRepository.observeWeekProgress(habitId)
    ) { habit, all, ticks ->
        val withProgress = all.firstOrNull { it.habit.id == habitId }
        HabitDetailsUiState(
            habit = habit,
            progress = withProgress?.progress ?: 0,
            streak = withProgress?.streak ?: 0,
            weekTicks = ticks,
            loading = false,
            notFound = habit == null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitDetailsUiState())

    val exists: StateFlow<Boolean> = habitRepository.observeHabit(habitId)
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun delete(onDeleted: () -> Unit) = viewModelScope.launch {
        habitRepository.deleteHabit(habitId)
        onDeleted()
    }
}
