package com.stalmate.user.view.profile

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.ActivityOptions
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.ActivityProfileEditBinding
import com.stalmate.user.model.*
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.PriceFormatter
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.adapter.ProfileAboutAdapter
import com.stalmate.user.view.dialogs.DialogAddEditEducation
import com.stalmate.user.view.dialogs.DialogAddEditProfession
import com.stalmate.user.view.dialogs.DialogVerifyNumber
import com.wedguruphotographer.adapter.CustumSpinAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class ActivityProfileEdit : BaseActivity(), EducationListAdapter.Callbackk, ProfessionListAdapter.Callbackk,
    AdapterFeed.Callbackk , DialogVerifyNumber.Callbackk{

    private lateinit var binding: ActivityProfileEditBinding
    var WRITE_REQUEST_CODE = 100
    private var GANDER: String = ""
    var merriage: String = ""
    var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    lateinit var userData: ModelUser
    var imageFile: File? = null
    var isCoverImage = false
    private lateinit var mAccount: Account
    var isNumberVerify : Boolean = false
    private lateinit var educationAdapter: EducationListAdapter
    private lateinit var professionListAdapter: ProfessionListAdapter
    private lateinit var profilePictureAdapter: ProfileAlbumAdapter
    private lateinit var coverPictureAdapter: ProfileAlbumAdapter
    private lateinit var blockedUserAdapter: BlockedUserAdapter
    private  var selectedMarriageStatus=""
    lateinit var feedAdapter: AdapterFeed
    var verifyPhoneNumber = ""

    override fun onClick(viewId: Int, view: View?) {
    }
    var currentYear=""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        setupSpinnerListener()
        getUserProfileData()
        var cal = Calendar.getInstance()
        currentYear = (cal.get(Calendar.YEAR) - 13).toString()

        binding.buttonSyncContacts.setOnClickListener {
            retreiveGoogleContacts()
        }

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
        callForAlbum()
    }

    /*override fun onResume() {
        super.onResume()
        getUserProfileData()
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
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
            }else  if (currentYear < selectedYear) {
                makeToast("Your age should be 13 years or more")
            }




            else  if (verifyPhoneNumber.isNotEmpty()) {

                if (verifyPhoneNumber == binding.layout.etNumber.text.toString()){
                    updateProfileApiHit()
                }else{
                    makeToast("Please verify the mobile number")
                }

            } else {
                makeToast("Please verify the mobile number")
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
                        educationAdapter.addToList(education)
                        networkViewModel.profileLiveData.postValue(userData)
                        professionListAdapter.notifyDataSetChanged()
                        educationAdapter.notifyDataSetChanged()
                        getUserProfileData()
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
                        professionListAdapter.addToList(profession)
                        networkViewModel.profileLiveData.postValue(userData)
                        getUserProfileData()
                    }
                })
            dialogAddEditProfession.show()
        }


        binding.layout.btnverify.setOnClickListener {

           if (binding.layout.etNumber.text.toString().isNotEmpty()) {

               if (binding.layout.etNumber.text!!.length >= 8){

               val hashMap = HashMap<String, String>()
               hashMap["number"] = binding.layout.etNumber.text.toString()
               networkViewModel.numberVerify(hashMap)
               networkViewModel.numberVerifyData.observe(this) {

                   it.let {
                       if (it!!.status == true) {
                           var dialoguenumberVerify = DialogVerifyNumber(
                               this,
                               networkViewModel,
                               binding.layout.etNumber.text.toString(),
                               this
                           )
                           dialoguenumberVerify.show()
                       } else {
                           makeToast(it.message)
                       }
                   }
               }
           }else{
                   makeToast(getString(R.string.please_enter_mobile_number_more_then))
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
                IntentHelper.getPhotoGalleryAlbumScreen(this)!!.putExtra("viewType", "viewPhotoListing")
                    .putExtra("albumId", "profile_img")
            )

        }


        binding.buttonSeeMoreCover.setOnClickListener {
            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(this)!!.putExtra("viewType", "viewPhotoListing")
                    .putExtra("albumId", "cover_img")
            )

        }

        binding.buttonSeeAllBlockList.setOnClickListener {

        }


    }


    private fun updateProfileApiHit() {

        fun getRequestBody(str: String?): RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

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
                makeToast(it!!.message)

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
                /*if (it.results.number.isNotEmpty()){
                    isNumberVerify = true
                }*/

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

        getAlbumPhotosById("profile_img")
        getAlbumPhotosById("cover_img")
        fetchDOB(userData.results.dob!!)
        binding.layout.etName.setText(userData.results.first_name)
        binding.layout.etLastName.setText(userData.results.last_name)
        binding.layout.bio.setText(userData.results.about)
        binding.layout.filledTextEmail.setText(userData.results.email)
        verifyPhoneNumber = userData.results.number!!
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
        educationAdapter.submitList(userData.results.profile_data[0].education)

        educationAdapter.notifyDataSetChanged()
        professionListAdapter = ProfessionListAdapter(networkViewModel, this, this)
        binding.layout.rvProfession.adapter = professionListAdapter
        binding.layout.rvProfession.layoutManager = LinearLayoutManager(this)

        professionListAdapter.submitList(userData.results.profile_data[0].profession)

        professionListAdapter.notifyDataSetChanged()
        binding.rvFeeds.layoutManager = LinearLayoutManager(this)

        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(this, Observer {
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })

        setUpAboutUI("Photos")



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
                    educationAdapter.notifyDataSetChanged()
                }
            })
        dialogAddEditProfession.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    professionListAdapter.notifyDataSetChanged()
                }
            })
        dialogAddEditProfession.show()
    }


    override fun onClickOnViewComments(postId: Int) {

    }


    private fun getAlbumPhotosById(id: String) {
        val hashMap = HashMap<String, String>()
        hashMap["album_id"] = id
        hashMap["limit"]="5"
        networkViewModel.getAlbumPhotos(hashMap)
        networkViewModel.photoLiveData.observe(this) {
            it.let {

                if (it!!.results.isNotEmpty()) {
                    if (id == "cover_img") {
                        coverPictureAdapter = ProfileAlbumAdapter(networkViewModel, this, id)
                        binding.rvCoverPicture.layoutManager = GridLayoutManager(this, 5)
                        if (userData.results.cover_img.isNotEmpty()) {
                            binding.rvCoverPicture.adapter = coverPictureAdapter
                            coverPictureAdapter.submitList(it.results)
                            binding.layoutCoverImages.visibility = View.VISIBLE
                        } else {
                            binding.layoutCoverImages.visibility = View.GONE
                        }
                    } else if (id == "profile_img") {

                        profilePictureAdapter = ProfileAlbumAdapter(networkViewModel, this, id)
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

    override fun onSuccessFullyAddNumber() {
        getUserProfileData()
    }


    private fun retreiveGoogleContacts() {

        mAccount= createSyncAccount(this)

        var bundle=Bundle()
        bundle.putBoolean("force",true)
        bundle.putBoolean("expedited",true)


        Log.d("asldkjalsda","sync")
        ContentResolver.requestSync(mAccount, "com.stalmate.user", bundle)
    }

    fun createSyncAccount(context: Context): Account {

        // Create the account type and default account
        val newAccount = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        // Get an instance of the Android account manager
        val accountManager = context.getSystemService(
            ACCOUNT_SERVICE
        ) as AccountManager
        /*
     * Add the account and account type, no password or user data
     * If successful, return the Account object, otherwise report an error.
     */return if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
          * If you don't set android:syncable="true" in
          * in your <provider> element in the manifest,
          * then call context.setIsSyncable(account, AUTHORITY, 1)
          * here.
          */
            Log.d("asdasd","pppooo")
            ContentResolver.setIsSyncable(newAccount, "com.android.contacts", 1)
            ContentResolver.setSyncAutomatically(newAccount, "com.android.contacts", true)
            newAccount
        } else {
            Log.d("asdasd","ppp")
            /*
          * The account exists or some other error occurred. Log this, report it,
          * or handle it internally.
          */
            Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        }
    }


    fun callForAlbum() {



        binding.albumLayout.tvAlbumPhotoSeeMore.setOnClickListener {
            if (binding.albumLayout.photoTab.selectedTabPosition ==0){
                startActivity(IntentHelper.getPhotoGalleryAlbumScreen(this)!!.putExtra("viewType", "viewNormal").putExtra("type", "photos"))
            }else{
                startActivity(IntentHelper.getPhotoGalleryAlbumScreen(this)!!.putExtra("viewType", "viewNormal").putExtra("type", "albums"))
            }
        }

        binding.albumLayout.photoTab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                if (position == 0){
                    setUpAboutUI("Photos")
                }else if(position == 1){
                    setUpAboutUI("Albums")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


    }


    private lateinit var albumImageAdapter: ProfileAlbumImageAdapter
    private lateinit var albumAdapter: SelfProfileAlbumAdapter

    fun setUpAboutUI(tabType : String) {

        if (tabType== "Photos") {
            albumImageAdapter = ProfileAlbumImageAdapter(networkViewModel,this, "")
            binding.albumLayout.rvPhotoAlbumData.adapter = albumImageAdapter
            albumImageAdapter.submitList(userData.results.photos)
        }else if (tabType== "Albums"){
            albumAdapter = SelfProfileAlbumAdapter(networkViewModel, this, "")
            binding.albumLayout.rvPhotoAlbumData.adapter =albumAdapter
            albumAdapter.submitList(userData.results.albums)

        }

    }
}
