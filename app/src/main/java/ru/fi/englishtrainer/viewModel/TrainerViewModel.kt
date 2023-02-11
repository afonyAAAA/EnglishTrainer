package ru.fi.englishtrainer.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.screens.targetIndex

class TrainerViewModel(application : Application): AndroidViewModel(application) {

    private val englishWord: List<EnglishWord> = listOf(EnglishWord("Red", "Красный"), EnglishWord("Green", "Зелёный"), EnglishWord("Dick", "хуй"))

    fun getEnglishWord(): List<EnglishWord>{
        return englishWord
    }

    fun wrongAnswer(list: List<EnglishWord>): List<EnglishWord>{
        list[targetIndex].answered = true
        return list
    }
    fun correctAnswer(list: List<EnglishWord>): List<EnglishWord>{
        list[targetIndex].correctly = true
        list[targetIndex].answered = true
        return list
    }
}

class TrainerViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainerViewModel::class.java)) {
            return TrainerViewModel(application = application) as T
        }
        throw IllegalStateException("")
    }
}