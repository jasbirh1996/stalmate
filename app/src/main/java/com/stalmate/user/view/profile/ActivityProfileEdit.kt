package com.stalmate.user.view.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityProfileEditBinding
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ActivityProfileEdit : BaseActivity() {

    private lateinit var binding : ActivityProfileEditBinding
    val PICK_IMAGE_PROFILE = 2
    val PICK_IMAGE_COVER = 1
    var WRITE_REQUEST_CODE = 100
    private var GANDER: String = ""
    var dates: String = ""
    var month: String = ""
    var year: String = ""
    var merriage: String = ""
    var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    val requiredPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    var isImageSelected = false
    var coverImageFile: String? = null
    var profileImageFile: String? = null
    var imageCoverFile: MultipartBody.Part? = null
    var imageProfileFile: MultipartBody.Part? = null
    var spinnerArrayFeb = arrayOf("Feb")
    var spinnerArrayFull = arrayOf("Jan", "Mar", "May", "July", "Aug", "Oct", "Dec")
    var spinnerArrayFullSemihalf = arrayOf("Apr", "Jun", "Sep", "Nov")
    var spinnerArrayFullhalf =
        arrayOf("jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var spinnerArrayBlank = arrayOf("")
    var ImageFile: File? = null
    var ImageCoverFile: File? = null



    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        requestPermissions(permissions, WRITE_REQUEST_CODE)
        binding.layout.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "1"
                binding.layout.rdmale.setChecked(true)
                binding.layout.rdFamel.setChecked(false)
                binding.layout.rdOthers.setChecked(false)
            }
        }

        binding.layout.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "2"
                binding.layout.rdmale.setChecked(false)
                binding.layout.rdFamel.setChecked(true)
                binding.layout.rdOthers.setChecked(false)
            }
        }


        binding.layout.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "3"
                binding.layout.rdmale.setChecked(false)
                binding.layout.rdFamel.setChecked(false)
                binding.layout.rdOthers.setChecked(true)
            }
        }



        binding.layout.spDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                dates = p0!!.getItemAtPosition(position).toString()


                Log.d("jcaujc", dates)
                if (dates == "31") {

                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFull
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter);
                } else if (dates == "30") {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFullSemihalf
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter);
                } else if (dates == "28") {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                       this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFeb
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter);
                } else if (dates != "30" || dates != "31" || dates != "28") {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFullhalf
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter)
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.layout.spMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                month = p0!!.getItemAtPosition(position).toString()
                Log.d("jcaujc", month)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


        binding.layout.spYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                year = p0!!.getItemAtPosition(position).toString()
                Log.d("jcaujc", year)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        clickLister()
    }

    private fun clickLister() {

        binding.idCoverPhoto.setOnClickListener {

            val checkVal: Int = checkCallingOrSelfPermission(requiredPermission)
            requestPermissions(permissions, WRITE_REQUEST_CODE)

            if (checkVal==PackageManager.PERMISSION_GRANTED) {
               /* val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_COVER)*/

                */

                startCrop()
            }
        }

        binding.idCameraProfile.setOnClickListener {

            val checkVal: Int = checkCallingOrSelfPermission(requiredPermission)
            requestPermissions(permissions, WRITE_REQUEST_CODE)

            if (checkVal==PackageManager.PERMISSION_GRANTED) {
               /* val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_PROFILE)
//                cropImage*/
                */

                startCropProfile()
            }
        }


        binding.btnCrateAccount.setOnClickListener {
            updateProfileApiHit()
        }

        binding.layout.tvMarriage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                merriage = p0!!.getItemAtPosition(position).toString()
                Log.d("jcaujc", year)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

    private fun updateProfileApiHit() {

        fun getRequestBody(str :String?) : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())
        val thumbnailBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), ImageFile!!)
        fun getMultipart(str : File) : MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            str.name,
            thumbnailBody
        )

        val thumbnailBodyCover: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), ImageCoverFile!!)
        fun getMultipartCover(str : File) : MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            str.name,
            thumbnailBodyCover
        )

        networkViewModel.etsProfileApi(getRequestBody(binding.layout.etName.text.toString()),getRequestBody( binding.layout.etLastName.text.toString())
        , getRequestBody(binding.layout.bio.text.toString()),getRequestBody(binding.layout.etNumber.text.toString()), getRequestBody(year+"-"+month+"-"+dates),
            getRequestBody(merriage), getRequestBody(binding.layout.etHowTown.text.toString())
            , getRequestBody(binding.layout.etCurrentCity.text.toString()),
            getRequestBody(""),  getRequestBody(binding.layout.etCompany.text.toString())
            , getRequestBody(GANDER), getMultipart(ImageFile!!), getMultipartCover(ImageCoverFile!!))

        networkViewModel.UpdateProfileLiveData.observe(this, Observer {

            it.let {

                makeToast(it!!.message)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath : String? = ""

        if (resultCode == RESULT_OK){
            Log.d("acjkbjab", requestCode.toString())

            if (requestCode == PICK_IMAGE_COVER) {

                Log.d("acjkbjab", "abhay1")
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

//                imageCoverFile = filePath
                binding.ivBackground.setImageURI(imageUri)

                Log.d("kcjkasdcb", "Chosen path = $filePath")
            }else  if (requestCode == PICK_IMAGE_PROFILE){

                Log.d("acjkbjab", "abhay2")
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

//                imageProfileFile = filePath
                binding.ivUserThumb.setImageURI(imageUri)

                Log.d("kcjkasdcb", "Chosen path = $filePath")
            }
        }
    }


    /*Cover Image Picker */
       private val cropImage = registerForActivityResult(CropImageContract()) { result ->
           if (result.isSuccessful) {
               // use the returned uri
               val uriContent = result.uriContent
               var uriFilePath = result.getUriFilePath(this) // optional usage
               coverImageFile = uriFilePath
               Log.d("imageUrl======", uriContent.toString())
               Log.d("imageUrl======", uriFilePath.toString())
               ImageCoverFile = File(uriContent!!.getPath())
               Glide.with(this).load(uriContent).into(binding.ivBackground)
           } else {
               // an error occurred
               val exception = result.error
           }
       }

       private fun startCrop() {
           // start picker to get image for cropping and then use the image in cropping activity
           cropImage.launch(
               options {
                   setGuidelines(CropImageView.Guidelines.ON)
               }
           )

         /*  //start picker to get image for cropping from only gallery and then use the image in
           //cropping activity
           cropImage.launch(
               options {
                   setImagePickerContractOptions(
                       PickImageContractOptions(includeGallery = true, includeCamera = false)
                   )
               }
           )*/

          /* // start cropping activity for pre-acquired image saved on the device and customize settings
           cropImage.launch(
               options(uri = uriContents) {
                   setGuidelines(CropImageView.Guidelines.ON)
                   setOutputCompressFormat(Bitmap.CompressFormat.PNG)
               }
           )*/
       }


    /*Profile Image Picker*/

    private val cropImageProfile = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            var uriFilePath = result.getUriFilePath(this) // optional usage
            profileImageFile = uriFilePath

            ImageFile = File(uriContent!!.getPath())


            Log.d("imageUrl======", uriContent.toString())
            Log.d("imageUrl======", uriFilePath.toString())

            Glide.with(this).load(uriContent).into(binding.ivUserThumb)
        } else {
            // an error occurred
            val exception = result.error
        }
    }

    private fun startCropProfile() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImageProfile.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )

        /*  //start picker to get image for cropping from only gallery and then use the image in
          //cropping activity
          cropImage.launch(
              options {
                  setImagePickerContractOptions(
                      PickImageContractOptions(includeGallery = true, includeCamera = false)
                  )
              }
          )*/

        /* // start cropping activity for pre-acquired image saved on the device and customize settings
         cropImage.launch(
             options(uri = uriContents) {
                 setGuidelines(CropImageView.Guidelines.ON)
                 setOutputCompressFormat(Bitmap.CompressFormat.PNG)
             }
         )*/
    }


}