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
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSearchBinding
import com.stalmate.user.model.ModelGlobalSearch
import com.stalmate.user.model.User
import com.stalmate.user.networking.ApiInterface
import com.stalmate.user.view.adapter.SearchedUserAdapter

class FragmentPeopleSearch : BaseFragment(), SearchedUserAdapter.Callbackk {
    lateinit var userAdapter: SearchedUserAdapter
    lateinit var binding: FragmentSearchBinding
    var sortBy = ""
    var currentPage = 1
    var contactString = ""
    var searchData = ""
    var isLastPage = false
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {

        if (requireArguments().getString("dataSearch") != null) {
            searchData = requireArguments().getString("dataSearch").toString()
        }
        if (requireArguments().getString("contacts") != null) {
            contactString = requireArguments().getString("contacts").toString()
        }
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





        hitApi(true, searchData)
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

        val hashmap = ApiInterface.SearchRequest(
            page = currentPage,
            limit = 20,
            search = this.searchData,
            number_array = contactString
        )

        networkViewModel.getGlobalSearch(prefManager?.access_token.toString(), hashmap)
        networkViewModel.globalSearchLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it?.reponse?.isNotEmpty() == true) {
                    if (isFresh) {
                        userAdapter.submitList(it!!.reponse)
                    } else {
                        userAdapter.addToList(it!!.reponse)
                    }
                    isLastPage = false
                    if ((it!!.reponse?.size ?: 0) < 6) {
                        binding.progressLoading.visibility = View.GONE
                    } else {
                        binding.progressLoading.visibility = View.VISIBLE
                    }
                } else {
                    isLastPage = true
                    binding.progressLoading.visibility = View.GONE
                    if (isFresh) {
                        userAdapter.submitList(it!!.reponse)
                    }
                    binding.layoutNoData.visibility = View.VISIBLE
                }
                if (userAdapter.list?.isEmpty() == true) {
                    binding.layoutNoData.visibility = View.VISIBLE
                } else {
                    binding.layoutNoData.visibility = View.GONE
                }
            }
        })
    }


    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {


    }


    override fun onClickOnProfile(friend: ModelGlobalSearch.Reponse?) {
        IntentHelper.getOtherUserProfileScreen(requireContext())?.apply {
            putExtra("id", friend?.id)
            putExtra("userData", friend)
        }?.let {
            startActivity(
                it
            )
        }
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