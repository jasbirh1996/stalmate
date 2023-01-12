package com.stalmate.user.view.profile


import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.commonadapters.AdapterTabPager
import com.stalmate.user.databinding.ActivityProfileBinding
import com.stalmate.user.databinding.FragmentProfileBinding
import com.stalmate.user.model.AboutProfileLine
import com.stalmate.user.model.User
import com.stalmate.user.modules.contactSync.SyncService
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.adapter.ProfileAboutAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboardNew
import com.stalmate.user.view.dashboard.HomeFragment.FragmentProfileActivityLog
import com.stalmate.user.view.dashboard.HomeFragment.FragmentProfileFuntime
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class FragmentProfile(var callback:Callback) : BaseFragment(), ProfileAboutAdapter.Callbackk, AdapterFeed.Callbackk,
    ProfileFriendAdapter.Callbackk {




    lateinit var syncBroadcastreceiver: SyncBroadcasReceiver
    lateinit var binding: FragmentProfileBinding
    lateinit var friendAdapter: ProfileFriendAdapter
    var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS)
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
    var isIncreasingPreviousValue=0
    var isIncreasingCurrentValue=1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val filter = IntentFilter()
        filter.addAction(Constants.ACTION_SYNC_COMPLETED)
        syncBroadcastreceiver = SyncBroadcasReceiver()
        requireContext().registerReceiver(syncBroadcastreceiver, filter)

        var permissionArray = arrayOf(Manifest.permission.READ_CONTACTS)
        if (isPermissionGranted(permissionArray,requireContext())) {
            Log.d("alskjdasd", ";aosjldsad")
            requireContext().startService(
                Intent(requireContext(), SyncService::class.java)
            )
        }



        var adapterTabPager= AdapterTabPager(requireActivity())
        adapterTabPager.addFragment(FragmentProfileActivityLog(),"Activity Log")
        adapterTabPager.addFragment(FragmentProfileFuntime(),"Funtime")
        binding.viewpager.adapter=adapterTabPager
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = adapterTabPager.getTabTitle(position)
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()





        binding.nestedScrollView.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {

            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {

             if (oldScrollY < scrollY) {//increase

                    callback.onScoll(true)
                    Log.d("aklsjdasdasd","aposkdasd")
                    onScrollToHideTopHeader(true)
                } else {

                 onScrollToHideTopHeader(false)
                 Log.d("aklsjdasdasd", "aposkdasdfghfgh")
             }


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
            retreiveGoogleContacts()
        }
        requestPermissions(permissions, WRITE_REQUEST_CODE)
        binding.layout.buttonEditProfile.visibility = View.VISIBLE

        val radius = resources.getDimension(R.dimen.dp_10)

        binding.ivBackground.shapeAppearanceModel = binding.ivBackground.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
            .setBottomRightCorner(CornerFamily.ROUNDED,radius)
            .build()
        getUserProfileData()

        friendAdapter = ProfileFriendAdapter(networkViewModel, requireContext(), this)
        binding.layout.rvFriends.adapter = friendAdapter
        binding.layout.rvFriends.setNestedScrollingEnabled(false);
        binding.layout.rvFriends.layoutManager = GridLayoutManager(requireContext(), 3)

        var hashmap = HashMap<String, String>()
        hashmap.put("type", Constants.TYPE_PROFILE_FRIENDS)
        hashmap.put("sub_type", "")
        hashmap.put("search", "")
        hashmap.put("page", "1")
        hashmap.put("limit", "6")
        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })



        binding.toolbar.tvhead.text="Profile"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            (requireActivity() as ActivityDashboardNew).onBackPressed()
        }

        binding.layout.tvAlbumPhotoSeeMore.setOnClickListener {
            if (binding.layout.photoTab.selectedTabPosition ==0){
                startActivity(IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!.putExtra("viewType", "viewNormal").putExtra("type", "photos"))
            }else{
                startActivity(IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!.putExtra("viewType", "viewNormal").putExtra("type", "albums"))
            }
        }

        binding.layout.photoTab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener {
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
        setupData()


    }
    var scrollEnable=true
    fun onScrollToHideTopHeader(toHide:Boolean) {
        Log.d("klajsdasd",toHide.toString())
        if (toHide) {
            if (scrollEnable){
                binding.toolbar.root.animate().translationY(-(binding.toolbar.root.getHeight() + 60).toFloat()).setDuration(150)
                    .setInterpolator(LinearInterpolator()).start()
                scrollEnable=false
                Handler(Looper.getMainLooper()).postDelayed(Runnable { scrollEnable=true },150)
            }




        }else{
            if (scrollEnable){
                binding.toolbar.root.animate().translationY(0f).setInterpolator(LinearInterpolator()).setDuration(150).start()
                scrollEnable=false
                Handler(Looper.getMainLooper()).postDelayed(Runnable {  scrollEnable=true },150)
            }


            //   binding.navigationBar.root.setVisibility(View.VISIBLE)
        }
    }

    inner class SyncBroadcasReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1!!.action == Constants.ACTION_SYNC_COMPLETED) {
                Log.d("==========wew", "wwwwwwwwwwww=====121=====wwwwwwwwwwwwww")
                makeToast("Synced")
                if (p1.extras!!.getString("contacts") != null) {
                    Log.d("==========wew", "wwwwwwwwwwwwwwwwwwwwwww11www")
                    startActivity(IntentHelper.getSearchScreen(requireContext())!!.putExtra("contacts", p1.extras!!.getString("contacts").toString()))
                }
            }
        }
    }

    public interface Callback {
        fun onScoll(toHide: Boolean)
    }


    override fun onResume() {
        super.onResume()
        getUserProfileData()
    }

    fun removeAccount(){
        // Get an instance of the Android account manager
        val accountManager = requireContext().getSystemService(
            AppCompatActivity.ACCOUNT_SERVICE
        ) as AccountManager


        if (isAccountAdded()){

            var acc=  Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
            accountManager.removeAccountExplicitly(acc)
        }
    }

    fun isAccountAdded():Boolean{

        // Get an instance of the Android account manager
        val accountManager = requireContext().getSystemService(AppCompatActivity.ACCOUNT_SERVICE) as AccountManager

        for (i in 0 until accountManager.accounts.size){
            if (accountManager.accounts[i].type==Constants.ACCOUNT_TYPE){
                return true

            }
        }
        return false
    }


    private fun retreiveGoogleContacts() {

        mAccount= createSyncAccount(requireContext())
        var bundle=Bundle()
        bundle.putBoolean("force",true)
        bundle.putBoolean("expedited",true)
        Log.d("asldkjalsda","sync")
        ContentResolver.requestSync(mAccount, "com.stalmate.user", bundle)

    }

    fun setupData() {
        binding.layout.layoutFollowers.setOnClickListener {
            startActivity(
                IntentHelper.getFollowersFollowingScreen(requireContext())!!.putExtra("id", userData.id)
                    .putExtra("type", Constants.TYPE_USER_TYPE_FOLLOWERS)
            )
        }
        binding.layout.layoutFollowing.setOnClickListener {
            startActivity(
                IntentHelper.getFollowersFollowingScreen(requireContext())!!.putExtra("id", userData.id)
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
            startActivity(IntentHelper.getPhotoGalleryAlbumScreen(requireContext())!!.putExtra("viewType","viewNormal"))
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


    fun setUpAboutUI(tabType : String) {

        Log.d("ajkbcb", tabType)

        if (userData.about!!.isEmpty()) {
            binding.tvUserAbout.visibility = View.GONE
        }

        binding.tvUserName.text = userData.first_name + " " + userData.last_name
        binding.layout.tvFollowerCount.text = userData.follower_count.toString()
        binding.layout.tvFollowingCount.text = userData.following_count.toString()
        binding.tvUserAbout.text = userData.about
        binding.layout.tvFriendCount.text = userData.friends_count.toString()
        ImageLoaderHelperGlide.setGlide(requireContext(), binding.ivBackground, userData.cover_img1,R.drawable.user_placeholder)
        //   Glide.with(this).load(userData.img_url+userData.profile_img1).into(binding.ivUserThumb)
        ImageLoaderHelperGlide.setGlide(requireContext(), binding.ivUserThumb, userData.profile_img1,R.drawable.user_placeholder)
        var aboutArrayList = ArrayList<AboutProfileLine>()

        if (tabType== "Photos") {
            albumImageAdapter = ProfileAlbumImageAdapter(networkViewModel, requireContext(), "")
            binding.layout.rvPhotoAlbumData.adapter = albumImageAdapter
            albumImageAdapter.submitList(userData.photos)
        }else if (tabType== "Albums"){
            albumAdapter = SelfProfileAlbumAdapter(networkViewModel, requireContext(), "")
            binding.layout.rvPhotoAlbumData.adapter =albumAdapter
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


    fun createSyncAccount(context: Context): Account {

        // Create the account type and default account
        val newAccount = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        // Get an instance of the Android account manager
        val accountManager = context.getSystemService(AppCompatActivity.ACCOUNT_SERVICE) as AccountManager
        /*
        * Add the account and account type, no password or user data
        * If successful, return the Account object, otherwise report an error.
        */
        return if (accountManager.addAccountExplicitly(newAccount, null, null)) {
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
    override fun onDestroy() {
        requireContext().unregisterReceiver(syncBroadcastreceiver)
        super.onDestroy()
    }

    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onClickOnProfile(user: User) {

    }

}