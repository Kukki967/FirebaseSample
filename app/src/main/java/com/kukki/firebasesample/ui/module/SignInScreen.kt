package com.kukki.firebasesample.ui.module

import android.app.Activity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kukki.firebasesample.*
import com.kukki.firebasesample.ui.theme.FirebaseSampleTheme
import com.kukki.firebasesample.ui.theme.Norway
import com.kukki.firebasesample.ui.utils.NavDest

@Composable
fun SignInScreen(navController: NavHostController) {

    StatusBarView(Norway)

    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val cellText = remember { mutableStateOf(TextFieldValue("")) }
    val passwordText = remember { mutableStateOf(TextFieldValue("")) }
    val clickEnabled = remember { mutableStateOf(true) }
    val snackbarVisible = remember { mutableStateOf(false) }
    val snackbarTextField = remember { mutableStateOf(0) }

    LoaderIndicator(
        isEnabled = clickEnabled.value,
        snackBarTextField = snackbarTextField.value,
        snackbarVisibleState = snackbarVisible
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .pointerInput(Unit) {
                    /** to close keyboard on tap outside */
                    this.detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {


                Column {

                    HeadingText(label = "Welcome Back !!!")

                    TextFieldView(nameText = cellText, labelTextId = R.string.phone_number, isNumberKeyBoard = true)
                    PasswordTextFieldView(passwordText)

                    ButtonView(textId = R.string.sign_in) {
                        if (cellText.value.text.isBlank() || passwordText.value.text.isBlank()) {
                            snackbarVisible.value = true
                            snackbarTextField.value = R.string._msg_product_error
                        } else {
                            snackbarTextField.value = R.string._msg_signIn_mismatched
                            clickEnabled.value = false
                            val email = cellText.value.text + "@test.com"
                            val password = passwordText.value.text

                            signInUser(email, password, activity, clickEnabled, snackbarVisible) {
                                navController.navigate(NavDest.mainScreen)
                            }
                        }
                    }

                    CaptionText(label = "New User ? Register") {
                        navController.navigate(NavDest.signUpScreen)
                    }

                } //end column
            } //end box
        }
    }

}


fun signInUser(
    email: String, password: String, activity: Activity, clickEnabled: MutableState<Boolean>, snackbarVisible: MutableState<Boolean>, function: () -> Unit
) {

    Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
        if (task.isSuccessful) {
            clickEnabled.value = true
            function()
        } else {
            clickEnabled.value = true
            snackbarVisible.value = true
            println("sign in failed ${task.exception}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignINScreenPreview() {
    FirebaseSampleTheme {
        SignInScreen(rememberNavController())
    }
}