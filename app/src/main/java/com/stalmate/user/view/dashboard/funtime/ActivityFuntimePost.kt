package com.stalmate.user.view.dashboard.funtime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityFuntimePostBinding
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel

class ActivityFuntimePost : BaseActivity() {
    lateinit var tagPeopleViewModel: TagPeopleViewModel
    lateinit var navController: NavController
    lateinit var binding: ActivityFuntimePostBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_funtime_post)!!
        setUpNavigation()

        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment)


        tagPeopleViewModel= ViewModelProvider(this).get(TagPeopleViewModel::class.java)
       tagPeopleViewModel
    }


    fun setUpNavigation() {
        navController=findNavController(R.id.nav_host_fragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}