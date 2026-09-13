package com.mindbloom.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindbloom.app.data.HabitWithProgress
import com.mindbloom.app.di.AppContainer
import kotlinx.coroutines.flow.first

/**
 * Runs once a day at the time chosen in Settings. The message is built from
 * what is actually outstanding, so the reminder is useful rather than generic.
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = AppContainer.get(applicationContext)

        // The user may have turned reminders off since this was scheduled.
        if (!container.preferences.notificationsEnabled) return Result.success()

        val habits = runCatching {
            container.habitRepository.observeHabitsWithProgress().first()
        }.getOrNull() ?: return Result.retry()

        val moodLogged = runCatching {
            container.moodRepository.observeForDate().first() != null
        }.getOrDefault(false)

        val outstanding = habits.filterNot { it.isComplete }
        if (outstanding.isEmpty() && moodLogged) return Result.success()

        NotificationHelper.showReminder(
            context = applicationContext,
            title = titleFor(outstanding, moodLogged),
            body = bodyFor(outstanding, moodLogged)
        )
        return Result.success()
    }

    private fun titleFor(outstanding: List<HabitWithProgress>, moodLogged: Boolean): String = when {
        outstanding.isEmpty() -> "How are you feeling today?"
        outstanding.size == 1 -> "One habit left today"
        !moodLogged -> "A few minutes for yourself"
        else -> "${outstanding.size} habits still open"
    }

    private fun bodyFor(outstanding: List<HabitWithProgress>, moodLogged: Boolean): String {
        val moodPart = if (moodLogged) null else "log today's mood"
        val habitPart = when {
            outstanding.isEmpty() -> null
            outstanding.size <= 2 -> outstanding.joinToString(" and ") { it.habit.name.lowercase() }
            else -> "${outstanding.take(2).joinToString(", ") { it.habit.name.lowercase() }} " +
                "and ${outstanding.size - 2} more"
        }

        val streak = outstanding.maxOfOrNull { it.streak } ?: 0
        val tail = if (streak >= 3) " Your $streak day streak is still alive." else ""

        return when {
            moodPart != null && habitPart != null ->
                "Time to $moodPart and finish $habitPart.$tail"

            habitPart != null -> "Still to do: $habitPart.$tail"
            else -> "Take a moment to check in with yourself."
        }
    }

    companion object {
        const val WORK_NAME = "mindbloom_daily_reminder"
    }
}
