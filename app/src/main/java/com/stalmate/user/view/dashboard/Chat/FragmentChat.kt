package com.stalmate.user.view.dashboard.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.databinding.FragmentChatBinding
import com.stalmate.user.view.adapter.ChatListAdapter
import com.stalmate.user.view.adapter.StoryListAdapter
import com.stalmate.user.view.dashboard.Chat.model.ChatModel
import com.stalmate.user.view.dashboard.Chat.model.StoryModel

class FragmentChat : Fragment() {

    private lateinit var _binding: FragmentChatBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var storyListAdapter: StoryListAdapter
    private var chatArrayList = ArrayList<ChatModel>()
    private var storyArrayList = ArrayList<StoryModel>()
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        chatListListener()
        storyListListener()
        return binding.root
    }

    private fun storyListListener() {
        storyArrayList.clear()
        for (i in 0 until 5) {
            val storyModel = StoryModel()
           /* Glide.with(requireContext())
                .load(R.drawable.app_logo)
                .placeholder(R.drawable.ic_processbar_check)
                .error(R.drawable.app_logo)
                .into()*/
            storyArrayList.add(storyModel)
        }
        storyListAdapter = StoryListAdapter(storyArrayList, requireContext())
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvUsers.layoutManager = linearLayoutManager
        binding.rvUsers.adapter = chatListAdapter
    }

    private fun chatListListener() {
        chatArrayList.clear()
        for (i in 0 until 5) {
            val chatModel = ChatModel()
            chatModel.userName = "Gopichand"
            chatModel.lastMessage = "This is a great offer"
            chatModel.lastMsgTiming = "10:00 AM"
            chatArrayList.add(chatModel)
        }
        for (i in 0 until 3) {
            val chatModel = ChatModel()
            chatModel.userName = "Vaibhav Nayak"
            chatModel.lastMessage = "This is a great offer"
            chatModel.lastMsgTiming = "10:00 AM"
            chatArrayList.add(chatModel)
        }
        chatListAdapter = ChatListAdapter(chatArrayList,requireContext())
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvUsers.layoutManager = linearLayoutManager
        binding.rvUsers.adapter = chatListAdapter
    }
}