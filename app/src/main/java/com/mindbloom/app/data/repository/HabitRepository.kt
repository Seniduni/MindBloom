package com.mindbloom.app.data.repository

import com.mindbloom.app.data.DayCompletion
import com.mindbloom.app.data.HabitWithProgress
import com.mindbloom.app.data.local.HabitDao
import com.mindbloom.app.data.local.HabitEntity
import com.mindbloom.app.data.local.HabitLogDao
import com.mindbloom.app.data.local.HabitLogEntity
import com.mindbloom.app.data.step
import com.mindbloom.app.util.DateUtils
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class HabitRepository(
    private val habitDao: HabitDao,
    private val logDao: HabitLogDao
) {

    fun observeHabits(): Flow<List<HabitEntity>> = habitDao.observeHabits()

    fun observeHabit(id: Long): Flow<HabitEntity?> = habitDao.observeHabit(id)

    fun observeAllLogs(): Flow<List<HabitLogEntity>> = logDao.observeAllLogs()

    /** Habits joined with the progress recorded for [date] plus their streak. */
    fun observeHabitsWithProgress(date: String = DateUtils.todayIso()): Flow<List<HabitWithProgress>> =
        combine(habitDao.observeHabits(), logDao.observeAllLogs()) { habits, logs ->
            val logsByHabit = logs.groupBy { it.habitId }
            habits.map { habit ->
                val habitLogs = logsByHabit[habit.id].orEmpty()
                HabitWithProgress(
                    habit = habit,
                    progress = habitLogs.firstOrNull { it.date == date }?.progress ?: 0,
                    streak = streakFor(habitLogs, habit.target, DateUtils.parse(date))
                )
            }
        }

    /** Progress of a single habit over the seven days of the current week. */
    fun observeWeekProgress(habitId: Long): Flow<List<Boolean>> =
        logDao.observeLogsForHabit(habitId).map { logs ->
            DateUtils.currentWeek().map { day ->
                val iso = DateUtils.iso(day)
                val log = logs.firstOrNull { it.date == iso }
                log != null && log.progress > 0
            }
        }

    suspend fun getHabit(id: Long): HabitEntity? = habitDao.getHabit(id)

    suspend fun addHabit(habit: HabitEntity): Long = habitDao.insert(habit)

    suspend fun updateHabit(habit: HabitEntity) = habitDao.update(habit)

    suspend fun deleteHabit(id: Long) {
        logDao.deleteLogsForHabit(id)
        habitDao.deleteById(id)
    }

    /**
     * Writes [progress] for the given habit/day, clamped to 0..target.
     * Returns true when this write completed the habit for the first time,
     * which is what triggers the celebration screen.
     */
    suspend fun setProgress(habitId: Long, date: String, progress: Int): Boolean {
        val habit = habitDao.getHabit(habitId) ?: return false
        val existing = logDao.getLog(habitId, date)
        val clamped = progress.coerceIn(0, habit.target)
        val wasComplete = (existing?.progress ?: 0) >= habit.target

        if (existing == null) {
            logDao.insert(HabitLogEntity(habitId = habitId, date = date, progress = clamped))
        } else {
            logDao.update(existing.copy(progress = clamped))
        }
        return !wasComplete && clamped >= habit.target
    }

    /** Advances a habit by one step; used by the tick button on the habit list. */
    suspend fun incrementProgress(habitId: Long, date: String = DateUtils.todayIso()): Boolean {
        val habit = habitDao.getHabit(habitId) ?: return false
        val current = logDao.getLog(habitId, date)?.progress ?: 0
        return setProgress(habitId, date, current + habit.step)
    }

    /** Marks a habit fully done, or resets it to zero when already done. */
    suspend fun toggleComplete(habitId: Long, date: String = DateUtils.todayIso()): Boolean {
        val habit = habitDao.getHabit(habitId) ?: return false
        val current = logDao.getLog(habitId, date)?.progress ?: 0
        val next = if (current >= habit.target) 0 else habit.target
        return setProgress(habitId, date, next)
    }

    /**
     * Consecutive days ending today (or yesterday, if today is still open) on
     * which at least [minRatio] of all habit targets were met.
     */
    fun observeOverallStreaks(minRatio: Float = 0.6f): Flow<Pair<Int, Int>> =
        combine(habitDao.observeHabits(), logDao.observeAllLogs()) { habits, logs ->
            if (habits.isEmpty()) return@combine 0 to 0
            val qualifying = dayScores(habits, logs)
                .filterValues { it >= minRatio }
                .keys
                .toSortedSet()

            val today = DateUtils.today()
            var current = 0
            var cursor = if (qualifying.contains(today)) today else today.minusDays(1)
            while (qualifying.contains(cursor)) {
                current++
                cursor = cursor.minusDays(1)
            }

            var longest = 0
            var run = 0
            var previous: LocalDate? = null
            qualifying.forEach { day ->
                run = if (previous != null && previous!!.plusDays(1) == day) run + 1 else 1
                longest = maxOf(longest, run)
                previous = day
            }
            current to maxOf(longest, current)
        }

    /** Per-day completion buckets for the statistics month grid. */
    fun observeMonthCompletion(year: Int, month: Int): Flow<List<DayCompletion>> =
        combine(habitDao.observeHabits(), logDao.observeAllLogs()) { habits, logs ->
            val scores = dayScores(habits, logs)
            val first = LocalDate.of(year, month, 1)
            val today = DateUtils.today()
            (0 until first.lengthOfMonth()).map { offset ->
                val day = first.plusDays(offset.toLong())
                when {
                    day.isAfter(today) -> DayCompletion.NONE
                    habits.isEmpty() -> DayCompletion.NONE
                    else -> when (val score = scores[day] ?: 0f) {
                        0f -> DayCompletion.MISSED
                        in 0f..0.4f -> DayCompletion.PARTIAL
                        in 0.4f..0.85f -> DayCompletion.MOSTLY
                        else -> if (score >= 0.85f) DayCompletion.COMPLETE else DayCompletion.MOSTLY
                    }
                }
            }
        }

    /**
     * Mean per-habit completion for each day, so a habit with a 6,000 step
     * target does not drown out a habit with a target of one.
     */
    private fun dayScores(
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Map<LocalDate, Float> {
        if (habits.isEmpty()) return emptyMap()
        val targets = habits.associate { it.id to it.target.coerceAtLeast(1) }
        val habitCount = habits.size.toFloat()
        return logs.groupBy { it.date }.mapNotNull { (date, dayLogs) ->
            val achieved = dayLogs.sumOf { log ->
                val target = targets[log.habitId] ?: return@sumOf 0.0
                (log.progress.toDouble() / target).coerceIn(0.0, 1.0)
            }
            val parsed = runCatching { DateUtils.parse(date) }.getOrNull()
            parsed?.let { it to (achieved / habitCount).toFloat() }
        }.toMap()
    }

    /** Consecutive completed days for one habit. */
    private fun streakFor(logs: List<HabitLogEntity>, target: Int, from: LocalDate): Int {
        if (target <= 0) return 0
        val completed = logs.filter { it.progress >= target }
            .mapNotNull { runCatching { DateUtils.parse(it.date) }.getOrNull() }
            .toHashSet()
        var cursor = if (completed.contains(from)) from else from.minusDays(1)
        var streak = 0
        while (completed.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
