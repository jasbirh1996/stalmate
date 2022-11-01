package com.stalmate.user.view.dashboard.funtime

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.TaggedUsersAdapter
import com.stalmate.user.databinding.FragmentSingleUserSelectorBinding

import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.FriendAdapter
import java.util.ArrayList

class FragmentSingleUserSelector: BaseFragment(), FriendAdapter.Callbackk,
    TaggedUsersAdapter.Callback {
    lateinit var friendAdapter: TaggedUsersAdapter
    lateinit var binding: FragmentSingleUserSelectorBinding
    var searchData = ""
    var currentPage=1
    var isLastPage = false
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentSingleUserSelectorBinding>(
            inflater.inflate(
                R.layout.fragment_single_user_selector,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendAdapter = TaggedUsersAdapter(taggedPeopleViewModel, requireContext(),isToSelect = true,this)
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
        binding.rvFriends.adapter = friendAdapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)

        hitApi(true,searchData)

        binding.nestedScrollView.getViewTreeObserver()
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
                val view =
                    binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
                val diff: Int =
                    view.bottom - (binding.nestedScrollView.getHeight() + binding.nestedScrollView
                        .getScrollY())
                if (diff == 0) {
                    if (!isLastPage) {
                        binding.progressLoading.visibility = View.VISIBLE
                        loadMoreItems()
                    }
                }
            })


    }

    private fun loadMoreItems() {
        isLoading = true
        currentPage++
        hitApi(false,searchData)
    }


    fun hitApi(isFresh:Boolean, dataSearch: String){
        this.searchData = dataSearch
        if (isFresh){
            currentPage=1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("other_user_id", "")
        hashmap.put("type", Constants.TYPE_PROFILE_FRIENDS)
        hashmap.put("sub_type", "")
        hashmap.put("search", this.searchData)
        hashmap.put("page", currentPage.toString())
        hashmap.put("limit", "20")
        hashmap.put("sortBy","")
        hashmap.put("filter","")

        networkViewModel.getFriendList(hashmap)
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                

                if (it!!.results.isNotEmpty()){

                    if (isFresh){
                        friendAdapter.submitList(it.results as ArrayList<User>)
                    }else{
                        friendAdapter.addToList(it.results as ArrayList<User>)
                    }

                    isLastPage=false
                    if (it.results.size<6){
                        binding.progressLoading.visibility = View.GONE
                    }else{
                        binding.progressLoading.visibility = View.VISIBLE
                    }
                }else{
                    if (isFresh){
                        friendAdapter.submitList(it.results as ArrayList<User>)
                    }
                    isLastPage=true
                    binding.progressLoading.visibility = View.GONE

                }
                

            }
        })
    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }


    override fun onClickOnProfile(friend: User) {


    }

    override fun onUserSelected(user: User) {
        var bundle=Bundle()
        bundle.putSerializable(SELECT_USER,user)
        setFragmentResult(
            SELECT_USER,bundle
        )
        findNavController().popBackStack()
    }


}