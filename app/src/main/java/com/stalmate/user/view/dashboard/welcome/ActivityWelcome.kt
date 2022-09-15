package com.stalmate.user.view.dashboard.welcome

import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityWelcomeBinding


class ActivityWelcome : BaseActivity() {
    lateinit var binding: ActivityWelcomeBinding
    var count = 0
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        var pagerAdapter = MyPagerAdapter(supportFragmentManager)
        binding.viewpager.adapter = pagerAdapter
        binding.indicator.setViewPager(binding.viewpager)
        count = binding.viewpager.currentItem
        binding.viewpager.setOnTouchListener(OnTouchListener { v, event -> true })
        binding.viewpager.offscreenPageLimit = 0
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnNext.setOnClickListener {
           /* if (count==6){
                finish()
            }else{
                count++
                binding.viewpager.setCurrentItem(count, true)
            }*/

            var page = pagerAdapter.getItem(count)

            if (page is FragmentWelcomePage){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
            }

            if (page is FragmentInformationSuggestions){

                count = count +1
                binding.viewpager.setCurrentItem(count,true)

                /*if (page.isValid()){
                    count = +1
                    binding.viewpager.setCurrentItem(count,true)
                }*/
            }

            if(page is FragmentSync){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
            }

            if(page is FragmentGroupSuggestionList){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
            }
            if(page is FragmentPageSugggestionsList){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
            }

            if(page is FragmentEventSuggestionsList){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
            }

            if(page is FragmentInterestSuggestionList){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
                finish()
            }
        }

        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        toolbar(true, "Welcome")
                    }
                    1 -> {
                        toolbar(true, "Welcome")
                    }
                    2 -> {
                        toolbar(false, "Group")
                    }
                    3 -> {
                        toolbar(false, "Pages")
                    }
                    4 -> {
                        toolbar(false, "Events")

                    }


                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        /*ToolBar Set*/
        toolbar(true, "Welcome")

    }


fun toolbar(isCenterVisible: Boolean, text: String) {

    if (isCenterVisible) {
        binding.toolbar.tvhead.visibility = View.GONE
        binding.toolbar.tvheadCenterHeadBold.visibility = View.VISIBLE
        binding.toolbar.tvheadCenterHeadBold.text = text
    } else {
        binding.toolbar.tvhead.visibility = View.VISIBLE
        binding.toolbar.tvheadCenterHeadBold.visibility = View.GONE
        binding.toolbar.tvhead.text = text
    }


}

class MyPagerAdapter(fragmentManager: FragmentManager?) :
    FragmentPagerAdapter(fragmentManager!!) {
    // Returns the fragment to display for that page
    override
    fun getItem(position: Int): Fragment {

        return when (position) {

            0 -> FragmentWelcomePage()
            1 -> FragmentInformationSuggestions()
            2 -> FragmentSync()
            3 -> FragmentGroupSuggestionList()
            4 -> FragmentPageSugggestionsList()
            5 -> FragmentEventSuggestionsList()
            6 -> FragmentInterestSuggestionList()
            else -> Fragment()
        }

    }

    override fun getCount(): Int {
        return 7
    }
}

override fun onBackPressed() {

    if (count != 0) {
        count--
        binding.viewpager.setCurrentItem(count, true)
    } else {
        super.onBackPressed()
    }
}


}