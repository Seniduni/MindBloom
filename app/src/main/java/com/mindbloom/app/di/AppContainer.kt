package com.mindbloom.app.di

import android.content.Context
import com.mindbloom.app.data.local.MindBloomDatabase
import com.mindbloom.app.data.prefs.AppPreferences
import com.mindbloom.app.data.repository.HabitRepository
import com.mindbloom.app.data.repository.JournalRepository
import com.mindbloom.app.data.repository.MoodRepository
import com.mindbloom.app.data.repository.UserRepository

/**
 * A hand-rolled service locator. The project deliberately avoids Hilt so the
 * dependency graph stays readable and the Gradle setup stays minimal.
 */
class AppContainer(context: Context) {

    /** Application context, used by the reminder scheduler. */
    val appContext: Context = context.applicationContext

    val database: MindBloomDatabase = MindBloomDatabase.getInstance(context)
    val preferences: AppPreferences = AppPreferences(context)

    val habitRepository: HabitRepository by lazy {
        HabitRepository(database.habitDao(), database.habitLogDao())
    }
    val moodRepository: MoodRepository by lazy { MoodRepository(database.moodDao()) }
    val journalRepository: JournalRepository by lazy { JournalRepository(database.journalDao()) }
    val userRepository: UserRepository by lazy { UserRepository(database.userDao(), preferences) }

    companion object {
        @Volatile
        private var instance: AppContainer? = null

        fun get(context: Context): AppContainer =
            instance ?: synchronized(this) {
                instance ?: AppContainer(context.applicationContext).also { instance = it }
            }
    }
}
