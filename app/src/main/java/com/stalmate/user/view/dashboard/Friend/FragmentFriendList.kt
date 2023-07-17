package com.stalmate.user.view.dashboard.Friend

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFriendListBinding
import com.stalmate.user.model.User
import com.stalmate.user.networking.ApiInterface
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.FriendAdapter

class FragmentFriendList(var type: String, var subtype: String, var userId: String) :
    BaseFragment(), FriendAdapter.Callbackk {
    lateinit var friendAdapter: FriendAdapter
    lateinit var binding: FragmentFriendListBinding
    var searchData = ""
    var sortBy = ""
    var filter = ""
    var currentPage = 1
    var isLastPage = false
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFriendListBinding>(
            inflater.inflate(
                R.layout.fragment_friend_list,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendAdapter = FriendAdapter(networkViewModel, requireContext(), this, type, subtype)
        setupUI()
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    searchData = p0.toString()

                    Handler(Looper.myLooper()!!).post {
                        hitApi(true, searchData)
                    }

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.shimmerViewContainer.startShimmer()
        binding.rvFriends.adapter = friendAdapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)
        binding.ivSortIcon.setOnClickListener {
            showMenuFilter(binding.ivSortIcon)
        }
        binding.tvFilter.setOnClickListener {
            showMenuOtherFilter(binding.tvFilter)
        }

        hitApi(true, searchData)


        // Refresh function for the layout
        binding.refreshLayout.setOnRefreshListener {

            hitApi(true, searchData)

        }

        binding!!.nestedScrollView.getViewTreeObserver()
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
                val view =
                    binding!!.nestedScrollView.getChildAt(binding!!.nestedScrollView.getChildCount() - 1) as View
                val diff: Int =
                    view.bottom - (binding!!.nestedScrollView.getHeight() + binding!!.nestedScrollView
                        .getScrollY())
                if (diff == 0) {
                    if (!isLastPage) {
                        binding!!.progressLoading.visibility = View.VISIBLE
                        loadMoreItems()
                    }
                }
            })


    }

    private fun loadMoreItems() {
        isLoading = true
        currentPage++
        hitApi(false, searchData)
    }


    fun hitApi(isFresh: Boolean, dataSearch: String) {
        this.searchData = dataSearch
        if (isFresh) {
            currentPage = 1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("other_user_id", userId)
        hashmap.put("type", type)
        hashmap.put("sub_type", subtype)
        hashmap.put("search", this.searchData)
        hashmap.put("page", currentPage.toString())
        hashmap.put("limit", "20")
        hashmap.put("sortBy", sortBy)
        hashmap.put("filter", filter)

        networkViewModel.getFriendListBody(
            prefManager?.access_token.toString(),
            map = ApiInterface.UsersListResponse(
                limit = "6",
                page = "1",
                type = if (subtype == Constants.TYPE_USER_TYPE_FOLLOWERS) Constants.NEW_Type_Follower else Constants.NEW_Type_Following,
                user_id = userId
            )
        )
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {

                binding.refreshLayout.isRefreshing = false

                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE

                if (!it?.results.isNullOrEmpty()) {

                    if (isFresh) {
                        friendAdapter.submitList(it?.results as java.util.ArrayList<User>)
                    } else {
                        friendAdapter.addToList(it?.results as java.util.ArrayList<User>)
                    }

                    isLastPage = false
                    if ((it?.results?.size?:0) < 6) {
                        binding.progressLoading.visibility = View.GONE
                    } else {
                        binding.progressLoading.visibility = View.VISIBLE
                    }
                } else {
                    if (isFresh) {
                        friendAdapter.submitList(it?.results as java.util.ArrayList<User>)
                    }
                    isLastPage = true
                    binding.progressLoading.visibility = View.GONE

                }



                setupUIAfterAiHit()

            }
        })
    }


    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

        var hashmap = HashMap<String, String>()
        hashmap.put("type", "")
        hashmap.put("search", "")
        hashmap.put("page", "1")


    }


    override fun onClickOnProfile(friend: User) {
        startActivity(
            IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id", friend.id)
        )
    }


    fun showMenuFilter(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.user_filter_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->

            Log.d("klasjdlsad", ";lasjd;a")

            when (menuItem.itemId) {
                R.id.actionSortByAZ -> {
                    sortBy = "ascending"
                    currentPage = 1
                    filter = ""
                    hitApi(true, searchData)
                }
                R.id.actionSortByZA -> {
                    sortBy = "descending"
                    currentPage = 1
                    filter = ""
                    hitApi(true, searchData)
                }
                R.id.actionSortByLatest -> {
                    sortBy = "recentlyAdded"
                    currentPage = 1
                    filter = ""
                    hitApi(true, searchData)
                }
            }
            true
        }
        popup.show()
    }


    fun showMenuOtherFilter(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.user_other_filter_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->


            when (menuItem.itemId) {
                R.id.actionFilterLocation -> {
                    filter = "location"
                    currentPage = 1
                    sortBy = ""
                    hitApi(true, searchData)
                }
                R.id.actionFilterCollege -> {
                    filter = "college"
                    currentPage = 1
                    sortBy = ""
                    hitApi(true, searchData)
                }
                R.id.actionFilterInterest -> {
                    filter = "interest"
                    currentPage = 1
                    sortBy = ""
                    hitApi(true, searchData)
                }
                R.id.actionFilterWorkplace -> {
                    filter = "workplace"
                    currentPage = 1
                    sortBy = ""
                    hitApi(true, searchData)
                }
            }
            true
        }
        popup.show()
    }

    fun setupUI() {
        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {
            binding.tvData.text = "No Friends Requests to show"
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
            binding.tvData.text = "No Friends Suggestions to show"
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS_FOLLOWERS)) {
            binding.tvData.text = "No Friends Suggestions to show"
        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWING)) {
            binding.tvData.text = "No Friends to show"
        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWER)) {
            binding.tvData.text = "No Friends to show"
        } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWERS)) {
            binding.tvData.text = "No Followers to show"
        } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWINGS)) {
            binding.tvData.text = "No Followings to show"
        }
    }


    fun setupUIAfterAiHit() {


        if (friendAdapter.list.isEmpty()) {
            binding.layoutNoData.visibility = View.VISIBLE
            /* binding.ivSortIcon.visibility=View.GONE
             binding.layoutFilter.visibility=View.GONE*/
        } else {
            binding.layoutNoData.visibility = View.GONE

            if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {

                binding.ivSortIcon.visibility = View.VISIBLE
                binding.layoutFilter.visibility = View.GONE
                binding.layoutSearchBox.visibility = View.GONE

            } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {

                binding.ivSortIcon.visibility = View.VISIBLE
                binding.layoutFilter.visibility = View.VISIBLE
                binding.layoutSearchBox.visibility = View.GONE

            } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS_FOLLOWERS)) {


            } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWING)) {
                binding.ivSortIcon.visibility = View.VISIBLE
                binding.layoutFilter.visibility = View.VISIBLE
                binding.layoutSearchBox.visibility = View.GONE

            } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWER)) {
                binding.ivSortIcon.visibility = View.VISIBLE
                binding.layoutFilter.visibility = View.VISIBLE
                binding.layoutSearchBox.visibility = View.GONE


            } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(
                    Constants.TYPE_USER_TYPE_FOLLOWERS
                )
            ) {
                binding.ivSortIcon.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
                binding.layoutSearchBox.visibility = View.VISIBLE

            } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(
                    Constants.TYPE_USER_TYPE_FOLLOWINGS
                )
            ) {
                binding.ivSortIcon.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
                binding.layoutSearchBox.visibility = View.VISIBLE
            }

        }

    }

}