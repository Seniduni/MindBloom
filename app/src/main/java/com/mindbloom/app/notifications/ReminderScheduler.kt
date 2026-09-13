package com.mindbloom.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Schedules [ReminderWorker] to run daily at the time shown in Settings.
 *
 * WorkManager cannot fire at an exact wall-clock time, so the first run is
 * delayed to the next occurrence of that time and it repeats every 24 hours.
 * Drift of a few minutes is fine for a wellbeing nudge, and it survives
 * reboots without needing an exact-alarm permission.
 */
object ReminderScheduler {

    private val timeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    /** Cancels any existing schedule and, if enabled, sets up a new one. */
    fun apply(context: Context, enabled: Boolean, timeLabel: String) {
        if (enabled) schedule(context, timeLabel) else cancel(context)
    }

    fun schedule(context: Context, timeLabel: String) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(minutesUntil(timeLabel), TimeUnit.MINUTES)
            .addTag(ReminderWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ReminderWorker.WORK_NAME)
    }

    /** Minutes from now until the next occurrence of [timeLabel], e.g. "09:00 AM". */
    internal fun minutesUntil(
        timeLabel: String,
        now: LocalDateTime = LocalDateTime.now()
    ): Long {
        val target = runCatching { LocalTime.parse(timeLabel.trim(), timeFormat) }
            .getOrElse { LocalTime.of(9, 0) }

        var next = LocalDateTime.of(now.toLocalDate(), target)
        if (!next.isAfter(now)) {
            next = LocalDateTime.of(now.toLocalDate().plusDays(1), target)
        }
        return Duration.between(now, next).toMinutes().coerceAtLeast(1)
    }

    /** Exposed for tests and for the "remind me now" debug path. */
    internal fun nextRunDate(timeLabel: String, now: LocalDateTime = LocalDateTime.now()): LocalDate {
        val minutes = minutesUntil(timeLabel, now)
        return now.plusMinutes(minutes).toLocalDate()
    }
}
