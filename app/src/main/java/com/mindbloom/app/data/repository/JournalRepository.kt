package com.mindbloom.app.data.repository

import com.mindbloom.app.data.local.JournalDao
import com.mindbloom.app.data.local.JournalEntity
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {

    fun observeAll(): Flow<List<JournalEntity>> = journalDao.observeAll()

    fun observeRecent(limit: Int = 5): Flow<List<JournalEntity>> = journalDao.observeRecent(limit)

    suspend fun getById(id: Long): JournalEntity? = journalDao.getById(id)

    suspend fun save(entry: JournalEntity): Long =
        if (entry.id == 0L) journalDao.insert(entry) else {
            journalDao.update(entry)
            entry.id
        }

    suspend fun delete(id: Long) = journalDao.deleteById(id)
}
