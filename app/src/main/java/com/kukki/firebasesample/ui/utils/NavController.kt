package com.kukki.firebasesample.ui.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kukki.firebasesample.ui.module.LandingScreen
import com.kukki.firebasesample.ui.module.MainScreen
import com.kukki.firebasesample.ui.module.SignInScreen
import com.kukki.firebasesample.ui.module.SignUpScreen
import com.kukki.firebasesample.uniqueId
import java.util.*

/**
 * MainNavDestinations used in the ([MainApp]).
 */
object NavDest {
    const val landingScreen = "landingScreen"
    const val signUpScreen = "signUpScreen"
    const val signInScreen = "signInScreen"
    const val mainScreen = "mainScreen"

}

@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavDest.landingScreen
) {

    NavHost(navController, startDestination,
        builder = {
            composable(NavDest.landingScreen) { LandingScreen(navController) }
            composable(NavDest.signUpScreen) { SignUpScreen(navController,id = uniqueId()) }
            composable(NavDest.signInScreen) { SignInScreen(navController) }
            composable(NavDest.mainScreen) { MainScreen(navController) }
        })
}