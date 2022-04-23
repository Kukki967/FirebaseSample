package com.kukki.firebasesample.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kukki.firebasesample.ui.theme.Firefly


val TitleAppBar by lazy {
    TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        color = Firefly
    )
}



val SubTitle by lazy {
    TextStyle(
        fontWeight = FontWeight.W300,
        fontSize = 16.sp,
        color = Firefly
    )
}


val Title by lazy {
    TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        color = Firefly
    )
}

val ButtonText by lazy {
    TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        color = Firefly
    )
}


val LightText by lazy {
    TextStyle(
        fontWeight = FontWeight.W300,
        fontSize = 14.sp,
        color = Firefly
    )
}


val BrandingIcon by lazy {
    TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 25.sp,
        color = Firefly
    )
}
