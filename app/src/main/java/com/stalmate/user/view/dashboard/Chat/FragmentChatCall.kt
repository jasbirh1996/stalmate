package com.stalmate.user.view.dashboard.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentChatCallBinding
import com.stalmate.user.view.adapter.ChatFragmentAdapter

class FragmentChatCall : Fragment() {
    private lateinit var _binding: FragmentChatCallBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatCallBinding.inflate(inflater, container, false)
        viewPagerBinding()
        return binding.root
    }

    private fun viewPagerBinding() {
        val fragmentAdapter = ChatFragmentAdapter(requireActivity().supportFragmentManager)
        fragmentAdapter.addFragment(FragmentChat(), getString(R.string.chats))
        fragmentAdapter.addFragment(FragmentCalls(), getString(R.string.calls))
        binding.viewPagerChatCall.adapter = fragmentAdapter
        binding.tabLayoutChatCall.setupWithViewPager(binding.viewPagerChatCall)
        binding.viewPagerChatCall.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tabLayoutChatCall
            )
        )
        binding.tabLayoutChatCall.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPagerChatCall.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                binding.viewPagerChatCall.currentItem = tab.position
            }
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewPagerBinding()
    }
}