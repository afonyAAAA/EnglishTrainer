package ru.fi.englishtrainer

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.fi.englishtrainer.database.firebase.AppFireBaseFirebaseRepository
import ru.fi.englishtrainer.navigation.TrainerNavHost
import ru.fi.englishtrainer.ui.theme.EnglishTrainerTheme
import ru.fi.englishtrainer.utils.Constants
import ru.fi.englishtrainer.viewModel.TrainerViewModel
import ru.fi.englishtrainer.viewModel.TrainerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val viewModel : TrainerViewModel =
                viewModel(factory = TrainerViewModelFactory(context.applicationContext as Application))

            Constants.FIREBASE_REPOSITORY = if(isOnline(this)){
                AppFireBaseFirebaseRepository()
            }else
                null

            viewModel.initRoomDatabase()

            Image(painter = painterResource(id = R.drawable.image_theme_app), contentDescription = "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

            EnglishTrainerTheme {
                TrainerNavHost(navController = rememberNavController(), viewModel)
            }
        }
    }
}

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetwork
    return netInfo != null
}
