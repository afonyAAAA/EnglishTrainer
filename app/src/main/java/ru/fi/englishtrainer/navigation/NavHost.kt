package ru.fi.englishtrainer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.fi.englishtrainer.screens.StartScreen
import ru.fi.englishtrainer.screens.TrainerScreen


sealed class NavRoutes(val route: String){
    object Start : NavRoutes("start_screen")
    object Trainer : NavRoutes("trainer_screen")
    object History : NavRoutes("history_screen")
    object Authorization : NavRoutes("authorization_screen")
    object Registration : NavRoutes("registration_screen")
}



@Composable
fun TrainerNavHost(navController: NavHostController){
    NavHost(navController = navController, startDestination = NavRoutes.Start.route){
        composable(NavRoutes.Start.route){
            StartScreen(navHostController = navController)
        }
        composable(NavRoutes.Trainer.route){
           TrainerScreen(navHostController = navController)
        }
    }
}