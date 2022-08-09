package com.stalmate.user.view.dashboard.Chat.ChatFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentChatUsersListBinding

class FragmentChatUsersList : Fragment() {
lateinit var binding:FragmentChatUsersListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.bind<FragmentChatUsersListBinding>(inflater.inflate(R.layout.fragment_chat_users_list, container, false))!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}