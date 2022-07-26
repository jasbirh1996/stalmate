package com.slatmate.user.Home.Dashboard.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.slatmate.user.R
import com.slatmate.user.databinding.FragmentChatBinding

class FragmentChat : Fragment() {

    private lateinit var binding : FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chat, container, false)
        binding = DataBindingUtil.bind<FragmentChatBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Common ToolBar SetUp*/
        toolbarSetUp()
    }

    private fun toolbarSetUp() {

        binding.toolbar.backButtonRightText.visibility =View.VISIBLE
        binding.toolbar.backButtonRightText.text =  getString(R.string.chat)
        binding.toolbar.chatNotification.visibility =View.VISIBLE
        binding.toolbar.chatSetting.visibility =View.VISIBLE
        binding.toolbar.menuChat.visibility =View.VISIBLE

    }
}