package com.mindbloom.app.data

import com.mindbloom.app.data.local.HabitEntity

/**
 * The five moods offered on the Mood Tracker, ordered best to worst.
 * [score] drives every chart and the wellness score.
 */
enum class Mood(val label: String, val emoji: String, val score: Int) {
    HAPPY("Happy", "\uD83D\uDE04", 5),
    CALM("Calm", "\uD83D\uDE0A", 4),
    NEUTRAL("Neutral", "\uD83D\uDE10", 3),
    STRESSED("Stressed", "\uD83D\uDE30", 2),
    SAD("Sad", "\uD83D\uDE14", 1);

    companion object {
        fun fromLabel(label: String?): Mood? =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) }

        /** Axis labels used on the mood charts, highest score first. */
        val axisEmojis = listOf(HAPPY.emoji, CALM.emoji, NEUTRAL.emoji, SAD.emoji)
    }
}

/**
 * How much one tap adds. Habits with large targets (6,000 steps) advance in
 * tenths so the control stays usable; small targets advance one at a time.
 */
val HabitEntity.step: Int
    get() = if (target >= 100) (target / 10).coerceAtLeast(1) else 1

/** A habit joined with today's progress and its current streak. */
data class HabitWithProgress(
    val habit: HabitEntity,
    val progress: Int,
    val streak: Int
) {
    val target: Int get() = habit.target
    val isComplete: Boolean get() = progress >= habit.target
    val fraction: Float
        get() = if (habit.target <= 0) 0f else (progress.toFloat() / habit.target).coerceIn(0f, 1f)
}

/** How a single day scored for habit completion, used by the statistics grid. */
enum class DayCompletion { NONE, PARTIAL, MOSTLY, COMPLETE, MISSED }

/** One rule-based suggestion shown on the AI Insights screen. */
data class Insight(
    val emoji: String,
    val title: String,
    val body: String,
    val tintColor: Long
)

/** Aggregated numbers behind the Statistics screen. */
data class StatsSummary(
    val moodScores: List<Float?>,
    val energyScores: List<Float?>,
    val axisLabels: List<String>,
    val completed: Int,
    val missed: Int,
    val partial: Int,
    val averagePercent: Int,
    val monthDays: List<DayCompletion>,
    val monthLabel: String
)

/** A guided audio session on the Meditation screen. */
data class MeditationSession(
    val id: Int,
    val title: String,
    val category: String,
    val minutes: Int,
    val emoji: String,
    val gradientStart: Long,
    val gradientEnd: Long
)

/** An unlocked badge on the Profile screen. */
data class Achievement(
    val emoji: String,
    val label: String,
    val backgroundColor: Long,
    val description: String
)
