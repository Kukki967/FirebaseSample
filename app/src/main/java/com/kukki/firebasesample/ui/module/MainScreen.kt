package com.kukki.firebasesample.ui.module

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import coil.annotation.ExperimentalCoilApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kukki.firebasesample.*
import com.kukki.firebasesample.R
import com.kukki.firebasesample.ui.module.vm.MainViewModel
import com.kukki.firebasesample.ui.module.vm.UserViewModel
import com.kukki.firebasesample.ui.theme.FirebaseSampleTheme
import com.kukki.firebasesample.ui.theme.Norway
import com.kukki.firebasesample.ui.utils.*
import com.kukki.firebasesample.ui.vo.ProductVo
import com.kukki.firebasesample.ui.vo.UserVo
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoilApi
@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
) {
    BackHandler(enabled = false) {

    }
    val focusManager = LocalFocusManager.current

    val selectedNav = remember { mutableStateOf(TopNavOption.PRODUCT_LIST) }

    val navList = listOf(TopNavOption.USER_LIST, TopNavOption.PRODUCT_LIST, TopNavOption.ADD_PRODUCT)

    StatusBarView(Norway)

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
        TopTabView(Norway, selectedNav.value, navList) {
            selectedNav.value = it
        }

        val id = Date().time.toString()

        when (selectedNav.value) {
            TopNavOption.USER_LIST -> UserList(userViewModel, navController)
            TopNavOption.PRODUCT_LIST -> ProductList(mainViewModel)
            TopNavOption.ADD_PRODUCT -> AddProduct(mainViewModel, id)
        }
    }
}


@Composable
fun AddProduct(vm: MainViewModel, id: String) {
    val nameText = remember { mutableStateOf(TextFieldValue("")) }
    val priceText = remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    val mediaAccessPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imageUploaded = remember { mutableStateOf(false) }
    val imgPath = ImageUtils().getFullPathProductImage(id) ?: ""

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
                .padding(12.dp)
        ) {

            //image view
            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ProductImageThumbnail(bitmap, imgPath, imageUri, context)

                        OutlinedButtonMainView(
                            textId = R.string.change,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .height(56.dp),
                        ) {
                            imageUploaded.value = false

                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                                    launcher.launch("image/*")
                                }
                                else -> {
                                    mediaAccessPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    }
                }
            }

            TextFieldView(nameText = nameText, labelTextId = R.string.product_name)
            TextFieldView(nameText = priceText, labelTextId = R.string.product_price, isNumberKeyBoard = true)

            ButtonView(textId = R.string.save) {
                if (nameText.value.text.isBlank() || priceText.value.text.isBlank()) {
                    snackbarVisible.value = true
                    snackbarTextField.value = R.string._msg_product_error
                } else {
                    clickEnabled.value = false
                    snackbarVisible.value = true

                    vm.addNewProduct(
                        productId = id,
                        name = nameText.value.text,
                        price = priceText.value.text,
                        img = if (imageUploaded.value) {
                            id
                        } else {
                            ""
                        },
                    )

                    //reload list
                    vm.getResponseUsingCallback()

                    //reset text fields
                    nameText.value = TextFieldValue("")
                    priceText.value = TextFieldValue("")
                    snackbarTextField.value = R.string._msg_upload_success
                    clickEnabled.value = true

                }
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ProductList(vm: MainViewModel) {
    val productList = vm.productsList.observeAsState()
    val clickEnabled = remember { mutableStateOf(true) }

    LoaderIndicator(
        isEnabled = clickEnabled.value,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            LazyColumn(content = {

                itemsIndexed(
                    items = productList.value ?: ArrayList(),
                    itemContent = { _, data: ProductVo ->

                        val imagePath = ImageUtils().getFullPathProductImage(data.img)

                        ListProductRowView(item = data, imgPath = imagePath)

                        Divider()
                    }
                )
            })
        }
    }
}


@Composable
fun ProductImageThumbnail(bitmap: MutableState<Bitmap?>, imgPath: String?, imageUri: Uri?, context: Context) {
    val img = loadPictureCircle(imgPath, R.drawable.ic_product_light_box).value

    //9
    if (imageUri == null) {
        img?.let { it ->
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "logo",
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Fit,
            )
        }
    } else {
        imageUri.let {
            //11
            bitmap.value = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { bitmapImage ->
                Image(
                    bitmap = bitmapImage.asImageBitmap(),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
fun UserList(vm: UserViewModel, navController: NavController) {
    val userList = vm.userList.observeAsState()
    val clickEnabled = remember { mutableStateOf(true) }

    LoaderIndicator(
        isEnabled = clickEnabled.value,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            LazyColumn(content = {

                itemsIndexed(
                    items = userList.value ?: ArrayList(),
                    itemContent = { _, data: UserVo ->
                        val isLoggedInUser = data.id == Firebase.auth.currentUser?.uid

                        val imagePath = ImageUtils().getFullPathUserImage(data.img)

                        ListUserRowView(userVo = data, userImgPath = imagePath, isLoggedInUser) {
                            Firebase.auth.signOut()
                            navController.navigate(NavDest.signInScreen)
                        }

                        Divider()
                    }
                )
            })
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FirebaseSampleTheme {
        MainScreen(rememberNavController())
    }
}