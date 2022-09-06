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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityProfileEditBinding
import com.stalmate.user.model.AboutProfileLine
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.adapter.ProfileAboutAdapter
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ActivityProfileEdit : BaseActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    val PICK_IMAGE_PROFILE = 2
    val PICK_IMAGE_COVER = 1
    var WRITE_REQUEST_CODE = 100
    private var GANDER: String = ""
    var dates: String = ""
    var month: String = ""
    var year: String = ""
    var merriage: String = ""
    var permissions =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    val requiredPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    lateinit var userData: User
    var spinnerArrayFeb = arrayOf("Feb")
    var spinnerArrayFull = arrayOf("Jan", "Mar", "May", "July", "Aug", "Oct", "Dec")
    var spinnerArrayFullSemihalf = arrayOf("Apr", "Jun", "Sep", "Nov")
    var spinnerArrayFullhalf =
        arrayOf("jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var spinnerArrayBlank = arrayOf("")
    var imageFile: File? = null
    var isCoverImage = false


    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        getUserProfileData()
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

        binding.layout.spMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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

            isCoverImage=true
            startCrop()
        }

        binding.ivUserProfileImage.setOnClickListener {
            isCoverImage=false
            startCrop()
        }


        binding.btnCrateAccount.setOnClickListener {
            updateProfileApiHit()
        }

        binding.layout.tvMarriage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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

        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())




        networkViewModel.etsProfileApi(
            getRequestBody(binding.layout.etName.text.toString()),
            getRequestBody(binding.layout.etLastName.text.toString()),
            getRequestBody(binding.layout.bio.text.toString()),
            getRequestBody(binding.layout.etNumber.text.toString()),
            getRequestBody(year + "-" + month + "-" + dates),
            getRequestBody(merriage),
            getRequestBody(binding.layout.etHowTown.text.toString()),
            getRequestBody(binding.layout.etCurrentCity.text.toString()),
            getRequestBody(""),
            getRequestBody(binding.layout.etCompany.text.toString()),
            getRequestBody(GANDER),
        )

        networkViewModel.UpdateProfileLiveData.observe(this, Observer {

            it.let {


                makeToast(it!!.message)
            }
        })
    }


    private fun updateProfileImageApiHit() {

        fun getRequestBody(str: String?): RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)

        fun getMultipart(str: File): MultipartBody.Part = MultipartBody.Part.createFormData(
            "cover_img".takeIf { isCoverImage } ?: "profile_img",
            str.name,
            thumbnailBody
        )


        networkViewModel.etsProfileApi(getMultipart(imageFile!!),)

    }



    override fun onDestroy() {
        super.onDestroy()
    }


    /*Cover Image Picker */
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            var uriFilePath = result.getUriFilePath(this) // optional usage
            imageFile = File(result.getUriFilePath(this, true)!!)
            Log.d("imageUrl======", uriContent.toString())
            Log.d("imageUrl======", uriFilePath.toString())

            if (isCoverImage){
                Glide.with(this).load(uriContent).into(binding.ivBackground)
            }else{
                Glide.with(this).load(uriContent).into(binding.ivUserThumb)
            }
           updateProfileImageApiHit()

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
    }



    fun getUserProfileData() {
        var hashMap = HashMap<String, String>()
        networkViewModel.getProfileData( hashMap)
        networkViewModel.profileLiveData.observe(this, Observer {
            it.let {
                userData = it!!.results
                setUpAboutUI()
            }
        })
    }


    fun setUpAboutUI() {
        binding.layout.etName.setText(userData.first_name)
        binding.layout.etLastName.setText(userData.last_name)
        binding.layout.bio.setText(userData.about)
        binding.layout.etEmail.setText(userData.email)
        binding.layout.etNumber.setText(userData.number)
        ImageLoaderHelperGlide.setGlide(this,binding.ivBackground,userData.img_url+userData.cover_img1)
        ImageLoaderHelperGlide.setGlide(this,binding.ivUserThumb,userData.img_url+userData.profile_img1)
    }



}