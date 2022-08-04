package com.slatmate.user.view.Authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.slatmate.user.R
import com.slatmate.user.databinding.ActivityParentLoginSignUpBinding

class ActivityParentLoginSignUp : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivityParentLoginSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_parent_login_sign_up)!!
        setUpNavigation()
    }


    fun setUpNavigation() {
        navController=findNavController(R.id.nav_host_fragment)
    }
}