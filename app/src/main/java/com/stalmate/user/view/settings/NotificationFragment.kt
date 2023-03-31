package com.stalmate.user.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAppSettingBinding
import com.stalmate.user.databinding.FragmentNotification2Binding
import com.stalmate.user.model.AccountSettingGetAndPut
import com.stalmate.user.modules.reels.activity.ActivitySettings

class NotificationFragment : BaseFragment() {

    private var accountSettingGetAndPut = AccountSettingGetAndPut()
    private var _binding: FragmentNotification2Binding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        networkViewModel.accountSettingGet(prefManager?.access_token.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotification2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    private fun listeners() {
        binding.NotificationHeader.topAppBar.setOnClickListener {
            (requireActivity() as ActivitySettings).onBackPressed()
        }
        networkViewModel.accountSettingGet.observe(this.viewLifecycleOwner) {

        }
        networkViewModel.accountSettingPut.observe(this.viewLifecycleOwner) {

        }
    }
}