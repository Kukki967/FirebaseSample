package com.kukki.firebasesample.ui.utils

import com.kukki.firebasesample.ui.vo.DataResponse

interface FirebaseCallback {
    fun onResponse(response: DataResponse)
}