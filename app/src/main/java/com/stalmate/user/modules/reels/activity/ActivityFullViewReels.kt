package com.stalmate.user.modules.reels.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityFullViewReelsBinding
import com.stalmate.user.modules.reels.adapter.ReelFullViewAdapter
import com.stalmate.user.modules.reels.player.VideoAutoPlayFullViewHelper
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.view.dashboard.funtime.viewmodel.ReelListViewModel


class ActivityFullViewReels : BaseActivity(), ReelFullViewAdapter.Callback {
    lateinit var adapter: ReelFullViewAdapter
    private var controlsVisibleShowHide: Boolean = false;
    private val HIDE_THRESHOLD = 100;
    private var isHeaderAlreadyHidden = false;
    lateinit var binding: ActivityFullViewReelsBinding;
    private var scrolledDistance: Int = 0;
    var isFirstApiHit = true
    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount: kotlin.Int = 0
    var totalItemCount: kotlin.Int = 0

    var videoAutoPlayHelper: VideoAutoPlayFullViewHelper? = null
    override fun onClick(viewId: Int, view: View?) {

    }

    lateinit var reelListViewModel: ReelListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        // window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //  setSystemUIVisibility(true)
        super.onCreate(savedInstanceState)
        //   getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view_reels)!!
        reelListViewModel = ViewModelProvider(this).get(ReelListViewModel::class.java)
        adapter = ReelFullViewAdapter(this, this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter

        /*Helper class to provide AutoPlay feature inside cell*/
        videoAutoPlayHelper = VideoAutoPlayFullViewHelper(recyclerView = binding.recyclerView)
        videoAutoPlayHelper?.startObserving();

        isSelfVideos = intent.getBooleanExtra("showMyVideos", false)

        /*Helper class to provide show/hide toolBar*/

        /*Helper class to provide show/hide toolBar*/
        //  attachScrollControlListener(binding.customToolBar, binding.recyclerView)
        if (intent.hasExtra("data")) {
            val list = ArrayList<ResultFuntime>()
            val data = intent.getParcelableExtra<ResultFuntime>("data") as ResultFuntime
            list.add(data)
            adapter.setList(list)
        }
        callApi()

        binding.ivBack.setOnClickListener {
            onPause()
            finish()
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).getChildCount()
                    totalItemCount =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).getItemCount()
                    pastVisiblesItems =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            Log.v("...", "Last Item Wow !")
                            // Do pagination.. i.e. fetch new data

                            if (!isApiRuning) {
                                page_count++
                                callApi()
                            }

                            loading = true
                        }
                    }
                }
            }
        })

    }

    var isSelfVideos = false
    var page_count = 1
    var isApiRuning = false
    var handler: Handler? = null
    private fun callApi() {
        isApiRuning = true
        val index = 0
        val hashmap = HashMap<String, String>()
        hashmap.put("page", page_count.toString())
        if (isSelfVideos) {
            if (!adapter.reelList.isNullOrEmpty()) {
                hashmap.put("id_user", adapter.reelList[0].user_id.toString())
            } else {
                hashmap.put("id_user", "")
            }
        } else {
            hashmap.put("id_user", "")
        }
        if (!adapter.reelList.isNullOrEmpty()) {
            hashmap.put("fun_id", adapter.reelList[0].id.toString())
        } else {
            hashmap.put("fun_id", "")
        }
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(this) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            Log.d("========", "empty")
            if (!it?.results.isNullOrEmpty()) {
                Log.d("========", "full")
                if (isFirstApiHit) {

                    var list = it?.results

                    list?.forEach {
                        it.isDataUpdated = false

                    }

                    list?.removeAt(0)
                    list?.let { it1 -> adapter.addToList(it1) }
                } else {

                    var list = it?.results

                    list?.forEach {
                        it.isDataUpdated = false
                    }


                    list?.let { it1 -> adapter.addToList(it1) }
                }
                isFirstApiHit = false
            }
        }
    }


    override fun onStart() {
        if (videoAutoPlayHelper != null) {
            var viewholder =
                binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
            if (viewholder != null) {
                val viewMainHolder = (viewholder as VideoReelFullViewHolder)
                viewMainHolder.customPlayerView.startPlaying()
            }
        }
        super.onStart()
    }


    override fun onPause() {
        try {
            val viewholder = binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
            val viewMainHolder = (viewholder as VideoReelFullViewHolder)
            viewMainHolder.customPlayerView.removePlayer()
        }catch (e:Exception){e.printStackTrace()}
        super.onPause()
    }


    private fun likeApiHit(funtime: ResultFuntime) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtime.id.toString())
        networkViewModel.funtimeLiveLikeUnlikeData(hashmap)
        networkViewModel.funtimeLiveLikeUnlikeData.observe(this) {

            it.let {
                if (it!!.status) {
                    adapter.likeReelById(funtime.id.toString())
                }
            }
        }
    }


    override fun finish() {
        val returnIntent = Intent()
        var updatedList = ArrayList<ResultFuntime>()
        adapter.reelList.forEach {
            if (it.isDataUpdated != null) {
                if (it.isDataUpdated!!) {
                    Log.d("alsjdasdddsddmm", it.isDataUpdated.toString())
                    updatedList.add(it)
                }
            }
        }
        returnIntent.putParcelableArrayListExtra("data", updatedList);
        returnIntent.putParcelableArrayListExtra("blockList", blockLisuUser);
        setResult(
            Activity.RESULT_OK,
            returnIntent
        )
        super.finish()
    }


    private fun hitBlockApi(funtime: ResultFuntime) {

        showLoader()
        /*val hashMap = HashMap<String, String>()
        hashMap["id_user"] = funtime.user_id!!
        networkViewModel.block(hashMap)*/
        networkViewModel.block(
            access_token = prefManager?.access_token.toString(),
            _id = funtime.user_id.toString()
        )
        networkViewModel.blockData.observe(this, Observer {
            dismissLoader()
            it.let {
                val position = adapter.reelList.indexOfFirst { it.id == funtime.id }
                binding.recyclerView.smoothScrollToPosition(position + 1)
                Handler(Looper.getMainLooper()).postDelayed(
                    Runnable {
                        val selectedList = ArrayList<ResultFuntime>()
                        adapter.reelList.forEach {
                            if (it.user_id == funtime.user_id) {
                                selectedList.add(it)
                            }
                        }

                        selectedList.forEach {
                            adapter.reelList.remove(it)
                        }



                        adapter.blockUserFromList(position)
                    },
                    500
                )
            }
        })


    }


    fun deleteReel(funtime: ResultFuntime) {

        var hashmap = HashMap<String, String>()
        hashmap.put("id", funtime.id.toString())
        hashmap.put("is_delete", "1")
        hashmap.put("text", "")
        networkViewModel.funtimUpdate(hashmap)
        networkViewModel.funtimeUpdateLiveData.observe(this, Observer {
            it.let {


                if (it!!.status) {
                    var position = adapter.reelList.indexOfFirst { it.id == funtime.id }
                    Log.d("a;lksdasd", position.toString())
                    binding.recyclerView.smoothScrollToPosition(position + 1)
                    Handler(Looper.getMainLooper()).postDelayed(
                        Runnable { adapter.removeReelById(funtime.id.toString()) },
                        500
                    )
                }

            }
        })


    }

    override fun onClickOnRemoveReel(resultFuntime: ResultFuntime) {
        deleteReel(resultFuntime)
    }

    override fun onClickOnLikeButtonReel(resultFuntime: ResultFuntime) {
        likeApiHit(resultFuntime)
    }

    override fun onClickOnEditReel(resultFuntime: ResultFuntime) {
        startActivity(
            IntentHelper.getCreateFuntimePostScreen(this)!!.putExtra("isEdit", true)
                .putExtra("data", resultFuntime)
        )

    }

    override fun onClickOnBlockUser(resultFuntime: ResultFuntime) {
        blockLisuUser.clear()
        blockLisuUser.add(resultFuntime)
        hitBlockApi(resultFuntime)
    }

    var blockLisuUser = ArrayList<ResultFuntime>()


}