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
import com.stalmate.user.databinding.FragmentFriendCategoryBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants

import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.adapter.ProfileFriendAdapter
const val CATEGORY_TYPE="categoryType"
class FragmentFriendCategory : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var binding: FragmentFriendCategoryBinding
    var type=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      arguments?.let {
            type = it.getString(CATEGORY_TYPE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFriendCategoryBinding>(
            inflater.inflate(
                R.layout.fragment_friend_category,
                container,
                false
            )
        )!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var list = ArrayList<Fragment>()
        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {
            list.add(FragmentFriendList(Constants.TYPE_FRIEND_REQUEST, "",""))
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
            list.add(FragmentFriendList(Constants.TYPE_FRIEND_SUGGESTIONS, "",""))
         //   list.add(FragmentFriendList(Constants.TYPE_FRIEND_SUGGESTIONS, "",""))
        } else if (type.equals(Constants.TYPE_MY_FRIENDS)) {
            list.add(FragmentFriendList(Constants.TYPE_MY_FRIENDS, Constants.TYPE_FRIEND_FOLLOWING,""))
            list.add(FragmentFriendList(Constants.TYPE_MY_FRIENDS, Constants.TYPE_FRIEND_FOLLOWER,""))
        }


        var pagerAdapter = FragmentViewPagerAdapter(requireActivity(), requireContext())
        pagerAdapter.addFragments(list)
        binding.viewPager.adapter = pagerAdapter

        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {
            binding.tabLayout.visibility=View.GONE
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                if (position == 0) tab.text = "Followers" else if (position == 1) {
                    tab.text = "Following"
                }
            }.attach()
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
       /*     TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                if (position == 0) tab.text = "Suggested" else if (position == 1) {
                    tab.text = "Followers"
                }
            }.attach()*/

            binding.tabLayout.visibility=View.GONE

        } else if (type.equals(Constants.TYPE_MY_FRIENDS)) {
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                if (position == 0) tab.text = "Following" else if (position == 1) {
                    tab.text = "Followers"
                }
            }.attach()
        }



    }


    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

          }



    companion object {
        @JvmStatic
        fun newInstance(categoryType: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY_TYPE, categoryType)
                }
            }
    }
    
}