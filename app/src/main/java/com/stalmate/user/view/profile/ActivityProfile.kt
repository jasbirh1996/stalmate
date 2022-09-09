package com.stalmate.user.view.profile

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.ActivityProfileBinding

import com.stalmate.user.model.AboutProfileLine
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.adapter.ProfileAboutAdapter

import com.stalmate.user.view.adapter.ProfileFriendAdapter
import com.stalmate.user.view.photoalbum.ActivityPhotoGallery
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ActivityProfile : BaseActivity(), AdapterFeed.Callbackk, ProfileFriendAdapter.Callbackk,
    ProfileAboutAdapter.Callbackk {
    lateinit var binding: ActivityProfileBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var friendAdapter: ProfileFriendAdapter
    val PICK_IMAGE_PROFILE = 1
    val PICK_IMAGE_COVER = 1
    var imageFile: File? = null
    var isCoverImage = false
    lateinit var userData: User

    override fun onClick(viewId: Int, view: View?) {
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.layout.buttonEditProfile.visibility=View.VISIBLE
        feedAdapter = AdapterFeed(networkViewModel, this, this)
        binding.layout.rvFeeds.setNestedScrollingEnabled(false);
        binding.layout.rvFeeds.adapter = feedAdapter
        binding.layout.rvFeeds.layoutManager = LinearLayoutManager(this)
        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(this, Observer {
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })

        getUserProfileData()

        friendAdapter = ProfileFriendAdapter(networkViewModel, this, this)
        binding.layout.rvFriends.adapter = friendAdapter
        binding.layout.rvFriends.setNestedScrollingEnabled(false);
        binding.layout.rvFriends.layoutManager = GridLayoutManager(this, 3)

        var hashmap=HashMap<String,String>()
        hashmap.put("type",Constants.TYPE_PROFILE_FRIENDS)
        hashmap.put("sub_type","")
        hashmap.put("search","")
        hashmap.put("page","1")
        hashmap.put("limit","6")
        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(this, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })




        setupData()
    }

    override fun onResume() {
        super.onResume()
        getUserProfileData()
    }


    fun setupData() {

        binding.layout.layoutFollowers.setOnClickListener {
            startActivity(IntentHelper.getFollowersFollowingScreen(this)!!.putExtra("id",userData.id).putExtra("type",Constants.TYPE_USER_TYPE_FOLLOWERS))
        }
        binding.layout.layoutFollowing.setOnClickListener {
            startActivity(IntentHelper.getFollowersFollowingScreen(this)!!.putExtra("id",userData.id).putExtra("type",Constants.TYPE_USER_TYPE_FOLLOWINGS))
        }

        binding.idCoverPhoto.setOnClickListener {

            isCoverImage=true
            startCrop()
        }

        binding.idCameraProfile.setOnClickListener {
            isCoverImage=false
            startCrop()
        }

        binding.layout.btnphoto.setOnClickListener {
            startActivity(Intent(this, ActivityPhotoGallery::class.java))
        }



        binding.layout.buttonEditProfile.setOnClickListener {



            // create an options object that defines the transition
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.layoutChangeBackgroundImage,
                "image"
            )


            // start the activity with transition
            startActivity(IntentHelper.getProfileEditScreen(this), options.toBundle())
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath : String? = ""

        if (requestCode == PICK_IMAGE_PROFILE && resultCode == RESULT_OK){

        }else if (requestCode == PICK_IMAGE_COVER && resultCode == RESULT_OK){

        }
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
                Glide.with(this)
                    .load(uriContent)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(binding.ivBackground)
            }else{
                Glide.with(this).load(uriContent)
                    .placeholder(R.drawable.profileplaceholder)
                    .into(binding.ivUserThumb)
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
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClickOnProfile(user: User) {
        startActivity(IntentHelper.getOtherUserProfileScreen(this)!!.putExtra("id", user.id))
    }

    override fun onClickOnViewComments(postId: Int) {

    }

    private fun updateProfileImageApiHit() {




        val thumbnailBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)
        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
            "cover_img".takeIf { isCoverImage } ?: "profile_img",
            imageFile!!.name,
            thumbnailBody
        ) //image[] for multiple image




        networkViewModel.etsProfileApi(profile_image1)


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

        if (userData.about.isEmpty()){
            binding.tvUserAbout.visibility = View.GONE
        }

        binding.tvUserName.text=userData.first_name+" "+userData.last_name
        binding.layout.tvFollowerCount.text=userData.follower_count.toString()
        binding.layout.tvFollowingCount.text=userData.following_count.toString()
        binding.tvUserAbout.text=userData.about
        binding.layout.tvFriendCount.text=userData.friends_count.toString()
        ImageLoaderHelperGlide.setGlide(this,binding.ivBackground,userData.img_url+userData.cover_img1)
     //   Glide.with(this).load(userData.img_url+userData.profile_img1).into(binding.ivUserThumb)
        ImageLoaderHelperGlide.setGlide(this,binding.ivUserThumb,userData.img_url+userData.profile_img1)
                Log.d("asdjasda",userData.img_url+userData.profile_img1)
        Log.d("asdjasda",userData.img_url+userData.cover_img1)

        var aboutArrayList = ArrayList<AboutProfileLine>()

        if (userData.profile_data[0].profession.isNotEmpty()){
            aboutArrayList.add(AboutProfileLine(R.drawable.ic_profile_designation_icon, userData.profile_data[0].profession[0].designation, userData.profile_data[0].profession[0].company_name, "at"))
        }

        if (userData.profile_data[0].education.isNotEmpty()){
            aboutArrayList.add(AboutProfileLine(R.drawable.ic_profile_graduation, "Student", userData.profile_data[0].education[0].sehool, "at"))
        }


        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_location,
                "Lives at",
                userData.profile_data[0].home_town,
                "at"
            )
        )
        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_location,
                "From",
                userData.profile_data[0].location,
                ""
            )
        )
        aboutArrayList.add(
            AboutProfileLine(
                R.drawable.ic_profile_heart_icon,
                "",
                userData.profile_data[0].marital_status,
                ""
            )
        )
        binding.layout.rvAbout.layoutManager = LinearLayoutManager(this)
        var profileAboutAdapter = ProfileAboutAdapter(networkViewModel, this, this)
        profileAboutAdapter.submitList(aboutArrayList)
        binding.layout.rvAbout.adapter = profileAboutAdapter

    }


}

