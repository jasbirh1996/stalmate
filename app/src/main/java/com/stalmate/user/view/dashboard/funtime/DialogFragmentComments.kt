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
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentCommentsBinding
import com.stalmate.user.model.Comment
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DialogFragmentComments(var networkViewModel: AppViewModel, var funtimeId: String) :
    BottomSheetDialogFragment(), CommentAdapter.Callback, CommentAdapterNew.Callback {
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


  //  lateinit var viewModel: CommentViewModel
    lateinit var commentAdapter: CommentAdapterNew
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvList)


      //  viewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        commentAdapter = CommentAdapterNew(requireContext(),this,networkViewModel,funtimeId,this)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = commentAdapter


   /*     viewModel.commentList.observe(this) {


            it.forEach {
                Log.d("klajsdasda", it.replies.size.toString())
            }
            commentAdapter.submitList(it.toMutableList())
            // commentAdapter.notifyDataSetChanged()
        }*/

        binding.toolbar.tvhead.text="Comments"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            dismiss()
        }

        binding.nestedScrollView.getViewTreeObserver()
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
                val view =
                    binding.nestedScrollView.getChildAt(binding.nestedScrollView.getChildCount() - 1) as View
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
            commentAdapter.replyOverComment(binding.etComment.text.toString(),commentOverId,parentPosition,childPosition,isReplyisChildComment)
            } else {
            commentAdapter.addComment(binding.etComment.text.toString())
            }

            /*        val id = (viewModel.commentList.value?.size ?: 0) + 1

                    viewModel.commentList.value!!.forEach {


                        try {
                            Log.d("askldjasdfghfgh",it.replies.size.toString())
                            Log.d("askldjasd",viewModel.commentList.value?.size.toString())
                        }catch (e:Exception){}

                    }*/



        /*    viewModel.addComment(
                Comment(
                    id.toString(),
                    Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                    "test",
                    "message$id",
                    first_name = "dfgdfg",
                    last_name = "dfgdfg",
                    child_count = 1,
                    profile_img = "",
                    parentId = "6385fff8ec61450e2feff028"
                )
            )*/
        }
        Glide.with(requireContext()).load(R.drawable.user_placeholder).circleCrop()
            .into(binding.ivUserImage)
        binding.etComment.setHint(
            "Comment as ${PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name} ${
                PrefManager.getInstance(
                    requireContext()
                )!!.userDetail.results[0].last_name
            }"
        )
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

          /*          it.results.forEach {
                        it.replies = kotlin.collections.ArrayList()
                        it.level = 1
                        viewModel.addComment(it)
                    }
                    */

                    it.results.forEach {
                        it.replies=ArrayList<Comment>()
                        commentAdapter.addToList(listOf(it))
                    }








                    isLastPage = false

                } else {
                    isLastPage = true
                }
            }
        }
    }





/*    fun likeComment(id: String) {
        var hashmap = HashMap<String, String>()
        hashmap.put("comment_id", id)
        networkViewModel.likeComment(hashmap)
        networkViewModel.likeCommentLiveData.observe(viewLifecycleOwner) {
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
                    binding.rvList.scrollToPosition(binding.rvList.adapter!!.itemCount - 1)
                }


            }
        }
    }*/



    var commentOverId = ""
    var parentPosition = 0
    var childPosition = 0
    var isReplyisChildComment = false




    override fun onClickOnReply(shortComment: Comment, position: Int) {

    }

    override fun onClickOnReply(
        shortComment: Comment,
        parentPosition: Int,
        childPosition: Int,
        isChild: Boolean
    ) {
        Log.d(";laksdsada", "a;skldasd")
        this.parentPosition = parentPosition
        this.childPosition = childPosition
        commentOverId = shortComment.parentId ?: shortComment._id
        binding.etComment.setText("@${shortComment.first_name} ${shortComment.last_name} ")
        isReplyisChildComment=isChild
    }

    override fun onClickOnViewMoreReply(shortComment: Comment, position: Int) {
      //  viewReplies(shortComment._id, position)
    }

    override fun onCommentAddedSucessfully() {
        binding.etComment.setText("")
    }


}



