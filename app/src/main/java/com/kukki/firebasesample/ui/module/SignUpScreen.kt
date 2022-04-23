package com.kukki.firebasesample.ui.module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kukki.firebasesample.*
import com.kukki.firebasesample.R
import com.kukki.firebasesample.ui.module.vm.UserViewModel
import com.kukki.firebasesample.ui.theme.FirebaseSampleTheme
import com.kukki.firebasesample.ui.theme.Norway
import com.kukki.firebasesample.ui.utils.ImageUtils
import com.kukki.firebasesample.ui.utils.NavDest
import com.kukki.firebasesample.ui.utils.UploadImageUtility

@Composable
fun SignUpScreen(
    navController: NavController,
    vm: UserViewModel = viewModel(),
    id: String
) {
    StatusBarView(Norway)
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var datePicked: String? by remember {
        mutableStateOf(null)
    }
    val nameText = remember { mutableStateOf(TextFieldValue("")) }
    val dobText = remember { mutableStateOf(TextFieldValue("")) }
    val cellText = remember { mutableStateOf(TextFieldValue("")) }
    val passwordText = remember { mutableStateOf(TextFieldValue("")) }

    val mediaAccessPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imageUploaded = remember { mutableStateOf(false) }
    val imgPath = ImageUtils().getFullPathUserImage(id) ?: ""

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    if (imageUri != null && !imageUploaded.value) {
        UploadImageUtility().uploadImage(context, imageUri, filePath = imgPath)
        imageUploaded.value = true
    }

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
                .pointerInput(Unit) {
                    /** to close keyboard on tap outside */
                    this.detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {

                Column {

                    HeadingText("Hey  There !!!")

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                uploadImageFunction(imageUploaded, context, launcher, mediaAccessPermission)
                            },
                        horizontalArrangement = Arrangement.Center,
                    ) {

                        val img = loadPictureCircle(imgPath, R.drawable.ic_user_thumbnail).value

                        UserImageThumbnail(imageUri, img, bitmap, context)
                    }

                    TextFieldView(nameText = nameText, labelTextId = R.string.name)
                    DatePickerView(dobText)
                    TextFieldView(nameText = cellText, labelTextId = R.string.phone_number, isNumberKeyBoard = true)
                    PasswordTextFieldView(passwordText)


                    ButtonView(R.string.sign_up) {
                        if (cellText.value.text.isBlank() || passwordText.value.text.isBlank()) {
                            snackbarVisible.value = true
                            snackbarTextField.value = R.string._msg_product_error

                        } else {
                            clickEnabled.value = false
                            snackbarTextField.value = R.string._msg_signUp_failed
                            val email = cellText.value.text + "@test.com"
                            val password = passwordText.value.text

                            Firebase.auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        vm.addNewUser(
                                            userId = Firebase.auth.currentUser?.uid ?: uniqueId(),
                                            name = nameText.value.text,
                                            cell = cellText.value.text,
                                            img = if (imageUploaded.value) {
                                                id
                                            } else {
                                                ""
                                            },
                                            dobText.value.text
                                        )
                                        navController.navigate(NavDest.landingScreen)
                                        clickEnabled.value = true

                                    } else {
                                        clickEnabled.value = true
                                        snackbarVisible.value = true
                                        println("xcxcx sign up failed ${task.exception}")
                                    }
                                }
                        }

                    }

                    CaptionText("Already a user ? Login") {
                        navController.navigate(NavDest.signInScreen)
                    }
                }
            }
        }
    }
}

private fun uploadImageFunction(
    imageUploaded: MutableState<Boolean>,
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Uri?>,
    mediaAccessPermission: ManagedActivityResultLauncher<String, Boolean>
) {
    imageUploaded.value = false

    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
            launcher.launch("image/*")
        }
        else -> {
            mediaAccessPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}

@Composable
private fun UserImageThumbnail(
    imageUri: Uri?,
    img: Bitmap?,
    bitmap: MutableState<Bitmap?>,
    context: Context
) {
    if (imageUri == null) {
        img?.let { it ->
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "logo",
                modifier = Modifier
                    .size(150.dp),
                contentScale = ContentScale.Crop,
            )
        }
    } else {
        imageUri.let {
            //11
            bitmap.value = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it!!)
                ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { bitmapImage ->
                Image(
                    bitmap = bitmapImage.asImageBitmap(),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(top = 44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        } //let end
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FirebaseSampleTheme {
        SignUpScreen(rememberNavController(), id = uniqueId())
    }
}