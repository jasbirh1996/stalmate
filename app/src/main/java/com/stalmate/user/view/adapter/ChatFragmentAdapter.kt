package com.stalmate.user.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ChatFragmentAdapter (fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
){
    private var fragmentList : ArrayList<Fragment> = ArrayList()
    private var fragmentTitle = ArrayList<String>()
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitle[position]
    }
    fun addFragment(fragment : Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitle.add(title)
    }
}