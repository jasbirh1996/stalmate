package com.stalmate.user.view.dashboard.Friend

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFriendListBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.FriendAdapter

class FragmentFriendList(var type: String, var subtype: String,var userId:String) : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var friendAdapter: FriendAdapter
    lateinit var binding: FragmentFriendListBinding
    var sortBy=""
    var filter=""
    var currentPage=1
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
        friendAdapter = FriendAdapter(networkViewModel, requireContext(), this,type,subtype)
        setupUI()
        binding.shimmerViewContainer.visibility=View.VISIBLE
        binding.shimmerViewContainer.startShimmer()
        binding.rvFriends.adapter = friendAdapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)
        binding.ivFilterIcon.setOnClickListener {
            showMenuFilter(binding.ivFilterIcon)
        }
        binding.tvFilter.setOnClickListener {
            showMenuOtherFilter(binding.tvFilter)
        }

        hitApi(true)


        // Refresh function for the layout
        binding.refreshLayout.setOnRefreshListener{

            hitApi(true)

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
        hitApi(false)
    }


    fun hitApi(isFresh:Boolean){

        if (isFresh){
            currentPage=1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("other_user_id", userId)
        hashmap.put("type", type)
        hashmap.put("sub_type", subtype)
        hashmap.put("search", "")
        hashmap.put("page", currentPage.toString())
        hashmap.put("limit", "20")
        hashmap.put("sortBy",sortBy)
        hashmap.put("filter",filter)

        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {

                binding.refreshLayout.isRefreshing=false

                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility=View.GONE

                if (it!!.results.isNotEmpty()){

                    if (isFresh){
                        friendAdapter.submitList(it.results as java.util.ArrayList<User>)
                    }else{
                        friendAdapter.addToList(it.results as java.util.ArrayList<User>)
                    }

                    isLastPage=false
                    if (it.results.size<6){
                        binding.progressLoading.visibility = View.GONE
                    }else{
                        binding.progressLoading.visibility = View.VISIBLE
                    }
                }else{
                    if (isFresh){
                        friendAdapter.submitList(it.results as java.util.ArrayList<User>)
                    }
                    isLastPage=true
                    binding.progressLoading.visibility = View.GONE

                }

                if (it.results.isEmpty()){
                    binding.layoutNoData.visibility=View.VISIBLE
                }else{
                    binding.ivFilterIcon.visibility=View.VISIBLE
                    binding.tvFilter.visibility=View.VISIBLE
                    binding.layoutNoData.visibility=View.GONE
                }

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
      startActivity(IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id", friend.id))
    }



    fun showMenuFilter(v : View){
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.user_filter_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->

            Log.d("klasjdlsad",";lasjd;a")

            when(menuItem.itemId){
                R.id.actionSortByAZ-> {
                    sortBy="ascending"
                    currentPage=1
                    filter = ""
                    hitApi(true)
                }
                R.id.actionSortByZA-> {
                    sortBy="descending"
                    currentPage=1
                    filter = ""
                    hitApi(true)
                }
                R.id.actionSortByLatest-> {
                    sortBy="recentlyAdded"
                    currentPage=1
                    filter = ""
                    hitApi(true)
                }
            }
            true
        }
        popup.show()
    }


    fun showMenuOtherFilter(v : View){
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.user_other_filter_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->


            when(menuItem.itemId){
                R.id.actionFilterLocation-> {
                    filter="location"
                    currentPage=1
                    sortBy = ""
                    hitApi(true)
                }
                R.id.actionFilterCollege-> {
                    filter="college"
                    currentPage=1
                    sortBy = ""
                    hitApi(true)
                }
                R.id.actionFilterInterest-> {
                    filter="interest"
                    currentPage=1
                    sortBy = ""
                    hitApi(true)
                }
                R.id.actionFilterWorkplace-> {
                    filter="workplace"
                    currentPage=1
                    sortBy = ""
                    hitApi(true)
                }
            }
            true
        }
        popup.show()
    }





    fun setupUI(){
        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {

        binding.tvData.text="No Friends Requests to show"

        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
            binding.tvData.text="No Friends Suggestions to show"
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS_FOLLOWERS)) {

            binding.tvData.text="No Friends Suggestions to show"

        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWING)) {
            binding.tvData.text="No Friends to show"

        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWER)) {
            binding.tvData.text="No Friends to show"


        } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWERS)) {

            binding.tvData.text="No Followers to show"
        } else if (type.equals(Constants.TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWINGS)) {
            binding.tvData.text="No Followings to show"
        }
    }

}