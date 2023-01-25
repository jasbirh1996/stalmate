package com.stalmate.user.view.dashboard.welcome

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivitySyncBinding
import com.stalmate.user.databinding.FragmentSyncBinding
import com.stalmate.user.utilities.Constants

class ActivitySync : BaseActivity() {
    private lateinit var binding: ActivitySyncBinding
    private lateinit var mAccount: Account
    override fun onClick(viewId: Int, view: View?) {

    }
    interface callBack{

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        loadragment(FragmentSync())
    }
    private fun loadragment(fragment: Fragment) {
        Log.d(":lkasdasd","ppopp")
        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding.frame.id, fragment, fragmentTag)
            ft.commit()
        }
    }

}