package com.stalmate.user.view.settings

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentAppSettingBinding
import com.stalmate.user.utilities.PrefManager


class AppSettingFragment : Fragment() {
    private var _binding: FragmentAppSettingBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_app_setting, container, false)

        _binding = FragmentAppSettingBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initControl()
        initViews()
    }

    private fun initControl() {
        binding.constDeleteMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_appSettingFragment_to_deleteMyAccountFragment)
        }
        binding.constCountry.setOnClickListener {
            binding.tvCountryAppSeting.launchCountrySelectionDialog()
        }
    }

    private fun initViews() {
        Glide.with(requireActivity())
            .load(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.profile_img1)
            .placeholder(R.drawable.user_placeholder).circleCrop().into(binding.userProfileImage)
        binding.tvUserName.setText(
            PrefManager.getInstance(requireContext())?.userProfileDetail?.results?.first_name + PrefManager.getInstance(
                requireContext()
            )?.userProfileDetail?.results?.last_name
        )
        binding.tvAbout.setText(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.city)


        val list = ArrayList<AppSettingMenuModel>()
        list.add(AppSettingMenuModel("Version"))
        list.add(AppSettingMenuModel("Rate App"))
        list.add(AppSettingMenuModel("Report Problem"))
        list.add(AppSettingMenuModel("Notification"))
        list.add(AppSettingMenuModel("Share App"))
        binding.rvListAppSeting.adapter = AppSettingAdapter(list)
    }
}