package com.mindbloom.app.data.seed

import com.mindbloom.app.data.Mood
import com.mindbloom.app.data.local.HabitEntity
import com.mindbloom.app.data.local.HabitLogEntity
import com.mindbloom.app.data.local.JournalEntity
import com.mindbloom.app.data.local.MindBloomDatabase
import com.mindbloom.app.data.local.MoodEntity
import com.mindbloom.app.data.local.UserEntity
import com.mindbloom.app.data.prefs.AppPreferences
import com.mindbloom.app.util.DateUtils
import com.mindbloom.app.util.Security
import java.time.LocalDate

/**
 * Populates the database the first time the app runs so every screen opens
 * with the same content as the design mock-ups: six habits, a 7 day current
 * streak against a 14 day best, a month of moods and two journal entries.
 *
 * Everything written here is ordinary data — the user can edit or delete all
 * of it, and once seeded the app never overwrites it again.
 */
object DemoDataSeeder {

    const val DEMO_EMAIL = "punara@email.com"
    const val DEMO_PASSWORD = "password123"

    suspend fun seedIfNeeded(db: MindBloomDatabase, prefs: AppPreferences) {
        if (prefs.demoDataSeeded) return

        seedUser(db)
        val habitIds = seedHabits(db)
        seedHabitLogs(db, habitIds)
        seedMoods(db)
        seedJournal(db)

        prefs.demoDataSeeded = true
    }

    private suspend fun seedUser(db: MindBloomDatabase) {
        if (db.userDao().countByEmail(DEMO_EMAIL) > 0) return
        db.userDao().insert(
            UserEntity(
                email = DEMO_EMAIL,
                name = "Punara Seniduni",
                passwordHash = Security.hash(DEMO_PASSWORD),
                wellnessGoal = "Reduce Stress",
                level = 5,
                experience = 1200
            )
        )
    }

    /** Returns the generated ids keyed by habit name. */
    private suspend fun seedHabits(db: MindBloomDatabase): Map<String, Long> {
        if (db.habitDao().count() > 0) return emptyMap()
        val habits = listOf(
            HabitEntity(
                name = "Drink Water", subtitle = "8 glasses a day", emoji = "\uD83D\uDCA7",
                target = 8, reminderTime = "09:00 AM",
                accentColor = 0xFF3B82F6, iconBackground = 0xFFDCEAFE, sortOrder = 0
            ),
            HabitEntity(
                name = "Meditation", subtitle = "10 minutes daily", emoji = "\uD83E\uDDD8",
                target = 1, reminderTime = "07:00 AM",
                accentColor = 0xFF6B4EE6, iconBackground = 0xFFEDE9FE, sortOrder = 1
            ),
            HabitEntity(
                name = "Reading", subtitle = "20 pages a day", emoji = "\uD83D\uDCD6",
                target = 20, reminderTime = "08:30 PM",
                accentColor = 0xFFF59E0B, iconBackground = 0xFFFDF3D5, sortOrder = 2
            ),
            HabitEntity(
                name = "Walking", subtitle = "6,000 steps", emoji = "\uD83D\uDEB6",
                target = 6000, reminderTime = "05:30 PM",
                accentColor = 0xFF22B85C, iconBackground = 0xFFD8F3E2, sortOrder = 3
            ),
            HabitEntity(
                name = "Exercise", subtitle = "30 minutes", emoji = "\uD83D\uDCAA",
                target = 30, reminderTime = "06:30 AM",
                accentColor = 0xFFEF4444, iconBackground = 0xFFFCE1E4, sortOrder = 4
            ),
            HabitEntity(
                name = "Sleep Early", subtitle = "Before 11:00 PM", emoji = "\uD83D\uDE34",
                target = 1, reminderTime = "10:30 PM",
                accentColor = 0xFF22B85C, iconBackground = 0xFFD6F7F0, sortOrder = 5
            )
        )
        val ids = mutableMapOf<String, Long>()
        habits.forEach { ids[it.name] = db.habitDao().insert(it) }
        return ids
    }

    /**
     * Streaks are never hard-coded — they fall out of the logs written here.
     * Days back from today: 1 = yesterday.
     *   Drink Water  complete 1..7   → 7 day streak
     *   Meditation   complete 0..11  → 12 day streak
     *   Reading      complete 1..5   → 5 day streak
     *   Walking      complete 1..9   → 9 day streak
     *   Exercise     complete 1..3   → 3 day streak
     *   Sleep Early  complete 0..6   → 7 day streak
     * Day 7 falls below the 60% overall bar, which caps the dashboard's
     * current streak at 7, while days 8..21 form the 14 day personal best.
     */
    private suspend fun seedHabitLogs(db: MindBloomDatabase, ids: Map<String, Long>) {
        if (ids.isEmpty()) return
        val today = DateUtils.today()
        val logs = mutableListOf<HabitLogEntity>()

        fun log(habit: String, daysBack: Int, progress: Int) {
            val id = ids[habit] ?: return
            logs += HabitLogEntity(
                habitId = id,
                date = DateUtils.iso(today.minusDays(daysBack.toLong())),
                progress = progress
            )
        }

        // Today — exactly the partial state drawn in the mock-ups.
        log("Drink Water", 0, 6)
        log("Meditation", 0, 1)
        log("Reading", 0, 14)
        log("Walking", 0, 4200)
        log("Exercise", 0, 0)
        log("Sleep Early", 0, 1)

        val completeDays = mapOf(
            "Drink Water" to (1..7) + (9..21),
            "Meditation" to (1..11) + (13..21),
            "Reading" to (1..5) + (8..21),
            "Walking" to (1..9) + (11..21),
            "Exercise" to (1..3) + (8..21),
            "Sleep Early" to (1..6) + (8..21)
        )
        val fullTargets = mapOf(
            "Drink Water" to 8, "Meditation" to 1, "Reading" to 20,
            "Walking" to 6000, "Exercise" to 30, "Sleep Early" to 1
        )

        completeDays.forEach { (habit, days) ->
            days.forEach { day -> log(habit, day, fullTargets.getValue(habit)) }
        }

        // Day 7 keeps only the three habits whose streaks must reach past it,
        // which drops that day below the overall bar and ends the run at 7.
        listOf("Reading", "Exercise", "Sleep Early").forEach { habit ->
            logs.removeAll { it.habitId == ids[habit] && it.date == DateUtils.iso(today.minusDays(7)) }
        }

        db.habitLogDao().insertAll(logs)
    }

    /** A month of moods, with the current week matching the dashboard chart. */
    private suspend fun seedMoods(db: MindBloomDatabase) {
        if (db.moodDao().count() > 0) return
        val today = DateUtils.today()
        val entries = mutableListOf<MoodEntity>()

        val thisWeek = listOf(
            Mood.SAD, Mood.NEUTRAL, Mood.CALM, Mood.NEUTRAL,
            Mood.CALM, Mood.HAPPY, Mood.CALM
        )
        DateUtils.currentWeek(today).forEachIndexed { index, day ->
            if (day.isAfter(today)) return@forEachIndexed
            entries += MoodEntity(
                date = DateUtils.iso(day),
                mood = thisWeek[index].label,
                note = noteFor(thisWeek[index])
            )
        }

        val earlier = listOf(
            Mood.NEUTRAL, Mood.CALM, Mood.CALM, Mood.STRESSED, Mood.NEUTRAL,
            Mood.HAPPY, Mood.CALM, Mood.CALM, Mood.NEUTRAL, Mood.SAD,
            Mood.NEUTRAL, Mood.CALM, Mood.HAPPY, Mood.CALM, Mood.NEUTRAL,
            Mood.STRESSED, Mood.CALM, Mood.HAPPY, Mood.CALM, Mood.NEUTRAL,
            Mood.CALM, Mood.CALM
        )
        val weekStart = DateUtils.startOfWeek(today)
        earlier.forEachIndexed { index, mood ->
            val day: LocalDate = weekStart.minusDays((index + 1).toLong())
            entries += MoodEntity(date = DateUtils.iso(day), mood = mood.label, note = noteFor(mood))
        }

        db.moodDao().upsertAll(entries)
    }

    private fun noteFor(mood: Mood): String = when (mood) {
        Mood.HAPPY -> "Good energy all day."
        Mood.CALM -> "Steady and settled."
        Mood.NEUTRAL -> "An ordinary day."
        Mood.STRESSED -> "Deadlines piling up."
        Mood.SAD -> "Low on sleep and motivation."
    }

    private suspend fun seedJournal(db: MindBloomDatabase) {
        if (db.journalDao().count() > 0) return
        val today = DateUtils.today()
        db.journalDao().insertAll(
            listOf(
                JournalEntity(
                    date = DateUtils.iso(today),
                    content = "Today felt lighter than yesterday. I finished my morning walk " +
                        "before class and actually stuck to my water goal. The evening got a " +
                        "bit stressful before the deadline, but ten minutes of breathing " +
                        "helped more than I expected.",
                    mood = Mood.CALM.label,
                    photos = "leaf,city"
                ),
                JournalEntity(
                    date = DateUtils.iso(today.minusDays(1)),
                    content = "Long study day, but I kept my streak going and finished the " +
                        "chapter I had been putting off all week.",
                    mood = Mood.HAPPY.label
                )
            )
        )
    }
}
