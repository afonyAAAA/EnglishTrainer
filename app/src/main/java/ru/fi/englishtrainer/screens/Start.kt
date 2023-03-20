package ru.fi.englishtrainer.screens

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import ru.fi.englishtrainer.R
import ru.fi.englishtrainer.navigation.NavRoutes
import ru.fi.englishtrainer.viewModel.TrainerViewModel


@Composable
fun StartScreen(navHostController: NavHostController, viewModel: TrainerViewModel){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround){
        TitleApp()
        ButtonStart(navHostController)
        ButtonHistory(navHostController)
    }
}


@Composable
fun TitleApp(){
    Text(text = stringResource(id = R.string.app_name))
}

@Composable
fun ButtonStart(navHostController: NavHostController){
    Button(onClick = {
        navHostController.navigate(route = NavRoutes.Trainer.route)
    } ) {
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



//@Composable
//@Preview(showBackground = true)
//fun PreviewButton(){
//    ButtonStart()
//}
