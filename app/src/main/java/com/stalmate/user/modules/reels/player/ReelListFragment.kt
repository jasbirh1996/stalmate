package com.stalmate.user.modules.reels.player


import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentreellistBinding
import com.stalmate.user.modules.reels.Extensions.Companion.findFirstVisibleItemPosition
import com.stalmate.user.modules.reels.Extensions.Companion.isAtTop
import com.stalmate.user.modules.reels.player.holders.VideoReelViewHolder
import com.stalmate.user.utilities.NetworkUtils

class ReelListFragment : BaseFragment() {
    lateinit var adapter: ReelAdapter
    private var controlsVisibleShowHide: Boolean = false;
    private val HIDE_THRESHOLD = 100;
    private var isHeaderAlreadyHidden = false;
    lateinit var binding: FragmentreellistBinding;
    private var scrolledDistance: Int = 0;
    var videoAutoPlayHelper: VideoAutoPlayHelper? = null

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


        if (isNetworkAvailable()){
            adapter = ReelAdapter(requireContext())
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
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
        }


    }

    fun isNetworkAvailable() : Boolean{

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


    var page_count = 0
    var isApiRuning = false
    var handler: Handler? = null
    private fun callApi() {
        isApiRuning = true
        val index = 0
        var hashmap = HashMap<String, String>()
        hashmap.put("page", "1")
        hashmap.put("limit", "5")
        hashmap.put("id_user", "")
        hashmap.put("fun_id", "")


        networkViewModel.funtimeLiveData(hashmap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner) {
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
                    val viewMainHolder = (viewholder as VideoReelViewHolder)
                    viewMainHolder.customPlayerView.startPlaying()
                }
            }
        super.onStart()
    }


    override fun onPause() {
       try {
           var viewholder = binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
           val viewMainHolder = (viewholder as VideoReelViewHolder)
           viewMainHolder.customPlayerView.removePlayer()
       }catch (e:Exception){

       }
        super.onPause()
    }


}
