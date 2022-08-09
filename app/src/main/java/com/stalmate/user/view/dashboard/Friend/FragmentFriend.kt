package com.stalmate.user.view.dashboard.Friend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.FragmentViewPagerAdapter
import com.stalmate.user.databinding.FragmentFriendBinding
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter

class FragmentFriend : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var binding:FragmentFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.bind<FragmentFriendBinding>(inflater.inflate(R.layout.fragment_friend, container, false))!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

   
        var list=ArrayList<Fragment>()
        list.add(FragmentFriendList())
        list.add(FragmentFriendList())
        var pagerAdapter=FragmentViewPagerAdapter(requireActivity(),requireContext())
        pagerAdapter.addFragments(list)
        binding.viewPager.adapter=pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
          if (position == 0) tab.text = "Followers" else if (position == 1) {
                tab.text = "Following"
            }
        }.attach()
    }


    override fun onClickOnViewComments(postId: Int) {

    }
}