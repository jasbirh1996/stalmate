package com.stalmate.user.view.dashboard.HomeFragment


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivitySearchBinding


class ActivitySearch : BaseActivity() {
    lateinit var   navHostFragment:NavHostFragment
    private lateinit var navController: NavController
    lateinit var binding:ActivitySearchBinding
    var searchData=""
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_search)

         navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
         navController = navHostFragment.navController

        binding.ivBack.setOnClickListener {

        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    searchData = p0.toString()
                 //   val currentFragment = supportFragmentManager.findFragmentById(binding.navHostContainer.id)
                   val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)
                    Log.d("asdasdasd",currentFragment!!.toString())
                    Handler(Looper.myLooper()!!).post {
                       if (currentFragment is FragmentGlobalSearch) {
                           currentFragment.hitApi(true, searchData)
                        } else if (currentFragment is FragmentPeopleSearch) {
                           currentFragment.hitApi(true, searchData)
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        }




}