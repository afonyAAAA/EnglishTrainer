package ru.fi.englishtrainer.screens

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import ru.fi.englishtrainer.R
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.models.History
import ru.fi.englishtrainer.navigation.NavRoutes
import ru.fi.englishtrainer.ui.theme.Shapes
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private var itemResult : History? = null

@Composable
fun HistoryScreen(navHostController: NavHostController, viewModel: TrainerViewModel){

    val history = viewModel.readAllHistory().observeAsState().value

    val openListResult = remember{ mutableStateOf(false) }

    val valuePercent = remember { mutableStateOf("") }

    if(!openListResult.value)
        history?.let { historyList ->
            if (history.isEmpty()){
                ButtonBack(navHostController, NavRoutes.Start)
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(text = "Здесь пока пусто...")
                }
            }else{
                Column{
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ButtonBack(navHostController, NavRoutes.Start)
                        if(viewModel.dateSelected.value) SelectedDate(viewModel)
                    }
                    Row(modifier = Modifier.fillMaxWidth()){
                        TextFieldChoosePercent(valuePercent)
                        DialogDateTimePicker(viewModel, viewModel.dateSelected)
                    }
                    ListHistory(listHistory = historyList.sortedByDescending { it.date }.toMutableList(), openListResult, valuePercent.value, viewModel)
                }
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    ButtonDeleteHistory(viewModel = viewModel, listHistory = history)
                }
            }
        }
    else{
        history?.let {
            itemResult?.let { itemHistory ->
                Column {
                    IconButton(onClick = {openListResult.value = false}) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                    ListResult(listResult = itemHistory.listResult)
                }
            }
        }
    }
}

@Composable
fun DeleteFilterDate(viewModel: TrainerViewModel, dateSelected: MutableState<Boolean>){
    IconButton(onClick = {
        viewModel.selectedDate = null
        dateSelected.value = false
    }, modifier = Modifier.padding(end = 10.dp)) {
        Icon(Icons.Filled.Close, contentDescription = "")
    }
}


@Composable
fun SelectedDate(viewModel: TrainerViewModel){
    val formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(viewModel.selectedDate!!.value)
    Row{
        Text(formattedDate,
            color = Color.White,
            modifier = Modifier.padding(top = 13.dp, end = 10.dp)
            .background(MaterialTheme.colors.primary, shape = Shapes.small)
        )
        DeleteFilterDate(viewModel = viewModel, dateSelected = viewModel.dateSelected)
    }

}

@Composable
fun DialogDateTimePicker(viewModel : TrainerViewModel, dateSelected : MutableState<Boolean>){

    val dialogStateDate = rememberMaterialDialogState()

    Button(onClick = {
        dialogStateDate.show()
        dateSelected.value = false
                     },
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {Text("Выбрать дату")}

    MaterialDialog(
        dialogState = dialogStateDate,
        buttons = {
            positiveButton(text = "ОК")
            negativeButton("Закрыть")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            locale = Locale("ru", "RU"),
            title = "Выберите дату",
        ){
            viewModel.selectedDate = mutableStateOf(it)
            dateSelected.value = true
        }
    }
}

@Composable
fun ButtonBack(navHostController: NavHostController, window : NavRoutes){
    IconButton(onClick = {navHostController.navigate(window.route)}, ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "")
    }
}

@Composable
fun TextFieldChoosePercent(valuePercent:MutableState<String>){
    val maxChar = 3
    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp)
            .height(50.dp)
            .width(100.dp)
            .background(Color.White),
        value = valuePercent.value,
        singleLine = true,
        onValueChange = { if(it.length <= maxChar) valuePercent.value = it},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { Icon(painter = painterResource(id = R.drawable.percent_black_24dp),
            contentDescription = null)}

    )
}

@Composable
fun ListHistory(
    listHistory: MutableList<History>,
    openList: MutableState<Boolean>,
    valuePercent: String,
    viewModel: TrainerViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
            items(items = viewModel.filterListHistory(valuePercent, viewModel.selectedDate?.value, listHistory)) {item ->
                ListItemHistory(item) {
                    itemResult = item
                    openList.value = true
                }
            }

        }
    }


@Composable
fun ListResult(listResult: List<EnglishWord>) {

    val openDialog = remember { mutableStateOf(false) }
    val translateWord = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = listResult) {item ->
            ListItemResult(item = item){
                translateWord.value = item.translatedWord
                openDialog.value = true
            }
        }
    }

    if(openDialog.value && translateWord.value.isNotEmpty()){
        TranslateDialogWindow(openDialog = openDialog, translateWord = translateWord.value)
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ListItemHistory(item: History, onItemClick:()->Unit) {

    val formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(item.date)
    val formattedTime = DateTimeFormatter.ofPattern("HH:mm").format(item.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(5.dp)
            .clickable(onClick = { onItemClick() }),
        shape = RoundedCornerShape(5.dp),
        elevation = 3.dp
    ) {
        Row(Modifier
            .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically)
        {
            Column(horizontalAlignment = Alignment.End) {
                Text(modifier = Modifier.padding(start = 75.dp), text = formattedDate)

                Text(modifier = Modifier.padding(start = 75.dp), text = formattedTime)
            }

            val textPaint = Paint().asFrameworkPaint().apply {
                strokeWidth = 75f
                isAntiAlias = true
                textSize = 40f
                textAlign = android.graphics.Paint.Align.CENTER
                color = android.graphics.Color.BLACK
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC)
            }

            Canvas(modifier = Modifier
                .size(size = 120.dp)
                .padding(start = 15.dp),
                onDraw = {
                    drawArc(
                        brush = Brush.horizontalGradient(colors =
                        when (item.percentCorrect) {
                            in 0..50 -> listOf(Color.Red, Color.Yellow)
                            in 50..99 -> listOf(Color.Yellow, Color.Green)
                            else -> listOf(Color.Green, Color.Green)
                        }),
                        startAngle = -90f,
                        sweepAngle = item.percentCorrect.toFloat() * 3.60f,
                        useCenter = true)
                    drawIntoCanvas {
                        it.nativeCanvas.drawText("${item.percentCorrect}%",
                            75f,
                            60.dp.toPx(),
                            textPaint)
                    } })
        }
    }
}


@Composable
fun ListItemResult(item: EnglishWord, onItemClick: () -> Unit){

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
            .clickable { onItemClick() },
        shape = Shapes.small,
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
fun ButtonDeleteHistory(viewModel: TrainerViewModel, listHistory: List<History>){
    OutlinedButton(onClick = {viewModel.deleteAllHistory(listHistory)}) {
        Text(stringResource(R.string.DeleteHistory))
    }
}

@Composable
fun TranslateDialogWindow(openDialog : MutableState<Boolean>, translateWord: String){
    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        text = { Text(text = "Перевод: $translateWord")},
        buttons = {
            Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(text = "ОК")
                }
            }
        }
    )
}