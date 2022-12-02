package com.stalmate.user.view.dashboard.funtime


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentCommentsBinding
import com.stalmate.user.model.Comment
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.dashboard.funtime.viewmodel.CommentViewModel
import com.stalmate.user.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*


class DialogFragmentComments(var networkViewModel: AppViewModel, var funtimeId: String) :
    BottomSheetDialogFragment(), CommentAdapter.Callback {
    lateinit var binding: FragmentCommentsBinding
    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.bind<FragmentCommentsBinding>(
            layoutInflater.inflate(
                R.layout.fragment_comments,
                null,
                false
            )
        )!!

        return binding.root

    }


    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }


    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)!! as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.getLayoutParams()
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.setLayoutParams(layoutParams)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }





    lateinit var viewModel: CommentViewModel
    lateinit var commentAdapter: CommentAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvList)


        viewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        commentAdapter = CommentAdapter(CommentAdapter.CommentClickListener { author, message, level, parentId,position ->





            }, requireContext(),this)
        recyclerView.layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = commentAdapter


        viewModel.commentList.observe(this) {


         it.forEach {
             Log.d("klajsdasda", it.replies.size.toString())
         }
            commentAdapter.submitList(it.toMutableList())
           // commentAdapter.notifyDataSetChanged()
        }


        binding.nestedScrollView.getViewTreeObserver()
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
                val view = binding.nestedScrollView.getChildAt(binding.nestedScrollView.getChildCount() - 1) as View
                val diff: Int =
                    view.bottom - (binding.nestedScrollView.getHeight() + binding.nestedScrollView
                        .getScrollY())
                if (diff == 0) {
                    if (!isLastPage) {
                       // loadMoreItems()
                    }
                }
            })

        binding.tvPOstButton.setOnClickListener {
          if (commentOverId != "") {
                replyOverComment(binding.etComment.text.toString(), commentOverId)
            } else {
                addComment(binding.etComment.text.toString())
            }

    /*        val id = (viewModel.commentList.value?.size ?: 0) + 1

            viewModel.commentList.value!!.forEach {


                try {
                    Log.d("askldjasdfghfgh",it.replies.size.toString())
                    Log.d("askldjasd",viewModel.commentList.value?.size.toString())
                }catch (e:Exception){}

            }*/



            viewModel.addComment(Comment(id.toString(), Calendar.getInstance(Locale.ROOT).toSimpleDate(), "test", "message$id", level = 2, first_name = "dfgdfg", last_name = "dfgdfg", child_count = 1, profile_img = "", parentId = "6385fff8ec61450e2feff028"))
        }
    Glide.with(requireContext()).load(R.drawable.user_placeholder).circleCrop().into(binding.ivUserImage)
        binding.etComment.setHint("Comment as ${PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name} ${PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name}")
        hitApi(true)
    }


    fun Calendar.toSimpleDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(time)
    }


    var currentPage = 1
    var isLastPage = false
    var isLoading = false
    private fun loadMoreItems() {
        isLoading = true
        currentPage++
        hitApi(false)
    }


    fun hitApi(isFresh: Boolean) {

        if (isFresh) {
            currentPage = 1
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtimeId)
        hashmap.put("comment_id", "")
/*        hashmap.put("page", currentPage.toString())*/

        networkViewModel.getCommentList(hashmap)
        networkViewModel.commentLiveData.observe(viewLifecycleOwner) { it ->


            if (it!!.status) {
                if (it.results.isNotEmpty()) {

                    it.results.forEach {
                        it.replies=kotlin.collections.ArrayList()
                        it.level=1
                        viewModel.addComment(it)
                    }
                    isLastPage = false

                } else {
                    isLastPage = true
                }
            }

        }
    }


    fun addComment(data: String) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtimeId)
        hashmap.put("comment", data)
        hashmap.put("id", "")
        hashmap.put("comment_id", "")
        hashmap.put("is_delete", "0")

        networkViewModel.addComment(hashmap)
        networkViewModel.addCommentLiveData.observe(viewLifecycleOwner) {
            it.let {

                if (it!!.status) {
                    viewModel.addComment(
                        Comment(
                            it.results._id,
                            Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                            "test",
                            data,
                            first_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name,
                            last_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name,
                            child_count = it.results.child_count,
                            profile_img = it.results.profile_img
                        )
                    )
                    binding.etComment.setText("")
                    binding.rvList.scrollToPosition(binding.rvList.adapter!!.itemCount-1)
                }


            }
        }
    }


    fun replyOverComment(data: String, commentId: String) {

        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtimeId)
        hashmap.put("comment", data)
        hashmap.put("id", "")
        hashmap.put("comment_id", commentId)
        hashmap.put("is_delete", "0")

        networkViewModel.addComment(hashmap)
        networkViewModel.addCommentLiveData.observe(viewLifecycleOwner) {
            it.let {

                if (it!!.status) {
                    viewModel.addComment(
                        Comment(
                            it.results._id,
                            Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                            "test",
                            data,
                            parentId = it.results.parentId ?: it.results._id,
                            first_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name,
                            last_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name,
                            child_count = it.results.child_count,
                            profile_img = it.results.profile_img
                        )
                    )
                    commentAdapter.notifyItemChanged(replyCommentposition)

                    binding.etComment.setText("")
                }
            }
        }
    }

    var commentOverId = ""
    var replyCommentposition = 0
      override fun onClickOnReply(shortComment: Comment,position:Int) {
          Log.d(";laksdsada","a;skldasd")
          replyCommentposition=position
          commentOverId = shortComment.parentId ?: shortComment._id
          binding.etComment.setText("@${shortComment.first_name} ${shortComment.last_name} ")
       }

       override fun onClickOnViewMoreReply(shortComment: Comment,position:Int) {
           viewReplies(shortComment._id,position)
       }


    fun viewReplies(commentId: String, position: Int) {

        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtimeId)
        hashmap.put("comment_id", commentId)

        networkViewModel.getRepliesList(hashmap)
        networkViewModel.repliesLiveData.observe(viewLifecycleOwner) { mainIt ->
            run {
                if (mainIt!!.status) {
                    mainIt.results.forEach {
                        viewModel.addComment(
                            Comment(
                                it._id,
                                Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                                "test",
                                it.comment,
                                parentId = it.parentId,
                                first_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name,
                                last_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name,
                                child_count = 0,
                                profile_img = it.profile_img
                            )
                        )
                    }
                    commentAdapter.notifyItemChanged(position)
                }
            }

        }
    }


}



