package com.stalmate.user.view.profile


import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
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
import com.stalmate.user.intentHelper.IntentHelper
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
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.HomeFragment.FragmentHome
import com.stalmate.user.view.dashboard.HomeFragment.FragmentProfileFuntime
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class FragmentProfile(val callback: FragmentHome.Callback? = null) : BaseFragment(),
    ProfileAboutAdapter.Callbackk, AdapterFeed.Callbackk, ProfileFriendAdapter.Callbackk {
    //Add empty constructor to avoid the exception

    lateinit var binding: FragmentProfileBinding
    lateinit var friendAdapter: ProfileFriendAdapter
    val PICK_IMAGE_PROFILE = 1
    val PICK_IMAGE_COVER = 1
    var imageFile: File? = null
    var isCoverImage = false
    lateinit var userData: User
    private lateinit var albumImageAdapter: ProfileAlbumImageAdapter
    private lateinit var albumAdapter: SelfProfileAlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapterTabPager = AdapterTabPager(requireActivity())
        binding.viewpager.adapter = adapterTabPager
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            when(position){
                0->{
                    tab.text = "Photos"
                }
                1->{
                    tab.text = "Videos"
                }
            }
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()

        (context as ActivityDashboard).pointToMyFuntime.observe(requireActivity()) {
            if (it) {
                val listOfXy = IntArray(2)
                binding.tabLayout.getLocationOnScreen(listOfXy)
                binding.nestedScrollView.dispatchNestedPreScroll(
                    listOfXy[0],
                    listOfXy[1],
                    null,
                    null
                )
                binding.nestedScrollView.isSmoothScrollingEnabled = true
                binding.nestedScrollView.smoothScrollTo(listOfXy[0], listOfXy[1])
                (context as ActivityDashboard).pointToMyFuntime.value = false
            }
        }
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollY < scrollY) {//increase
                callback?.onScoll(true)
            } else {
                callback?.onScoll(false)
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
            IntentHelper.getSyncContactsScreen(requireActivity())?.let { it1 -> startActivity(it1) }
        }

        binding.layout.etSearchFriend.setOnClickListener {
            IntentHelper.getSearchScreen(requireContext())?.let { it1 -> startActivity(it1) }
        }
        val radius = resources.getDimension(R.dimen.dp_10)
        binding.ivBackground.shapeAppearanceModel = binding.ivBackground.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()
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
        networkViewModel.getFriendList(prefManager?.access_token.toString(), hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            if (it?.results != null && !it?.results.isNullOrEmpty())
                friendAdapter.submitList(it?.results)
        })



        binding.toolbar.ivButtonSearch.setImageResource(R.drawable.ic_profile_searchbar)
        binding.toolbar.ivButtonSearch.setOnClickListener {
            IntentHelper.getSearchScreen(requireContext())?.let { it1 -> startActivity(it1) }
        }
        binding.toolbar.ivButtonMenu.visibility = View.GONE
        binding.toolbar.ivButtonMenu1.visibility = View.VISIBLE
        binding.toolbar.ivButtonMenu1.setImageResource(R.drawable.menu)
        binding.toolbar.ivButtonMenu1.setOnClickListener {
            //ShowMenu from here
            try {
                (requireActivity() as ActivityDashboard).loadDrawerFragment()
            } catch (e: ClassCastException) {
                (requireActivity() as ActivitySettings).onBackPressed()
            }
        }

        binding.ivBack.setOnClickListener {
            try {
                (requireActivity() as ActivityDashboard).onBackPressed()
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
            val acc = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
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
            IntentHelper.getProfileEditScreen(requireContext())
                ?.let { it1 -> startActivity(it1, options.toBundle()) }
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
    val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
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
        try {
            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
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

        networkViewModel.etsProfileApi(prefManager?.access_token.toString(), profile_image1)
        networkViewModel.UpdateProfileLiveData.observe(this, Observer {
            it.let {
                prefManager?.profile_img_1 = it?.results?.profile_img_1
                makeToast(it!!.message)
                var hashMap = HashMap<String, String>()
                networkViewModel.getProfileData(hashMap, prefManager?.access_token.toString())
                getUserProfileData()
            }
        })
    }

    private fun getUserProfileData() {
        val hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap, prefManager?.access_token.toString())
        networkViewModel.profileLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                userData = it?.results!!
                //PrefManager.getInstance(requireContext())?.userDetail = it?.results
                prefManager?.profile_img_1 = it?.results?.profile_img1.toString()
                setUpAboutUI("Photos")
                PrefManager.getInstance(requireContext())!!.userProfileDetail = it
            }
        })
    }


    fun setUpAboutUI(tabType: String) {
        if (userData.about.isNullOrEmpty()) {
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

        val aboutArrayList = ArrayList<AboutProfileLine>()

        if (tabType == "Photos") {
            albumImageAdapter = ProfileAlbumImageAdapter(networkViewModel, requireContext(), "")
            binding.layout.rvPhotoAlbumData.adapter = albumImageAdapter
            binding.layout.rvPhotoAlbumData.adapter = albumImageAdapter
            userData.photos?.let {
                albumImageAdapter.submitList(it)
            }
        } else if (tabType == "Albums") {
            /* albumAdapter = SelfProfileAlbumAdapter(networkViewModel, requireContext(), "")
             binding.layout.rvPhotoAlbumData.adapter = albumAdapter
             albumAdapter.submitList(userData.albums)*/

            IntentHelper.getPhotoGalleryAlbumScreen(requireContext())
                ?.putExtra("viewType", "viewNormal")
                ?.putExtra("type", "photos")?.let {
                    startActivity(
                        it
                    )
                }
        }

        if (!userData.profileData()?.profession.isNullOrEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_job,
                    userData.profileData()?.profession?.get(0)?.designation ?: "",
                    userData.profileData()?.profession?.get(0)?.company_name ?: "",
                    "at"
                )
            )
        }

        if (!userData.profileData()?.education.isNullOrEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_graduation,
                    "Student",
                    userData.profileData()?.education?.get(0)?.sehool.toString(),
                    "at"
                )
            )
        }

        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_location,
                "From",
                userData.profileData()?.home_town.toString(),
                ""
            )
        )

        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_status,
                "",
                userData.profileData()?.marital_status.toString(),
                ""
            )
        )

        binding.layout.rvAbout.layoutManager = LinearLayoutManager(requireContext())
        val profileAboutAdapter = ProfileAboutAdapter(networkViewModel, requireContext(), this)
        profileAboutAdapter.submitList(aboutArrayList)
        binding.layout.rvAbout.adapter = profileAboutAdapter

        if (!ValidationHelper.isNull(userData.company)) {
            binding.layout.tvWebsite.text = userData.company
            binding.layout.layoutWebsite.visibility = View.VISIBLE
        }

        binding.layout.buttonEditProfile.visibility = View.VISIBLE
    }


    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onCLickItem(item: ResultFuntime) {

    }

    override fun onCLickUserProfile(item: String) {
        startActivity(IntentHelper.getOtherUserProfileScreen(this.requireContext())!!.putExtra("id", item))
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

    override fun onClickOnProfile(user: User) {

    }
}