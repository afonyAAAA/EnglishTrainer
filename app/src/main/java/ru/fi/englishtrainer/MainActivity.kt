package ru.fi.englishtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ru.fi.englishtrainer.navigation.NavRoutes
import ru.fi.englishtrainer.navigation.TrainerNavHost
import ru.fi.englishtrainer.ui.theme.EnglishTrainerTheme
import ru.fi.englishtrainer.utils.Constants.context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnglishTrainerTheme {
                context = applicationContext
                TrainerNavHost(navController = rememberNavController())
            }
        }
    }
}


