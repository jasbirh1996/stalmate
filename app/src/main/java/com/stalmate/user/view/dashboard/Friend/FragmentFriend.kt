package com.stalmate.user.view.dashboard.Friend

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.FragmentFriendBinding
import com.stalmate.user.utilities.Constants

class FragmentFriend : BaseActivity() {
    lateinit var binding: FragmentFriendBinding
    lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onClick(viewId: Int, view: View?) {

    }
//    var activityDashboard : ActivityDashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_friend)
        setContentView(binding.root)
        onViewCreated()
    }

    fun onViewCreated() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Friend Requests"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Suggestions"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My Friends"));

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.btnCreateCategory.setOnClickListener {
            startActivity(IntentHelper.getCategoryCreateScreen(this))
        }
        var bundlex = Bundle()
        bundlex.putString("categoryType", Constants.TYPE_FRIEND_REQUEST)


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        navController.navigate(R.id.idFragmentCategory, bundlex)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                var bundle = Bundle()

                when (tab!!.position) {

                    0 -> {
                        binding.btnCreateCategory.visibility = View.VISIBLE
                        bundle.putString("categoryType", Constants.TYPE_FRIEND_REQUEST)
                        navController.navigate(
                            R.id.idFragmentCategory, bundle, NavOptions.Builder()
                                // .setPopUpTo(R.id.loginFragment, true)
                                .build()
                        )
                    }
                    1 -> {
                        binding.btnCreateCategory.visibility = View.GONE
                        bundle.putString("categoryType", Constants.TYPE_FRIEND_SUGGESTIONS)
                        navController.navigate(
                            R.id.idFragmentCategory, bundle, NavOptions.Builder()
                                // .setPopUpTo(R.id.loginFragment, true)
                                .build()
                        )
                    }
                    2 -> {
                        binding.btnCreateCategory.visibility = View.GONE
                        bundle.putString("categoryType", Constants.TYPE_MY_FRIENDS)
                        navController.navigate(
                            R.id.idFragmentCategory, bundle, NavOptions.Builder()
                                // .setPopUpTo(R.id.loginFragment, true)
                                .build()
                        )
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                binding.tabLayout.tabTextColors = this@FragmentFriend.let {
                    ContextCompat.getColorStateList(
                        it, R.color.grey_dark
                    )
                };
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

    }


    public interface Callbackk {
        fun onClickBack()
    }
}