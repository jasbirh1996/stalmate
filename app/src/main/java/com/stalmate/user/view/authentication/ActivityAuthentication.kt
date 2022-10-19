package com.stalmate.user.view.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityAuthenticationBinding


class ActivityAuthentication : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_authentication)!!
        setUpNavigation()

        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment)

    }


    fun setUpNavigation() {
        navController=findNavController(R.id.nav_host_fragment)

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}