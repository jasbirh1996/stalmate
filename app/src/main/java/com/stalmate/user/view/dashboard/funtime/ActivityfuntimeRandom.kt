package com.stalmate.user.view.dashboard.funtime

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityActivityfuntimeRandomBinding
import java.io.*

class ActivityfuntimeRandom : AppCompatActivity() {

    private lateinit var binding : ActivityActivityfuntimeRandomBinding
    var imageFile: Uri? = null
    var musicFile: String = ""
    val PICK_FILE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this,R.layout.activity_activityfuntime_random)

        binding.music.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, PICK_FILE)
        }

        startCrop()
    }



    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            var uriFilePath = result.getUriFilePath(this) // optional usage
//            imageFile = File(result.getUriFilePath(this, true)!!)
            imageFile = result.uriContent

            Glide.with(this)
                .load(uriContent)
                .placeholder(R.drawable.profileplaceholder)
                .into(binding.image)

        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath: String? = ""
        if (requestCode == PICK_FILE && resultCode == RESULT_OK) {

            if (android.R.attr.data != null) {
                val uri: Uri? = data!!.getData()
                /* uri?.let { getContentResolver().openInputStream(it).toString() }
                     ?.let { Log.d("cbjkabcjk", it) }*/

                val wholeID = DocumentsContract.getDocumentId(uri)

                // Split at colon, use second item in the array
                val id = wholeID.split(":").toTypedArray()[1]

                val column = arrayOf(MediaStore.MediaColumns.DATA)

                // where id is equal to
                val sel = MediaStore.Audio.Media._ID + "=?"

                val cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf(id), null
                )

                val columnIndex = cursor!!.getColumnIndex(column[0])

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
                /*setImageFromIntent(filePath)*/
                Log.d("jcbjscb", "Chosen path = $filePath")

                musicFile = filePath!!
                val mergedFilePath = ""
                val files = arrayOfNulls<File>(2)
                files[0] = File(imageFile!!.path)
                files[1] = File(musicFile)

                val mergedFile: File = File(mergedFilePath)

                mergeFiles(files, mergedFile)


                var mp= MediaPlayer()
                mp.setDataSource(this, Uri.parse(filePath))
                mp.prepare()
                mp.start()
            }
        }
    }


    fun mergeFiles(files: Array<File?>, mergedFile: File?) {
        var fstream: FileWriter? = null
        var out: BufferedWriter? = null
        try {
            fstream = FileWriter(mergedFile, true)
            out = BufferedWriter(fstream)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        for (f in files) {
            println("merging: " + f!!.name)
            var fis: FileInputStream?
            try {
                fis = FileInputStream(f)
                val `in` = BufferedReader(InputStreamReader(fis))
                var aLine: String?
                while (`in`.readLine().also { aLine = it } != null) {
                    out!!.write(aLine)
                    out.newLine()
                }
                `in`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            out!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}