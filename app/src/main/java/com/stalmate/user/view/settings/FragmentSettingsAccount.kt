package com.stalmate.user.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentSettingListingBinding
import com.stalmate.user.databinding.FragmentSettingsAccountBinding
import com.stalmate.user.utilities.Constants

class FragmentSettingsAccount : Fragment(), MainSettingCategoryAdapter.Callbackk {

lateinit var binding:FragmentSettingsAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v=inflater.inflate(R.layout.fragment_settings_account, container, false)
        binding= DataBindingUtil.bind<FragmentSettingsAccountBinding>(v)!!
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var settingCategoryAdapter=MainSettingCategoryAdapter(requireContext(),this)
        binding.rvList.adapter=settingCategoryAdapter


        binding.toolbar.tvhead.text="Account"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


        var list=ArrayList<SettingMenuModel>()
        list.add(SettingMenuModel(R.drawable.account_privacy_icon, Constants.SETTING_ACCOUNT_PRIVACY))
        list.add(SettingMenuModel(R.drawable.account_change_number_icon, Constants.SETTING_CHANGE_NUMBER))
        list.add(SettingMenuModel(R.drawable.account_changepassword_icon, Constants.SETTING_CHANGE_PASSWORD))
        list.add(SettingMenuModel(R.drawable.account_blocked_contact_icon, Constants.SETTING_BLOCKED_CONTACTS))
        list.add(SettingMenuModel(R.drawable.account_delete_account_icon, Constants.SETTING_DELETEACCOUNT))
        settingCategoryAdapter.submitList(list)
        binding.rvList.layoutManager= LinearLayoutManager(requireContext())
    }


    override fun onCLickONMenu(settingName: String) {

        when(settingName){

            Constants.SETTING_ACCOUNT_PRIVACY->{
//                findNavController().navigate(R.id.action_fragment_account_setting_to_fragmentPrivacy2)
                startActivity(IntentHelper.getFragmentPrivacyScreen(requireActivity()))
            }

            Constants.SETTING_CHANGE_NUMBER->{

            }

            Constants.SETTING_CHANGE_PASSWORD->{

            }

            Constants.SETTING_BLOCKED_CONTACTS->{
//                findNavController().navigate(R.id.fragment_account_setting_to_fragment_blocked_users)
                startActivity(IntentHelper.getBlockListScreen(requireActivity()))
            }

            Constants.SETTING_DELETEACCOUNT->{

            }




        }

    }

}