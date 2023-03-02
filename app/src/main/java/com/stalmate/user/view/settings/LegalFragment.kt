package com.stalmate.user.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentLegalBinding
import com.stalmate.user.databinding.FragmentNotification2Binding

class LegalFragment : Fragment() {

    private var _binding: FragmentLegalBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLegalBinding.inflate(inflater, container, false)
        return binding.root
    }

}