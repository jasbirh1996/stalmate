package com.stalmate.user.view.dashboard.HomeFragment


import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentGlobalSearchBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.SearchedUserAdapter

class FragmentGlobalSearch : BaseFragment(),
    SearchedUserAdapter.Callbackk {
    lateinit var userAdapter: SearchedUserAdapter
    lateinit var binding: FragmentGlobalSearchBinding
    var sortBy = ""
    var currentPage = 1
    var searchData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    public interface Callback{
        fun onClickOnSeeMore(searData:String,type:String)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentGlobalSearchBinding>(
            inflater.inflate(
                R.layout.fragment_global_search,
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

        binding.buttonSeeMoreGroups.setOnClickListener {
          //  callback.onClickOnSeeMore(searchData,"groups")
            findNavController().navigate(R.id.action_fragmentGlobalToFragmentPeopleSearch)
        }
        binding.buttonSeeMoreUsers.setOnClickListener {
          //  callback.onClickOnSeeMore(searchData,"users")
            findNavController().navigate(R.id.action_fragmentGlobalToFragmentPeopleSearch)
        }
        binding.buttonSeeMoreEvents.setOnClickListener {
          //  callback.onClickOnSeeMore(searchData,"events")
            findNavController().navigate(R.id.action_fragmentGlobalToFragmentPeopleSearch)
        }



    }

    fun hitApi(isFresh: Boolean, searchData: String) {
        this.searchData=searchData
        if (isFresh) {
            currentPage = 1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("search", this.searchData)
        hashmap.put("page", currentPage.toString())
        hashmap.put("limit", "5")

        networkViewModel.getGlobalSearch(hashmap)
        networkViewModel.globalSearchLiveData.observe(viewLifecycleOwner, Observer {
            it.let {





                if (it!!.user_list.isNotEmpty()) {


                    val divider = DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL)

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




                    binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvGroups.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())


                    binding.rvEvents.addItemDecoration(divider)
                    binding.rvGroups.addItemDecoration(divider)
                    binding.rvUsers.addItemDecoration(divider)





                    binding.rvEvents.adapter = userAdapter
                    binding.rvGroups.adapter = userAdapter
                    binding.rvUsers.adapter = userAdapter






                    binding.layoutUsers.visibility = View.VISIBLE
                    binding.layoutEvents.visibility = View.VISIBLE
                    binding.layoutGroups.visibility = View.VISIBLE




                    if (isFresh) {
                        if (it.user_list.size>4){
                            userAdapter.setList(it.user_list.subList(0,4))
                        }else{
                            userAdapter.setList(it.user_list)
                        }


                    } else {
                        userAdapter.addToList(it.user_list)
                    }



                    if (it.user_list.size>4){
                        binding.buttonSeeMoreEvents.visibility=View.VISIBLE
                        binding.buttonSeeMoreGroups.visibility=View.VISIBLE
                        binding.buttonSeeMoreUsers.visibility=View.VISIBLE
                    }else{
                        binding.buttonSeeMoreEvents.visibility=View.GONE
                        binding.buttonSeeMoreGroups.visibility=View.GONE
                        binding.buttonSeeMoreUsers.visibility=View.GONE
                    }
                }else{
                    binding.layoutUsers.visibility = View.GONE
                    binding.layoutEvents.visibility = View.GONE
                    binding.layoutGroups.visibility = View.GONE
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


        startActivity(IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id", friend.id)
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