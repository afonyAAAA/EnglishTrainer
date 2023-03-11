package ru.fi.englishtrainer.database

import com.google.android.gms.tasks.OnSuccessListener
import ru.fi.englishtrainer.models.EnglishWord

interface DatabaseRepository {

    suspend fun getEnglishWord() : List<EnglishWord>

    fun addEnglishWord()

}