package com.stalmate.user.view.dashboard.HomeFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentHomeNewBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.NetworkUtils
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.adapter.SuggestedFriendAdapter
import com.stalmate.user.view.adapter.UserHomeStoryAdapter
import com.stalmate.user.view.dashboard.funtime.ResultFuntime


class FragmentHome(var callback: Callback) : BaseFragment(),
    UserHomeStoryAdapter.Callbackk, SuggestedFriendAdapter.Callbackk {

    private lateinit var binding: FragmentHomeNewBinding
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
        val view = inflater.inflate(R.layout.fragment_home_new, container, false)
        binding = DataBindingUtil.bind<FragmentHomeNewBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeSetUp()

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
            if (isNetworkAvailable()) {
                callApi()
                isFirstApiHit = true
                page_count = 1
            } else {
                binding.nointernet.visibility = View.VISIBLE
            }
        }

        if (isNetworkAvailable()) {
            getUserProfileData()
            callApi()
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


    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount: kotlin.Int = 0
    var totalItemCount: kotlin.Int = 0
    private fun homeSetUp() {
        setupSearchBox()
        feedAdapter = AdapterFeed(
            networkViewModel,
            requireContext(),
            requireActivity(),
            object : AdapterFeed.Callbackk {
                override fun onClickOnViewComments(postId: Int) {

                }

                override fun onCLickItem(item: ResultFuntime) {
                    startActivity(
                        IntentHelper.getFullViewReelActivity(context)!!.putExtra("data", item)
                    )
                }
            })
        homeStoryAdapter = UserHomeStoryAdapter(networkViewModel, requireContext(), this)
        binding.shimmerViewContainer.startShimmer()
        binding.shimmerLayoutFeeds.startShimmer()

        binding.rvFeeds.adapter = feedAdapter
        binding.rvStory.adapter = homeStoryAdapter

        binding.rvFeeds.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount =
                        (binding.rvFeeds.layoutManager as LinearLayoutManager).getChildCount()
                    totalItemCount =
                        (binding.rvFeeds.layoutManager as LinearLayoutManager).getItemCount()
                    pastVisiblesItems =
                        (binding.rvFeeds.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            Log.v("...", "Last Item Wow !")
                            // Do pagination.. i.e. fetch new data

                            if (!isApiRuning) {
                                page_count++
                                callApi()
                            }

                            loading = true
                        }
                    }
                }
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
//            callback.onCLickOnMenuButton()
            callback.onCLickOnProfileButton()
        }

        binding.nointernet.visibility = View.GONE
    }

    var page_count = 1
    var isFirstApiHit = true
    var isSelfVideos = false
    var isApiRuning = false
    var handler: Handler? = null
    fun callApi() {
        isApiRuning = true
        val hashmap = HashMap<String, String>()
        hashmap.put("page", page_count.toString())
        hashmap.put("id_user", "")
        hashmap.put("fun_id", "")
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner, Observer {
            /*
            binding.shimmerViewContainer.stopShimmer()
                binding.storyView.visibility = View.GONE
                if (!it?.results.isNullOrEmpty())
                    it?.results?.let { it1 -> homeStoryAdapter.submitList(it1) }
            */
            isApiRuning = false
            if (!it?.results.isNullOrEmpty()) {
                binding.shimmerLayoutFeeds.stopShimmer()
                binding.rvFeeds.visibility = View.VISIBLE
                it?.results?.let {
                    if (isFirstApiHit) {
                        it.forEach {
                            it.isDataUpdated = false
                        }
                        feedAdapter.submitList(it)
                    } else {
                        it.forEach {
                            it.isDataUpdated = false
                        }
                        feedAdapter.addToList(it)
                    }
                }
                isFirstApiHit = false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Glide.with(this).load(prefManager?.profile_img_1.toString())
            .placeholder(R.drawable.user_placeholder).error(R.drawable.user_placeholder)
            .into(binding.toolbar.ivButtonMenu)
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
        binding.toolbar.ivButtonSearch.setImageResource(R.drawable.ic_profile_searchbar)
        binding.toolbar.ivButtonSearch.setOnClickListener {
            startActivity(IntentHelper.getSearchScreen(requireContext()))
        }
        /*binding.toolbar.ivButtonSearch.setOnTouchListener(View.OnTouchListener { v, event ->
            val x = event.x.toInt()
            val y = event.y.toInt()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.toolbar.ivButtonSearch.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.tapped_search_background
                    )
                }
                MotionEvent.ACTION_MOVE -> Log.i("TAG", "moving: ($x, $y)")
                MotionEvent.ACTION_UP -> {
                    binding.toolbar.ivButtonSearch.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.search_background)
                }
            }
            true
        })*/
    }
}