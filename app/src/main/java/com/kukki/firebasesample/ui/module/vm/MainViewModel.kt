package com.kukki.firebasesample.ui.module.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kukki.firebasesample.ui.utils.FirebaseCallback
import com.kukki.firebasesample.ui.vo.DataResponse
import com.kukki.firebasesample.ui.vo.ProductVo

class MainViewModel : ViewModel() {

    val _productsList = MutableLiveData<List<ProductVo>>()
    val productsList: LiveData<List<ProductVo>> = _productsList

    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val productRef: DatabaseReference = rootRef.child("products")

    init {
        getResponseUsingCallback()
    }

    /************************************************* add new product *************************************************/

    fun addNewProduct(productId: String, name: String, price: String, img: String = "") {
        val prod = ProductVo(id = productId, name = name, price = price, img = img)
        productRef.child(productId).setValue(prod)
    }

    /************************************************* get list  *************************************************/
    private fun getProductListFromRealtimeDatabaseUsingCallback(callback: FirebaseCallback) {
        productRef.get().addOnCompleteListener { task ->
            val response = DataResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.products = result.children.map { snapShot ->
                        snapShot.getValue(ProductVo::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            callback.onResponse(response)
        }
    }

    fun getResponseUsingCallback() {
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: DataResponse) {
                val productList = response.products ?: ArrayList<ProductVo>()
                _productsList.postValue(productList)
            }
        })
    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        getProductListFromRealtimeDatabaseUsingCallback(callback)
    }


}