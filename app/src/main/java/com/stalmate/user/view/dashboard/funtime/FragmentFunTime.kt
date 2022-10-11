package com.stalmate.user.view.dashboard.funtime

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFunTimeBinding
import fr.castorflex.android.verticalviewpager.VerticalViewPager


class FragmentFunTime() : BaseFragment(), Player.Listener {

    lateinit var binding: FragmentFunTimeBinding
    private lateinit var viewPagerStatAdapter: ViewPagerStatAdapter
    var exoplayer: SimpleExoPlayer? = null
    var videoList: ArrayList<ResultFuntime>? = null
    lateinit var menuPager: VerticalViewPager
    var swiperefresh: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFunTimeBinding>(inflater.inflate(R.layout.fragment_fun_time, container, false))!!


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuPager = view.findViewById(R.id.rvRecyclerView)

        viewPagerStatAdapter = ViewPagerStatAdapter(childFragmentManager)
        binding.rvRecyclerView.adapter = viewPagerStatAdapter

        binding.shimmerLayout.visibility =  View.VISIBLE

        setVideoData()

        binding.ivAddButton.setOnClickListener {
            startActivity(IntentHelper.getCreateReelsScreen(requireActivity()))
        }


        val is_visible_to_user = false
        menuPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

               /* if (position == 0) {
                    swiperefresh!!.setEnabled(true)
                } else {
                    swiperefresh!!.setEnabled(false)
                }*/
                if (position == 0 && viewPagerStatAdapter != null && viewPagerStatAdapter.getCount() > 0) {
                    //  VideoListF fragment = (VideoListF) pagerSatetAdapter.getItem(menuPager.getCurrentItem());
                    val fragment: FragmentReelPlay = viewPagerStatAdapter.getItem(menuPager.currentItem) as FragmentReelPlay
                    fragment.initializePlayer()
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragment.setPlayer(
                            is_visible_to_user
                        )
                    }, 200)
                }
                if (videoList!!.size > 5 && videoList!!.size - 5 == position + 1) {
                    /*if (!isApiRuning) {
                        page_count++
                        loadVideos()
                    }*/
                }
            }
            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }


    private fun setVideoData() {
        val index = 0
        var hashmap = HashMap<String, String>()
        hashmap.put("page", "1")
        networkViewModel.funtimeLiveData(hashmap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner) {

            Log.d("========","empty")
            if (it!!.results.isNotEmpty()){
                binding.shimmerLayout.visibility = View.GONE
                binding.rvRecyclerView.visibility = View.VISIBLE
                videoList = it.results

                Log.d("========","Notempty")
                for (item in it.results) {
                    viewPagerStatAdapter.addFragment(FragmentReelPlay(item), "")
                }
                viewPagerStatAdapter.refreshStateSet(false)
                viewPagerStatAdapter.notifyDataSetChanged()
                menuPager.visibility = View.VISIBLE
            }
        }
    }


    var isVisibleToUser = false

    override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        isVisibleToUser = visible
        Handler(Looper.getMainLooper()).postDelayed({
            if (exoplayer != null && visible) {

            }
        }, 200)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (exoplayer != null) {
            exoplayer!!.playWhenReady = false
            val fragment: FragmentReelPlay = viewPagerStatAdapter.getItem(menuPager.currentItem) as FragmentReelPlay
            fragment.mainMenuVisibility(false)
        }
    }
}