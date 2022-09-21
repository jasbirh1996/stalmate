package com.stalmate.user.view.dashboard.HomeFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentHomeBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.SuggestedFriendAdapter
import com.stalmate.user.view.adapter.UserHomeStoryAdapter
import com.stalmate.user.view.dialogs.DialogFragmentLoader


class FragmentHome(var callback:Callback) : BaseFragment(), AdapterFeed.Callbackk, UserHomeStoryAdapter.Callbackk,
    SuggestedFriendAdapter.Callbackk {

    private lateinit var binding: FragmentHomeBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var homeStoryAdapter: UserHomeStoryAdapter
    lateinit var suggestedFriendAdapter:  SuggestedFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    public interface Callback{
        fun onCLickOnMenuButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_home, container, false)
        binding=DataBindingUtil.bind<FragmentHomeBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchBox()
        feedAdapter = AdapterFeed(networkViewModel, requireContext(), this)
        homeStoryAdapter = UserHomeStoryAdapter(networkViewModel, requireContext(), this)
        binding.shimmerViewContainer.startShimmer()
        binding.shimmerLayoutFeeds.startShimmer()


        binding.rvFeeds.adapter=feedAdapter
        binding.rvStory.adapter=homeStoryAdapter


        binding.rvFeeds.layoutManager= LinearLayoutManager(context)
        binding.rvStory.layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)


        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd","oaspiasddsad")
            it.let {
                feedAdapter.submitList(it!!.results)
                binding.shimmerLayoutFeeds.stopShimmer()
                binding.rvFeeds.visibility=View.VISIBLE
            }
        })

        networkViewModel.feedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("asdasdasd","oaspiasddsad")
            it.let {
                binding.shimmerViewContainer.stopShimmer()
                binding.storyView.visibility=View.VISIBLE
                homeStoryAdapter.submitList(it!!.results)
            }
        })

        getFriendSuggestionListing()


        binding.postContant.userImage.setOnClickListener {
            startActivity(IntentHelper.getProfileScreen(requireContext()))
        }


        binding.layoutNewUser.setOnClickListener {
            startActivity(IntentHelper.getActivityWelcomeScreen(requireContext()))
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing=false
        }

        binding.toolbar.ivButtonMenu.setOnClickListener {
            callback.onCLickOnMenuButton()
        }



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


    private fun getFriendSuggestionListing() {

        var hashmap = HashMap<String, String>()
        hashmap.put("id_user", "")
        hashmap.put("type",Constants.TYPE_FRIEND_SUGGESTIONS)
        hashmap.put("sub_type", "")
        hashmap.put("search", "")
        hashmap.put("page", "1")
        hashmap.put("limit", "6")

        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.d("asdasdasd","asdasdasdasd")

                suggestedFriendAdapter = SuggestedFriendAdapter(networkViewModel, requireContext(), this)
                binding.rvSuggestedFriends.layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                binding.rvSuggestedFriends.adapter=suggestedFriendAdapter
                suggestedFriendAdapter.submitList(it!!.results)
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private  fun setupSearchBox(){
      binding.toolbar.layoutSearchBox.setOnTouchListener(View.OnTouchListener { v, event ->
          val x = event.x.toInt()
          val y = event.y.toInt()
          when (event.action) {
              MotionEvent.ACTION_DOWN ->{
                  binding.toolbar.layoutSearchBox.background=ContextCompat.getDrawable(requireContext(),R.drawable.tapped_search_background)
              }
              MotionEvent.ACTION_MOVE -> Log.i("TAG", "moving: ($x, $y)")
              MotionEvent.ACTION_UP ->{
                  binding.toolbar.layoutSearchBox.background=ContextCompat.getDrawable(requireContext(),R.drawable.search_background)


                  startActivity(IntentHelper.getSearchScreen(requireContext()))

              }
          }
          true
      })




    }


}