package com.stalmate.user.view.profile

import android.app.ActivityOptions
import android.content.Intent
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
import com.stalmate.user.databinding.ActivityProfileBinding
import com.stalmate.user.view.adapter.ProfileFriendAdapter

class ActivityProfile : BaseActivity(), AdapterFeed.Callbackk, ProfileFriendAdapter.Callbackk {
    lateinit var binding: ActivityProfileBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var friendAdapter: ProfileFriendAdapter
    val PICK_IMAGE_PROFILE = 1
    val PICK_IMAGE_COVER = 1

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


/*
        friendAdapter = ProfileFriendAdapter(networkViewModel, this, this)

        binding.layout.rvFriends.adapter = friendAdapter
        binding.layout.rvFriends.layoutManager = GridLayoutManager(this, 3)
        networkViewModel.getFriendList("", HashMap())
        networkViewModel.friendLiveData.observe(this, Observer {
            Log.d("asdasdasd", "oaspidsad")
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })*/



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


        binding.idCoverPhoto.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent,PICK_IMAGE_COVER )
        }

        binding.idCameraProfile.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent,PICK_IMAGE_PROFILE)
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath : String? = ""

        if (requestCode == PICK_IMAGE_PROFILE && resultCode == RESULT_OK){

        }else if (requestCode == PICK_IMAGE_COVER && resultCode == RESULT_OK){

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

