package com.stalmate.user.view.dashboard.Chat



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentCallNChatBaseBinding
import com.stalmate.user.databinding.FragmentCallUsersListBinding
import com.stalmate.user.model.Friend
import com.stalmate.user.view.adapter.FriendAdapter

class FragmentChatNCallBase : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var friendAdapter:FriendAdapter
    lateinit var binding:FragmentCallNChatBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.bind<FragmentCallNChatBaseBinding>(inflater.inflate(R.layout.fragment_call_n_chat_base, container, false))!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onClickOnUpdateFriendRequest(friend: Friend, status: String) {

    }
}