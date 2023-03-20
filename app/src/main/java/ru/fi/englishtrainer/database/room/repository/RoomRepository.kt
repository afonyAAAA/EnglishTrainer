package ru.fi.englishtrainer.database.room.repository

import androidx.lifecycle.LiveData
import ru.fi.englishtrainer.database.DatabaseRoomRepository
import ru.fi.englishtrainer.database.room.dao.TrainerRoomDao
import ru.fi.englishtrainer.models.History

class RoomRepository(private val trainerRoomDao: TrainerRoomDao) : DatabaseRoomRepository {

    override val readAllHistory: LiveData<List<History>>
        get() = trainerRoomDao.getAllHistory()

    override suspend fun create(history: History, onSuccess: () -> Unit) {
        trainerRoomDao.addHistory(history)
        onSuccess()
    }

    override suspend fun delete(history: List<History>, onSuccess: () -> Unit) {
        trainerRoomDao.deleteAllHistory(history)
        onSuccess()
    }


}