package com.stalmate.user.view.dashboard.welcome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.NonNull
import androidx.annotation.Nullable
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

        var viewpagerAdapter=ViewPagerAdapter(supportFragmentManager)
        viewpagerAdapter.add(FragmentWelcomePage(),"title")
        viewpagerAdapter.add(FragmentInformationSuggestions(),"title")
        viewpagerAdapter.add(FragmentSync(),"title")
        viewpagerAdapter.add(FragmentGroupSuggestionList(),"title")
        viewpagerAdapter.add(FragmentPageSugggestionsList(),"title")
        viewpagerAdapter.add(FragmentEventSuggestionsList(),"title")
        viewpagerAdapter.add(FragmentInterestSuggestionList(),"title")

        binding.viewpager.adapter=viewpagerAdapter
        binding.indicator.setViewPager(binding.viewpager)


        count = binding.viewpager.currentItem
        binding.viewpager.setOnTouchListener(OnTouchListener { v, event -> true })
      /*  binding.viewpager.offscreenPageLimit = 0*/
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            var page=viewpagerAdapter.getItem(count)

            Log.d("asghdasd",page.toString())
            if (count==6){
                finish()
            }else{
                if (page is FragmentInformationSuggestions){

                    /* count = count +1
                     binding.viewpager.setCurrentItem(count,true)
     */
                    if (page.isValid()){
                        count = +1
                        binding.viewpager.setCurrentItem(count,true)
                    }
                }else{
                    count++
                    binding.viewpager.setCurrentItem(count, true)
                }


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



    class ViewPagerAdapter(@NonNull fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val fragmentTitle: MutableList<String> = ArrayList()
        fun add(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitle.add(title)
        }

        @NonNull
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitle[position]
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