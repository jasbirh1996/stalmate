package com.stalmate.user.view.dashboard.welcome

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityWelcomeBinding
import com.stalmate.user.model.Category
import com.stalmate.user.modules.contactSync.SyncService
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.AdapterCategory


class ActivityWelcome : BaseActivity(),
    AdapterCategory.Callbackk, FragmentSync.Callback, FragmentInformationSuggestions.Callback {
    lateinit var binding: ActivityWelcomeBinding
    lateinit var syncBroadcastreceiver: SyncBroadcasReceiver
    var count = 0
    var countryText = ""
    var graduationText = ""
    var graduationTextId = ""
    var majorTextText = ""
    var majorTextTextId = ""
    var cityText = ""
    private var datasss: ArrayList<Category>? = null

    override fun onClick(viewId: Int, view: View?) {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        toolbar(true, "Welcome")

        val filter = IntentFilter()
        filter.addAction(Constants.ACTION_SYNC_COMPLETED)
        syncBroadcastreceiver = SyncBroadcasReceiver()
        registerReceiver(syncBroadcastreceiver, filter)

        var viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewpagerAdapter.add(FragmentWelcomePage(), "title")
        viewpagerAdapter.add(FragmentInterestSuggestionList(), "title")
        viewpagerAdapter.add(FragmentInformationSuggestions(this),"title")
        viewpagerAdapter.add(FragmentSync(), "title")
        viewpagerAdapter.add(FragmentSync(), "title")


        binding.viewpager.adapter = viewpagerAdapter
        binding.indicator.setViewPager(binding.viewpager)

        count = binding.viewpager.currentItem
        binding.viewpager.setOnTouchListener(OnTouchListener { v, event -> true })
        /*  binding.viewpager.offscreenPageLimit = 0*/
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            var page = viewpagerAdapter.getItem(count)
            if (count == 3) {
                finish()
            } else {
                if (page is FragmentInformationSuggestions) {
                    if (page.isValid()){
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

                }
                } else if (page is FragmentInterestSuggestionList) {

                    if (page.getSelectedDAta().size > 0) {
                        val selectedInterestString: String =
                            java.lang.String.join(",", page.getSelectedDAta())

                            val hashMap = HashMap<String, String>()
                            hashMap["category_id"] = selectedInterestString

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
                    } else {
                        makeToast("Select atleast one interest")
                    }

                } else {
                    count++
                    binding.viewpager.setCurrentItem(count, true)
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
                            setUpNextButton(false)
                        }
                        1 -> {
                            toolbar(true, "Welcome")
                            setUpNextButton(false)
                        }
                        2 -> {
                            toolbar(false, "Group")
                            setUpNextButton(true)
                        }
                        3 -> {
                            toolbar(false, "Pages")
                            setUpNextButton(true)
                        }
                        4 -> {
                            toolbar(false, "Events")
                            setUpNextButton(true)

                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })

            /*ToolBar Set*/
            toolbar(true, "Welcome")

            var permissionArray = arrayOf(android.Manifest.permission.READ_CONTACTS)
            if (isPermissionGranted(permissionArray)) {
                Log.d("alskjdasd", ";aosjldsad")
                startService(Intent(this, SyncService::class.java)
                )
            }
        }
    }



    inner class SyncBroadcasReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            Log.d("aklsjdlajsdasd",p1!!.action.toString())
            if (p1!!.action == Constants.ACTION_SYNC_COMPLETED) {
                dismissLoader()
                makeToast("Synced")
                if (p1.extras!!.getString("contacts") != null) {
                    startActivity(IntentHelper.getSearchScreen(this@ActivityWelcome)!!.putExtra("contacts", p1.extras!!.getString("contacts").toString())
                    )
                }
            }
        }
    }

    fun toolbar(isCenterVisible: Boolean, text: String) {

        if (isCenterVisible) {
            binding.toolbar.tvhead.visibility = View.GONE
            binding.toolbar.tvheadCenterHeadBold.visibility = View.VISIBLE
            binding.toolbar.tvheadCenterHeadBold.text = text
        } else {
            binding.toolbar.tvhead.visibility = View.GONE
            binding.toolbar.tvheadCenterHeadBold.visibility = View.VISIBLE
            binding.toolbar.tvhead.text = text
        }
    }

    override fun onDestroy() {
        unregisterReceiver(syncBroadcastreceiver)
        super.onDestroy()
    }


    class ViewPagerAdapter(@SuppressLint("KotlinNullnessAnnotation") @NonNull fm: FragmentManager?) :
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

    override fun onClickOnNextButtonOnSuggestionPage() {
        binding.btnNext.performClick()
    }

    override fun onClickIntrastedItem(data: ArrayList<Category>) {
        datasss = data
    }

    fun setUpNextButton(isRoundButtonToHide:Boolean){
        if (isRoundButtonToHide){
            binding.btnNext.animate().translationY(200f).setDuration(100).start()
        }else{
            binding.btnNext.animate().translationY(0f).setDuration(100).start()
        }
    }

    override fun onClickOnNextButtonOnSyncPage() {
        binding.btnNext.performClick()
    }
}