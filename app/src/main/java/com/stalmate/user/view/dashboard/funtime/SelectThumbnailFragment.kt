package com.stalmate.user.view.dashboard.funtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSelectThumbnailBinding

class SelectThumbnailFragment : BaseFragment() {

    lateinit var binding: FragmentSelectThumbnailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.bind<FragmentSelectThumbnailBinding>(
                inflater.inflate(
                    R.layout.fragment_select_thumbnail,
                    container,
                    false
                )
            )!!
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tBar.tvhead.text = "Select Thumbnail"
        binding.tBar.topAppBar.setOnClickListener {
            findNavController().popBackStack()
        }

    }
}