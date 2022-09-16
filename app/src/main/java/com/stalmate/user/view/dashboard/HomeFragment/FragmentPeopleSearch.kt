package com.stalmate.user.view.dashboard.HomeFragment


import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSearchBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.SearchedUserAdapter

class FragmentPeopleSearch : BaseFragment(), SearchedUserAdapter.Callbackk {
    lateinit var userAdapter: SearchedUserAdapter
    lateinit var binding: FragmentSearchBinding
    var sortBy = ""
    var currentPage = 1
    var searchData = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        searchData= requireArguments().getString("dataSearch").toString()
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentSearchBinding>(
            inflater.inflate(
                R.layout.fragment_search,
                container,
                false
            )
        )!!
        userAdapter = SearchedUserAdapter(networkViewModel, requireContext(), this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hitApi(true, searchData)
    }

    fun hitApi(isFresh: Boolean, dataSearch: String) {
        this.searchData = dataSearch
        if (isFresh) {
            currentPage = 1
        }
        Log.d("asdasdsa", "asdasfdg")
        var hashmap = HashMap<String, String>()
        hashmap.put("search", this.searchData)
        hashmap.put("page", currentPage.toString())
        hashmap.put("limit", "")

        networkViewModel.getGlobalSearch(hashmap)
        networkViewModel.globalSearchLiveData.observe(viewLifecycleOwner, Observer {
            it.let {


                if (it!!.user_list.isNotEmpty()) {


                    val divider = DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    )

                    divider.setDrawable(ShapeDrawable().apply {
                        intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.dp_1)
                        paint.color = Color.GRAY // Note:
                        //   Currently (support version 28.0.0), we
                        //   can not use tranparent color here. If
                        //   we use transparent, we still see a
                        //   small divider line. So if we want
                        //   to display transparent space, we
                        //   can set color = background color
                        //   or we can create a custom ItemDecoration
                        //   instead of DividerItemDecoration.
                    })


                    binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvItems.addItemDecoration(divider)
                    binding.rvItems.adapter = userAdapter
                    binding.layoutUsers.visibility = View.VISIBLE
                    Log.d("aasdasf", it.user_list.size.toString())
                    if (isFresh) {
                        userAdapter.setList(it.user_list)
                    } else {
                        userAdapter.addToList(it.user_list)
                    }
                } else {
                    binding.layoutUsers.visibility = View.GONE
                }

            }
        })
    }


    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {


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
                    hitApi(true, searchData)
                }
                R.id.actionSortByZA -> {
                    sortBy = "descending"
                    currentPage = 1
                    hitApi(true, searchData)
                }
                R.id.actionSortByLatest -> {
                    sortBy = "recentlyAdded"
                    currentPage = 1
                    hitApi(true, searchData)
                }
            }
            true
        }
        popup.show()
    }


}