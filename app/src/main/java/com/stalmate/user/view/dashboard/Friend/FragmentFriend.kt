package com.stalmate.user.view.dashboard.Friend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.FragmentViewPagerAdapter
import com.stalmate.user.databinding.FragmentFriendBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants

import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboard

class FragmentFriend(var callback: Callbackk) : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var binding: FragmentFriendBinding
    lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
//    var activityDashboard : ActivityDashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFriendBinding>(
            inflater.inflate(
                R.layout.fragment_friend,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tabLayout.addTab(  binding.tabLayout.newTab().setText("Friend Requests"));
        binding.tabLayout.addTab(  binding.tabLayout.newTab().setText("Suggestions"));
        binding.tabLayout.addTab(  binding.tabLayout.newTab().setText("My Friends"));

        navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        binding.btnCreateCategory.setOnClickListener {
            startActivity(IntentHelper.getCategoryCreateScreen(context))
        }
        var bundlex = Bundle()
        bundlex.putString("categoryType", Constants.TYPE_FRIEND_REQUEST)


        binding.btnBack.setOnClickListener {
            callback.onClickBack()
        }

        navController.navigate(R.id.idFragmentCategory,bundlex)
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

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }


    public interface Callbackk {
        fun onClickBack()
    }
}