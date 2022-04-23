package com.kukki.firebasesample.ui.utils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadImageUtility {

    @SuppressLint("CheckResult")
    fun uploadImage(context: Context, filePathUri: Uri?, filePath: String) {

        val filename = Date().time.toString()

        val storage = FirebaseStorage.getInstance(ImageUtils().BUCKETNAME)
        val storageReference = storage.reference
        val storageReferenceChild = storageReference.child(filePath)

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        if (filePathUri != null) {

            storageReferenceChild.putFile(filePathUri)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(context, "addOnFailureListener " + e.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = ((100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount))
                    progressDialog.setMessage("addOnProgressListener " + progress.toInt() + "%")
                }
        }
    }
}