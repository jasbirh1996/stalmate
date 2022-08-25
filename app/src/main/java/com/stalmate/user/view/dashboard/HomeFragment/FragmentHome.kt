package com.stalmate.user.view.dashboard.HomeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentHomeBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.UserHomeStoryAdapter


class FragmentHome : BaseFragment(), AdapterFeed.Callbackk, UserHomeStoryAdapter.Callbackk {

    private lateinit var binding: FragmentHomeBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var homeStoryAdapter: UserHomeStoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_home, container, false)
        binding=DataBindingUtil.bind<FragmentHomeBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        feedAdapter = AdapterFeed(networkViewModel, requireContext(), this)
        homeStoryAdapter = UserHomeStoryAdapter(networkViewModel, requireContext(), this)

        binding.rvFeeds.adapter=feedAdapter
        binding.rvStory.adapter=homeStoryAdapter
        binding.rvFeeds.layoutManager= LinearLayoutManager(context)
        binding.rvStory.layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd","oaspiasddsad")
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })



        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd","oaspiasddsad")
            it.let {
                homeStoryAdapter.submitList(it!!.results)
            }
        })



        binding.postContant.userImage.setOnClickListener {
            startActivity(IntentHelper.getProfileScreen(context!!))
        }


    }

    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onClickOnProfile(user: Feed) {

    }

}