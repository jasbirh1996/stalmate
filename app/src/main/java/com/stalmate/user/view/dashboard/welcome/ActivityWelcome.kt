package com.stalmate.user.view.dashboard.welcome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityWelcomeBinding


class ActivityWelcome : BaseActivity(){
    lateinit var binding:ActivityWelcomeBinding
    var current_position = 0
    var count = 0
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_welcome)

        var pagerAdapter=MyPagerAdapter(supportFragmentManager)

        binding.viewpager.adapter = pagerAdapter

        binding.indicator.setViewPager(binding.viewpager)

        current_position = binding.viewpager.currentItem

        binding.viewpager.setOnTouchListener(OnTouchListener { v, event -> true })

        binding.viewpager.offscreenPageLimit = 0

        binding.btnNext.setOnClickListener {

            var page = pagerAdapter.getItem(count)

            Log.d("alskdjas", count.toString())
            Log.d("alskdjas", page.toString())

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

            if(page is FragmentPageSugggestionsList){
                count = count +1
                binding.viewpager.setCurrentItem(count,true)
            }
        }

        /*ToolBar Set*/
        toolbar()

    }

    private fun toolbar() {

        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text = getString(R.string.welcome)
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
    }

    class MyPagerAdapter(fragmentManager: FragmentManager?) : FragmentPagerAdapter(fragmentManager!!) {
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
                else -> Fragment()
            }

        }

        override fun getCount(): Int {
            return 6
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }



}