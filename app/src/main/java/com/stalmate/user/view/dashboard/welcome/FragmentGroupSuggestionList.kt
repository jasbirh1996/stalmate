package com.stalmate.user.view.dashboard.welcome




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
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment

import com.stalmate.user.databinding.FragmentGroupSuggestionListBinding
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter

class FragmentGroupSuggestionList : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var friendAdapter:FriendAdapter
    lateinit var binding:FragmentGroupSuggestionListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.bind<FragmentGroupSuggestionListBinding>(inflater.inflate(R.layout.fragment_group_suggestion_list, container, false))!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendAdapter = FriendAdapter(networkViewModel, context!!, this)

        binding.rvUsers.adapter=friendAdapter
        binding.rvUsers.layoutManager= LinearLayoutManager(context)
        networkViewModel.getFriendList("", HashMap())
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })
    }


    override fun onClickOnViewComments(postId: Int) {

    }
}