package com.kukki.firebasesample.ui.module.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kukki.firebasesample.ui.utils.FirebaseCallback
import com.kukki.firebasesample.ui.vo.DataResponse
import com.kukki.firebasesample.ui.vo.UserVo

class UserViewModel : ViewModel() {

    val _userList = MutableLiveData<List<UserVo>>()
    val userList: LiveData<List<UserVo>> = _userList

    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val userRef: DatabaseReference = rootRef.child("users")


    init {
        getResponseUsingCallback()
    }

    fun addNewUser(userId: String, name: String, cell: String, img: String = "", dob: String) {
        val user = UserVo(id = userId, name = name, phoneNumber = cell, img = img, dob = dob)
        userRef.child(userId).setValue(user)
    }

    /************************************************* get list  *************************************************/
    private fun getProductListFromRealtimeDatabaseUsingCallback(callback: FirebaseCallback) {
        userRef.get().addOnCompleteListener { task ->
            val response = DataResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.users = result.children.map { snapShot ->
                        snapShot.getValue(UserVo::class.java)!!
                    }
                }

            } else {
                response.exception = task.exception
            }
            callback.onResponse(response)
        }
    }

    private fun getResponseUsingCallback() {

        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: DataResponse) {
                val userList = response.users ?: ArrayList<UserVo>()
                _userList.postValue(userList)
            }
        })
    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        getProductListFromRealtimeDatabaseUsingCallback(callback)
    }

    /************************************************* authentication methods  *************************************************/



}



