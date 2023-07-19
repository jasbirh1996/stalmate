package com.stalmate.user.view.profile

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentProfileEditBinding
import com.stalmate.user.model.*
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.SpinnerUtil.setSpinner
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.view.dialogs.DialogAddEditEducation
import com.stalmate.user.view.dialogs.DialogAddEditProfession
import com.stalmate.user.view.dialogs.DialogVerifyNumber
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class FragmentProfileEdit : BaseFragment(), EducationListAdapter.Callbackk,
    ProfessionListAdapter.Callbackk, AdapterFeed.Callbackk, DialogVerifyNumber.Callbackk {
    private lateinit var _binding: FragmentProfileEditBinding
    private val binding get() = _binding
    var WRITE_REQUEST_CODE = 100
    private var GANDER: String = ""
    var merriage: String = ""
    var permissions =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    lateinit var userData: ModelUser
    var imageFile: File? = null
    var isCoverImage = false
    private lateinit var mAccount: Account
    var isNumberVerify: Boolean = false
    private lateinit var educationAdapter: EducationListAdapter
    private lateinit var professionListAdapter: ProfessionListAdapter
    private lateinit var profilePictureAdapter: ProfileAlbumAdapter
    private lateinit var coverPictureAdapter: ProfileAlbumAdapter
    private lateinit var blockedUserAdapter: BlockedUserAdapter
    private lateinit var feedAdapter: AdapterFeed
    var verifyPhoneNumber = ""
    var currentYear = ""
    private var dialog: Dialog? = null

    interface CAllback {
        fun onCLickONBlockedContactSeeAllButton()
        fun onCLickONSyncContactButton()
        fun onClickBackPress()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserProfileData()
        blockList()
        binding.ivBackground.setOnClickListener {

            startActivity(
                IntentHelper.getFullImageScreen(requireActivity())!!
                    .putExtra("picture", userData.results?.cover_img1)
            )
        }
        binding.ivUserThumb.setOnClickListener {
            startActivity(
                IntentHelper.getFullImageScreen(requireActivity())!!
                    .putExtra("picture", userData.results?.profile_img1)
            )
        }
        binding.buttonSyncContacts.setOnClickListener {
            val fragmentAlertDialogAccessContacts = FragmentAlertDialogAccessContacts(object :
                FragmentAlertDialogAccessContacts.Callback {
                override fun onCLickONAccessButton() {
                    // callback.onCLickONSyncContactButton()
                    //retreiveGoogleContacts()

                    findNavController().navigate(R.id.action_fragmentProfileEdit_to_fragmentSync22)

                }
            })
            val bundle = Bundle()
            bundle.putString("LEAVE_INTENT_TYPE", "LEAVE")
            fragmentAlertDialogAccessContacts.arguments = bundle
            fragmentAlertDialogAccessContacts.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.CustomAlertDialog
            )
            fragmentAlertDialogAccessContacts.dialog?.window?.setLayout(100, 100)
            fragmentAlertDialogAccessContacts.show(
                childFragmentManager,
                "FragmentAlertDialogAccessContacts"
            )
        }
        feedAdapter = AdapterFeed(networkViewModel, requireContext(), requireActivity())
        binding.rvFeeds.isNestedScrollingEnabled = false
        binding.rvFeeds.adapter = feedAdapter
        val radius = resources.getDimension(R.dimen.dp_10)
        binding.ivBackground.shapeAppearanceModel = binding.ivBackground.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()


        requestPermissions(permissions, WRITE_REQUEST_CODE)
        binding.layout.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "Male"
                binding.layout.rdmale.isChecked = true
                binding.layout.rdFamel.isChecked = false
                binding.layout.rdOthers.isChecked = false
            }
        }

        binding.layout.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "Female"
                binding.layout.rdmale.isChecked = false
                binding.layout.rdFamel.isChecked = true
                binding.layout.rdOthers.isChecked = false
            }
        }

        binding.layout.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "Other"
                binding.layout.rdmale.isChecked = false
                binding.layout.rdFamel.isChecked = false
                binding.layout.rdOthers.isChecked = true
            }
        }

        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.buttonSeemoreProfile.setOnClickListener {
            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(requireActivity())!!
                    .putExtra("viewType", "viewPhotoListing")
                    .putExtra("albumId", "profile_img")
            )

        }


        binding.buttonSeeMoreCover.setOnClickListener {
            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(requireActivity())!!
                    .putExtra("viewType", "viewPhotoListing")
                    .putExtra("albumId", "cover_img")
            )

        }

        binding.buttonSeeAllBlockList.setOnClickListener {
            //callback.onCLickONBlockedContactSeeAllButton()
            IntentHelper.getBlockListScreen(requireActivity())?.let { it1 -> startActivity(it1) }

        }
        binding.buttonFindFriends.setOnClickListener {
            IntentHelper.getSearchScreen(requireContext())?.let { it1 -> startActivity(it1) }
        }
        clickLister()
        callForAlbum()

        networkViewModel.mThrowable.observe(requireActivity()) {
//            binding.progressBar.visibility = View.GONE
            Toast.makeText(this.requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::_binding.isInitialized) {
            _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    fun blockList() {
        networkViewModel.blockListLiveData.observe(requireActivity()) {
            it.let {

                if (it!!.status) {
                    blockedUserAdapter = BlockedUserAdapter(
                        networkViewModel,
                        requireActivity(),
                        object : BlockedUserAdapter.Callback {
                            override fun onListEmpty() {
                                binding.layoutBlockList.visibility = View.GONE
                            }

                            override fun onItemRemove() {
                                getBlockList()
                            }

                        }, prefManager?.access_token.toString()
                    )

                    if (it?.results.isNullOrEmpty()) {
                        binding.layoutBlockList.visibility = View.GONE
                    } else {
                        binding.layoutBlockList.visibility = View.VISIBLE
                        binding.buttonSeeAllBlockList.visibility = View.VISIBLE
                        val firstTwoElements: List<User?>
                        blockedUserAdapter.list.clear()
                        /*     if (it?.results?.size > 2) {
                                 firstTwoElements = it?.results?.subList(0, 2)
                                 binding.buttonSeeAllBlockList.visibility = View.VISIBLE
                             } else {
                                 firstTwoElements = it?.results
                             }*/
                        it?.results?.let { it1 -> blockedUserAdapter.submitList(it1) }
                        binding.rvBlockList.adapter = blockedUserAdapter
                        blockedUserAdapter.notifyDataSetChanged()


                    }
                }

            }
        }
    }


    fun getBlockList() {
        val hashMap = HashMap<String, String>()
        hashMap.put("limit", "2")
        hashMap.put("page", "1")
        networkViewModel.getBlockList(hashMap)
        blockList()
    }


    var selectedDay = ""
    var selectedMonth = ""
    var selectedYear = ""
    fun getUserProfileData() {
        val hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap, prefManager?.access_token.toString())
        hashMap.put("limit", "5")
        hashMap.put("page", "1")
        getBlockList()
        networkViewModel.profileLiveData.observe(requireActivity()) {
            it.let {
                userData = it!!
                setUpAboutUI()
                if (!it?.results?.profileData()?.education.isNullOrEmpty()) {
                    binding.layout.rvEducation.visibility = View.VISIBLE
                }
                if (!it?.results?.profileData()?.profession.isNullOrEmpty()) {
                    binding.layout.rvProfession.visibility = View.VISIBLE
                }
                if (!it?.results?.number.isNullOrEmpty()) {
                    isNumberVerify = true
                }
            }
        }
    }

    private lateinit var albumImageAdapter: ProfileAlbumImageAdapter
    private lateinit var albumAdapter: SelfProfileAlbumAdapter

    private fun setUpAboutUI() {
        val changeUsernameBottomSheet = UpdateUsernameBottomSheet(
            usernameUpdated = {
                getUserProfileData()
            })
        changeUsernameBottomSheet.arguments = Bundle().apply {
            putString("etUsername", userData.results?.user_name)
        }
        binding.layout.etUsername.setOnClickListener {
            if (changeUsernameBottomSheet.isAdded) {
                return@setOnClickListener
            }
            changeUsernameBottomSheet.show(
                childFragmentManager,
                changeUsernameBottomSheet.tag
            )
        }

        getAlbumPhotosById("profile_img")
        getAlbumPhotosById("cover_img")

        binding.layout.etName.setText(userData.results?.first_name)
        binding.layout.etLastName.setText(userData.results?.last_name)
        binding.layout.etUsername.setText(userData.results?.user_name)
        binding.layout.bio.setText(userData.results?.about)
        binding.etWebsite.setText(userData.results?.url)
        binding.layout.filledTextEmail.text = userData.results?.email
        verifyPhoneNumber = userData.results?.number.toString()
        binding.layout.etNumber.setText(userData.results?.number)
        binding.layout.etHowTown.setText(userData.results?.profileData()?.home_town)
        binding.layout.etCurrentCity.setText(userData.results?.city)

        ImageLoaderHelperGlide.setGlide(
            requireContext(),
            binding.ivBackground,
            userData.results?.cover_img1,
            R.drawable.user_placeholder
        )
        ImageLoaderHelperGlide.setGlide(
            requireContext(),
            binding.ivUserThumb,
            userData.results?.profile_img1,
            R.drawable.user_placeholder
        )

//        binding.etWebsite.setText(userData.results?.company)
        educationAdapter = EducationListAdapter(networkViewModel, requireContext(), this)

        binding.layout.rvEducation.adapter = educationAdapter
        binding.layout.rvEducation.layoutManager = LinearLayoutManager(requireContext())
        userData.results?.profileData()?.education?.let { educationAdapter.submitList(it) }
        educationAdapter.notifyDataSetChanged()
        professionListAdapter = ProfessionListAdapter(networkViewModel, requireContext(), this)
        binding.layout.rvProfession.adapter = professionListAdapter
        binding.layout.rvProfession.layoutManager = LinearLayoutManager(requireActivity())

        userData.results?.profileData()?.profession?.let { professionListAdapter.submitList(it) }

        professionListAdapter.notifyDataSetChanged()
        binding.rvFeeds.layoutManager = LinearLayoutManager(requireContext())

        val hashmap = HashMap<String, String>()
        hashmap.put("page", 1.toString())
        hashmap.put("id_user", "")
        hashmap.put("fun_id", "")
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(requireActivity()) {
            it.let {
                if (!it?.results.isNullOrEmpty()) {
                    it?.results?.let { it1 -> feedAdapter.submitList(it1) }
                }
            }
        }

        setUpAboutUI("Photos")
        fetchDOB(userData.results?.dob.toString())
    }

    fun fetchDOB(date: String) {

        GANDER = userData.results?.gender.toString()
        when (userData.results?.gender) {
            "Male" -> {
                binding.layout.rdmale.isChecked = true
            }
            "Female" -> {
                binding.layout.rdFamel.isChecked = true
            }
            "Other" -> {
                binding.layout.rdOthers.isChecked = true
            }
        }

        try {
            merriage = userData.results?.profileData()?.marital_status.toString()
            binding.layout.tvmarriage.setSpinner(
                listFromResources = R.array.marrage,
                setSelection = resources.getStringArray(R.array.marrage)
                    .indexOf(merriage),
                onItemSelectedListener = {
                    merriage =
                        if ("Marital Status" != binding.layout.tvmarriage.selectedItem.toString())
                            binding.layout.tvmarriage.selectedItem.toString()
                        else {
                            Toast.makeText(
                                this.requireContext(),
                                "Select marital status",
                                Toast.LENGTH_SHORT
                            ).show()
                            ""
                        }
                }
            )
            date.split("-").apply {
                selectedDay= this[2]
                selectedMonth= this[1]
                selectedYear= this[0]
            }

           /* selectedDay = date.split("-")[0]
            selectedMonth = date.split("-")[1]
            selectedYear = date.split("-")[2]*/

            binding.layout.spDate.setSpinner(
                listFromResources = R.array.date,
                setSelection = resources.getStringArray(R.array.date).indexOf(selectedDay),
                onItemSelectedListener = {
                    selectedDay = binding.layout.spDate.selectedItem.toString()
                }
            )
            binding.layout.spMonth.setSpinner(
                listFromResources = R.array.month,
//                setSelection =  resources.getStringArray(R.array.month).map { it.substring(0,3) }.indexOf(selectedMonth),
                setSelection =  resources.getStringArray(R.array.month).indexOfFirst { it.contains(selectedMonth) },
//                setSelection = resources.getStringArray(R.array.month).indexOf(selectedMonth),
                onItemSelectedListener = {
                    selectedMonth = binding.layout.spMonth.selectedItem.toString()
                }
            )
            binding.layout.spYear.setSpinner(
                listFromResources = R.array.year,
                setSelection = resources.getStringArray(R.array.year).indexOf(selectedYear),
                onItemSelectedListener = {
                    selectedYear = binding.layout.spYear.selectedItem.toString()
                }
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getAlbumPhotosById(id: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["album_id"] = id
//        hashMap["user_id"] = prefManager?._id.toString()
//        hashMap["limit"] = 100
//        hashMap["page"] = 1
        networkViewModel.getAlbumPhotos(hashMap)
        networkViewModel.photoLiveData.observe(requireActivity()) {
            it.let {

                if (!it?.results.isNullOrEmpty()) {
                    if (id == "cover_img") {
                        coverPictureAdapter =
                            ProfileAlbumAdapter(networkViewModel, requireActivity(), id)
                        binding.rvCoverPicture.layoutManager =
                            GridLayoutManager(requireActivity(), 5)
                        if (!userData.results?.cover_img.isNullOrEmpty()) {
                            binding.rvCoverPicture.adapter = coverPictureAdapter
                            it?.results?.let { coverPictureAdapter.submitList(it) }
                            binding.layoutCoverImages.visibility = View.VISIBLE
                        } else {
                            binding.layoutCoverImages.visibility = View.GONE
                        }
                    } else if (id == "profile_img") {

                        profilePictureAdapter =
                            ProfileAlbumAdapter(networkViewModel, requireActivity(), id)
                        binding.rvProfilePicture.layoutManager =
                            GridLayoutManager(requireActivity(), 5)
                        if (!userData.results?.profile_img.isNullOrEmpty()) {
                            binding.rvProfilePicture.adapter = profilePictureAdapter
                            it?.results?.let { profilePictureAdapter.submitList(it) }
                            binding.layoutProfileImages.visibility = View.VISIBLE
                        } else {
                            binding.layoutProfileImages.visibility = View.GONE
                        }

                    }
                }
            }
        }
    }

    fun setUpAboutUI(tabType: String) {
        if (tabType == "Photos") {
            albumImageAdapter = ProfileAlbumImageAdapter(networkViewModel, requireActivity(), "")
            binding.albumLayout.rvPhotoAlbumData.adapter = albumImageAdapter
            userData.results?.photos?.let { albumImageAdapter.submitList(it) }
        } else if (tabType == "Albums") {
          /*  albumAdapter = SelfProfileAlbumAdapter(networkViewModel, requireActivity(), "")
            binding.albumLayout.rvPhotoAlbumData.adapter = albumAdapter
            albumAdapter.submitList(userData.results?.albums)*/

            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!
                    .putExtra("viewType", "viewNormal").putExtra("type", "photos")
            )

        }

    }

    fun callForAlbum() {

        binding.albumLayout.tvAlbumPhotoSeeMore.setOnClickListener {
            if (binding.albumLayout.photoTab.selectedTabPosition == 0) {
                   startActivity(
                    IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!
                        .putExtra("viewType", "viewNormal").putExtra("type", "photos")
                )
            } else {
                startActivity(
                    IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!
                        .putExtra("viewType", "viewNormal").putExtra("type", "albums")
                )
            }
        }

        binding.albumLayout.photoTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                if (position == 0) {
                    setUpAboutUI("Photos")
                } else if (position == 1) {
                    setUpAboutUI("Albums")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

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
            if (ValidationHelper.isNull(merriage)) {
                makeToast("Please select marriage Status")
            } else if (selectedYear.isNotEmpty() && (Calendar.getInstance()[Calendar.YEAR] < selectedYear.toInt())) {
                makeToast("Your age should be 13 years or more")
            } else if (verifyPhoneNumber.isNotEmpty()) {
                if (verifyPhoneNumber == binding.layout.etNumber.text.toString()) {
                    updateProfileApiHit()
                } else {
                    makeToast("Please verify the mobile number")
                }
            } else {
                makeToast("Please verify the mobile number")
            }
        }

        binding.layout.tvAddMore.setOnClickListener {

            val dialogAddEditEducation = DialogAddEditEducation(
                requireActivity(),
                Education("", "", 0, "", "", "", "", "", "", ""),
                networkViewModel,
                false,
                object : DialogAddEditEducation.Callbackk {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onSuccessfullyEditedEducation(education: Education) {
                        userData.results?.profileData()?.education?.add(education)
                        educationAdapter.list.add(education)
                        educationAdapter.notifyDataSetChanged()
                    }
                })
            dialogAddEditEducation.show()
        }

        binding.layout.tvaddMoreProfession.setOnClickListener {
            val dialogAddEditProfession = DialogAddEditProfession(
                requireContext(),
                Profession(
                    "",
                    "",
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                ),
                networkViewModel,
                false,
                object : DialogAddEditProfession.Callbackk {
                    override fun onSuccessfullyEditedProfession(profession: Profession) {
                        userData.results?.profileData()?.profession?.add(profession)
                        professionListAdapter.list.add(profession)
                        professionListAdapter.notifyDataSetChanged()
                    }
                })
            dialogAddEditProfession.show()
        }

        binding.layout.btnverify.setOnClickListener {
            if (binding.layout.etNumber.text.toString().isNotEmpty()) {
                if (binding.layout.etNumber.text!!.length >= 8) {
                    val hashMap = HashMap<String, String>()
                    hashMap["number"] = binding.layout.etNumber.text.toString()
                    networkViewModel.numberVerify(prefManager?.access_token.toString(),hashMap)
                    networkViewModel.numberVerifyData.observe(requireActivity()) {
                        verifyPhoneNumber = binding.layout.etNumber.text.toString()
                        it.let {
                            if (it!!.status == true) {
                                var dialoguenumberVerify = DialogVerifyNumber(
                                    requireContext(),
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
                } else {
                    makeToast(getString(R.string.please_enter_mobile_number_more_then))
                }
            } else {
                makeToast(getString(R.string.please_enter_mobile_number))
            }
        }
    }

    private fun updateProfileApiHit() {
        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

        networkViewModel.etsProfileApi1(
            access_token = prefManager?.access_token.toString(),
            first_name = getRequestBody(binding.layout.etName.text.toString()),
            last_name = getRequestBody(binding.layout.etLastName.text.toString()),
            about = getRequestBody(binding.layout.bio.text.toString()),
            countrycode = getRequestBody(binding.layout.ccpMObile.selectedCountryCode.toString()),
            number = getRequestBody(binding.layout.etNumber.text.toString()),
            dob = getRequestBody("$selectedDay-$selectedMonth-$selectedYear"),
            marital_status = getRequestBody(merriage),
            home_town = getRequestBody(binding.layout.etHowTown.text.toString()),
            city = getRequestBody(binding.layout.etCurrentCity.text.toString()),
            url = getRequestBody(binding.etWebsite.text.toString()),
            gender = getRequestBody(GANDER)
        )

        networkViewModel.UpdateProfileLiveData.observe(requireActivity()) {
            it.let {
//              makeToast(it!!.message)
                val hashMap = HashMap<String, String>()
                makeToast(it!!.message)
            }
            (context as BaseActivity).onBackPressed()
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

    private fun retreiveGoogleContacts() {
        mAccount = createSyncAccount(requireContext())

        var bundle = Bundle()
        bundle.putBoolean("force", true)
        bundle.putBoolean("expedited", true)


        Log.d("asldkjalsda", "sync")
        ContentResolver.requestSync(mAccount, "com.stalmate.user", bundle)
    }

    /*Cover Image Picker */
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            var uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            imageFile = File(result.getUriFilePath(requireContext(), true)!!)
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

    private fun updateProfileImageApiHit() {

        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)

        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
            "cover_img".takeIf { isCoverImage } ?: "profile_img",
            imageFile!!.name,
            thumbnailBody
        ) //image[] for multiple image
        networkViewModel.etsProfileApi(prefManager?.access_token.toString(),profile_image1)
        networkViewModel.UpdateProfileLiveData.observe(requireActivity()) {
            it.let {

                getUserProfileData()
                makeToast(it!!.message)
                var hashMap = HashMap<String, String>()
            }
            (context as BaseActivity).onBackPressed()
        }
    }

    fun createSyncAccount(context: Context): Account {

        // Create the account type and default account
        val newAccount = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        // Get an instance of the Android account manager
        val accountManager = context.getSystemService(
            AppCompatActivity.ACCOUNT_SERVICE
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
            Log.d("asdasd", "pppooo")
            ContentResolver.setIsSyncable(newAccount, "com.android.contacts", 1)
            ContentResolver.setSyncAutomatically(newAccount, "com.android.contacts", true)
            newAccount
        } else {
            Log.d("asdasd", "ppp")
            /*
          * The account exists or some other error occurred. Log this, report it,
          * or handle it internally.
          */
            Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        }
    }

    override fun onClickOnViewComments(postId: Int) {
        TODO("Not yet implemented")
    }

    override fun onCLickItem(item: ResultFuntime) {

    }

    override fun onClickOnLikeButtonReel(feed: ResultFuntime) {

    }

    override fun onClickOnFollowButtonReel(feed: ResultFuntime) {

    }

    override fun onSendComment(feed: ResultFuntime, comment: String) {

    }

    override fun onCaptureImage(feed: ResultFuntime, position: Int) {

    }

    override fun showCommentOverlay(feed: ResultFuntime, position: Int) {

    }

    override fun hideCommentOverlay(feed: ResultFuntime, position: Int) {

    }

    override fun onSuccessFullyAddNumber() {
        //getUserProfileData()
    }

    override fun onClickItemEdit(position: Education, index: Int) {
        var dialogAddEditProfession = DialogAddEditEducation(
            requireActivity(),
            position,
            networkViewModel,
            true,
            object : DialogAddEditEducation.Callbackk {
                override fun onSuccessfullyEditedEducation(education: Education) {
                    userData.results?.profileData()?.education?.set(0, education)
                    networkViewModel.profileLiveData.postValue(userData)
                    educationAdapter.notifyDataSetChanged()
                }
            })
        dialogAddEditProfession.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClickItemProfessionEdit(position: Profession, index: Int) {
        var dialogAddEditProfession = DialogAddEditProfession(
            requireActivity(),
            position,
            networkViewModel,
            true,
            object : DialogAddEditProfession.Callbackk {
                override fun onSuccessfullyEditedProfession(profession: Profession) {
                    userData.results?.profileData()?.profession?.set(0, profession)
                    networkViewModel.profileLiveData.postValue(userData)
                    professionListAdapter.notifyDataSetChanged()
                }
            })
        dialogAddEditProfession.show()
    }
}