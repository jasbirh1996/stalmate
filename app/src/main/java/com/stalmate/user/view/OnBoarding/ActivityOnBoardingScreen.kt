package com.stalmate.user.view.OnBoarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityOnBoardingScreenBinding
import com.stalmate.user.view.authentication.ActivityAuthentication

class ActivityOnBoardingScreen : AppCompatActivity(), View.OnClickListener {

    private lateinit var onBoardingViewPagerAdapter: OnBoardingAdapter
    private lateinit var binding : ActivityOnBoardingScreenBinding
    private var currentPageIndex = 0
    private val onBoardingPages: MutableList<OnBoardingModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        onBoardingPages.add(OnBoardingModel(resources.getString(R.string.first_screen_title), resources.getString(R.string.find_lorem), R.raw.walkthrough_one))
        onBoardingPages.add(OnBoardingModel(resources.getString(R.string.second_screen_title), resources.getString(R.string.description_second_screen), R.raw.walkthrough_two))
        onBoardingPages.add(OnBoardingModel(resources.getString(R.string.third_screen_title), resources.getString(R.string.third_screen_description), R.raw.walkthrough_three))
        onBoardingPages.add(OnBoardingModel(resources.getString(R.string.four_screen_title), resources.getString(R.string.four_screen_description), R.raw.walkthrough_four))
        setOnBoardingViewPager(onBoardingPages)
        currentPageIndex = binding.viewpager.currentItem
        binding.dotsIndicator.setViewPager(binding.viewpager)
        activateSlideChangeListener()
        binding.viewpager.offscreenPageLimit=4


        binding.skip.setOnClickListener(this)

        binding.btnNext.setOnClickListener(this)
    }

    private fun activateSlideChangeListener() {


        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(currentPageIndex: Int, currentPageIndexOffset: Float, currentPageIndexOffsetPixels: Int) {

            }

            override fun onPageSelected(currentPageIndex: Int) {
                this@ActivityOnBoardingScreen.currentPageIndex=currentPageIndex
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }


    private fun setOnBoardingViewPager(onBoardingModel: List<OnBoardingModel>) {
        onBoardingViewPagerAdapter = OnBoardingAdapter(this, onBoardingModel)
        binding.viewpager.adapter = onBoardingViewPagerAdapter
    }

    override fun onClick(view: View?) {
        when (view!!.id) {

          R.id.btn_next ->{
              if (currentPageIndex < onBoardingPages.size-1) {
                  currentPageIndex++
                  binding.viewpager.setCurrentItem(currentPageIndex,true)
              } else {
                  startActivity(Intent(applicationContext, ActivityAuthentication::class.java))
                  finish()
              }

          }

            R.id.skip->{
                startActivity(Intent(applicationContext, ActivityAuthentication::class.java))
                finish()
            }

        }
    }


}