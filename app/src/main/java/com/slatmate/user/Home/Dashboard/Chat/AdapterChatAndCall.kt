package com.slatmate.user.Home.Dashboard.Chat

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.slatmate.user.Home.Dashboard.Chat.CallFragment.FragmentCall
import com.slatmate.user.Home.Dashboard.Chat.ChatFragment.FragmentChat

class AdapterChatAndCall(myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
         when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
               return FragmentChat()
            }
            1 -> {
               return FragmentCall()
            }

            else -> {
                return FragmentChat()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}