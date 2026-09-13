package com.mindbloom.app.data.repository

import com.mindbloom.app.data.Mood
import com.mindbloom.app.data.local.MoodDao
import com.mindbloom.app.data.local.MoodEntity
import com.mindbloom.app.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoodRepository(private val moodDao: MoodDao) {

    fun observeAll(): Flow<List<MoodEntity>> = moodDao.observeAll()

    fun observeForDate(date: String = DateUtils.todayIso()): Flow<MoodEntity?> =
        moodDao.observeForDate(date)

    /** Mood scores for Monday..Sunday of the current week; null where unlogged. */
    fun observeWeekScores(reference: LocalDate = DateUtils.today()): Flow<List<Float?>> =
        moodDao.observeAll().map { moods ->
            val byDate = moods.associateBy { it.date }
            DateUtils.currentWeek(reference).map { day ->
                byDate[DateUtils.iso(day)]?.let { Mood.fromLabel(it.mood)?.score?.toFloat() }
            }
        }

    /** Mood entries for a whole month, keyed by day of month. */
    fun observeMonth(month: YearMonth): Flow<Map<Int, Mood>> =
        moodDao.observeAll().map { moods ->
            moods.mapNotNull { entry ->
                val date = runCatching { DateUtils.parse(entry.date) }.getOrNull()
                    ?: return@mapNotNull null
                if (YearMonth.from(date) != month) return@mapNotNull null
                val mood = Mood.fromLabel(entry.mood) ?: return@mapNotNull null
                date.dayOfMonth to mood
            }.toMap()
        }

    suspend fun getForDate(date: String): MoodEntity? = moodDao.getForDate(date)

    suspend fun saveMood(date: String, mood: Mood, note: String) {
        moodDao.upsert(MoodEntity(date = date, mood = mood.label, note = note.trim()))
    }
}
