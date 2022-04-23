package com.kukki.firebasesample.ui.vo

data class UserVo(
    val id: String = "",
    val img: String? = "",
    val name: String? = null,
    val phoneNumber: String? = null,
    val dob: String? = null,
//    val password: String? = null,
)

data class ProductVo(
    val id: String = "",
    val name: String? = null,
    val price: String? = null,
    val img: String? = "",
)


data class DataResponse(
    var products: List<ProductVo>? = null,
    var users: List<UserVo>? = null,
    var exception: Exception? = null
)