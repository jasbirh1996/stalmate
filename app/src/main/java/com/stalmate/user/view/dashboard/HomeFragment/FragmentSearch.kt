package com.stalmate.user.view.dashboard.HomeFragment


import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
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

class FragmentSearch(var type: String, var subtype: String) : BaseFragment(),
    SearchedUserAdapter.Callbackk {
    lateinit var userAdapter: SearchedUserAdapter
    lateinit var binding: FragmentSearchBinding
    var sortBy = ""
    var currentPage = 1
    var searchData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
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


        hitApi(true)



        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    searchData = p0.toString()
                    hitApi(true)
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


    }

    fun hitApi(isFresh: Boolean) {

        if (isFresh) {
            currentPage = 1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("search", searchData)
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
                        intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.dp_05)
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
                    hitApi(true)
                }
                R.id.actionSortByZA -> {
                    sortBy = "descending"
                    currentPage = 1
                    hitApi(true)
                }
                R.id.actionSortByLatest -> {
                    sortBy = "recentlyAdded"
                    currentPage = 1
                    hitApi(true)
                }
            }
            true
        }
        popup.show()
    }


}