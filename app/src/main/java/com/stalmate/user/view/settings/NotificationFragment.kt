package com.stalmate.user.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentAppSettingBinding
import com.stalmate.user.databinding.FragmentNotification2Binding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotification2Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    //    return inflater.inflate(R.layout.fragment_notification2, container, false)
        _binding = FragmentNotification2Binding.inflate(inflater, container, false)
        return binding.root
    }

}