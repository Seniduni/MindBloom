package com.mindbloom.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A habit the user is tracking, e.g. "Drink Water — 8 glasses a day".
 * Colours are stored as ARGB longs so each row keeps the exact accent shown
 * in the design (blue for water, purple for meditation, and so on).
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val subtitle: String,
    val emoji: String,
    val target: Int,
    val frequency: String = "Daily",
    val reminderTime: String = "09:00 AM",
    val accentColor: Long,
    val iconBackground: Long,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/** One row per habit per day holding how much of the target was done. */
@Entity(
    tableName = "habit_logs",
    indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    /** ISO date, yyyy-MM-dd. */
    val date: String,
    val progress: Int
)

/** One mood check-in per calendar day; the date doubles as the primary key. */
@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey val date: String,
    val mood: String,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

/** A journal entry. Photos are stored as a comma separated list of ids. */
@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val content: String,
    val mood: String,
    val photos: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/** Local account created through the Register screen. */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val passwordHash: String,
    val wellnessGoal: String,
    val level: Int = 5,
    val experience: Int = 1200,
    val createdAt: Long = System.currentTimeMillis()
)
