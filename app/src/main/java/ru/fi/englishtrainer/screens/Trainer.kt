package ru.fi.englishtrainer.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.models.History
import ru.fi.englishtrainer.navigation.NavRoutes
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import java.time.LocalDateTime

@Composable
fun TrainerScreen(navHostController: NavHostController, viewModel: TrainerViewModel){

    var lazyListState: LazyListState = rememberLazyListState()

    LaunchedEffect(viewModel.englishWords){
        viewModel.englishWords.value = viewModel.getCollectionEnglishWord().toMutableList()

        //Shuffle lists
        if(viewModel.shuffledListEnglish.isEmpty() && viewModel.shuffledListTranslate.isEmpty()){
            viewModel.shuffledListEnglish.addAll(viewModel.shuffleList(viewModel.englishWords))
            viewModel.shuffledListTranslate.addAll(viewModel.shuffleList(viewModel.englishWords))
        }

        viewModel.startTrainer.value = true
        lazyListState.scrollToItem(0)
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if(viewModel.startTrainer.value){

            viewModel.targetWordChange(viewModel.shuffledListEnglish)

            ListEnglish(listEnglish = viewModel.shuffledListEnglish, viewModel, lazyListState)

            ListTranslate(listTranslate = viewModel.shuffledListTranslate, viewModel, lazyListState)

            if(viewModel.openEndDialog.value) {

                var resultIsNotNull by remember { mutableStateOf(false) }

                LaunchedEffect(viewModel.resultTrainer) {
                    viewModel.resultTrainer.value = viewModel.checkResult(viewModel.shuffledListEnglish)
                    resultIsNotNull = true
                }

                if (resultIsNotNull) {
                    EndDialogWindow(
                        viewModel.openEndDialog,
                        navHostController,
                        viewModel
                    )

                    viewModel.addHistory(
                        history = History(
                            date = LocalDateTime.now(),
                            percentCorrect = viewModel.resultTrainer.value,
                            listResult = viewModel.shuffledListEnglish))
                }
            }
        }else{
            Column(Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center)
            {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ListEnglish(listEnglish: MutableList<EnglishWord>, viewModel: TrainerViewModel, lazyListState: LazyListState){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentPadding = PaddingValues(8.dp),
        state = lazyListState
    ){
        itemsIndexed(items = listEnglish){ index,item ->
            ListItem(item, viewModel)
        }
    }
}

@Composable
fun ListItem(item: EnglishWord, viewModel : TrainerViewModel){


    if(item.englishWord == viewModel.targetWord)
        viewModel.targetColor.value = Color.Yellow
    else if(item.englishWord != viewModel.targetWord && item.correctly)
        viewModel.targetColor.value = Color.Green
    else if(item.englishWord != viewModel.targetWord && !item.correctly && item.answered)
        viewModel.targetColor.value = Color.Red
    else
        viewModel.targetColor.value = Color.White

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
                border = BorderStroke(5.dp, color = viewModel.targetColor.value),
                shape = RoundedCornerShape(5.dp)),
        elevation = 3.dp,
        backgroundColor = color
    ) {
        Column(Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center ){
            Text(text = item.englishWord)
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
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center ){
            Text(text = item.translatedWord)
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ListTranslate(listTranslate: MutableList<EnglishWord>, viewModel: TrainerViewModel, lazyListState: LazyListState){

    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(8.dp)
    ){
        items(items = listTranslate){item ->
            ListItemTranslate(item,
                onItemClick = {
                    if(item.translatedWord == viewModel.targetTranslate){

                        viewModel.shuffledListEnglish = viewModel.correctAnswer(viewModel.shuffledListEnglish)

                        listTranslate.remove(item)

                    }else{
                        Toast.makeText(
                            viewModel.context,
                            "Ответ не верный, правильный перевод: ${viewModel.targetTranslate}",
                            Toast.LENGTH_SHORT).show()

                        viewModel.shuffledListEnglish = viewModel.wrongAnswer(viewModel.shuffledListEnglish)

                        viewModel.deleteItemWrong(listTranslate)
                    }

                    scope.launch {
                        if(viewModel.isEditTagItemFullyVisible(lazyListState, viewModel.targetIndex)){
                            lazyListState.animateScrollToItem(viewModel.targetIndex)
                        }
                    }

                    if(viewModel.shuffledListEnglish.size - 1 == viewModel.targetIndex){
                        viewModel.openEndDialog.value = true
                    }else{
                        ++viewModel.targetIndex
                        viewModel.targetWordChange(viewModel.shuffledListEnglish)
                    }

                })
        }
    }
}

@Composable
fun EndDialogWindow(
    openDialog : MutableState<Boolean>,
    navHostController: NavHostController,
    viewModel: TrainerViewModel
){
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = { Text(text = "Конец")},
        text = { Text(text = "Игра окончена. Вы ответили правильно на ${viewModel.resultTrainer.value}%.")},
        buttons = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                Button(
                    onClick = {
                        openDialog.value = false
                        viewModel.resumeTrainer()
                    }
                ) {
                    Text(text = "Продолжить")
                }

                Button(
                    onClick = {
                        openDialog.value = false
                        viewModel.resumeTrainer()
                        navHostController.navigate(NavRoutes.Start.route)
                    }
                ) {
                    Text(text = "Закончить")
                }

            }
        }
    )
}

