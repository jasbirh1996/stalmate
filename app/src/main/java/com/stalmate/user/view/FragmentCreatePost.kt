package com.stalmate.user.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentCreatePostBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.SuggestedFriendAdapter
import com.stalmate.user.view.adapter.UserHomeStoryAdapter


class FragmentCreatePost : BaseFragment(), AdapterFeed.Callbackk, UserHomeStoryAdapter.Callbackk,
    SuggestedFriendAdapter.Callbackk {

    private lateinit var binding: FragmentCreatePostBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var homeStoryAdapter: UserHomeStoryAdapter

    lateinit var suggestedFriendAdapter:  SuggestedFriendAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_create_post, container, false)
        binding=DataBindingUtil.bind<FragmentCreatePostBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        feedAdapter = AdapterFeed(networkViewModel, requireContext(), requireActivity())
        homeStoryAdapter = UserHomeStoryAdapter(networkViewModel, requireContext(), this)
        suggestedFriendAdapter = SuggestedFriendAdapter(networkViewModel, requireContext(), this)



        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd","oaspiasddsad")
            it.let {
                homeStoryAdapter.submitList(it!!.results)
            }
        })


    }

    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onClickOnProfile(user: Feed) {

    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }

}