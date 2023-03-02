package com.stalmate.user.view.About

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityAboutBinding
import com.stalmate.user.view.settings.AboutSettingAdapter
import com.stalmate.user.view.settings.AboutUsSettingMenuModel


class AboutActivity : AppCompatActivity() {
    lateinit  var binding:ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    setContentView(R.layout.activity_about)
        binding=ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

    }

    private fun initViews() {
var list=ArrayList<AboutUsSettingMenuModel>()
        list.add(AboutUsSettingMenuModel("FAQS",R.drawable.account_privacy_icon))
        list.add(AboutUsSettingMenuModel("Contact Us",R.drawable.account_blocked_contact_icon))
        list.add(AboutUsSettingMenuModel("Share App",R.drawable.path))
        list.add(AboutUsSettingMenuModel("Messages",R.drawable.iconly_light_outline_activity))
        binding.rvAboutSetting.adapter=AboutSettingAdapter(this,list)
    }
}