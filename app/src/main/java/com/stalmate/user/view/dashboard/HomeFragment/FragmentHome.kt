package com.stalmate.user.view.dashboard.HomeFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentHomeBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.NetworkUtils
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.adapter.SuggestedFriendAdapter
import com.stalmate.user.view.adapter.UserHomeStoryAdapter


class FragmentHome(var callback: Callback) : BaseFragment(), AdapterFeed.Callbackk,
    UserHomeStoryAdapter.Callbackk, SuggestedFriendAdapter.Callbackk {

    private lateinit var binding: FragmentHomeBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var homeStoryAdapter: UserHomeStoryAdapter
    lateinit var suggestedFriendAdapter: SuggestedFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    public interface Callback {
        fun onCLickOnMenuButton()
        fun onCLickOnProfileButton()
        fun onScoll(toHide: Boolean)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = DataBindingUtil.bind<FragmentHomeBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
            if (isNetworkAvailable()) {
                homeSetUp()
            } else {

                binding.nointernet.visibility = View.VISIBLE
            }
        }

        if (isNetworkAvailable()) {
            getUserProfileData()
            homeSetUp()
        } else {
            binding.nointernet.visibility = View.VISIBLE
        }

        binding.nestedScrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollY < scrollY) {//increase
                callback.onScoll(true)
            } else {
                callback.onScoll(false)
            }
        })
    }

    fun homeSetUp() {
        setupSearchBox()
        feedAdapter = AdapterFeed(networkViewModel, requireContext(), requireActivity())
        homeStoryAdapter = UserHomeStoryAdapter(networkViewModel, requireContext(), this)
        binding.shimmerViewContainer.startShimmer()
        binding.shimmerLayoutFeeds.startShimmer()

        binding.rvFeeds.adapter = feedAdapter
        binding.rvStory.adapter = homeStoryAdapter

        networkViewModel.getFeedList(prefManager?.access_token.toString(), HashMap())
        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                if (!it?.results.isNullOrEmpty())
                    it?.results?.let { it1 -> feedAdapter.submitList(it1) }
                else
                    it?.reponse?.let { it1 -> feedAdapter.submitList(it1) }
                binding.shimmerLayoutFeeds.stopShimmer()
                binding.rvFeeds.visibility = View.VISIBLE

                binding.shimmerViewContainer.stopShimmer()
                binding.storyView.visibility = View.VISIBLE
                if (!it?.results.isNullOrEmpty())
                    it?.results?.let { it1 -> homeStoryAdapter.submitList(it1) }
                else
                    it?.reponse?.let { it1 -> homeStoryAdapter.submitList(it1) }
            }
        })

        getFriendSuggestionListing()

        binding.postContant.userImage.setOnClickListener {
            callback.onCLickOnProfileButton()
        }

        binding.layoutNewUser.setOnClickListener {
            startActivity(IntentHelper.getActivityWelcomeScreen(requireContext()))
        }

        binding.toolbar.ivButtonMenu.setOnClickListener {
//            startActivity(Intent(requireContext(), ActivitySideDawer::class.java))
            callback.onCLickOnMenuButton()
        }

        binding.nointernet.visibility = View.GONE
    }


    private fun getUserProfileData() {
        val hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap, prefManager?.access_token.toString())
        networkViewModel.profileLiveData.observe(requireActivity(), Observer {
            it.let {
                if (it != null) {
                    PrefManager.getInstance(requireContext())!!.userProfileDetail = it
                }
                Glide.with(requireContext())
                    .load(PrefManager.getInstance(requireContext())?.userProfileDetail?.results?.profile_img1)
                    .placeholder(R.drawable.profileplaceholder).circleCrop()
                    .into(binding.postContant.userImage)
                binding.postContant.appCompatEditText.hint =
                    "${PrefManager.getInstance(requireContext())?.userProfileDetail?.results?.first_name}, What's in your mind?"
            }
        })
    }

    override fun onClickOnViewComments(postId: Int) {

    }

    override fun onClickOnProfile(user: Feed) {

    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {
        startActivity(
            IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id", friend.id)
        )
    }

    private fun isNetworkAvailable(): Boolean {
        return NetworkUtils.isNetworkAvailable()
    }

    private fun getFriendSuggestionListing() {
        val hashmap = HashMap<String, String>()
        hashmap["id_user"] = ""
        hashmap["type"] = Constants.TYPE_FRIEND_SUGGESTIONS
        hashmap["sub_type"] = ""
        hashmap["search"] = ""
        hashmap["page"] = "1"
        hashmap["limit"] = "6"

        networkViewModel.getFriendList(prefManager?.access_token.toString(), hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                suggestedFriendAdapter =
                    SuggestedFriendAdapter(networkViewModel, requireContext(), this)
                binding.rvSuggestedFriends.adapter = suggestedFriendAdapter
                suggestedFriendAdapter.submitList(it!!.results)
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchBox() {
        binding.toolbar.layoutSearchBox.setOnTouchListener(View.OnTouchListener { v, event ->
            val x = event.x.toInt()
            val y = event.y.toInt()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.toolbar.layoutSearchBox.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.tapped_search_background
                    )
                }
                MotionEvent.ACTION_MOVE -> Log.i("TAG", "moving: ($x, $y)")
                MotionEvent.ACTION_UP -> {
                    binding.toolbar.layoutSearchBox.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.search_background)
                    startActivity(IntentHelper.getSearchScreen(requireContext()))
                }
            }
            true
        })
    }
}