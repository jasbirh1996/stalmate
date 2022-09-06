package com.stalmate.user.view.profile

import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.adapter.ProfileAboutAdapter

import com.stalmate.user.view.adapter.ProfileFriendAdapter

class ActivityOtherUserProfile : BaseActivity(), AdapterFeed.Callbackk,
    ProfileFriendAdapter.Callbackk, ProfileAboutAdapter.Callbackk {

    lateinit var binding: ActivityOtherUserProfileBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var friendAdapter: ProfileFriendAdapter
    var userId = ""
    lateinit var userData: ModelUser
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
        binding.layout.rvFriends.setNestedScrollingEnabled(false);
        binding.layout.rvFriends.layoutManager = GridLayoutManager(this, 3)
        var hashmap = HashMap<String, String>()


        hashmap.put("other_user_id", userId)
        hashmap.put("type", "profile_friends")
        hashmap.put("sub_type", "")
        hashmap.put("search", "")
        hashmap.put("page", "1")
        hashmap.put("limit", "6")
        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(this, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })

        feedAdapter = AdapterFeed(networkViewModel, this, this)
        binding.layout.rvFeeds.adapter = feedAdapter
        binding.layout.rvFeeds.setNestedScrollingEnabled(false);
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

    override fun onResume() {
        super.onResume()
        getUserProfileData()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun setupData() {


        binding.layout.layoutFollowers.setOnClickListener {
            startActivity(
                IntentHelper.getFollowersFollowingScreen(this)!!.putExtra("id", userData.results.id)
                    .putExtra("type", "follower")
            )
        }
        binding.layout.layoutFollowing.setOnClickListener {
            startActivity(
                IntentHelper.getFollowersFollowingScreen(this)!!.putExtra("id", userData.results.id)
                    .putExtra("type", "following")
            )
        }


        binding.buttonFriendUpdate.setOnClickListener {
            updateFriendStatus("add_friend")
        }

        binding.buttonFollow.setOnClickListener {
            updateFriendStatus("follow")
        }


        binding.buttonBlock.setOnClickListener {
            hitBlockApi()
        }

        binding.accept.setOnClickListener {
            hitAcceptRejectApi("Accept")
        }

        binding.reject.setOnClickListener {
            hitAcceptRejectApi("Reject")
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonFriend.setOnClickListener {
     updateFriendStatus(Constants.TYPE_USER_ACTION_ADD_FRIEND)
        }

    }

    private fun hitAcceptRejectApi(type: String) {

        val hashMap = HashMap<String, String>()
        hashMap["id_user"] = userId
        hashMap["type"] = type

        networkViewModel.updateFriendRequest(hashMap)
        networkViewModel.updateFriendRequestLiveData.observe(this, Observer {

            it.let {
                if (it!!.status == true) {

                    if (type == "Accept") {
                        binding.accept.visibility = View.GONE
                        binding.reject.visibility = View.GONE
                        binding.layoutTopControlls.visibility = View.VISIBLE
                    } else {
                        onBackPressed()
                    }

                    dismissLoader()
                    makeToast(it.message)
                }
            }

        })

    }

    private fun hitBlockApi() {

        showLoader()
        val hashMap = HashMap<String, String>()
        hashMap["id_user"] = userId

        networkViewModel.block(hashMap)
        networkViewModel.blockData.observe(this, Observer {

            it.let {
                if (it!!.status == true) {
                    dismissLoader()

                    if (userData.results.isBlocked == 0) {
                        userData.results.isBlocked = 1
                    } else {
                        userData.results.isBlocked = 0
                    }
                    networkViewModel.otherUserProfileLiveData.postValue(userData)
                }
            }

        })


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClickOnProfile(user: User) {
        startActivity(IntentHelper.getOtherUserProfileScreen(this)!!.putExtra("id", user.id))
    }

    override fun onClickOnViewComments(postId: Int) {

    }


    fun updateFriendStatus(status: String) {
        var hashMap = HashMap<String, String>()
        hashMap.put("id_user", userId)
        Log.d("a;ksda","akjasdasdsd;asd")
        if (status.equals(Constants.TYPE_USER_ACTION_ADD_FRIEND)) {
            networkViewModel.sendFriendRequest("", hashMap)
            networkViewModel.sendFriendRequestLiveData.observe(this, Observer {
                it.let {


                        if (userData.results.isFriend==1){
                            userData.results.isFriend=0
                            userData.results.isFollowed=0
                        }else{
                            userData.results.isFollowed=1
                            userData.results.friendRequestsent=1
                            if (userData.results.friendRequestsent==1){
                                userData.results.friendRequestsent=0
                            }
                        }


                        Log.d("a;ksda","akjsd;asd")



                    notifyData()




                }



            })
        }
        if (status.equals(Constants.TYPE_USER_ACTION_FOLLOW)) {
            networkViewModel.sendFollowRequest("", hashMap)
            networkViewModel.followRequestLiveData.observe(this, Observer {
                it.let {
                    if (networkViewModel.otherUserProfileLiveData.value!!.results.isFollowed == 1) {
                        userData.results.isFollowed = 0
                        userData.results.follower_count = userData.results.follower_count - 1
                    } else {
                        userData.results.isFollowed = 1
                        userData.results.follower_count = userData.results.follower_count + 1
                    }
                    notifyData()
                }
            })
        }

    }


    fun notifyData() {
        networkViewModel.otherUserProfileLiveData.postValue(userData)
    }


    fun getUserProfileData() {
        Log.d("asdasdasd", "asdasasdd")
        var hashMap = HashMap<String, String>()
        networkViewModel.getOtherUserProfileData(hashMap, user_id = userId)
        networkViewModel.otherUserProfileLiveData.observe(this, Observer {
            Log.d("asdasdasd", "asdasdfgdfgdfgasdd//..d")
            it.let {
                userData = it!!
                setUpAboutUI()
            }
        })
    }

    fun setUpAboutUI() {


        binding.tvUserName.text = userData.results.first_name + " " + userData.results.last_name
        binding.layout.tvFollowerCount.text = userData.results.follower_count.toString()
        binding.layout.tvFollowingCount.text = userData.results.following_count.toString()
        binding.tvUserAbout.text = userData.results.about
        binding.layout.tvFriendCount.text = userData.results.friends_count.toString()



        ImageLoaderHelperGlide.setGlide(
            this,
            binding.ivBackground,
            userData.results.img_url + userData.results.cover_img1
        )
        ImageLoaderHelperGlide.setGlide(
            this,
            binding.ivUserThumb,
            userData.results.img_url + userData.results.profile_img1
        )


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
                userData.results.profile_data[0].home_town,
                "at"
            )
        )
        aboutArrayList.add(
            AboutProfileLine(
                "",
                "From",
                userData.results.profile_data[0].location,
                ""
            )
        )
        aboutArrayList.add(
            AboutProfileLine(
                "",
                "",
                userData.results.profile_data[0].marital_status,
                ""
            )
        )
        binding.layout.rvAbout.layoutManager = LinearLayoutManager(this)
        var profileAboutAdapter = ProfileAboutAdapter(networkViewModel, this, this)
        profileAboutAdapter.submitList(aboutArrayList)
        binding.layout.rvAbout.adapter = profileAboutAdapter



        if (userData.results.isFollowed == 1) {
            binding.tvFollowStatus.text = "Following"
        } else {
            binding.tvFollowStatus.text = "Follow"
        }
        if (userData.results.isBlocked == 1) {
            binding.tvBlockStatus.text = "Blocked"
        } else {
            binding.tvBlockStatus.text = "Block"
        }


        if (userData.results.isFriend == 1) {
            binding.layoutButtonsAcceptReject.visibility = View.GONE
            binding.layoutTopControlls.visibility = View.VISIBLE
            binding.layoutButtonsFriends.visibility = View.VISIBLE
            binding.buttonFriend.text = "Unfriend"

        } else {//not a friend
            if (userData.results.hasFriendRequest == 1) {
                binding.layoutButtonsFriends.visibility = View.GONE
                binding.layoutButtonsAcceptReject.visibility = View.VISIBLE
                binding.layoutTopControlls.visibility = View.GONE
            } else {
                if (userData.results.friendRequestsent == 1) {
                    binding.layoutButtonsFriends.visibility = View.VISIBLE
                    binding.buttonFriend.text = "Friend Request Sent"
                    binding.layoutButtonsAcceptReject.visibility = View.GONE
                }else{
                    binding.layoutTopControlls.visibility = View.VISIBLE
                    binding.layoutButtonsFriends.visibility = View.VISIBLE
                    binding.buttonFriend.text = "Add Friend"
                    binding.layoutButtonsAcceptReject.visibility = View.GONE
                }


            }
        }

/*        if (userData.results.isFriend == 1) {
            binding.tvBlockStatus.text = "Blocked"
        }else{
            binding.tvBlockStatus.text = "Block"
        }*/

    }


}

