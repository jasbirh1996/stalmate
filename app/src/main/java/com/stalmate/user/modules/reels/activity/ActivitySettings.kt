package com.stalmate.user.modules.reels.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivitySettingsBinding

class ActivitySettings : BaseActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivitySettingsBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_settings)!!
        setUpNavigation()

    }


    private fun setUpNavigation() {
        navController=findNavController(R.id.nav_host_fragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}