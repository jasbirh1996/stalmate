package com.stalmate.user.view.dashboard.Friend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFriendListBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants

import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter

class FragmentFriendList(var type: String, var subtype: String,var userId:String) : BaseFragment(),
    FriendAdapter.Callbackk {
    lateinit var friendAdapter: FriendAdapter
    lateinit var binding: FragmentFriendListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFriendListBinding>(
            inflater.inflate(
                R.layout.fragment_friend_list,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendAdapter = FriendAdapter(networkViewModel, requireContext(), this,type,subtype)
        setupUI()
        binding.shimmerViewContainer.visibility=View.VISIBLE
        binding.shimmerViewContainer.startShimmer()
        binding.rvFriends.adapter = friendAdapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)
        var hashmap = HashMap<String, String>()
        hashmap.put("id_user", userId)
        hashmap.put("type", type)
        hashmap.put("sub_type", subtype)
        hashmap.put("search", "")
        hashmap.put("page", "1")
        hashmap.put("limit", "")
        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility=View.GONE
                friendAdapter.submitList(it!!.results)
                if (it.results.isEmpty()){
                    binding.layoutNoData.visibility=View.VISIBLE
                }else{
                    binding.layoutNoData.visibility=View.GONE
                }
            }
        })
    }


    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

        var hashmap = HashMap<String, String>()
        hashmap.put("type", "")
        hashmap.put("search", "")
        hashmap.put("page", "1")


    }




    override fun onClickOnProfile(friend: User) {




      startActivity(
            IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id", friend.id)
        )
    }




    private fun hitAcceptRejectApi(type : String) {
        showLoader()
        val hashMap = HashMap<String, String>()
        hashMap["id_user"] =userId
        hashMap["type"] = type
        networkViewModel.updateFriendRequest(hashMap)
        networkViewModel.updateFriendRequestLiveData.observe(this, Observer {

            it.let {
                if (it!!.status== true){
                    friendAdapter.notifyDataSetChanged()
                    dismissLoader()
                    makeToast(it.message)
                }
            }

        })

    }


    fun setupUI(){
        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {

        binding.tvData.text="No Friends Requests to show"

        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
            binding.tvData.text="No Friends Suggestions to show"
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS_FOLLOWERS)) {

            binding.tvData.text="No Friends Suggestions to show"

        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWING)) {
            binding.tvData.text="No Friends to show"

        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWER)) {
            binding.tvData.text="No Friends to show"


        } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWERS)) {

            binding.tvData.text="No Followers to show"
        } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWINGS)) {
            binding.tvData.text="No Followings to show"
        }





    }
}