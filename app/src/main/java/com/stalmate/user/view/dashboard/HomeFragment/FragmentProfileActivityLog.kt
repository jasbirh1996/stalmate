package com.stalmate.user.view.dashboard.HomeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentProfileActivityLogBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.SuggestedFriendAdapter
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

class FragmentProfileActivityLog : BaseFragment(), AdapterFeed.Callbackk,
    SuggestedFriendAdapter.Callbackk {
    lateinit var feedAdapter: AdapterFeed
    lateinit var suggestedFriendAdapter: SuggestedFriendAdapter
    lateinit var binding: FragmentProfileActivityLogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentProfileActivityLogBinding>(
            inflater.inflate(
                R.layout.fragment_profile_activity_log,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getActivityLogs()
    }

    fun getActivityLogs() {
        feedAdapter = AdapterFeed(networkViewModel, requireContext(), requireActivity())
        binding.shimmerLayoutFeeds.startShimmer()
        binding.rvFeeds.adapter = feedAdapter
        binding.rvFeeds.layoutManager = LinearLayoutManager(context)
        val hashmap = HashMap<String, String>()
        hashmap.put("page", 1.toString())
        hashmap.put("id_user", "")
        hashmap.put("fun_id", "")
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd", "oaspiasddsad")
            it.let {
                if (!it?.results.isNullOrEmpty()) {
                    it?.results?.let { it1 -> feedAdapter.submitList(it1) }
                }
                binding.shimmerLayoutFeeds.stopShimmer()
                binding.rvFeeds.visibility = View.VISIBLE
            }
        })


        getFriendSuggestionListing()

    }

    private fun getFriendSuggestionListing() {

        var hashmap = HashMap<String, String>()
        hashmap.put("id_user", "")
        hashmap.put("type", Constants.TYPE_FRIEND_SUGGESTIONS)
        hashmap.put("sub_type", "")
        hashmap.put("search", "")
        hashmap.put("page", "1")
        hashmap.put("limit", "6")

        networkViewModel.getFriendList(prefManager?.access_token.toString(), hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.d("asdasdasd", "asdasdasdasd")
                suggestedFriendAdapter =
                    SuggestedFriendAdapter(networkViewModel, requireContext(), this)
                binding.rvSuggestedFriends.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvSuggestedFriends.adapter = suggestedFriendAdapter
                suggestedFriendAdapter.submitList(it!!.results)
            }
        })
    }

    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onCLickItem(item: ResultFuntime) {

    }

    override fun onClickOnLikeButtonReel(feed: ResultFuntime) {

    }

    override fun onClickOnFollowButtonReel(feed: ResultFuntime) {

    }

    override fun onSendComment(feed: ResultFuntime, comment: String) {

    }

    override fun onCaptureImage(feed: ResultFuntime, position: Int) {

    }

    override fun showCommentOverlay(feed: ResultFuntime, position: Int) {

    }

    override fun hideCommentOverlay(feed: ResultFuntime, position: Int) {
    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }


}