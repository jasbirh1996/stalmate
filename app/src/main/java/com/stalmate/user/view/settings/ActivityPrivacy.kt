package com.stalmate.user.view.settings

import android.os.Bundle
import android.view.View
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.FragmentProfilePrivacySettingsBinding

class ActivityPrivacy : BaseActivity() {
    private lateinit var binding: FragmentProfilePrivacySettingsBinding
    private var privacyGroup = ""
//    private val profileList: Array<String> = resources.getStringArray(R.array.profile)


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentProfilePrivacySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        super.onCreate(savedInstanceState)
        listener()


    }

    private fun listener() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
        /*binding.spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.selectedItem as Map<*, *>
                privacyGroup = selectedItem["accountVal"].toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }*/


    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onClick(viewId: Int, view: View?) {
        TODO("Not yet implemented")
    }
}