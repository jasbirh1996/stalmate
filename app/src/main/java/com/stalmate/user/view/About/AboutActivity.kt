package com.stalmate.user.view.About

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c2m.storyviewer.utils.showToast
import com.stalmate.user.BuildConfig
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityAboutBinding
import com.stalmate.user.model.ContactUsBottomSheet
import com.stalmate.user.view.settings.TermsAndConditionsAndPrivacyPolicyAndFaqs


class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_about)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        binding.tvAbout.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initViews() {
        val counter = ContactUsBottomSheet() { category: String, topic: String, details: String ->
            showToast("Request sent successfully.")
        }
        binding.clFaq.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    TermsAndConditionsAndPrivacyPolicyAndFaqs::class.java
                ).apply {
                    putExtra("comingFor", "2")
                })
        }
        binding.clContact.setOnClickListener {
            if (counter.isAdded) {
                return@setOnClickListener
            }
            counter.show(
                supportFragmentManager, counter.tag
            )
        }

        binding.clShare.setOnClickListener {
            shareApp(this)
        }
        /*val list = ArrayList<AboutUsSettingMenuModel>()
        list.add(AboutUsSettingMenuModel("FAQS", R.drawable.account_privacy_icon))
        list.add(AboutUsSettingMenuModel("Contact Us", R.drawable.account_blocked_contact_icon))
        list.add(AboutUsSettingMenuModel("Share App", R.drawable.path))
        list.add(AboutUsSettingMenuModel("Messages", R.drawable.iconly_light_outline_activity))
        binding.rvAboutSetting.adapter = AboutSettingAdapter(this, list) {
            when (it) {
                "Contact Us" -> {
                    if (counter.isAdded) {
                        return@AboutSettingAdapter
                    }
                    counter.show(
                        supportFragmentManager, counter.tag
                    )
                }
            }
        }*/
    }

    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                shareMessage + "https://play.google.com/store/apps/details?_id=" + BuildConfig.APPLICATION_ID
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share Stalmate with your friends"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}