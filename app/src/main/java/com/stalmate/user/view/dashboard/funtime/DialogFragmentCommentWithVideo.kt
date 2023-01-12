package com.stalmate.user.view.dashboard.funtime

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R

import com.stalmate.user.databinding.FragmentCommentsWithVideoBinding
import com.stalmate.user.model.Comment
import com.stalmate.user.model.User
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.viewmodel.AppViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DialogFragmentCommentWithVideo(var networkViewModel: AppViewModel, var funtime: ResultFuntime, var callBack: Callback) :
    DialogFragment(), CommentAdapter.Callback, CommentAdapterNew.Callback {
    lateinit var binding: FragmentCommentsWithVideoBinding
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

        binding = DataBindingUtil.bind<FragmentCommentsWithVideoBinding>(
            layoutInflater.inflate(
                R.layout.fragment_comments_with_video,
                null,
                false
            )
        )!!

        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeBlack);
    }


    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        /*   dialog.setOnShowListener { dialogInterface ->
               val bottomSheetDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
               setupFullHeight(bottomSheetDialog)
           }*/
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

    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {

        super.onStop()

    }

    private var mPlayer: ExoPlayer? = null
    //  lateinit var viewModel: CommentViewModel
    lateinit var commentAdapter: CommentAdapterNew
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvList)


        //  viewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        commentAdapter = CommentAdapterNew(requireContext(),this,networkViewModel,funtime.id,this)
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
            callBack.funOnCommentDialogDismissed(commentAdapter.commentList.size)
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
        }
        Glide.with(requireContext()).load(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.profile_img1).placeholder(
            R.drawable.user_placeholder).circleCrop()
            .into(binding.ivUserImage)

        Glide.with(requireContext()).load(funtime.profile_img).placeholder(R.drawable.user_placeholder).circleCrop()
            .into(binding.ivMainUserImage)


        binding.tvComment.text=funtime.text

/*
        val text = "<font color=#000000>${funtime.first_name+" "+funtime.last_name+" "}</font><font color=#0f53b8>${funtime.text}  </font>"
        binding.tvUserName.text= Html.fromHtml(text)
*/

        binding.tvUserName.text="${funtime.first_name+" "+funtime.last_name+" "}"
        binding.tvDate.text =funtime.Created_date

        binding.etComment.setHint(
            "Comment as ${PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name} ${
                PrefManager.getInstance(
                    requireContext()
                )!!.userDetail.results[0].last_name
            }"
        )
        hitApi(true)



        mPlayer = ExoPlayer.Builder(requireContext()).build()
        mPlayer!!.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        val factory = DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))
        val mediaItem: MediaItem
        mediaItem = MediaItem.fromUri(Uri.parse(funtime.file))
        val source: ProgressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        binding.playerView.player=mPlayer;
        mPlayer!!.prepare(source);
        mPlayer!!.playWhenReady = true;
        mPlayer!!.play()

    }


    fun Calendar.toSimpleDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(time)
    }


    override fun onDestroy() {
        callBack.funOnCommentDialogDismissed(commentAdapter.commentList.size)
        mPlayer!!.stop(true)
        mPlayer!!.playWhenReady = false
        mPlayer!!.release()
        mPlayer = null
        super.onDestroy()
    }





    override fun onResume() {
        if (mPlayer!=null){
            mPlayer!!.play()
        }
        super.onResume()
    }


    override fun onPause() {
        if (mPlayer!=null){
            mPlayer!!.release()
        }
        super.onPause()
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
        hashmap.put("funtime_id", funtime.id)
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

    public interface Callback{
        fun funOnCommentDialogDismissed(commentCount:Int)
    }



}

