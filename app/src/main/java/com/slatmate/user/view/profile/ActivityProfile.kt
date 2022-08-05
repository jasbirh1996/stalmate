package com.slatmate.user.view.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.layout.rvFeeds.adapter=feedAdapter
        binding.layout.rvFeeds.layoutManager=LinearLayoutManager(this)

        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(this, Observer {
            Log.d("asdasdasd","oaspidsad")
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        })
        setupData()
    }

    override fun onClickOnViewComments(postId: Int) {

    }
    
    
    fun setupData(){

        binding.layout.line1.root.visibility=View.VISIBLE
        binding.layout.line2.root.visibility=View.VISIBLE
        binding.layout.line3.root.visibility=View.VISIBLE
        binding.layout.line4.root.visibility=View.VISIBLE

        binding.layout.line1.tvKey.text="Designer at"
        binding.layout.line1.tvValue.text="Etisalat"

        binding.layout.line2.tvKey.text="Studied at"
        binding.layout.line2.tvValue.text="Zayed University"

        binding.layout.line3.tvKey.text="From"
        binding.layout.line3.tvValue.text="Abu Shabi, UAE"

        binding.layout.line4.tvKey.text="Single"
        binding.layout.line4.tvValue.visibility=View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

