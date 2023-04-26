package com.stalmate.user.view.dashboard.funtime


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.commonadapters.ShareWithFriendAdapter
import com.stalmate.user.databinding.FragmentShareWithFriendsBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.viewmodel.AppViewModel

class DialogFragmentShareWithFriends(
    var networkViewModel: AppViewModel,
    var funtime: ResultFuntime,
    var callback: CAllback
) : BottomSheetDialogFragment(),
    ShareWithFriendAdapter.Callback {
    lateinit var friendAdapter: ShareWithFriendAdapter
    lateinit var binding: FragmentShareWithFriendsBinding
    var searchData = ""
    var currentPage = 1
    var isLastPage = false
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NonDraggableBottomSheet)


    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_share_with_friends, null)

        binding = DataBindingUtil.bind<FragmentShareWithFriendsBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }


        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_share_with_friends, null, false)
        binding = DataBindingUtil.bind<FragmentShareWithFriendsBinding>(v)!!
        return binding.root
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {


                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }


    public interface CAllback {
        fun onTotalShareCountFromDialog(count: Int)
    }

    override fun onDestroy() {
        var count = 0
        friendAdapter.list.forEach {
            if (it.isSelected) {
                count++
            }
        }


        callback.onTotalShareCountFromDialog(count)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        friendAdapter = ShareWithFriendAdapter(requireContext(), this)
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



        binding.buttonOk.setOnClickListener {

            dismiss()

        }

        binding.rvFriends.adapter = friendAdapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)

        hitApi(true, searchData)

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
        hitApi(false, searchData)
    }


    fun hitApi(isFresh: Boolean, dataSearch: String) {
        this.searchData = dataSearch
        if (isFresh) {
            currentPage = 1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("other_user_id", "")
        hashmap.put("type", Constants.TYPE_PROFILE_FRIENDS)
        hashmap.put("sub_type", "")
        hashmap.put("search", this.searchData)
        hashmap.put("page", currentPage.toString())
        hashmap.put("limit", "20")
        hashmap.put("sortBy", "")
        hashmap.put("filter", "")
        networkViewModel.getFriendList(
            PrefManager.getInstance(App.getInstance())?.userDetail?.results?.get(0)?.access_token.toString(),
            hashmap
        )
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it!!.results.isNotEmpty()) {
                    if (isFresh) {
                        friendAdapter.addToList(it.results as ArrayList<User>)
                    } else {
                        friendAdapter.addToList(it.results as ArrayList<User>)
                    }

                    isLastPage = false
                    if (it.results.size < 6) {
                        binding.progressLoading.visibility = View.GONE
                    } else {
                        binding.progressLoading.visibility = View.VISIBLE
                    }
                    binding.layoutNoData.visibility = View.GONE
                } else {
                    if (isFresh) {
                        friendAdapter.submitList(it.results as ArrayList<User>)
                        binding.layoutNoData.visibility = View.VISIBLE
                    }

                    isLastPage = true
                    binding.progressLoading.visibility = View.GONE


                }


            }
        })
    }

    override fun onUserSelected(user: User) {
        shareWithFriend(user)
    }


    fun shareWithFriend(user: User) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtime.id)
        hashmap.put("user_id", user.id)
        networkViewModel.shareWithFriend(hashmap)
        networkViewModel.shareWithFriendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it!!.status!!) {

                }
            }
        })
    }


}