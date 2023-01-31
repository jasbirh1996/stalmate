package com.stalmate.user.view.settings

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentChatSettingBinding

class FragmentChatSettings : BaseFragment() {
    private lateinit var binding: FragmentChatSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatSettingBinding.inflate(inflater, container, false)
        expandCard()
        listener()
        return binding.root
    }

    private fun listener() {
        binding.chatBackup.setOnClickListener {
            // dialog
        }
        binding.fontSize.setOnClickListener {
// dialog
        }
        binding.wallpaper.setOnClickListener {
            makeToast("Wallpaper")
        }
        binding.chatHistory.setOnClickListener {
            expandCard()
        }
    }

    private fun expandCard() {
        binding.chatHistoryArrow.setOnClickListener {
            // If the CardView is already expanded, set its visibility
            // to gone and change the expand less icon to expand more.
            if (binding.hiddenView.visibility == View.VISIBLE) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                TransitionManager.beginDelayedTransition(binding.cardChatHistory, AutoTransition())
                binding.hiddenView.visibility = View.GONE
                binding.chatHistoryArrow.setImageResource(R.drawable.baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(binding.cardChatHistory, AutoTransition())
                binding.hiddenView.visibility = View.VISIBLE
                binding.chatHistoryArrow.setImageResource(R.drawable.menu_arrow_icon)
            }
        }
    }
}