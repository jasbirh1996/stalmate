package com.stalmate.user.view.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.stalmate.user.R
import com.stalmate.user.commonadapters.FragmentViewPagerAdapter
import com.stalmate.user.databinding.ActivityFollowersFollowingScreenBinding
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.dashboard.Friend.FragmentFriendList

class ActivityFollowersFollowingScreen : AppCompatActivity() {
    lateinit var binding: ActivityFollowersFollowingScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_followers_following_screen)


        var list = ArrayList<Fragment>()

        list.add(
            FragmentFriendList(
                Constants.TYPE_ALL_FOLLOWERS_FOLLOWING,
                Constants.TYPE_USER_TYPE_FOLLOWERS,
                ""
            )
        )
        list.add(
            FragmentFriendList(
                Constants.TYPE_ALL_FOLLOWERS_FOLLOWING,
                Constants.TYPE_USER_TYPE_FOLLOWINGS,
                ""
            )
        )
        var adapter=FragmentViewPagerAdapter(this,this)
        adapter.addFragments(list)
        binding.viewPager.adapter=adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position == 0) tab.text = "Followers" else if (position == 1) {
                tab.text = "Followings"
            }
        }.attach()

        if (intent.getStringExtra("type").equals("following")) {
            binding.viewPager.setCurrentItem(1,false)
        }
    }

/*    private fun loadFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(binding!!.bottomsheet.bottomsheet.id, fragment, fragmentTag)
            ft.addToBackStack(backStateName)
            ft.commit()
        }

    }*/

}