package com.kukki.firebasesample.ui.utils

class ImageUtils {

    var SEPARATOR_FILE = "/"
    var BUCKETNAME = "gs://fir-sample-1eab7.appspot.com"
    private val USER = "user"
    private val PRODUCT = "product"

    fun getFullPathProductImage(fileName: String?): String? {
        if (fileName.isNullOrBlank()) {
            return null
        }
        return PRODUCT + SEPARATOR_FILE + fileName
    }

    fun getFullPathUserImage( fileName: String?): String? {
        if (fileName.isNullOrBlank()) {
            return null
        }
        return USER + SEPARATOR_FILE  + fileName
    }
}