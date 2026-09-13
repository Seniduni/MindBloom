package com.mindbloom.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindbloom.app.di.AppContainer

/**
 * A single factory for every ViewModel in the app. Using one factory keeps the
 * wiring in one readable place without pulling in a dependency injection
 * framework.
 */
class MindBloomViewModelFactory(
    private val container: AppContainer,
    private val habitId: Long = 0L
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(container.userRepository, container.preferences) as T

        modelClass.isAssignableFrom(HomeViewModel::class.java) ->
            HomeViewModel(
                container.habitRepository,
                container.moodRepository,
                container.userRepository
            ) as T

        modelClass.isAssignableFrom(HabitsViewModel::class.java) ->
            HabitsViewModel(container.habitRepository, container.userRepository) as T

        modelClass.isAssignableFrom(HabitDetailsViewModel::class.java) ->
            HabitDetailsViewModel(habitId, container.habitRepository) as T

        modelClass.isAssignableFrom(MoodViewModel::class.java) ->
            MoodViewModel(container.moodRepository) as T

        modelClass.isAssignableFrom(JournalViewModel::class.java) ->
            JournalViewModel(container.journalRepository) as T

        modelClass.isAssignableFrom(StatisticsViewModel::class.java) ->
            StatisticsViewModel(container.habitRepository, container.moodRepository) as T

        modelClass.isAssignableFrom(InsightsViewModel::class.java) ->
            InsightsViewModel(container.habitRepository, container.moodRepository) as T

        modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
            ProfileViewModel(container.userRepository, container.habitRepository) as T

        modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
            SettingsViewModel(
                container.preferences,
                container.userRepository,
                container.appContext
            ) as T

        modelClass.isAssignableFrom(ThemeViewModel::class.java) ->
            ThemeViewModel(container.preferences) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
