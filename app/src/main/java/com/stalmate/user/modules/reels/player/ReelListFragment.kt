package com.stalmate.user.modules.reels.player


import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.gson.Gson
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentreellistBinding
import com.stalmate.user.modules.reels.Extensions.Companion.findFirstVisibleItemPosition
import com.stalmate.user.modules.reels.Extensions.Companion.isAtTop
import com.stalmate.user.modules.reels.player.holders.VideoReelViewHolder
import com.stalmate.user.utilities.NetworkUtils
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

class ReelListFragment : BaseFragment(), ReelAdapter.Callback {
    lateinit var adapter: ReelAdapter
    private var controlsVisibleShowHide: Boolean = false;
    private val HIDE_THRESHOLD = 100;
    private var isHeaderAlreadyHidden = false;
    lateinit var binding: FragmentreellistBinding;
    private var scrolledDistance: Int = 0;
    var videoAutoPlayHelper: VideoAutoPlayHelper? = null

    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount: kotlin.Int = 0
    var totalItemCount: kotlin.Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentreellistBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* Set adapter (items are being used inside adapter, you can setup in your own way*/

        if (isNetworkAvailable()) {
            adapter = ReelAdapter(requireContext(), this)
            binding.recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding.recyclerView)
            binding.recyclerView.adapter = adapter

            /*Helper class to provide AutoPlay feature inside cell*/
            videoAutoPlayHelper = VideoAutoPlayHelper(recyclerView = binding.recyclerView)
            videoAutoPlayHelper!!.startObserving();


            /*Helper class to provide show/hide toolBar*/

            /*Helper class to provide show/hide toolBar*/
            //  attachScrollControlListener(binding.customToolBar, binding.recyclerView)
            callApi()
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


    }

    fun isNetworkAvailable(): Boolean {

        return NetworkUtils.isNetworkAvailable()
    }


    /**
     * This method will show hide view passed as @param -toolBar
     */
    fun attachScrollControlListener(toolBar: View?, recyclerView: RecyclerView) {

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                var firstVisibleItem = -1
                try {
                    firstVisibleItem = recyclerView.findFirstVisibleItemPosition()
                } catch (e: Exception) {

                }

                if (firstVisibleItem == -1) {
                    return
                }

                //show views if first item is first visible position and views are hidden

                if (firstVisibleItem == 0 && recyclerView.computeVerticalScrollOffset() < HIDE_THRESHOLD) {
                    if (!controlsVisibleShowHide) {
                        controlsVisibleShowHide = true
                        showTopBarWithAnim(toolBar, recyclerView, true, null)
                        scrolledDistance = 0
                    }
                } else {
                    if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisibleShowHide) {
                        controlsVisibleShowHide = true
                        showTopBarWithAnim(toolBar, recyclerView, true, null)
                        scrolledDistance = 0
                    } else if (dy > 0/* && hideForcefully()*/ || scrolledDistance > HIDE_THRESHOLD && controlsVisibleShowHide) {
                        controlsVisibleShowHide = false
                        showTopBarWithAnim(toolBar, recyclerView, false, null)
                        scrolledDistance = 0
                    }
                }

                if (controlsVisibleShowHide && dy > 0 || !controlsVisibleShowHide && dy < 0) {
                    scrolledDistance += dy
                }

            }
        })

    }


    /***
     * Animation to show/hide
     */
    fun showTopBarWithAnim(
        toolBar: View?,
        recyclerView: RecyclerView,
        show: Boolean,
        animationListener: Animator.AnimatorListener?
    ) {
        if (show) {
            if (!isHeaderAlreadyHidden) {
                return
            }
            isHeaderAlreadyHidden = false
            toolBar?.animate()?.translationY(0f)
                ?.setInterpolator(DecelerateInterpolator(2f))
        } else {
            // To check if at the top of recycler view
            if (recyclerView.isAtTop()
            ) {
                // Its at top
                return
            }
            if (isHeaderAlreadyHidden) {
                return
            }
            isHeaderAlreadyHidden = true
            toolBar?.animate()
                ?.translationY((-toolBar?.getHeight()!!).toFloat())
                ?.setInterpolator(AccelerateInterpolator(2F))

        }
    }


    var isFirstApiHit = true
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
            hashmap.put("id_user", adapter.reelList[0].user_id.toString())
        } else {
            hashmap.put("id_user", "")
        }
        hashmap.put("fun_id", "")
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            if (!it?.results.isNullOrEmpty()) {
                it?.results?.let {
                    if (isFirstApiHit) {
                        it.forEach {
                            it.isDataUpdated = false
                        }
                        adapter.setList(it)
                    } else {
                        it.forEach {
                            it.isDataUpdated = false
                        }
                        adapter.addToList(it)
                    }
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
                val viewMainHolder = (viewholder as VideoReelViewHolder)
                /*  if ((requireActivity()  as ActivityDashboard).active is FragmentFunTime){
                      viewMainHolder.customPlayerView.startPlaying()
                  }*/
            }
        }
        super.onStart()
    }


    override fun onPause() {
        try {
            var viewholder =
                binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
            val viewMainHolder = (viewholder as VideoReelViewHolder)
            viewMainHolder.customPlayerView.removePlayer()
        } catch (e: Exception) {

        }
        super.onPause()
    }

    override fun onClickOnRemoveReel(resultFuntime: ResultFuntime) {

    }

    override fun onClickOnLikeButtonReel(resultFuntime: ResultFuntime) {

        likeApiHit(resultFuntime)


    }


    private fun likeApiHit(funtime: ResultFuntime) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtime.id.toString())
        networkViewModel.funtimeLiveLikeUnlikeData(hashmap)
        networkViewModel.funtimeLiveLikeUnlikeData.observe(this) {

            it.let {
                if (it!!.status) {

                }
            }
        }
    }

    override fun onClickOnEditReel(resultFuntime: ResultFuntime) {

    }

    override fun onClickOnBlockUser(resultFuntime: ResultFuntime) {

    }

    override fun onClickOnFullView(resultFuntime: ResultFuntime) {
        startActivityForResult(
            IntentHelper.getFullViewReelActivity(context)!!.putExtra("data", resultFuntime), 120
        )
        //  startForResultReels.launch(IntentHelper.getFullViewReelActivity(requireContext()))
    }

    override fun onClickOnShareReel(resultFuntime: ResultFuntime) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 120) {


            val blocklList: ArrayList<ResultFuntime> =
                data!!.getParcelableArrayListExtra("blockList")!!

            if (blocklList.size == 0) {
                val updatedReelList: ArrayList<ResultFuntime> =
                    data!!.getParcelableArrayListExtra("data")!!
                Log.d("lakjdasd", Gson().toJson(updatedReelList))
                adapter.updateList(updatedReelList!!)
            } else {
                Log.d("lakjdasd", "Gson().toJson(updatedReelList)")
                isFirstApiHit = true
                page_count = 1
                callApi()
            }


        }
    }

    val startForResultReels =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //  you will get result here in result.data
                Log.d(";laskdasd", ";alksdasd")


                adapter
            }

        }


}
