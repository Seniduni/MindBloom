package com.mindbloom.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        HabitEntity::class,
        HabitLogEntity::class,
        MoodEntity::class,
        JournalEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MindBloomDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun moodDao(): MoodDao
    abstract fun journalDao(): JournalDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MindBloomDatabase? = null

        fun getInstance(context: Context): MindBloomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MindBloomDatabase::class.java,
                    "mindbloom.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
