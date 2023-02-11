package ru.fi.englishtrainer.screens

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import ru.fi.englishtrainer.viewModel.TrainerViewModelFactory

lateinit var targetWord : String
lateinit var targetTranslate : String
lateinit var selectedTranslate : String
var targetIndex : Int = 0
lateinit var targetColor : MutableState<Color>
lateinit var list: List<EnglishWord>


@Composable
fun TrainerScreen(navHostController: NavHostController){


    val context = LocalContext.current
    val tViewModel: TrainerViewModel =
        viewModel(factory = TrainerViewModelFactory(context.applicationContext as Application))

    list = tViewModel.getEnglishWord()
    targetColor = remember{ mutableStateOf(Color.White) }
    targetWord = list[targetIndex].word
    targetTranslate = list[targetIndex].translateWord

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        ListEnglish(listEnglish = list)
        ListTranslate(listTranslate = list, tViewModel)
    }

}

@Composable
fun ListEnglish(listEnglish: List<EnglishWord>){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentPadding = PaddingValues(8.dp)
    ){
        items(items = listEnglish){ item ->
            ListItem(item)
        }
    }

}

@Composable
fun ListItem(item: EnglishWord){

    if(item.word == targetWord){
        targetColor.value = Color.Yellow
    }else if(item.word != targetWord){
        targetColor.value = Color.White
    }

    val color = if(item.answered && item.correctly)
        Color.Green
    else if (item.answered && !item.correctly)
        Color.Red
    else{
        Color.White
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(5.dp)
            .border(
                border = BorderStroke(5.dp, color = targetColor.value),
                shape = RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp,
        backgroundColor = color
    ) {
        Column(Modifier
            .fillMaxSize()
            .padding(start = 10.dp), verticalArrangement = Arrangement.Center ){
            Text(text = item.word)
        }


    }
}

@Composable
fun ListItemTranslate(item: EnglishWord, onItemClick:() -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(5.dp)
            .clickable(onClick = { onItemClick() }),
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp
    ) {
        Column(Modifier
            .fillMaxSize()
            .padding(start = 10.dp), verticalArrangement = Arrangement.Center ){
            Text(text = item.translateWord)
        }


    }
}


@Composable
fun ListTranslate(listTranslate: List<EnglishWord>, viewModel : TrainerViewModel){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(8.dp)
    ){
        items(items = listTranslate){ item ->
            ListItemTranslate(item,
                onItemClick = {
                    selectedTranslate = item.translateWord

                    list = if(selectedTranslate == targetTranslate){
                        viewModel.correctAnswer(listTranslate)
                    } else{
                        viewModel.wrongAnswer(listTranslate)
                    }
                    ++targetIndex
                    targetWord = listTranslate[targetIndex].word
                    targetTranslate = listTranslate[targetIndex].translateWord

                })
        }
    }
}

