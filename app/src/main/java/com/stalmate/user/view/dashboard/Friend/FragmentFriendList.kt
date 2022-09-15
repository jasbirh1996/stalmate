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

class FragmentFriendList(var type: String, var subtype: String,var userId:String) : BaseFragment(),
    FriendAdapter.Callbackk {
    lateinit var friendAdapter: FriendAdapter
    lateinit var binding: FragmentFriendListBinding
    var sortBy=""
    var currentPage=1
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
        hitApi(true)


        // Refresh function for the layout
        binding.refreshLayout.setOnRefreshListener{

            hitApi(true)

        }


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
        hashmap.put("limit", "")
        hashmap.put("sortBy",sortBy)

        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {

                binding.refreshLayout.isRefreshing=false

                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility=View.GONE
                if (isFresh){
                    friendAdapter.submitList(it!!.results)
                }else{
                    friendAdapter.submitList(it!!.results)
                }

                if (it.results.isEmpty()){

                    binding.layoutNoData.visibility=View.VISIBLE
                }else{
                    binding.ivFilterIcon.visibility=View.VISIBLE
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




      startActivity(
            IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id", friend.id)
        )
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
                    hitApi(true)
                }
                R.id.actionSortByZA-> {
                    sortBy="descending"
                    currentPage=1
                    hitApi(true)
                }
                R.id.actionSortByLatest-> {
                    sortBy="recentlyAdded"
                    currentPage=1
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