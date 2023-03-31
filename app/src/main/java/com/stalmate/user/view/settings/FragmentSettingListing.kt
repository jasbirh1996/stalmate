package com.stalmate.user.view.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentSettingListingBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.About.AboutActivity
import com.stalmate.user.view.dashboard.ActivityDashboardNew

class FragmentSettingListing : Fragment(), MainSettingCategoryAdapter.Callbackk {
    lateinit var binding: com.stalmate.user.databinding.FragmentSettingListingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_setting_listing, container, false)
        binding = DataBindingUtil.bind<FragmentSettingListingBinding>(v)!!
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.tvhead.text = "My Profile"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            (requireActivity() as ActivitySettings).onBackPressed()
        }


        var settingCategoryAdapter = MainSettingCategoryAdapter(requireContext(), this)
        binding.rvList.adapter = settingCategoryAdapter

        Glide.with(requireActivity())
            .load(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.profile_img1)
            .placeholder(R.drawable.user_placeholder).circleCrop().into(binding.userProfileImage)
        binding.tvUserName.setText(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.first_name+PrefManager.getInstance(requireContext())!!.userProfileDetail.results.last_name)
        binding.tvAbout.setText(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.city)
        binding.userProfileImage.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_setting_main_to_profileFragment)
        }

        var list = ArrayList<SettingMenuModel>()
        list.add(SettingMenuModel(R.drawable.menu_settings_icon, Constants.SETTING_TYPE_ACCOUNT))
        list.add(SettingMenuModel(R.drawable.menu_chatsetting_icon, Constants.SETTING_TYPE_CHAT))
        list.add(SettingMenuModel(R.drawable.menu_appsetting_icon, Constants.SETTING_TYPE_APP))
        list.add(
            SettingMenuModel(
                R.drawable.menu_notificationsetting_icon,
                Constants.SETTING_TYPE_NOTIFICATION
            )
        )
        list.add(SettingMenuModel(R.drawable.menu_aboutus_icon, Constants.SETTING_TYPE_ABOUT_US))
        list.add(SettingMenuModel(R.drawable.menu_leagal_icon, Constants.SETTING_TYPE_LEGAL))
        settingCategoryAdapter.submitList(list)
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onCLickONMenu(settingName: String) {

        when (settingName) {

            Constants.SETTING_TYPE_ACCOUNT -> {
                findNavController().navigate(R.id.fragment_setting_main_to_fragment_account_setting)
            }

            Constants.SETTING_TYPE_CHAT -> {
                Toast.makeText(requireContext(), "Chat Clicked", Toast.LENGTH_SHORT).show()

            }

            Constants.SETTING_TYPE_APP -> {
                findNavController().navigate(R.id.action_fragment_setting_main_to_appSettingFragment)
            }

            Constants.SETTING_TYPE_NOTIFICATION -> {
                Toast.makeText(requireContext(), "Notification Setting Clicked", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_fragment_setting_main_to_notificationFragment)
            }

            Constants.SETTING_TYPE_ABOUT_US -> {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
            }

            Constants.SETTING_TYPE_LEGAL -> {
                findNavController().navigate(R.id.action_fragment_setting_main_to_legalFragment)
            }

        }

    }


}





