package com.stalmate.user.view.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityProfileEditBinding
import com.stalmate.user.view.dashboard.HomeFragment.FragmentSearchBase
import com.stalmate.user.view.dashboard.welcome.FragmentSync
import com.stalmate.user.view.settings.FragmentBlockedContacts

class ActivityProfileEdit : BaseActivity() /*FragmentSync.Callback*/ {

    private lateinit var binding: ActivityProfileEditBinding
    override fun onClick(viewId: Int, view: View?) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpNavigation()
    }

    private fun setUpNavigation() {

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

/*
    private fun loadFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
            //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.replace(binding.navEditFragment.id, fragment, backStateName)
            ft.commit()
        }
    }

    override fun onCLickONBlockedContactSeeAllButton() {
        loadFragment(FragmentBlockedContacts())

    }

    override fun onCLickONSyncContactButton() {
        loadFragment(FragmentSync(this))
    }

    override fun onClickBackPress() {

        Log.d("asdkjasd","aiosdjasd")

        loadFragment(FragmentProfileEdit(this))
    }

    override fun onClickOnNextButtonOnSyncPage() {
        loadFragment(FragmentSearchBase(this))
    }*/
}
