package com.stalmate.user.modules.reels.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityFullViewReelsBinding
import com.stalmate.user.modules.reels.adapter.ReelFullViewAdapter
import com.stalmate.user.modules.reels.player.VideoAutoPlayFullViewHelper
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.ActivityDashboardNew
import com.stalmate.user.view.dashboard.funtime.ActivityFuntimePost
import com.stalmate.user.view.dashboard.funtime.ModelFuntimeResponse
import com.stalmate.user.view.dashboard.funtime.ResultFuntime


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

    override fun onCreate(savedInstanceState: Bundle?) {
        // window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //  setSystemUIVisibility(true)
        super.onCreate(savedInstanceState)
        //   getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view_reels)!!

        adapter = ReelFullViewAdapter(this, this)
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
        var list = ArrayList<ResultFuntime>()
        var data = intent.getParcelableExtra<ResultFuntime>("data") as ResultFuntime


        isSelfVideos = intent.getBooleanExtra("showMyVideos", false)
        Log.d("klajsdasd", isSelfVideos.toString())


        list.add(data)
        adapter.setList(list)
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


        var hashmap = HashMap<String, String>()
        hashmap.put("page", page_count.toString())
        if (isSelfVideos) {
            hashmap.put("id_user", adapter.reelList[0].user_id)
        } else {
            hashmap.put("id_user", "")
        }

        hashmap.put("fun_id", adapter.reelList[0].id)
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(hashmap)
        networkViewModel.funtimeLiveData.observe(this) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            Log.d("========", "empty")
            if (it!!.results.isNotEmpty()) {
                Log.d("========", "full")
                if (isFirstApiHit) {

                    var list = it.results
                    list.removeAt(0)
                    adapter.addToList(list)
                } else {
                    adapter.addToList(it.results)
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
        var viewholder =
            binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
        val viewMainHolder = (viewholder as VideoReelFullViewHolder)
        viewMainHolder.customPlayerView.removePlayer()
        super.onPause()

    }

/*    fun setSystemUIVisibility(hide: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window = window.insetsController!!
            val windows = WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            if (hide) window.hide(windows) else window.show(windows)
            // needed for hide, doesn't do anything in show
            window.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            val view = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = if (hide) view else view.inv()
        }
    }*/

/*    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val decorView = window.decorView
        if (hasFocus) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }*/

    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }


    private fun likeApiHit(funtime: ResultFuntime) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtime.id)
        networkViewModel.funtimeLiveLikeUnlikeData(hashmap)
        networkViewModel.funtimeLiveLikeUnlikeData.observe(this) {

            it.let {
                if (it!!.status) {
                    adapter.likeReelById(funtime.id)
                }
            }
        }
    }


    fun deleteReel(funtime: ResultFuntime) {

        /*   var hashmap = HashMap<String, String>()
           hashmap.put("id", funtime.id)
           hashmap.put("is_delete", "1")
           hashmap.put("text", "")
           networkViewModel.funtimUpdate(hashmap)
           networkViewModel.funtimeUpdateLiveData.observe(this, Observer {
               it.let {


                   if (it!!.status) {
                       adapter.removeReelById(funtime.id)
                   }

               }
           })*/
        var position = adapter.reelList.indexOfFirst { it.id == funtime.id }
        Log.d("a;lksdasd", position.toString())
        binding.recyclerView.smoothScrollToPosition(position + 1)
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable { adapter.removeReelById(funtime.id) },
            500
        )

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

    }


}