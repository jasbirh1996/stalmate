package com.stalmate.user.commonadapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentViewPagerAdapter(fragmentActivity: FragmentActivity, private val context: Context) :
    FragmentStateAdapter(fragmentActivity) {
    var fragments = ArrayList<Fragment>()
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragments(fragments: ArrayList<Fragment>) {
        this.fragments = fragments
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}