package com.stalmate.user.view.profile

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.ActivityProfileEditBinding
import com.stalmate.user.model.*
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.dialogs.DialogAddEditEducation
import com.stalmate.user.view.dialogs.DialogAddEditProfession
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ActivityProfileEdit : BaseActivity(), EducationListAdapter.Callbackk, ProfessionListAdapter.Callbackk, ProfilePictureAdapter.Callbackk, CoverPictureAdapter.Callbackk, AdapterFeed.Callbackk{

    private lateinit var binding: ActivityProfileEditBinding
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
    lateinit var userData: ModelUser
    var spinnerArrayFeb = arrayOf("Feb")
    var spinnerArrayFull = arrayOf("Jan", "Mar", "May", "July", "Aug", "Oct", "Dec")
    var spinnerArrayFullSemihalf = arrayOf("Apr", "Jun", "Sep", "Nov")
    var spinnerArrayFullhalf =
        arrayOf("jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var spinnerArrayBlank = arrayOf("")
    var imageFile: File? = null
    var isCoverImage = false
    private lateinit var educationAdapter: EducationListAdapter
    private lateinit var professionListAdapter: ProfessionListAdapter
    private lateinit var profilePictureAdapter : ProfilePictureAdapter
    private lateinit var coverPictureAdapter: CoverPictureAdapter
    lateinit var feedAdapter: AdapterFeed
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        getUserProfileData()

        feedAdapter = AdapterFeed(networkViewModel, this, this)
        binding.rvFeeds.setNestedScrollingEnabled(false);
        binding.rvFeeds.adapter = feedAdapter


        requestPermissions(permissions, WRITE_REQUEST_CODE)
        binding.layout.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "Male"
                binding.layout.rdmale.setChecked(true)
                binding.layout.rdFamel.setChecked(false)
                binding.layout.rdOthers.setChecked(false)
            }
        }

        binding.layout.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "Female"
                binding.layout.rdmale.setChecked(false)
                binding.layout.rdFamel.setChecked(true)
                binding.layout.rdOthers.setChecked(false)
            }
        }


        binding.layout.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "Other"
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

                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(this@ActivityProfileEdit, android.R.layout.simple_spinner_item,
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

        binding.layout.tvAddMore.setOnClickListener {


            var dialogAddEditEducation=DialogAddEditEducation(this, Education("","",0,"","","","","","",""),networkViewModel,false,object :DialogAddEditEducation.Callbackk
            {
                override fun onSuccessfullyEditedEducation(education: Education) {
                    userData.results.profile_data[0].education.add(education)
                    networkViewModel.profileLiveData.postValue(userData)
                }
            })
            dialogAddEditEducation.show()


        }

        binding.layout.tvaddMoreProfession.setOnClickListener {

            var dialogAddEditProfession=DialogAddEditProfession(this,Profession("","",0,"","","","","","","","",""),networkViewModel,false,object :DialogAddEditProfession.Callbackk
            {
                override fun onSuccessfullyEditedProfession(profession: Profession) {
                    userData.results.profile_data[0].profession.add(profession)
                    networkViewModel.profileLiveData.postValue(userData)

                }
            })
            dialogAddEditProfession.show()
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

        binding.ivBack.setOnClickListener {
            finish()
        }


    }


    private fun updateProfileApiHit() {

        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())


        networkViewModel.etsProfileApi(
            getRequestBody(binding.layout.etName.text.toString()),
            getRequestBody(binding.layout.etLastName.text.toString()),
            getRequestBody(binding.layout.bio.text.toString()),
            /*getRequestBody(binding.layout.etNumber.text.toString()),*/
            getRequestBody(year + "-" + month + "-" + dates),
            getRequestBody(merriage),
            getRequestBody(binding.layout.etHowTown.text.toString()),
            getRequestBody(binding.layout.etCurrentCity.text.toString()),
            getRequestBody(""),
            getRequestBody(binding.etWebsite.text.toString()),
            getRequestBody(GANDER),
        )

        networkViewModel.UpdateProfileLiveData.observe(this, Observer {

            it.let {
                makeToast(it!!.message)
            }
        })
    }


    private fun updateProfileImageApiHit() {


        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)

        fun getMultipart(str: File): MultipartBody.Part = MultipartBody.Part.createFormData(
            "cover_img".takeIf { isCoverImage } ?: "profile_img",
            str.name,
            thumbnailBody
        )
        networkViewModel.etsProfileApi(getMultipart(imageFile!!))
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
                userData = it!!
                setUpAboutUI()

                if (it.results.profile_data[0].education.isNotEmpty()){
                    binding.layout.rvEducation.visibility = View.VISIBLE
                }

                if (it.results.profile_data[0].profession.isNotEmpty()){
                    binding.layout.rvProfession.visibility = View.VISIBLE
                }



            }
        })
    }


    fun setUpAboutUI() {
        binding.layout.etName.setText(userData.results.first_name)
        binding.layout.etLastName.setText(userData.results.last_name)
        binding.layout.bio.setText(userData.results.about)
        binding.layout.etEmail.setText(userData.results.email)
        binding.layout.etNumber.setText(userData.results.number)
        binding.layout.etHowTown.setText(userData.results.profile_data[0].home_town)
        binding.layout.etCurrentCity.setText(userData.results.city)

        ImageLoaderHelperGlide.setGlide(this,binding.ivBackground,userData.results.cover_img1)
        ImageLoaderHelperGlide.setGlide(this,binding.ivUserThumb,userData.results.profile_img1)


        binding.etWebsite.setText(userData.results.company)

        if (userData.results.profile_data[0].marital_status == "Male"){
            binding.layout.rdmale.setChecked(true)
        }else if (userData.results.profile_data[0].marital_status == "Female"){
            binding.layout.rdFamel.setChecked(true)
        }else if (userData.results.profile_data[0].marital_status == "Other"){
            binding.layout.rdOthers.setChecked(true)
        }


        educationAdapter = EducationListAdapter(networkViewModel,this, this)
        binding.layout.rvEducation.adapter=educationAdapter
        binding.layout.rvEducation.layoutManager= LinearLayoutManager(this)

        educationAdapter.submitList(userData.results.profile_data.get(0).education)


        professionListAdapter = ProfessionListAdapter(networkViewModel,this, this)
        binding.layout.rvProfession.adapter=professionListAdapter
        binding.layout.rvProfession.layoutManager= LinearLayoutManager(this)

        professionListAdapter.submitList(userData.results.profile_data.get(0).profession)


        profilePictureAdapter =  ProfilePictureAdapter(networkViewModel, this, this)

        if (userData.results.profile_img.isNotEmpty()){
            binding.rvProfilePicture.adapter=profilePictureAdapter
            profilePictureAdapter.submitList(userData.results.profile_img)
            binding.layoutProfileImages.visibility=View.VISIBLE
        }else{
            binding.layoutProfileImages.visibility=View.GONE
        }

        coverPictureAdapter =  CoverPictureAdapter(networkViewModel, this, this)


        if (userData.results.cover_img.isNotEmpty()){
            binding.rvCoverPicture.adapter=profilePictureAdapter
            coverPictureAdapter.submitList(userData.results.cover_img)
            binding.layoutCoverImages.visibility=View.VISIBLE
        }else{
            binding.layoutCoverImages.visibility=View.GONE
        }



        binding.rvFeeds.layoutManager = LinearLayoutManager(this)

        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(this, Observer {
            Log.d("asdasdasd", "oaspiasddsad")
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })

    }


    override fun onClickItemEdit(position: Education, index: Int) {
        var dialogAddEditProfession= DialogAddEditEducation(this,position,networkViewModel,true,object :DialogAddEditEducation.Callbackk{


            override fun onSuccessfullyEditedEducation(education: Education) {
                userData.results.profile_data[0].education[0]=education
                networkViewModel.profileLiveData.postValue(userData)
            }
        })
        dialogAddEditProfession.show()
    }

    override fun onClickItemProfessionEdit(position: Profession, index: Int) {
        var dialogAddEditProfession= DialogAddEditProfession(this,position,networkViewModel,true,object :DialogAddEditProfession.Callbackk{
            override fun onSuccessfullyEditedProfession(profession: Profession) {
                userData.results.profile_data[0].profession[0]=profession
                networkViewModel.profileLiveData.postValue(userData)
            }
        })
        dialogAddEditProfession.show()
    }

    override fun onClickItemEdit(position: ProfileImg, index: Int) {

    }

    override fun onClickItemEdit(position: CoverImg, index: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickOnViewComments(postId: Int) {
        TODO("Not yet implemented")
    }

}
