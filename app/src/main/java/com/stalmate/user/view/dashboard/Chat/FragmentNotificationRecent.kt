package com.stalmate.user.view.dashboard.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.databinding.FragmentnChatNotificationRecentBinding
import com.stalmate.user.view.adapter.ChatNotificationAdapter
import com.stalmate.user.view.dashboard.Chat.model.ChatNotificationModel

class FragmentNotificationRecent: Fragment() {
    private lateinit var _binding: FragmentnChatNotificationRecentBinding
    private val binding get() = _binding
    private lateinit var chatNotificationAdapter: ChatNotificationAdapter
    private var notificationRecentArrayList = ArrayList<ChatNotificationModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentnChatNotificationRecentBinding.inflate(inflater, container, false)
        recentListListener()
        return binding.root
    }

    private fun recentListListener() {
        notificationRecentArrayList.clear()
        for (i in 0 until 3) {
            val chatModel = ChatNotificationModel()
            chatModel.userName ="It's Bruce Banner's birthday today. help him a great day!"
            chatModel.timeHrs = "1 hours ago"
            notificationRecentArrayList.add(chatModel)
        }
        for (i in 0 until 3) {
            val chatModel = ChatNotificationModel()
            chatModel.userName = "It's Bruce Banner's birthday today. help him a great day!"
            chatModel.timeHrs = "4 hours ago"
            notificationRecentArrayList.add(chatModel)
        }
        for (i in 0 until 2) {
            val chatModel = ChatNotificationModel()
            chatModel.userName = "It's Bruce Banner's birthday today. help him a great day!"
            chatModel.timeHrs = "4 hours ago"
            notificationRecentArrayList.add(chatModel)
        }

        chatNotificationAdapter = ChatNotificationAdapter(notificationRecentArrayList, requireActivity())
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationRecent.layoutManager = linearLayoutManager
        binding.rvNotificationRecent.adapter = chatNotificationAdapter
    }
}