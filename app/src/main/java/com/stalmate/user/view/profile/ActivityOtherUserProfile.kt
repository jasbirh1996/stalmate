package com.stalmate.user.view.profile

import android.app.ActionBar
import android.app.ActivityOptions
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.ActivityOtherUserProfileBinding
import com.stalmate.user.model.AboutProfileLine

import com.stalmate.user.model.ModelUser
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.ProfileAboutAdapter

import com.stalmate.user.view.adapter.ProfileFriendAdapter

class ActivityOtherUserProfile : BaseActivity(), AdapterFeed.Callbackk,
    ProfileFriendAdapter.Callbackk,
    ProfileAboutAdapter.Callbackk {
    lateinit var binding: ActivityOtherUserProfileBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var friendAdapter: ProfileFriendAdapter
    var userId = ""
    lateinit var userData: User
    override fun onClick(viewId: Int, view: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.getSerializableExtra("id") != null) {
            userId = intent.getSerializableExtra("id").toString()
        }
        getUserProfileData()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user_profile)


        friendAdapter = ProfileFriendAdapter(networkViewModel, this, this)
        binding.layout.rvFriends.adapter = friendAdapter
        binding.layout.rvFriends.layoutManager = GridLayoutManager(this, 3)
        networkViewModel.getFriendList("", HashMap())
        networkViewModel.friendLiveData.observe(this, Observer {
            Log.d("asdasdasd", "oaspidsad")
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })
        feedAdapter = AdapterFeed(networkViewModel, this, this)
        binding.layout.rvFeeds.adapter = feedAdapter
        binding.layout.rvFeeds.layoutManager = LinearLayoutManager(this)
        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(this, Observer {
            Log.d("asdasdasd", "oaspiasddsad")
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })




        setupData()
    }


    fun setupData() {


        binding.layout.layoutFollowers.setOnClickListener {

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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClickOnProfile(user: User) {
        startActivity(IntentHelper.getOtherUserProfileScreen(this)!!.putExtra("id", user.id))
    }

    override fun onClickOnViewComments(postId: Int) {

    }


    fun updateFriendStatus() {
        var hashMap = HashMap<String, String>()
        hashMap.put("id_user", userId)
        networkViewModel.sendFriendRequest("", hashMap)
        networkViewModel.sendFriendRequestLiveData.observe(this, Observer {
            it.let {

            }
        })
    }


    fun getUserProfileData() {
        Log.d("asdasdasd", "asdasasdd")
        var hashMap = HashMap<String, String>()
        networkViewModel.getOtherUserProfileData(hashMap, user_id = userId)
        networkViewModel.otherUserProfileLiveData.observe(this, Observer {
            Log.d("asdasdasd", "asdasdfgdfgdfgd")
            it.let {
                userData = it!!.results
                setUpAboutUI()
            }
        })
    }

    fun setUpAboutUI() {


        binding.tvUserName.text=userData.first_name+" "+userData.last_name
        binding.layout.tvFollowerCount.text=userData.follower
        binding.layout.tvFollowingCount.text=userData.following




        var aboutArrayList = ArrayList<AboutProfileLine>()
        aboutArrayList.add(AboutProfileLine("", "Student", "IMS Ghaziabad", "at"))
        aboutArrayList.add(AboutProfileLine("", "Designer", "Flupper", "at"))
        /*     for (i in 0 until userData.profile_data[0].education.size){

             }
             for (i in 0 until userData.profile_data[0].profession.size){

             }*/
        aboutArrayList.add(
            AboutProfileLine(
                "",
                "Lives at",
                userData.profile_data[0].home_town,
                "at"
            )
        )
        aboutArrayList.add(AboutProfileLine("", "From", userData.profile_data[0].location, ""))
        aboutArrayList.add(AboutProfileLine("", "", userData.profile_data[0].marital_status, ""))
        binding.layout.rvAbout.layoutManager = LinearLayoutManager(this)
        var profileAboutAdapter = ProfileAboutAdapter(networkViewModel, this, this)
        profileAboutAdapter.submitList(aboutArrayList)
        binding.layout.rvAbout.adapter = profileAboutAdapter

    }

}

