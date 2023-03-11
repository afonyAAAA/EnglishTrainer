package ru.fi.englishtrainer.viewModel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.fi.englishtrainer.utils.Constants
import ru.fi.englishtrainer.models.EnglishWord
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TrainerViewModel(application : Application): AndroidViewModel(application) {

    lateinit var targetWord : String
    lateinit var targetTranslate : String
    lateinit var selectedTranslate : String
    lateinit var shuffledListTranslate : MutableList<EnglishWord>
    lateinit var notCorrect: MutableState<Boolean>
    lateinit var openEndDialog : MutableState<Boolean>
    lateinit var targetColor : MutableState<Color>
    var startGame : MutableState<Boolean> = mutableStateOf(false)
    var resultTrainer : MutableState<Float> = mutableStateOf(0.0f)
    var englishWords : MutableState<MutableList<EnglishWord>> = mutableStateOf(mutableListOf())
    var shuffledListEnglish : MutableList<EnglishWord> = mutableListOf()
    var targetIndex : Int = 0

    suspend fun getCollectionEnglishWord(): List<EnglishWord> {
        return Constants.REPOSITORY.getEnglishWord()
    }

    fun addword(){
        Constants.REPOSITORY.addEnglishWord()
    }

    fun wrongAnswer(list: MutableList<EnglishWord>): MutableList<EnglishWord>{
        list[targetIndex].answered = true
        list[targetIndex].correctly = false
        return list
    }

    fun shuffleList(noShuffledList: MutableState<MutableList<EnglishWord>>): MutableList<EnglishWord> {
        return noShuffledList.value.shuffled().toMutableList()
    }

    fun correctAnswer(list: MutableList<EnglishWord>): MutableList<EnglishWord>{
        list[targetIndex].correctly = true
        list[targetIndex].answered = true
        return list
    }

    fun targetWordChange(list: MutableList<EnglishWord>){
        targetWord = list[targetIndex].englishWord
        targetTranslate = list[targetIndex].translatedWord
    }

    fun deleteItemWrong(listTranslate : MutableList<EnglishWord>){
        lateinit var item : EnglishWord
        listTranslate.forEach {
            if(targetTranslate == it.translatedWord){
                item = it
            }
        }
        listTranslate.remove(item)
    }

    fun resumeTrainer(){

        targetIndex = 0

        englishWords.value.clear()
        shuffledListTranslate.clear()
        shuffledListEnglish.clear()

        englishWords = mutableStateOf(mutableListOf())

    }

    suspend fun checkResult(list: MutableList<EnglishWord>) : Float{
        var count = 0
        var countCorrectAnswer = 0f
        return suspendCoroutine{ continuation ->
            list.forEach {
                if(it.correctly)
                    ++countCorrectAnswer
                if(count == list.size - 1)
                    continuation.resume((countCorrectAnswer / list.size.toFloat()) * 100.0f)

                ++count
            }
        }
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