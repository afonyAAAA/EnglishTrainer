package ru.fi.englishtrainer.database

import ru.fi.englishtrainer.models.EnglishWord

interface DatabaseFirebaseRepository {

    suspend fun getEnglishWord() : List<EnglishWord>

}