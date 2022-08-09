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


class FragmentHome : BaseFragment(), AdapterFeed.Callbackk {

    private lateinit var binding: FragmentHomeBinding
    lateinit var feedAdapter: AdapterFeed


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


        binding.rvFeeds.adapter=feedAdapter
        binding.rvFeeds.layoutManager= LinearLayoutManager(context)

        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd","oaspiasddsad")
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })


        binding.postContant.userImage.setOnClickListener {
            startActivity(IntentHelper.getProfileScreen(context!!))
        }


    }

    override fun onClickOnViewComments(postId: Int) {

    }

}