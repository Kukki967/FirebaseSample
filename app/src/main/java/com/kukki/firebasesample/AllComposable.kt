package com.kukki.firebasesample

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.DatePicker
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.storage.FirebaseStorage
import com.kukki.firebasesample.ui.theme.*
import com.kukki.firebasesample.ui.utils.ImageUtils
import com.kukki.firebasesample.ui.vo.GlideApp
import com.kukki.firebasesample.ui.vo.ProductVo
import com.kukki.firebasesample.ui.vo.UserVo
import java.util.*

fun uniqueId(): String = UUID.randomUUID().toString()

@Composable
fun StatusBarView(colorBg: Color) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(colorBg, darkIcons = true)
    }
}

@Composable
fun LoaderIndicator(
    isEnabled: Boolean,
    snackbarVisibleState: MutableState<Boolean> = remember { mutableStateOf(false) },
    snackBarTextField: Int = 0,
    content: @Composable () -> Unit
) {
    Box {
        content()

        if (!isEnabled) {
            // Semi Transparent background overlay on the main content to disable click
            Card(
                elevation = 0.dp,
                shape = MaterialTheme.shapes.large,
                backgroundColor = SemiTransparent,
                modifier = Modifier.fillMaxSize()
            ) {

            }

            //loader
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        //snackbar
        if (snackbarVisibleState.value) {
            Snackbar(
                Modifier
                    .padding(4.dp)
                    .align(Alignment.BottomCenter),
                action = {
                    Button(onClick = {
                        snackbarVisibleState.value = false
                    }) {
                        Text(stringResource(id = R.string.ok))
                    }
                }
            ) {
                Text(stringResource(id = snackBarTextField))
            }
        }//end snackbar
    }
}

@Composable
fun ButtonView(textId: Int, function: () -> Unit) {
    OutlinedButtonMainView(
        textId = textId,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp)

    ) {
        function()
    }
}

@Composable
fun CaptionText(label: String, function: () -> Unit) {
    Text(
        text = label,
        style = LightText,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                function()
            }
    )
}

@Composable
fun HeadingText(label: String) {
    Text(
        text = label,
        style = BrandingIcon,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}


@ExperimentalCoilApi
@Composable
fun ListProductRowView(item: ProductVo?, imgPath: String? = null) {
    val title = item?.name

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val img = loadPictureCircle(imgPath, R.drawable.ic_product_light_box).value

            img?.let { it ->
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(56.dp),
                    contentScale = ContentScale.Fit
                )
            }

        }

        Column {
            Text(title ?: "", style = Title, textAlign = TextAlign.Center)
            Text(item?.price ?: "0", style = SubTitle, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ListUserRowView(userVo: UserVo?, userImgPath: String? = null, isLoggedInUser: Boolean, function: () -> Unit) {
    val title = userVo?.name

    Row(
        Modifier
            .fillMaxWidth() // full length click ripple
            .padding(vertical = 20.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .padding(horizontal = 12.dp)
                .weight(0.3f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val img = loadPictureCircle(userImgPath, R.drawable.ic_user_thumbnail_24).value

            img?.let { it ->
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(56.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(0.4f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title ?: "", style = Title, textAlign = TextAlign.Center)
            Text(userVo?.phoneNumber ?: "NA", style = SubTitle, textAlign = TextAlign.Center)
            Text(userVo?.dob ?: "NA", style = SubTitle, textAlign = TextAlign.Center)
        }

        Row(
            Modifier
                .padding(horizontal = 12.dp)
                .weight(0.3f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoggedInUser) {

                Image(
                    painter = painterResource(id = R.drawable.ic_log_out),
                    contentDescription = "logout",
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(24.dp)
                        .clickable {
                            function()
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }

    }
}

@Composable
@SuppressLint("ModifierParameter")
fun OutlinedButtonMainView(
    @StringRes textId: Int? = null,
    text: String? = null,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(100),
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Norway, contentColor = Firefly),
        enabled = enable
    ) {
        Row(
            Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val btnTextVal = if (textId != null) stringResource(textId) else text ?: ""
            Text(btnTextVal, style = ButtonText)
        }
    }
}


@Composable
fun TextFieldView(nameText: MutableState<TextFieldValue>, labelTextId: Int, isNumberKeyBoard: Boolean = false) {
    OutlinedTextField(
        value = nameText.value,
        onValueChange = {
            nameText.value = it
        },
        label = {
            Text(text = stringResource(id = labelTextId))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumberKeyBoard) {
                KeyboardType.Number
            } else {
                KeyboardType.Text
            }
        )

    )
}

@Composable
fun PasswordTextFieldView(passwordText: MutableState<TextFieldValue>) {
    OutlinedTextField(
        value = passwordText.value,
        onValueChange = {
            passwordText.value = it
        },
        label = {
            Text(text = "Password")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun loadPictureCircle(url: String?, @DrawableRes defaultImage: Int): MutableState<Bitmap?> {
    val context = LocalContext.current
    val bitmapState: MutableState<Bitmap?> = mutableStateOf(null)

    GlideApp
        .with(context)
        .asBitmap()
        .load(defaultImage)
        .apply(RequestOptions.circleCropTransform()) //
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })

    if (!url.isNullOrBlank()) {
        //FirebaseStorage singleton instance
        val storage = FirebaseStorage.getInstance(ImageUtils().BUCKETNAME)

        // Create a storage reference from our app
        val storageRef = storage.reference
        val ref = storageRef.child(url)
        //gs://fir-sample-1eab7.appspot.com/product/1650694005518

        GlideApp
            .with(context)
            .asBitmap()
            .load(ref)
            .apply(RequestOptions.circleCropTransform()) //
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmapState.value = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    return bitmapState
}

@Composable
fun DatePickerView(
    datePicked: MutableState<TextFieldValue>,
) {

    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext, { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
        }, mYear, mMonth, mDay
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        value = mDate.value,
        onValueChange = {
            datePicked.value = TextFieldValue(text = mDate.value)
        },
        label = {
            Text(text = stringResource(id = R.string.dob))
        },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .clickable {
                        mDatePickerDialog.show()
                    },
                tint = MaterialTheme.colors.onSurface
            )
        }
    )
}