package com.slatmate.user.view.OnBoarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.slatmate.user.view.Authentication.ActivityParentLoginSignUp
import com.slatmate.user.R
import com.slatmate.user.databinding.ActivityOnBoardingScreenBinding

class ActivityOnBoardingScreen : AppCompatActivity(), View.OnClickListener {

    private lateinit var onBoardingViewPagerAdapter: OnBoardingAdapter
    private lateinit var binding :ActivityOnBoardingScreenBinding
    private var position = 0
    private val onBoardingData: MutableList<OnBoardingModel> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityOnBoardingScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        onBoardingData.add(OnBoardingModel(resources.getString(R.string.first_screen_title), resources.getString(R.string.find_lorem), R.raw.walkthrough_one))
        onBoardingData.add(OnBoardingModel(resources.getString(R.string.second_screen_title), resources.getString(R.string.description_second_screen), R.raw.walkthrough_two))
        onBoardingData.add(OnBoardingModel(resources.getString(R.string.third_screen_title), resources.getString(R.string.third_screen_description), R.raw.walkthrough_three))
        onBoardingData.add(OnBoardingModel(resources.getString(R.string.four_screen_title), resources.getString(R.string.four_screen_description), R.raw.walkthrough_four))
        setOnBoardingViewPager(onBoardingData)
        position = binding.viewpager.currentItem
        binding.dotsIndicator.setViewPager(binding.viewpager)


        addSlideChangeListener()
        binding.viewpager.offscreenPageLimit=4


        binding.skip.setOnClickListener {
            startActivity(Intent(applicationContext, ActivityParentLoginSignUp::class.java))
            finish()
        }

        binding.btnNext.setOnClickListener {

            val current = getItem(+1)
            if (current < onBoardingData.size) {
                binding.viewpager.currentItem = current
            } else {
                startActivity(Intent(applicationContext, ActivityParentLoginSignUp::class.java))
                finish()
            }

        }
    }
    private fun getItem(i: Int): Int {
        return binding.viewpager.currentItem + i
    }

    private fun addSlideChangeListener() {


        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
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



          }

        }
    }


}