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
import com.stalmate.user.databinding.ActivityProfileBinding

import com.stalmate.user.view.adapter.ProfileFriendAdapter

class ActivityProfile : BaseActivity(), AdapterFeed.Callbackk, ProfileFriendAdapter.Callbackk {
    lateinit var binding: ActivityProfileBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var friendAdapter: ProfileFriendAdapter

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
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



        setupData()
    }

    override fun onClickOnViewComments(postId: Int) {

    }


    fun setupData() {

        binding.layout.line1.root.visibility = View.VISIBLE
        binding.layout.line2.root.visibility = View.VISIBLE
        binding.layout.line3.root.visibility = View.VISIBLE
        binding.layout.line4.root.visibility = View.VISIBLE

        binding.layout.line1.tvKey.text = "Designer at"
        binding.layout.line1.tvValue.text = "Etisalat"

        binding.layout.line2.tvKey.text = "Studied at"
        binding.layout.line2.tvValue.text = "Zayed University"

        binding.layout.line3.tvKey.text = "From"
        binding.layout.line3.tvValue.text = "Abu Shabi, UAE"

        binding.layout.line4.tvKey.text = "Single"
        binding.layout.line4.tvValue.visibility = View.GONE




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
}

