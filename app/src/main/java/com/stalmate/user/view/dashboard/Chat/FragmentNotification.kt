package com.stalmate.user.view.dashboard.Chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentNotificationBinding
import com.stalmate.user.view.adapter.ChatFragmentAdapter

class FragmentNotification : AppCompatActivity() {
    private lateinit var binding: FragmentNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNotificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewPagerNotificationBinding()
        listener()
    }

    private fun listener() {
        binding.topAppBar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewPagerNotificationBinding()
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        viewPagerNotificationBinding()
        return binding.root
    }*/

    private fun viewPagerNotificationBinding() {
        val fragmentAdapter = ChatFragmentAdapter(this.supportFragmentManager)
        fragmentAdapter.addFragment(FragmentNotificationRecent(), getString(R.string.recent))
        fragmentAdapter.addFragment(FragmentNotificationSeeMore(), getString(R.string.seeMore))
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
}