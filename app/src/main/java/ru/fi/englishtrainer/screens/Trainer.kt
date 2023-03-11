package ru.fi.englishtrainer.screens

import android.annotation.SuppressLint
import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.navigation.NavRoutes
import ru.fi.englishtrainer.utils.Constants
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import ru.fi.englishtrainer.viewModel.TrainerViewModelFactory


@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@Composable
fun TrainerScreen(navHostController: NavHostController){

    val context = LocalContext.current
    val tViewModel: TrainerViewModel =
        viewModel(factory = TrainerViewModelFactory(context.applicationContext as Application))

    tViewModel.openEndDialog = remember { mutableStateOf(false) }
    tViewModel.shuffledListTranslate = remember { mutableStateListOf() }
    tViewModel.notCorrect = remember { mutableStateOf(false) }
    tViewModel.targetColor = remember{ mutableStateOf(Color.White) }

    //Initial lists for trainer (english words and translate words)
    LaunchedEffect(tViewModel.englishWords){
        tViewModel.englishWords.value = tViewModel.getCollectionEnglishWord().toMutableList()

        //Shuffle lists
        tViewModel.shuffledListEnglish.addAll(tViewModel.shuffleList(tViewModel.englishWords))
        tViewModel.shuffledListTranslate.addAll(tViewModel.shuffleList(tViewModel.englishWords))

        //tViewModel.addword()

        tViewModel.startGame.value = true
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if(tViewModel.startGame.value){

            tViewModel.targetWordChange(tViewModel.shuffledListEnglish)

            ListEnglish(listEnglish = tViewModel.shuffledListEnglish, tViewModel)

            ListTranslate(listTranslate = tViewModel.shuffledListTranslate, tViewModel)

            if(tViewModel.openEndDialog.value) {

                var resultIsNotNull by remember { mutableStateOf(false) }

                LaunchedEffect(tViewModel.resultTrainer) {
                    tViewModel.resultTrainer.value = tViewModel.checkResult(tViewModel.shuffledListEnglish)
                    resultIsNotNull = true
                }

                if (resultIsNotNull) {
                    EndDialogWindow(
                        tViewModel.openEndDialog,
                        navHostController,
                        tViewModel
                    )
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
fun ListEnglish(listEnglish: MutableList<EnglishWord>, viewModel: TrainerViewModel){


//    lateinit var targetIndex : MutableState<Int>
//
//    val lazyListState: LazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentPadding = PaddingValues(8.dp)
       // state = lazyListState
    ){
        itemsIndexed(items = listEnglish){ index,item ->
//            if(item.englishWord == viewModel.targetWord){
//               targetIndex = remember { mutableStateOf(index) }
//            }
            ListItem(item, viewModel)
        }
    }

//    fun isEditTagItemFullyVisible(lazyListState: LazyListState, editTagItemIndex: Int): Boolean {
//        with(lazyListState.layoutInfo) {
//            val editingTagItemVisibleInfo = visibleItemsInfo.find { it.index == editTagItemIndex }
//            return if (editingTagItemVisibleInfo == null) {
//                false
//            } else {
//                viewportEndOffset - editingTagItemVisibleInfo.offset >= editingTagItemVisibleInfo.size
//            }
//        }
//    }
//
//    LaunchedEffect(targetIndex.value){
//        if (!isEditTagItemFullyVisible(lazyListState, targetIndex.value)) {
//            lazyListState.scrollToItem(targetIndex.value)
//        }
//    }

}

@Composable
fun ListItem(item: EnglishWord, viewModel : TrainerViewModel){

    if(item.englishWord == viewModel.targetWord){
        viewModel.targetColor.value = Color.Yellow
    }else if(item.englishWord != viewModel.targetWord){
        viewModel.targetColor.value = Color.White
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
fun ListTranslate(listTranslate: MutableList<EnglishWord>, viewModel: TrainerViewModel){

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(8.dp)
    ){
        items(items = listTranslate){item ->
            ListItemTranslate(item,
                onItemClick = {
                    viewModel.selectedTranslate = item.translatedWord

                        if(viewModel.selectedTranslate == viewModel.targetTranslate){

                            viewModel.shuffledListEnglish = viewModel.correctAnswer(viewModel.shuffledListEnglish)

                            listTranslate.remove(item)


                        }else{
                            Toast.makeText(Constants.context, "Ответ не верный, правильный перевод: ${viewModel.targetTranslate}", Toast.LENGTH_SHORT)
                                .show()

                            viewModel.shuffledListEnglish = viewModel.wrongAnswer(viewModel.shuffledListEnglish)

                            viewModel.deleteItemWrong(listTranslate)
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
        text = { Text(text = "Игра окончена. Вы ответили правильно на ${viewModel.resultTrainer.value.toInt()}%.")},
        buttons = {
            Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.SpaceBetween){

                    Button(
                        onClick = {
                            openDialog.value = false
                            viewModel.startGame.value = false
                            viewModel.resumeTrainer()
                        }
                    ) {
                        Text(text = "Продолжить")
                    }



                    Button(
                        onClick = {
                            openDialog.value = false
                            navHostController.navigate(NavRoutes.Start.route)
                        }
                    ) {
                        Text(text = "Закончить")
                    }

                }
            }

        }
    )
}

