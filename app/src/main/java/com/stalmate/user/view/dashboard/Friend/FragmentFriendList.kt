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

import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter

class FragmentFriendList : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var friendAdapter:FriendAdapter
lateinit var binding:FragmentFriendListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.bind<FragmentFriendListBinding>(inflater.inflate(R.layout.fragment_friend_list, container, false))!!
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendAdapter = FriendAdapter(networkViewModel, requireContext(), this)
        binding.rvFriends.adapter=friendAdapter
        binding.rvFriends.layoutManager= LinearLayoutManager(context)
        var hashmap=HashMap<String,String>()
        hashmap.put("type","")
        hashmap.put("search","")
        hashmap.put("page","1")
        networkViewModel.getFriendList("", hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })
    }


    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

        var hashmap=HashMap<String,String>()
        hashmap.put("type","")
        hashmap.put("search","")
        hashmap.put("page","1")





    }

    override fun onClickOnProfile(friend: User) {
        startActivity(IntentHelper.getProfileScreen(requireContext())!!.putExtra("userData",friend))
    }
}