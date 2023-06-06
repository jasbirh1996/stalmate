package com.stalmate.user.view.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.shape.CornerFamily
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityOtherUserProfileBinding
import com.stalmate.user.model.AboutProfileLine

import com.stalmate.user.model.ModelUser
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.adapter.ProfileAboutAdapter

import com.stalmate.user.view.adapter.ProfileFriendAdapter

class ActivityOtherUserProfile : BaseActivity(),
    ProfileFriendAdapter.Callbackk, ProfileAboutAdapter.Callbackk {

    lateinit var binding: ActivityOtherUserProfileBinding
    lateinit var friendAdapter: ProfileFriendAdapter
    var userId = ""
    lateinit var userData: ModelUser
    override fun onClick(viewId: Int, view: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user_profile)
        if (intent.getSerializableExtra("id") != null) {
            userId = intent.getSerializableExtra("id").toString()
        }
        getUserProfileData()
        val radius = resources.getDimension(R.dimen.dp_10)
        binding.ivBackground.setShapeAppearanceModel(
            binding.ivBackground.getShapeAppearanceModel()
                .toBuilder()
                .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                .build()
        );


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
        networkViewModel.getFriendList(prefManager?.access_token.toString(), hashmap)
        networkViewModel.friendLiveData.observe(this, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })


        binding.buttonChat.setOnClickListener {
            startActivity(IntentHelper.getChatScreen(this)!!.putExtra("id", userData.results.id))
        }

        setupData()
    }

    override fun onResume() {
        super.onResume()
        getUserProfileData()
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
        /*val hashMap = HashMap<String, String>()
        hashMap["id_user"] = userId
        networkViewModel.block(hashMap)*/
        networkViewModel.block(access_token = prefManager?.access_token.toString(), _id = userId)
        networkViewModel.blockData.observe(this, Observer {
            dismissLoader()
            it.let {
                if (userData.results.isBlocked == 0) {
                    userData.results.isBlocked = 1
                } else {
                    userData.results.isBlocked = 0
                }
                networkViewModel.otherUserProfileLiveData.postValue(userData)
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
        Log.d("a;ksda", "akjasdasdsd;asd")
        if (status.equals(Constants.TYPE_USER_ACTION_ADD_FRIEND)) {
            networkViewModel.sendFriendRequest(prefManager?.access_token.toString(), hashMap)
            networkViewModel.sendFriendRequestLiveData.observe(this, Observer {
                it.let {


                    if (userData.results.isFriend == 1) {
                        userData.results.isFriend = 0
                        userData.results.isFollowed = 0
                    } else {

                        if (userData.results.friendRequestsent == 0) {
                            userData.results.isFollowed = 1
                            userData.results.friendRequestsent = 1
                        } else {
                            userData.results.friendRequestsent = 0
                        }
                        Log.d("alkshdasldaupdating", userData.results.friendRequestsent.toString())
                    }


                    Log.d("a;ksda", "akjsd;asd")



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
        networkViewModel.getOtherUserProfileData(
            access_token = prefManager?.access_token.toString(),
            user_id = userId
        )
        networkViewModel.otherUserProfileLiveData.observe(this, Observer {
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
            userData.results.cover_img1,
            R.drawable.user_placeholder
        )
        ImageLoaderHelperGlide.setGlide(
            this,
            binding.ivUserThumb,
            userData.results.profile_img1,
            R.drawable.user_placeholder
        )
        var aboutArrayList = ArrayList<AboutProfileLine>()
        if (userData.results.profile_data[0].profession.isNotEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_designation_icon,
                    userData.results.profile_data[0].profession[0].designation,
                    userData.results.profile_data[0].profession[0].company_name,
                    "at"
                )
            )
        }

        if (userData.results.profile_data[0].education.isNotEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_graduation,
                    "Student",
                    userData.results.profile_data[0].education[0].sehool,
                    "at"
                )
            )
        }

        if (userData.results.profile_data[0].home_town.isNotEmpty()) {

            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_location,
                    "Lives at",
                    userData.results.profile_data[0].home_town,
                    "at"
                )
            )
        }

        if (userData.results.profile_data[0].location.isNotEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_location,
                    "From",
                    userData.results.profile_data[0].location,
                    ""
                )
            )
        }

        if (userData.results.profile_data[0].marital_status.isNotEmpty()) {
            aboutArrayList.add(
                AboutProfileLine(
                    R.drawable.ic_profile_heart_icon,
                    "",
                    userData.results.profile_data[0].marital_status,
                    ""
                )
            )
        }

        binding.layout.rvAbout.layoutManager = LinearLayoutManager(this)
        var profileAboutAdapter = ProfileAboutAdapter(networkViewModel, this, this)
        profileAboutAdapter.submitList(aboutArrayList)
        binding.layout.rvAbout.adapter = profileAboutAdapter

        if (!ValidationHelper.isNull(userData.results.company)) {
            binding.layout.tvWebsite.text = userData.results.company
            binding.layout.layoutWebsite.visibility = View.VISIBLE
        }


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
            binding.layout.layoutConnectionDetails.visibility = View.VISIBLE
            binding.layout.tvsince.visibility = View.VISIBLE
            binding.buttonFriend.text = "Unfriend"

        } else {//not a friend
            if (userData.results.hasFriendRequest == 1) {
                binding.layoutButtonsFriends.visibility = View.GONE
                binding.layoutButtonsAcceptReject.visibility = View.VISIBLE
                binding.layoutTopControlls.visibility = View.VISIBLE
            } else {
                Log.d("alkshdasldaview", userData.results.friendRequestsent.toString())

                if (userData.results.friendRequestsent == 1) {
                    binding.layoutButtonsFriends.visibility = View.VISIBLE
                    binding.buttonFriend.text = "Friend Request Sent"
                    binding.layoutButtonsAcceptReject.visibility = View.GONE
                    binding.layoutTopControlls.visibility = View.VISIBLE
                } else {
                    binding.layoutTopControlls.visibility = View.VISIBLE
                    binding.layoutButtonsFriends.visibility = View.VISIBLE
                    binding.buttonFriend.text = "Add Friend"
                    binding.layoutButtonsAcceptReject.visibility = View.GONE
                }
            }
        }
    }


}

