package com.stalmate.user.view.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.databinding.ActivitySideDawerBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.authentication.ActivityAuthentication

class ActivitySideDawer : AppCompatActivity() {

    private lateinit var binding : ActivitySideDawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_side_dawer)

        binding.logout.setOnClickListener {
            PrefManager.getInstance(this)!!.keyIsLoggedIn = false
            startActivity(Intent(this, ActivityAuthentication::class.java))
        }
        binding.tvUserName.text = PrefManager.getInstance(App.getInstance())!!.userProfileDetail.results.first_name + " " +  PrefManager.getInstance(
            App.getInstance())!!.userProfileDetail.results.last_name

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}