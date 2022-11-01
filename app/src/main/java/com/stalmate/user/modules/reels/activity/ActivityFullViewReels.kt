package com.stalmate.user.modules.reels.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityFullViewReelsBinding
import com.stalmate.user.databinding.FragmentreellistBinding
import com.stalmate.user.modules.reels.adapter.ReelFullViewAdapter
import com.stalmate.user.modules.reels.player.ReelAdapter
import com.stalmate.user.modules.reels.player.VideoAutoPlayFullViewHelper

import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

class ActivityFullViewReels : BaseActivity() {
    lateinit var adapter: ReelFullViewAdapter
    private var controlsVisibleShowHide: Boolean = false;
    private val HIDE_THRESHOLD = 100;
    private var isHeaderAlreadyHidden = false;
    lateinit var binding: ActivityFullViewReelsBinding;
    private var scrolledDistance: Int = 0;
    var videoAutoPlayHelper: VideoAutoPlayFullViewHelper? = null
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this,R.layout.activity_full_view_reels)!!

        adapter = ReelFullViewAdapter(this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter

        /*Helper class to provide AutoPlay feature inside cell*/
        videoAutoPlayHelper =
            VideoAutoPlayFullViewHelper(recyclerView = binding.recyclerView)
        videoAutoPlayHelper!!.startObserving();


        /*Helper class to provide show/hide toolBar*/

        /*Helper class to provide show/hide toolBar*/
        //  attachScrollControlListener(binding.customToolBar, binding.recyclerView)
        var list=ArrayList<ResultFuntime>()
       var data =intent.getParcelableExtra<ResultFuntime>("data") as ResultFuntime
        list.add(data)
        adapter.setList(list)
        callApi()

        binding.ivBack.setOnClickListener {
            onPause()
            finish()
        }

    }


    var page_count = 0
    var isApiRuning = false
    var handler: Handler? = null
    private fun callApi() {
        isApiRuning = true
        val index = 0
        var hashmap = HashMap<String, String>()
        hashmap.put("page", "1")
        networkViewModel.funtimeLiveData(hashmap)
        networkViewModel.funtimeLiveData.observe(this) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            Log.d("========", "empty")
            if (it!!.results.isNotEmpty()) {
                Log.d("========", "full")
                adapter.addToList(it.results)
            }
        }
    }


    override fun onStart() {
        if (videoAutoPlayHelper!=null){
            var viewholder =
                binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
            if (viewholder!=null){
                val viewMainHolder = (viewholder as VideoReelFullViewHolder)
                viewMainHolder.customPlayerView.startPlaying()
            }

        }
        super.onStart()
    }


    override fun onPause() {
        var viewholder =
            binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
        val viewMainHolder = (viewholder as VideoReelFullViewHolder)
        viewMainHolder.customPlayerView.removePlayer()
        super.onPause()

    }

}