package com.stalmate.user.view.dashboard.HomeFragment





import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.BubbleGradient
import com.igalata.bubblepicker.model.PickerItem
import com.igalata.bubblepicker.rendering.BubblePicker


import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentMenuBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.FriendAdapter


class FragmentMenu : BaseFragment(){
    lateinit var binding: FragmentMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentMenuBinding>(
            inflater.inflate(
                R.layout.fragment_menu,
                container,
                false
            )
        )!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}
