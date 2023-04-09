package ru.fi.englishtrainer.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.fi.englishtrainer.R
import ru.fi.englishtrainer.navigation.NavRoutes
import ru.fi.englishtrainer.ui.theme.Shapes
import ru.fi.englishtrainer.utils.Constants
import ru.fi.englishtrainer.viewModel.TrainerViewModel

@Composable
fun StartScreen(navHostController: NavHostController, viewModel: TrainerViewModel){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround){
        TitleApp()
        ButtonStart(navHostController, viewModel)
        ButtonHistory(navHostController)
    }
    if(Constants.FIREBASE_REPOSITORY == null){
        Toast.makeText(viewModel.context, "Для работы тренера требуется интеренет соединение", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun TitleApp(){
    Text(text = stringResource(id = R.string.app_name),
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        letterSpacing = 4.5.sp,
    modifier = Modifier
        .background(MaterialTheme.colors.primary, shape = Shapes.large)
    )
}

@Composable
fun ButtonStart(navHostController: NavHostController, viewModel: TrainerViewModel){
    Button(onClick = {
        viewModel.resumeTrainer()
        navHostController.navigate(route = NavRoutes.Trainer.route)
    }, enabled = Constants.FIREBASE_REPOSITORY != null,
    ) {
        Text(text = stringResource(R.string.start))
    }
}

@Composable
fun ButtonHistory(navHostController: NavHostController){
    Button(onClick = {
        navHostController.navigate(NavRoutes.History.route)
    }) {
        Text(text = stringResource(R.string.history))
    }
}

@Composable
fun ButtonRating(){
    Button(onClick = { /*TODO*/ }) {
        Text(text = stringResource(R.string.rating))
    }
}

@Preview(showBackground = true)
@Composable
fun prevTittle(){
    TitleApp()
}
