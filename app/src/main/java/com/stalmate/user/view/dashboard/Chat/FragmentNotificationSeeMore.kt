package com.stalmate.user.view.dashboard.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.databinding.FragmentnChatNotificationSeemoreBinding
import com.stalmate.user.view.adapter.ChatNotificationAdapter
import com.stalmate.user.view.dashboard.Chat.model.ChatNotificationModel

class FragmentNotificationSeeMore: Fragment() {
    private lateinit var _binding: FragmentnChatNotificationSeemoreBinding
    private val binding get() = _binding
    private lateinit var chatNotificationAdapter: ChatNotificationAdapter
    private var notificationRecentArrayList = ArrayList<ChatNotificationModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentnChatNotificationSeemoreBinding.inflate(inflater, container, false)
        seeMoreListListener()
        return binding.root
    }
    private fun seeMoreListListener() {
        notificationRecentArrayList.clear()
        for (i in 0 until 5) {
            val chatModel = ChatNotificationModel()
            chatModel.userName = "It's Bruce Banner's birthday today. help him a great day!"
            chatModel.timeHrs = "1 hours ago"
            notificationRecentArrayList.add(chatModel)
        }
        for (i in 0 until 4) {
            val chatModel = ChatNotificationModel()
            chatModel.userName = "It's Bruce Banner's birthday today. help him a great day!"
            chatModel.timeHrs = "4 hours ago"
            notificationRecentArrayList.add(chatModel)
        }
        for (i in 0 until 2) {
            val chatModel = ChatNotificationModel()
            chatModel.userName = "It's Bruce Banner's birthday today. help him a great day!"
            chatModel.timeHrs = "5hours ago"
            notificationRecentArrayList.add(chatModel)
        }

        chatNotificationAdapter = ChatNotificationAdapter(notificationRecentArrayList, requireActivity())
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationSeeMore.layoutManager = linearLayoutManager
        binding.rvNotificationSeeMore.adapter = chatNotificationAdapter
    }
}