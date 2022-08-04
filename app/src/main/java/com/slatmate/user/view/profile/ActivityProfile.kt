package com.slatmate.user.view.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.slatmate.user.R
import com.slatmate.user.base.BaseActivity
import com.slatmate.user.commonadapters.AdapterFeed
import com.slatmate.user.databinding.ActivityProfileBinding
import com.slatmate.user.viewmodel.AppViewModel

class ActivityProfile : BaseActivity(), AdapterFeed.Callbackk {
    lateinit var binding: ActivityProfileBinding
    lateinit var feedAdapter: AdapterFeed
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        feedAdapter = AdapterFeed(networkViewModel, this, this)

        networkViewModel.feedLiveData.observe(this, Observer {
            it.let {

                feedAdapter.submitList(it!!.data)
            }
        })

        networkViewModel.getFeedList("", HashMap())
    }

    override fun onClickOnViewComments(postId: Int) {
    }
}