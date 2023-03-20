package com.stalmate.user.view.profile


import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.commonadapters.AdapterTabPager
import com.stalmate.user.databinding.FragmentProfileBinding
import com.stalmate.user.model.AboutProfileLine
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.*
import com.stalmate.user.view.adapter.ProfileAboutAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboardNew
import com.stalmate.user.view.dashboard.HomeFragment.FragmentProfileActivityLog
import com.stalmate.user.view.dashboard.HomeFragment.FragmentProfileFuntime
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class FragmentProfile() : BaseFragment(),
    ProfileAboutAdapter.Callbackk, AdapterFeed.Callbackk,
    ProfileFriendAdapter.Callbackk {
    lateinit var binding: FragmentProfileBinding
    lateinit var friendAdapter: ProfileFriendAdapter
    var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS
    )
    var WRITE_REQUEST_CODE = 100
    val PICK_IMAGE_PROFILE = 1
    val PICK_IMAGE_COVER = 1
    var imageFile: File? = null
    var isCoverImage = false
    lateinit var userData: User
    var albumTabType = ""
    private lateinit var mAccount: Account
    private lateinit var albumImageAdapter: ProfileAlbumImageAdapter
    private lateinit var albumAdapter: SelfProfileAlbumAdapter


    lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
//    var ActivityDashboardNew : ActivityDashboardNew

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentProfileBinding>(
            inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
            )
        )!!
        return binding.root
    }

    var isIncreasingPreviousValue = 0
    var isIncreasingCurrentValue = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var adapterTabPager = AdapterTabPager(requireActivity())
        adapterTabPager.addFragment(FragmentProfileActivityLog(), "Activity Log")
        adapterTabPager.addFragment(FragmentProfileFuntime(), "Funtime")
        binding.viewpager.adapter = adapterTabPager
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = adapterTabPager.getTabTitle(position)
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()


        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollY < scrollY) {//increase

                //callback.onScoll(true)
                Log.d("aklsjdasdasd", "aposkdasd")
                onScrollToHideTopHeader(true)
            } else {

                onScrollToHideTopHeader(false)
                Log.d("aklsjdasdasd", "aposkdasdfghfgh")
            }
        })

        /*      binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                  override fun onPageSelected(position: Int) {
                      super.onPageSelected(position)
                      val view = // ... get the view
                          view.post {
                              val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
                              val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                              view.measure(wMeasureSpec, hMeasureSpec)

                              if (binding.viewpager.layoutParams.height != view.measuredHeight) {
                                  // ParentViewGroup is, for example, LinearLayout
                                  // ... or whatever the parent of the ViewPager2 is
                                  binding.viewpager.layoutParams = (binding.viewpager.layoutParams as ViewGroup.LayoutParams)
                                      .also { lp -> lp.height = view.measuredHeight }
                              }
                          }
                  }
              })*/

        binding.appCompatTextView17.setOnClickListener {
            startActivity(IntentHelper.getSyncContactsScreen(requireActivity()))
            // retreiveGoogleContacts()
        }
        binding.layout.etSearchFriend.setOnClickListener {
            startActivity(IntentHelper.getSearchScreen(requireContext()))
        }
        requestPermissions(permissions, WRITE_REQUEST_CODE)
        binding.layout.buttonEditProfile.visibility = View.VISIBLE

        val radius = resources.getDimension(R.dimen.dp_10)

        binding.ivBackground.shapeAppearanceModel = binding.ivBackground.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()
        getUserProfileData()
        binding.ivBackground.setOnClickListener {

            startActivity(
                IntentHelper.getFullImageScreen(requireActivity())!!
                    .putExtra("picture", userData.cover_img1)
            )
        }
        binding.ivUserThumb.setOnClickListener {
            startActivity(
                IntentHelper.getFullImageScreen(requireActivity())!!
                    .putExtra("picture", userData.profile_img1)
            )
        }

        friendAdapter = ProfileFriendAdapter(networkViewModel, requireContext(), this)
        binding.layout.rvFriends.adapter = friendAdapter
        binding.layout.rvFriends.setNestedScrollingEnabled(false);
        binding.layout.rvFriends.layoutManager = GridLayoutManager(requireContext(), 3)

        var hashmap = HashMap<String, String>()
        hashmap["type"] = Constants.TYPE_PROFILE_FRIENDS
        hashmap["sub_type"] = ""
        hashmap["search"] = ""
        hashmap["page"] = "1"
        hashmap["limit"] = "6"
        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })



        binding.toolbar.tvhead.text = "Profile"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            try {
                (requireActivity() as ActivityDashboardNew).onBackPressed()
            } catch (e: ClassCastException) {
                (requireActivity() as ActivitySettings).onBackPressed()
            }
        }

        binding.layout.tvAlbumPhotoSeeMore.setOnClickListener {
            if (binding.layout.photoTab.selectedTabPosition == 0) {
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

        binding.layout.photoTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        setupData()
    }

    public interface Callbackk {
        fun onClickONSyncContTACTbUTTON()
    }

    var scrollEnable = true
    fun onScrollToHideTopHeader(toHide: Boolean) {
        Log.d("klajsdasd", toHide.toString())
        if (toHide) {
            if (scrollEnable) {
                binding.toolbar.root.animate()
                    .translationY(-(binding.toolbar.root.getHeight() + 60).toFloat())
                    .setDuration(150)
                    .setInterpolator(LinearInterpolator()).start()
                scrollEnable = false
                Handler(Looper.getMainLooper()).postDelayed(Runnable { scrollEnable = true }, 150)
            }


        } else {
            if (scrollEnable) {
                binding.toolbar.root.animate().translationY(0f)
                    .setInterpolator(LinearInterpolator()).setDuration(150).start()
                scrollEnable = false
                Handler(Looper.getMainLooper()).postDelayed(Runnable { scrollEnable = true }, 150)
            }


            //   binding.navigationBar.root.setVisibility(View.VISIBLE)
        }
    }


    override fun onResume() {
        super.onResume()
        getUserProfileData()
    }

    fun removeAccount() {
        // Get an instance of the Android account manager
        val accountManager = requireContext().getSystemService(
            AppCompatActivity.ACCOUNT_SERVICE
        ) as AccountManager


        if (isAccountAdded()) {

            var acc = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
            accountManager.removeAccountExplicitly(acc)
        }
    }

    fun isAccountAdded(): Boolean {

        // Get an instance of the Android account manager
        val accountManager =
            requireContext().getSystemService(AppCompatActivity.ACCOUNT_SERVICE) as AccountManager

        for (i in 0 until accountManager.accounts.size) {
            if (accountManager.accounts[i].type == Constants.ACCOUNT_TYPE) {
                return true

            }
        }
        return false
    }


    fun setupData() {
        binding.layout.layoutFollowers.setOnClickListener {
            startActivity(
                IntentHelper.getFollowersFollowingScreen(requireContext())!!
                    .putExtra("id", userData.id)
                    .putExtra("type", Constants.TYPE_USER_TYPE_FOLLOWERS)
            )
        }
        binding.layout.layoutFollowing.setOnClickListener {
            startActivity(
                IntentHelper.getFollowersFollowingScreen(requireContext())!!
                    .putExtra("id", userData.id)
                    .putExtra("type", Constants.TYPE_USER_TYPE_FOLLOWINGS)
            )
        }

        binding.idCoverPhoto.setOnClickListener {
            isCoverImage = true
            startCrop()
        }

        binding.idCameraProfile.setOnClickListener {
            isCoverImage = false
            startCrop()
        }

        binding.layout.btnphoto.setOnClickListener {
            startActivity(
                IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!
                    .putExtra("viewType", "viewNormal")
            )
        }

        binding.layout.buttonEditProfile.setOnClickListener {

            // create an options object that defines the transition
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                binding.layoutChangeBackgroundImage,
                "image"
            )
            // start the activity with transition
            startActivity(IntentHelper.getProfileEditScreen(requireContext()), options.toBundle())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath: String? = ""

        if (requestCode == PICK_IMAGE_PROFILE && resultCode == AppCompatActivity.RESULT_OK) {

        } else if (requestCode == PICK_IMAGE_COVER && resultCode == AppCompatActivity.RESULT_OK) {

        }
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

            /*if (isCoverImage) {
                Glide.with(this).load(uriContent).into(binding.ivBackground)
            } else {
                Glide.with(this).load(uriContent).into(binding.ivUserThumb)
            }*/

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


    private fun updateProfileImageApiHit() {
        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)
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
                getUserProfileData()
            }
        })
    }

    fun getUserProfileData() {
        val hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap)
        networkViewModel.profileLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                userData = it!!.results
                setUpAboutUI("Photos")
                PrefManager.getInstance(requireContext())!!.userProfileDetail = it
            }
        })
    }


    fun setUpAboutUI(tabType: String) {

        Log.d("ajkbcb", tabType)

        if (userData.about!!.isEmpty()) {
            binding.tvUserAbout.visibility = View.GONE
        }

        binding.tvUserName.text = userData.first_name + " " + userData.last_name
        binding.layout.tvFollowerCount.text = userData.follower_count.toString()
        binding.layout.tvFollowingCount.text = userData.following_count.toString()
        binding.tvUserAbout.text = userData.about
        binding.layout.tvFriendCount.text = userData.friends_count.toString()
        ImageLoaderHelperGlide.setGlide(
            requireContext(),
            binding.ivBackground,
            userData.cover_img1,
            R.drawable.user_placeholder
        )
        //   Glide.with(this).load(userData.img_url+userData.profile_img1).into(binding.ivUserThumb)
        ImageLoaderHelperGlide.setGlide(
            requireContext(),
            binding.ivUserThumb,
            userData.profile_img1,
            R.drawable.user_placeholder
        )

        var aboutArrayList = ArrayList<AboutProfileLine>()

        if (tabType == "Photos") {
            albumImageAdapter = ProfileAlbumImageAdapter(networkViewModel, requireContext(), "")
            binding.layout.rvPhotoAlbumData.adapter = albumImageAdapter
            albumImageAdapter.submitList(userData.photos)
        } else if (tabType == "Albums") {
            albumAdapter = SelfProfileAlbumAdapter(networkViewModel, requireContext(), "")
            binding.layout.rvPhotoAlbumData.adapter = albumAdapter
            albumAdapter.submitList(userData.albums)

        }

        if (userData.profile_data[0].profession.isNotEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_job,
                    userData.profile_data[0].profession[0].designation,
                    userData.profile_data[0].profession[0].company_name,
                    "at"
                )
            )
        }

        if (userData.profile_data[0].education.isNotEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_graduation,
                    "Student",
                    userData.profile_data[0].education[0].sehool,
                    "at"
                )
            )
        }

        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_location,
                "From",
                userData.profile_data[0].home_town,
                ""
            )
        )

        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_status,
                "",
                userData.profile_data[0].marital_status,
                ""
            )
        )

        binding.layout.rvAbout.layoutManager = LinearLayoutManager(requireContext())
        var profileAboutAdapter = ProfileAboutAdapter(networkViewModel, requireContext(), this)
        profileAboutAdapter.submitList(aboutArrayList)
        binding.layout.rvAbout.adapter = profileAboutAdapter

        if (!ValidationHelper.isNull(userData.company)) {
            binding.layout.tvWebsite.text = userData.company
            binding.layout.layoutWebsite.visibility = View.VISIBLE
        }
    }


    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onClickOnProfile(user: User) {

    }

}