package com.mtehan.kotlinfirebaserealtime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*
import java.sql.Timestamp
import java.util.*
import java.util.jar.Manifest
import android.app.ProgressDialog
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class UploadActivity : AppCompatActivity() {
    var selectedPicture: Uri? = null
    private lateinit var fireDb: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        mAuth = FirebaseAuth.getInstance()
        fireDb = FirebaseFirestore.getInstance()
    }

    fun imageViewClicked(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                selectedPicture = data.data
                if (selectedPicture != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(contentResolver, selectedPicture!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(bitmap)
                    } else {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        imageView.setImageBitmap(bitmap)
                    }

                }
            } catch (ex: Exception) {
                println(ex.printStackTrace())
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadClicked(view: View) {
        //UUID image id
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val storage = FirebaseStorage.getInstance()
        val imageReference = storage.reference.child("images").child(imageName)
        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->
                //Database firestore
                val uploadedPictureReference =
                    FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val postMap = hashMapOf<String, Any>()

                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("userEmail", mAuth.currentUser!!.email.toString())
                    postMap.put("comment", editText.text.toString())
                    postMap.put("date", com.google.firebase.Timestamp.now())

                    fireDb.collection("Posts").add(postMap).addOnCompleteListener { task ->
                        if (task.isSuccessful && task.isSuccessful) {
                            Toast.makeText(applicationContext, "Yükleme Başarılı", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            applicationContext,
                            "${exception.printStackTrace()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.addOnProgressListener { taskSnapshot ->
                var progress: Double = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount()
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("%"+progress.toInt().toString()+" Uploaded...");
                progressDialog.show()
            }
        }


    }
}
