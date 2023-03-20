package ru.fi.englishtrainer.viewModel

import android.app.Application
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.fi.englishtrainer.database.room.AppRoomDatabase
import ru.fi.englishtrainer.database.room.repository.RoomRepository
import ru.fi.englishtrainer.utils.Constants
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.models.History
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TrainerViewModel(application : Application): AndroidViewModel(application) {

    val context = application

    lateinit var targetWord : String
    lateinit var targetTranslate : String
    lateinit var selectedTranslate : String
    lateinit var shuffledListTranslate : MutableList<EnglishWord>
    lateinit var openEndDialog : MutableState<Boolean>
    lateinit var targetColor : MutableState<Color>
    var startTrainer : MutableState<Boolean> = mutableStateOf(false)
    var resultTrainer : MutableState<Int> = mutableStateOf(0)
    var englishWords : MutableState<MutableList<EnglishWord>> = mutableStateOf(mutableListOf())
    var shuffledListEnglish : MutableList<EnglishWord> = mutableListOf()
    var targetIndex : Int = 0

    var selectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now())

    suspend fun getCollectionEnglishWord(): List<EnglishWord> {
        return Constants.FIREBASE_REPOSITORY.getEnglishWord()
    }

    fun initRoomDatabase(){
        val dao = AppRoomDatabase.getInstance(context = context).getRoomDao()
        Constants.ROOM_REPOSITORY = RoomRepository(dao)
    }

    fun addword(){
        Constants.FIREBASE_REPOSITORY.addEnglishWord()
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
        startTrainer.value = false
        targetIndex = 0
        englishWords = mutableStateOf(mutableListOf())
        shuffledListTranslate.clear()
        shuffledListEnglish.clear()
    }

    suspend fun checkResult(list: MutableList<EnglishWord>) : Int{
        var count = 0
        var countCorrectAnswer = 0f
        return suspendCoroutine{ continuation ->
            list.forEach {
                if(it.correctly)
                    ++countCorrectAnswer
                if(count == list.size - 1)
                    continuation.resume(((countCorrectAnswer / list.size.toFloat()) * 100.0f).toInt())

                ++count
            }
        }
    }

    fun isEditTagItemFullyVisible(lazyListState: LazyListState, editTagItemIndex: Int): Boolean {
        with(lazyListState.layoutInfo) {
            val editingTagItemVisibleInfo = visibleItemsInfo.find { it.index == editTagItemIndex }
            return if (editingTagItemVisibleInfo == null) {
                false
            } else {
                viewportEndOffset - editingTagItemVisibleInfo.offset >= editingTagItemVisibleInfo.size
            }
        }
    }

    fun addHistory(history: History){
        viewModelScope.launch(Dispatchers.Main) {
            Constants.ROOM_REPOSITORY.create(history){}
        }
    }

    fun deleteAllHistory(history: List<History>){
        viewModelScope.launch(Dispatchers.Main){
            Constants.ROOM_REPOSITORY.delete(history){}
        }
    }

    fun readAllHistory() = Constants.ROOM_REPOSITORY.readAllHistory



}

class TrainerViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainerViewModel::class.java)) {
            return TrainerViewModel(application = application) as T
        }
        throw IllegalStateException("")
    }
}