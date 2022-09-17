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
import com.google.gson.Gson
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityWelcomeBinding
import com.stalmate.user.model.Category
import com.stalmate.user.view.adapter.AdapterCategory


class ActivityWelcome : BaseActivity(), FragmentInformationSuggestions.Callbackk, AdapterCategory.Callbackk {
    lateinit var binding: ActivityWelcomeBinding
    var count = 0
    var countryText = ""
    var graduationText = ""
    var graduationTextId = ""
    var majorTextText = ""
    var majorTextTextId = ""
    var cityText = ""
    private var datasss: ArrayList<Category>? = null
    private var datasssaa: ArrayList<String>? = null

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)

        var viewpagerAdapter=ViewPagerAdapter(supportFragmentManager)
        viewpagerAdapter.add(FragmentWelcomePage(),"title")
        viewpagerAdapter.add(FragmentInformationSuggestions(this),"title")
        viewpagerAdapter.add(FragmentInterestSuggestionList(),"title")
        viewpagerAdapter.add(FragmentSync(),"title")
        viewpagerAdapter.add(FragmentGroupSuggestionList(),"title")
        viewpagerAdapter.add(FragmentPageSugggestionsList(),"title")
        viewpagerAdapter.add(FragmentEventSuggestionsList(),"title")


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

            if (count==6){
                finish()
            }else{
                if (page is FragmentInformationSuggestions){

                   /* if (page.isValid()){
                        val hashMap = HashMap<String, String>()
                        hashMap["university_name"] = graduationText
                        hashMap["university_id"] = graduationTextId
                        hashMap["branch_name"] = majorTextText
                        hashMap["branch_id"] = majorTextTextId
                        hashMap["country"] = countryText
                        hashMap["city"] = cityText

                       showLoader()

                        networkViewModel.aboutProfileUpdate(hashMap)
                        networkViewModel.aboutProfileData.observe(this){

                            it?.let {
                                val message = it.message

                                if (it.status == true){
                                    dismissLoader()
                                    count++
                                    binding.viewpager.setCurrentItem(count, true)
                                }else{

                                    dismissLoader()
                                    makeToast(message)

                                }
                            }
                        }

                    }*/

                    count++
                    binding.viewpager.setCurrentItem(count, true)


                }else if (page is FragmentInterestSuggestionList) {

                    if (page.isvalid()) {


                      /* var adapterCategory : AdapterCategory? = null
                       makeToast(adapterCategory!!.getSelected()!!.name)*/

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

    override fun onCallBackData(
        graducation: String,
        graducationId: String,
        major: String,
        majorId: String,
        country: String,
        state: String,
        city: String
    ) {

        graduationText = graducation
        majorTextText = major
        countryText = country
        cityText = city
        graduationTextId = graducationId
        majorTextTextId = majorId

    }
    override fun onClickIntrastedItem(data: ArrayList<Category>) {

        datasss = data


    }


}