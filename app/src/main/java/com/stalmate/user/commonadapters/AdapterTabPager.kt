package com.stalmate.user.commonadapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stalmate.user.view.dashboard.HomeFragment.FragmentProfileFuntime


class AdapterTabPager(activity: FragmentActivity?) : FragmentStateAdapter(activity!!) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                imageFragment
            }
            1 -> {
                videoFragment
            }
            else -> {
                imageFragment
            }
        }
    }

    private val imageFragment by lazy {
        FragmentProfileFuntime().apply {
            this.arguments = Bundle().apply {
                putBoolean("isVideos", false)
            }
        }
    }
    private val videoFragment by lazy {
        FragmentProfileFuntime().apply {
            this.arguments = Bundle().apply {
                putBoolean("isVideos", true)
            }
        }
    }
}