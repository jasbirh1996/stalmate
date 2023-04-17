package com.stalmate.user.view.settings

import android.os.Bundle
import android.view.View
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityTermsAndConditionsAndPrivacyPolicyBinding

class TermsAndConditionsAndPrivacyPolicyAndFaqs : BaseActivity() {

    private lateinit var binding: ActivityTermsAndConditionsAndPrivacyPolicyBinding

    private val comingFor: String
        get() = intent.getStringExtra("comingFor").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionsAndPrivacyPolicyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.toolBar.topAppBar.setOnClickListener {
            onBackPressed()
        }
        when (comingFor) {
            "0" -> {
                binding.toolBar.tvhead.text = "Terms and Conditions"
            }
            "1" -> {
                binding.toolBar.tvhead.text = "Privacy Policy"
            }
            else -> {
                binding.toolBar.tvhead.text = "FAQs"
                binding.cardViewNotification.visibility = View.VISIBLE
                binding.btnClose.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(viewId: Int, view: View?) {

    }
}