package com.stalmate.user.view.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityProfileEditBinding
import java.io.File
import java.util.HashMap

class ActivityProfileEdit : BaseActivity() {

    private lateinit var binding : ActivityProfileEditBinding
    val PICK_IMAGE_PROFILE = 1
    val PICK_IMAGE_COVER = 1
    var WRITE_REQUEST_CODE = 100
    var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val requiredPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    var isImageSelected = false
    var imageFile: File? = null



    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)


        clickLister()
    }

    private fun clickLister() {

        binding.idCoverPhoto.setOnClickListener {

            val checkVal: Int = checkCallingOrSelfPermission(requiredPermission)
            requestPermissions(permissions, WRITE_REQUEST_CODE)

            if (checkVal==PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_COVER)
            }else{
                Toast.makeText(this,"Please enable Write permission from Setting", Toast.LENGTH_SHORT).show()
            }

        }

        binding.idCameraProfile.setOnClickListener {
            val checkVal: Int = checkCallingOrSelfPermission(requiredPermission)
            requestPermissions(permissions, WRITE_REQUEST_CODE)
            if (checkVal==PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_PROFILE)

            } else {
                Toast.makeText(this,"Please enable Write permission from Setting", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {

        super.onDestroy()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath : String? = ""

        if (resultCode == RESULT_OK){
            if (requestCode == PICK_IMAGE_COVER) {
               /* val result: CropImage.ActivityResult = CropImage.getActivityResult(data)!!
                val resultUri: Uri = result.uriContent!!


                imageFile = File(result.getUriFilePath(this, true))
               *//* isImageSelected = true*//*

                Glide.with(this).load(resultUri).circleCrop().into(binding.ivBackground)
*/
                val imageUri = data?.data

                val uri: Uri? = data!!.getData()
                val wholeID = DocumentsContract.getDocumentId(uri)

                // Split at colon, use second item in the array
                val id = wholeID.split(":").toTypedArray()[1]

                val column = arrayOf(MediaStore.MediaColumns.DATA)

                // where id is equal to
                val sel = MediaStore.Images.Media._ID  + "=?"

                val cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf(id), null
                )
                val columnIndex = cursor!!.getColumnIndex(column[0])

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()


                binding.ivBackground.setImageURI(imageUri)

                Log.d("kcjkasdcb", "Chosen path = $filePath")
                Log.d("kcjkasdcb", uri.toString())
            }


        }else if (resultCode == RESULT_OK){

        }
    }
}