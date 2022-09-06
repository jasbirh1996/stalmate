package com.stalmate.user.view.dashboard.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityWelcomeBinding

class ActivityWelcome : AppCompatActivity() {
    lateinit var binding:ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_welcome)

        binding.viewpager.adapter=MyPagerAdapter(supportFragmentManager)
        binding.indicator.setViewPager(binding.viewpager);




    }

    class MyPagerAdapter(fragmentManager: FragmentManager?) :
        FragmentPagerAdapter(fragmentManager!!) {
        // Returns the fragment to display for that page
        override
        fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> FragmentInterestSuggestionList()
                1 -> FragmentEventSuggestionsList()
                2 -> FragmentGroupSuggestionList()
                3 ->FragmentInterestSuggestionList()
                4 -> FragmentInterestSuggestionList()
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return 5
        }

        // Returns the page title for the top indicator
        override
        fun getPageTitle(position: Int): CharSequence {
            return "Page $position"
        }

    }

}