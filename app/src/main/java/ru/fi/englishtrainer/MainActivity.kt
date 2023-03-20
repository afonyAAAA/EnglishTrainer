package ru.fi.englishtrainer

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.fi.englishtrainer.navigation.TrainerNavHost
import ru.fi.englishtrainer.ui.theme.EnglishTrainerTheme
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import ru.fi.englishtrainer.viewModel.TrainerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val viewModel : TrainerViewModel =
                viewModel(factory = TrainerViewModelFactory(context.applicationContext as Application))

            viewModel.initRoomDatabase()

            EnglishTrainerTheme {
                TrainerNavHost(navController = rememberNavController(), viewModel)
            }
        }
    }
}


