package com.stalmate.user.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentAppSettingBinding


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
        initViews()
    }

    private fun initViews() {
        var list = ArrayList<AppSettingMenuModel>()

        list.add(AppSettingMenuModel("Version"))
        list.add(AppSettingMenuModel("Rate App"))
        list.add(AppSettingMenuModel("Report Problem"))
        list.add(AppSettingMenuModel("Notification"))
        list.add(AppSettingMenuModel("Share App"))
       binding.rvListAppSeting.adapter = AppSettingAdapter(list)
    }

}