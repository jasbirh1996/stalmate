package com.stalmate.user.view.dashboard.funtime

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFunTimeBinding
import com.stalmate.user.databinding.FragmentreellistBinding
import com.stalmate.user.modules.reels.player.Constants
import com.stalmate.user.modules.reels.player.ReelListFragment
import com.stalmate.user.modules.reels.player.VideoPreLoadingService
import com.stalmate.user.view.dashboard.ActivityDashboardNew
import fr.castorflex.android.verticalviewpager.VerticalViewPager

class FragmentFunTime() : BaseFragment(), FragmentCallBack {
    var handler: Handler? = null
        var isPlusButtonActive=false
    lateinit var binding: FragmentFunTimeBinding
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
        binding.ivBack.setOnClickListener {
            (requireActivity() as ActivityDashboardNew).onBackPressed()
        }

        binding.ivAddButton.setOnClickListener {
            startActivity(IntentHelper.getCreateReelsScreen(requireActivity())!!.putExtra("type","image"))
        }

        loadFragment(ReelListFragment())



    }


    fun toggleButton(){
        if (isPlusButtonActive){
            isPlusButtonActive=false
            binding.layoutImagenVideo.animate().alpha(0f).setDuration(500).start()
        }else{
            isPlusButtonActive=true
            binding.layoutImagenVideo.animate().alpha(1f).setDuration(500).start()
        }
    }



  
    private fun loadFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = childFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding.frame.id, fragment, fragmentTag)
            ft.commit()
        }
    }

    override fun onResponce(bundle: Bundle?) {
        
    }

    fun pauseMusic(){
          var fragment=  childFragmentManager.findFragmentById(binding.frame.id) as ReelListFragment
        fragment.onPause()
    }
    fun resumeMusic(){
        var fragment=  childFragmentManager.findFragmentById(binding.frame.id) as ReelListFragment
        fragment.onStart()
    }









}













