package ru.fi.englishtrainer.screens

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.widget.Space
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.checkerframework.checker.units.qual.Time
import ru.fi.englishtrainer.R
import ru.fi.englishtrainer.models.EnglishWord
import ru.fi.englishtrainer.models.History
import ru.fi.englishtrainer.ui.theme.Shapes
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(text = "Здесь пока пусто...")
                }
            }else{
                Column{
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextFieldChoosePercent(valuePercent = valuePercent)
                        SelectedDateAndTime(viewModel)
                    }
                    Row(modifier = Modifier.fillMaxWidth()){
                        DialogDateTimePicker(viewModel)
                    }
                    ListHistory(listHistory = historyList, openListResult, valuePercent.value, viewModel)
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
            itemResult?.let { itemHistory -> ListResult(listResult = itemHistory.listResult) }
        }
    }
}

@Composable
fun SelectedDateAndTime(viewModel: TrainerViewModel){
    val formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(viewModel.selectedDate.value)
    Text(formattedDate, modifier = Modifier.padding(10.dp))
}

@Composable
fun DialogDateTimePicker(viewModel : TrainerViewModel){

    val dialogStateDate = rememberMaterialDialogState()

    Button(onClick = {dialogStateDate.show()}, modifier = Modifier.padding(start = 16.dp)) {Text("Выбрать дату")}

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
            viewModel.selectedDate.value = it

        }
    }
}


@Composable
fun TextFieldChoosePercent(valuePercent:MutableState<String>){
    val maxChar = 3
    OutlinedTextField(
        modifier = Modifier
            .padding(16.dp)
            .height(50.dp)
            .width(100.dp),
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
    listHistory: List<History>,
    openList: MutableState<Boolean>,
    valuePercent: String,
    viewModel: TrainerViewModel

) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentPadding = PaddingValues(8.dp)
    ) {
        if(valuePercent.isNotEmpty()){
            itemsIndexed(items = listHistory) { index, item ->
                if(item.percentCorrect >= valuePercent.toInt() && viewModel.selectedDate.value == LocalDate.of(item.date.year, item.date.month, item.date.dayOfMonth)
                ){
                    ListItemHistory(item) {
                        itemResult = item
                        openList.value = true
                    }
                }
            }
        }else if(valuePercent.isEmpty()){
            itemsIndexed(items = listHistory) { index, item ->
                if(viewModel.selectedDate.value == LocalDate.of(item.date.year, item.date.month, item.date.dayOfMonth)
                ){
                    ListItemHistory(item) {
                        itemResult = item
                        openList.value = true
                    }
                }
            }
        }
        else{
            itemsIndexed(items = listHistory) { index, item ->
                ListItemHistory(item) {
                    itemResult = item
                    openList.value = true
                }
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
        itemsIndexed(items = listResult) { index, item ->
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
fun ListItemHistory(item: History, onItemClick:()->Unit){

    val formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(item.date)
    val formattedTime = DateTimeFormatter.ofPattern("HH:mm").format(item.date.plusHours(8))

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
                isAntiAlias = true
                textSize = 40f
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
                            110f,
                            67.dp.toPx(),
                            textPaint)
                    }

                }


            )
        } }
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

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview(showBackground = true)
fun PrevHistory() {
    ListItemHistory(item = History(0, LocalDateTime.of(2023, 2, 2, 4, 22, 22), 80, listOf())) {

    }
}

