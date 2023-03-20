package ru.fi.englishtrainer.database.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.models.History

@Dao
interface TrainerRoomDao {

    @Query("SELECT * FROM history_table")
    fun getAllHistory() : LiveData<List<History>>

    @Insert
    suspend fun addHistory(history: History)

    @Delete
    suspend fun deleteAllHistory(history: List<History>)
}