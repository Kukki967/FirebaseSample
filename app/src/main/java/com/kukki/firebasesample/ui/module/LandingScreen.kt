package com.kukki.firebasesample.ui.module

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kukki.firebasesample.ui.theme.FirebaseSampleTheme
import com.kukki.firebasesample.ui.utils.NavDest

@Composable
fun LandingScreen(navController: NavHostController) {

    val isSignedIn = Firebase.auth.currentUser != null

    if (isSignedIn) {
        navController.navigate(NavDest.mainScreen)
    } else {
        navController.navigate(NavDest.signInScreen)
    }
}


@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    FirebaseSampleTheme {
        LandingScreen(rememberNavController())
    }
}