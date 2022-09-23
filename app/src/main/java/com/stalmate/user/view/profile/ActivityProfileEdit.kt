package com.stalmate.user.view.profile

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.shape.CornerFamily
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.ActivityProfileEditBinding
import com.stalmate.user.databinding.DialogueNumberVerifyBinding
import com.stalmate.user.model.*
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.PriceFormatter
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dialogs.DialogAddEditEducation
import com.stalmate.user.view.dialogs.DialogAddEditProfession
import com.stalmate.user.view.dialogs.DialogVerifyNumber
import com.wedguruphotographer.adapter.CustumSpinAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class ActivityProfileEdit : BaseActivity(), EducationListAdapter.Callbackk,
    ProfessionListAdapter.Callbackk,
    AdapterFeed.Callbackk {

    private lateinit var binding: ActivityProfileEditBinding
    var WRITE_REQUEST_CODE = 100
    private var GANDER: String = ""

    var merriage: String = ""
    var permissions =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    lateinit var userData: ModelUser
    var marriageStatus = arrayOf("Single", "Marriage")
    var imageFile: File? = null
    var isCoverImage = false
    var isNumberVerify : Boolean = false
    private lateinit var educationAdapter: EducationListAdapter
    private lateinit var professionListAdapter: ProfessionListAdapter
    private lateinit var profilePictureAdapter: ProfileAlbumAdapter
    private lateinit var coverPictureAdapter: ProfileAlbumAdapter
    private lateinit var blockedUserAdapter: BlockedUserAdapter

    private lateinit var marriageAdapter: CustumSpinAdapter
    val marriageList: ArrayList<ModelCustumSpinner> = ArrayList<ModelCustumSpinner>()
    private  var selectedMarriageStatus=""

    lateinit var feedAdapter: AdapterFeed
    override fun onClick(viewId: Int, view: View?) {


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        setupSpinnerListener()
        getUserProfileData()



        feedAdapter = AdapterFeed(networkViewModel, this, this)
        binding.rvFeeds.setNestedScrollingEnabled(false);
        binding.rvFeeds.adapter = feedAdapter
        val radius = resources.getDimension(R.dimen.dp_10)
        binding.ivBackground.setShapeAppearanceModel(
            binding.ivBackground.getShapeAppearanceModel()
                .toBuilder()
                .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                .build()
        );


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


        clickLister()
    }

    private fun clickLister() {

        binding.idCoverPhoto.setOnClickListener {

            isCoverImage = true
            startCrop()
        }

        binding.idCameraProfile.setOnClickListener {
            isCoverImage = false
            startCrop()
        }


        binding.btnCrateAccount.setOnClickListener {

            if (ValidationHelper.isNull(selectedMarriageStatus)) {
                makeToast("Please select marriage Status")
            } else if (isNumberVerify) {
                updateProfileApiHit()
            } else {
                makeToast("Please verfiy the mobile number")
            }

        }

        binding.layout.tvAddMore.setOnClickListener {


            var dialogAddEditEducation = DialogAddEditEducation(
                this,
                Education("", "", 0, "", "", "", "", "", "", ""),
                networkViewModel,
                false,
                object : DialogAddEditEducation.Callbackk {
                    override fun onSuccessfullyEditedEducation(education: Education) {
                        userData.results.profile_data[0].education.add(education)
                        networkViewModel.profileLiveData.postValue(userData)
                    }
                })
            dialogAddEditEducation.show()


        }

        binding.layout.tvaddMoreProfession.setOnClickListener {

            var dialogAddEditProfession = DialogAddEditProfession(
                this,
                Profession("", "", 0, "", "", "", "", "", "", "", "", ""),
                networkViewModel,
                false,
                object : DialogAddEditProfession.Callbackk {
                    override fun onSuccessfullyEditedProfession(profession: Profession) {
                        userData.results.profile_data[0].profession.add(profession)
                        networkViewModel.profileLiveData.postValue(userData)

                    }
                })
            dialogAddEditProfession.show()
        }


        binding.layout.btnverify.setOnClickListener {

           if (binding.layout.etNumber.text.toString().isNotEmpty()) {

               val hashMap = HashMap<String, String>()
               hashMap["number"] = binding.layout.etNumber.text.toString()
               networkViewModel.numberVerify(hashMap)
               networkViewModel.numberVerifyData.observe(this) {

                   it.let {
                       if (it!!.status == true) {
                           var dialoguenumberVerify = DialogVerifyNumber(this, networkViewModel, binding.layout.etNumber.text.toString())
                           dialoguenumberVerify.show()

                       }else{
                           makeToast(it.message)
                       }
                   }
               }
           }else{
               makeToast(getString(R.string.please_enter_mobile_number))
           }

        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.buttonSeemoreProfile.setOnClickListener {
            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(this)!!.putExtra("viewType", "viewListing")
                    .putExtra("type", "profile_img")
            )

        }

        binding.buttonSeeMoreCover.setOnClickListener {
            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(this)!!.putExtra("viewType", "viewListing")
                    .putExtra("type", "cover_img")
            )

        }

        binding.buttonSeeAllBlockList.setOnClickListener {

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
            getRequestBody(selectedYear +"-" + selectedMonth + "-" + selectedDay),
            getRequestBody(merriage),
            getRequestBody(binding.layout.etHowTown.text.toString()),
            getRequestBody(binding.layout.etCurrentCity.text.toString()),
            getRequestBody(""),
            getRequestBody(binding.etWebsite.text.toString()),
            getRequestBody(GANDER),
        )

        networkViewModel.UpdateProfileLiveData.observe(this, Observer {

            it.let {
//                makeToast(it!!.message)
                var hashMap = HashMap<String, String>()
                networkViewModel.getProfileData(hashMap)
                onBackPressed()
            }
        })
    }


    private fun updateProfileImageApiHit() {


        val thumbnailBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)

        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
            "cover_img".takeIf { isCoverImage } ?: "profile_img",
            imageFile!!.name,
            thumbnailBody
        ) //image[] for multiple image
        networkViewModel.etsProfileApi(profile_image1)
        networkViewModel.UpdateProfileLiveData.observe(this, Observer {
            it.let {
                makeToast(it!!.message)
                var hashMap = HashMap<String, String>()
                networkViewModel.getProfileData(hashMap)
            }
        })
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

            if (isCoverImage) {
                Glide.with(this).load(uriContent).into(binding.ivBackground)
            } else {
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
        networkViewModel.getProfileData(hashMap)
        hashMap.put("limit", "5")
        hashMap.put("page", "1")
        networkViewModel.getBlockList(hashMap)


        networkViewModel.profileLiveData.observe(this, Observer {
            it.let {
                userData = it!!
                setUpAboutUI()

                if (it.results.profile_data[0].education.isNotEmpty()) {
                    binding.layout.rvEducation.visibility = View.VISIBLE
                }

                if (it.results.profile_data[0].profession.isNotEmpty()) {
                    binding.layout.rvProfession.visibility = View.VISIBLE
                }


            }
        })


        networkViewModel.blockListLiveData.observe(this, Observer {
            it.let {


                if (it!!.status) {

                    blockedUserAdapter = BlockedUserAdapter(
                        networkViewModel,
                        this,
                        object : BlockedUserAdapter.Callback {
                            override fun onListEmpty() {
                                binding.layoutBlockList.visibility = View.GONE
                            }
                        })
                    if (it.results.isEmpty()) {
                        binding.layoutBlockList.visibility = View.GONE
                    } else {
                        binding.layoutBlockList.visibility = View.VISIBLE
                    }
                    binding.rvBlockList.adapter = blockedUserAdapter
                    blockedUserAdapter.submitList(it.results as ArrayList<User>)

                }

            }
        })
    }

    fun setUpAboutUI() {

        hitphotoListApi("profile_img")
        hitphotoListApi("cover_img")
        fetchDOB(userData.results.dob)
        binding.layout.etName.setText(userData.results.first_name)
        binding.layout.etLastName.setText(userData.results.last_name)
        binding.layout.bio.setText(userData.results.about)
        binding.layout.filledTextEmail.setText(userData.results.email)

        if (userData.results.number.isNotEmpty()){
            isNumberVerify =true
        }
        binding.layout.etNumber.setText(userData.results.number)
        binding.layout.etHowTown.setText(userData.results.profile_data[0].home_town)
        binding.layout.etCurrentCity.setText(userData.results.city)

        ImageLoaderHelperGlide.setGlide(
            this,
            binding.ivBackground,
            userData.results.cover_img1,
            R.drawable.user_placeholder
        )
        ImageLoaderHelperGlide.setGlide(
            this,
            binding.ivUserThumb,
            userData.results.profile_img1,
            R.drawable.user_placeholder
        )

        binding.etWebsite.setText(userData.results.company)

        educationAdapter = EducationListAdapter(networkViewModel, this, this)
        binding.layout.rvEducation.adapter = educationAdapter
        binding.layout.rvEducation.layoutManager = LinearLayoutManager(this)
        educationAdapter.submitList(userData.results.profile_data.get(0).education)
        professionListAdapter = ProfessionListAdapter(networkViewModel, this, this)
        binding.layout.rvProfession.adapter = professionListAdapter
        binding.layout.rvProfession.layoutManager = LinearLayoutManager(this)

        professionListAdapter.submitList(userData.results.profile_data.get(0).profession)


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
        var dialogAddEditProfession = DialogAddEditEducation(
            this,
            position,
            networkViewModel,
            true,
            object : DialogAddEditEducation.Callbackk {


                override fun onSuccessfullyEditedEducation(education: Education) {
                    userData.results.profile_data[0].education[0] = education
                    networkViewModel.profileLiveData.postValue(userData)
                }
            })
        dialogAddEditProfession.show()
    }

    override fun onClickItemProfessionEdit(position: Profession, index: Int) {
        var dialogAddEditProfession = DialogAddEditProfession(
            this,
            position,
            networkViewModel,
            true,
            object : DialogAddEditProfession.Callbackk {
                override fun onSuccessfullyEditedProfession(profession: Profession) {
                    userData.results.profile_data[0].profession[0] = profession
                    networkViewModel.profileLiveData.postValue(userData)
                }
            })
        dialogAddEditProfession.show()
    }

    override fun deleteitem() {
        getUserProfileData()
    }

    override fun onClickOnViewComments(postId: Int) {

    }


    private fun hitphotoListApi(type: String) {


        val hashMap = HashMap<String, String>()
        hashMap["img_type"] = type
        hashMap["page"] = "1"
        hashMap["limit"] = "5"

        networkViewModel.photoIndexLiveData(hashMap)
        networkViewModel.photoIndexLiveData.observe(this) {
            it.let {

                if (it!!.results.isNotEmpty()) {
                    if (type == "cover_img") {
                        coverPictureAdapter = ProfileAlbumAdapter(networkViewModel, this, type)

                        binding.rvCoverPicture.layoutManager = GridLayoutManager(this, 5)
                        if (userData.results.cover_img.isNotEmpty()) {
                            binding.rvCoverPicture.adapter = coverPictureAdapter
                            coverPictureAdapter.submitList(it.results)
                            binding.layoutCoverImages.visibility = View.VISIBLE
                        } else {
                            binding.layoutCoverImages.visibility = View.GONE
                        }
                    } else if (type == "profile_img") {

                        profilePictureAdapter = ProfileAlbumAdapter(networkViewModel, this, type)
                        binding.rvProfilePicture.layoutManager = GridLayoutManager(this, 5)
                        if (userData.results.profile_img.isNotEmpty()) {
                            binding.rvProfilePicture.adapter = profilePictureAdapter
                            profilePictureAdapter.submitList(it.results)
                            binding.layoutProfileImages.visibility = View.VISIBLE
                        } else {
                            binding.layoutProfileImages.visibility = View.GONE
                        }

                    }
                }


            }

        }
    }

    var selectedDay="1"
    var selectedMonth="January"
    var selectedYear="1996"
    lateinit var dataAdapter: ArrayAdapter<String>

    fun fetchDOB(date:String){
        val calender=Calendar.getInstance()
        val datee=PriceFormatter.getDateObject(date)
        calender.time=datee
        selectedYear= calender.get(Calendar.YEAR).toString()
        selectedMonth = PriceFormatter.getMonth(date)
        selectedDay = calender.get(Calendar.DATE).toString()

        Log.d("jkabjkcbajb",selectedYear)
        Log.d("jkabjkcbajb",selectedMonth)
        Log.d("jkabjkcbajb",selectedDay)


        val selectedYearIndex = getResources().getStringArray(R.array.year).indexOf(selectedYear)
        val selectedMonthIndex = getResources().getStringArray(R.array.month).indexOf(selectedMonth)
        Log.d("asdajhksd",selectedMonthIndex.toString())
        val selectedDayIndex = getResources().getStringArray(R.array.date).indexOf(selectedDay)

        binding.layout.spYear.setSelection(selectedYearIndex)
        binding.layout.spMonth.setSelection(selectedMonthIndex)
        binding.layout.spDate.setSelection(selectedDayIndex)

        if (userData.results.gender  == "Male") {
            binding.layout.rdmale.isChecked=true
        } else if (userData.results.gender  == "Female") {
            binding.layout.rdFamel.isChecked=true
        } else if (userData.results.gender  == "Other") {
            binding.layout.rdOthers.isChecked=true
        }

        val selecteMarriegeStatus = resources.getStringArray(R.array.marrage).indexOf(userData.results.profile_data[0].marital_status)
        binding.layout.tvmarriage.setSelection(selecteMarriegeStatus)

    }


    fun setupSpinnerListener(){

        binding.layout.spDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                selectedDay = p0!!.getItemAtPosition(position).toString()
                dataAdapter= ArrayAdapter(
                    this@ActivityProfileEdit,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.month)
                )

                if (selectedDay.toInt() ==31){
                   dataAdapter= ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        resources.getStringArray(R.array.monthOfthreeOne)
                    )
                }


                if (selectedDay.toInt() ==30){
                    dataAdapter= ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        resources.getStringArray(R.array.month)
                    )
                }

                if (selectedDay.toInt() ==28){
                    dataAdapter= ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        resources.getStringArray(R.array.monthOfTwentyEight)
                    )
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
                    selectedMonth = p0!!.getItemAtPosition(position).toString()
                    Log.d("jcaujc", selectedMonth)
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
                selectedYear = p0!!.getItemAtPosition(position).toString()
                Log.d("jcaujc", selectedYear)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.layout.tvmarriage.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedMarriageStatus = parent!!.getItemAtPosition(position).toString()
                merriage = selectedMarriageStatus
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
