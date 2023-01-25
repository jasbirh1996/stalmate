package com.stalmate.user.view.dashboard.HomeFragment

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentSearchBaseBinding
import com.stalmate.user.view.adapter.SuggestedFriendAdapter
import com.stalmate.user.view.adapter.UserHomeStoryAdapter
import com.stalmate.user.view.profile.FragmentProfileEdit


class FragmentSearchBase(var callbackProfileEdit: FragmentProfileEdit.CAllback) : BaseFragment(),
    FragmentGlobalSearch.Callback {
    var searchData = ""
    private lateinit var binding: FragmentSearchBaseBinding
    lateinit var feedAdapter: AdapterFeed
    lateinit var homeStoryAdapter: UserHomeStoryAdapter
    lateinit var suggestedFriendAdapter: SuggestedFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    public interface Callback {
        fun onCLickOnMenuButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_search_base, container, false)
        binding = DataBindingUtil.bind<FragmentSearchBaseBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  loadFragment(FragmentGlobalSearch("", this))


        binding.ivBack.setOnClickListener {
            //onClickOnBackPress()
            callbackProfileEdit.onClickBackPress()

        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    searchData = p0.toString()
                    Log.d(
                        "asdasdsa",
                        childFragmentManager.findFragmentById(binding.frame.id)!!.tag.toString()
                    )
                    Handler(Looper.myLooper()!!).post {
                        if (childFragmentManager.findFragmentById(binding.frame.id) is FragmentGlobalSearch) {
                            var fragment =
                                childFragmentManager.findFragmentByTag(backStateName) as FragmentGlobalSearch
                            fragment.hitApi(true, searchData)
                        } else if (childFragmentManager.findFragmentById(binding.frame.id) is FragmentPeopleSearch) {
                            var fragment =
                                childFragmentManager.findFragmentByTag(backStateName) as FragmentPeopleSearch
                            fragment.hitApi(true, searchData)
                        }
                    }

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onClickOnBackPress()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun onClickOnBackPress() {
        if (childFragmentManager.backStackEntryCount == 1) {
            requireActivity().finish()
        } else {
            childFragmentManager.popBackStack()
        }
    }


/*    private fun getFriendSuggestionListing() {

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
    }*/

    var backStateName = ""
    private fun loadFragment(fragment: Fragment) {
        backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = childFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(binding.frame.id, fragment, fragmentTag)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    override fun onClickOnSeeMore(searData: String, type: String) {
        // loadFragment(FragmentPeopleSearch(searData))
    }

}