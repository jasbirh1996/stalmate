package com.slatmate.user.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.slatmate.user.R
import com.slatmate.user.base.BaseActivity
import com.slatmate.user.databinding.ActivityDashboardBinding

class ActivityDashboard : BaseActivity() {

    lateinit var binding: ActivityDashboardBinding

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
    }
}