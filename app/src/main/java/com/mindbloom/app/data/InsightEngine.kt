package com.mindbloom.app.data

import com.mindbloom.app.data.local.HabitEntity
import com.mindbloom.app.data.local.HabitLogEntity
import com.mindbloom.app.data.local.MoodEntity
import com.mindbloom.app.util.DateUtils
import java.time.LocalDate
import kotlin.math.roundToInt

data class WellnessReport(
    val score: Int,
    val delta: Int,
    val label: String,
    val emoji: String,
    val insights: List<Insight>
)

/**
 * "Bloom Bot" is a transparent, rule-based analyser rather than a black box:
 * it correlates logged moods with habit completion and reports what it finds.
 * Keeping the reasoning explicit makes the results reproducible, which matters
 * more than novelty for a wellbeing app.
 */
object InsightEngine {

    fun build(
        moods: List<MoodEntity>,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): WellnessReport {
        val today = DateUtils.today()
        val moodByDate = moods.mapNotNull { entry ->
            val date = runCatching { DateUtils.parse(entry.date) }.getOrNull()
            val mood = Mood.fromLabel(entry.mood)
            if (date == null || mood == null) null else date to mood
        }.toMap()

        val thisWeek = (0..6).map { today.minusDays(it.toLong()) }
        val lastWeek = (7..13).map { today.minusDays(it.toLong()) }

        val score = scoreFor(thisWeek, moodByDate, habits, logs)
        val previous = scoreFor(lastWeek, moodByDate, habits, logs)
        val delta = score - previous

        val (label, emoji) = when {
            score >= 85 -> "Excellent" to "\uD83E\uDD29"
            score >= 70 -> "Good" to "\uD83D\uDE0A"
            score >= 50 -> "Steady" to "\uD83D\uDE42"
            score >= 30 -> "Needs care" to "\uD83D\uDE10"
            else -> "Take it easy" to "\uD83D\uDE14"
        }

        return WellnessReport(
            score = score,
            delta = delta,
            label = label,
            emoji = emoji,
            insights = buildInsights(today, moodByDate, habits, logs)
        )
    }

    /** 60% mood, 40% habit completion, expressed out of 100. */
    private fun scoreFor(
        days: List<LocalDate>,
        moodByDate: Map<LocalDate, Mood>,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Int {
        val moodValues = days.mapNotNull { moodByDate[it]?.score }
        val moodComponent = if (moodValues.isEmpty()) 0.5
        else (moodValues.average() - 1) / 4.0

        val habitComponent = if (habits.isEmpty()) 0.5
        else days.map { completionRatio(it, habits, logs).toDouble() }.average()

        return ((moodComponent * 0.6 + habitComponent * 0.4) * 100).roundToInt().coerceIn(0, 100)
    }

    private fun completionRatio(
        day: LocalDate,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Float {
        if (habits.isEmpty()) return 0f
        val iso = DateUtils.iso(day)
        val dayLogs = logs.filter { it.date == iso }.associateBy { it.habitId }
        val total = habits.sumOf { habit ->
            val progress = dayLogs[habit.id]?.progress ?: 0
            (progress.toDouble() / habit.target.coerceAtLeast(1)).coerceIn(0.0, 1.0)
        }
        return (total / habits.size).toFloat()
    }

    private fun buildInsights(
        today: LocalDate,
        moodByDate: Map<LocalDate, Mood>,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): List<Insight> {
        val insights = mutableListOf<Insight>()
        val window = (0..13).map { today.minusDays(it.toLong()) }

        habitNamed(habits, "Sleep")?.let { sleep ->
            val onDays = daysCompleted(sleep, logs, window)
            val after = window.filter { onDays.contains(it.minusDays(1)) }
                .mapNotNull { moodByDate[it]?.score }
            val others = window.filterNot { onDays.contains(it.minusDays(1)) }
                .mapNotNull { moodByDate[it]?.score }
            if (after.size >= 2 && after.average() > others.averageOrZero()) {
                insights += Insight(
                    emoji = "\uD83C\uDF19",
                    title = "Sleep is driving your mood",
                    body = "Your calmest days all followed nights where you slept before 11 PM.",
                    tintColor = 0xFFEDE9FE
                )
            }
        }

        habitNamed(habits, "Walk")?.let { walking ->
            val onDays = daysCompleted(walking, logs, window)
            val withWalk = window.filter { onDays.contains(it) }.mapNotNull { moodByDate[it]?.score }
            val withoutWalk = window.filterNot { onDays.contains(it) }
                .mapNotNull { moodByDate[it]?.score }
            if (withWalk.size >= 2 && withoutWalk.isNotEmpty()) {
                val lift = ((withWalk.average() - withoutWalk.average()) /
                    withoutWalk.average() * 100).roundToInt()
                if (lift > 0) {
                    insights += Insight(
                        emoji = "\uD83D\uDEB6",
                        title = "Move earlier in the day",
                        body = "Walks before noon lifted your mood score by $lift% on average.",
                        tintColor = 0xFFD8F3E2
                    )
                }
            }
        }

        val stressedThisWeek = (0..6).count {
            moodByDate[today.minusDays(it.toLong())] == Mood.STRESSED
        }
        if (stressedThisWeek >= 2) {
            insights += Insight(
                emoji = "\uD83E\uDEC1",
                title = "Try breathing on busy days",
                body = "You logged 'Stressed' ${spell(stressedThisWeek)} times this week — " +
                    "a 5-minute session may help.",
                tintColor = 0xFFD6F7F0
            )
        }

        if (insights.size < 3) {
            val weakest = habits.minByOrNull { habit ->
                window.count { day ->
                    logs.any {
                        it.habitId == habit.id && it.date == DateUtils.iso(day) &&
                            it.progress >= habit.target
                    }
                }
            }
            if (weakest != null) {
                insights += Insight(
                    emoji = weakest.emoji,
                    title = "${weakest.name} needs attention",
                    body = "This is the habit you have missed most over the last two weeks. " +
                        "Try shrinking the target for a few days.",
                    tintColor = 0xFFFDF0D8
                )
            }
        }

        if (insights.isEmpty()) {
            insights += Insight(
                emoji = "\u2728",
                title = "Log a few more days",
                body = "Bloom Bot needs about a week of moods and habits before patterns " +
                    "start to show up.",
                tintColor = 0xFFEDE9FE
            )
        }

        return insights.take(3)
    }

    private fun habitNamed(habits: List<HabitEntity>, contains: String): HabitEntity? =
        habits.firstOrNull { it.name.contains(contains, ignoreCase = true) }

    private fun daysCompleted(
        habit: HabitEntity,
        logs: List<HabitLogEntity>,
        window: List<LocalDate>
    ): Set<LocalDate> = window.filter { day ->
        logs.any {
            it.habitId == habit.id && it.date == DateUtils.iso(day) && it.progress >= habit.target
        }
    }.toSet()

    private fun List<Int>.averageOrZero(): Double = if (isEmpty()) 0.0 else average()

    private fun spell(value: Int): String = when (value) {
        2 -> "two"
        3 -> "three"
        4 -> "four"
        5 -> "five"
        6 -> "six"
        7 -> "seven"
        else -> value.toString()
    }
}
