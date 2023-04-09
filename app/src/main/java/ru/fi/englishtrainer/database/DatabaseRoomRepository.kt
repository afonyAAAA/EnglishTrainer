package ru.fi.englishtrainer.database

import androidx.lifecycle.LiveData
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.models.History

interface DatabaseRoomRepository {

    val readAllHistory : LiveData<List<History>>

    suspend fun create(history: History)

    suspend fun delete(history: List<History>)

}