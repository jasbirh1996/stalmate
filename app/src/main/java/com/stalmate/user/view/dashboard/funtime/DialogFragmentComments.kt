package com.stalmate.user.view.dashboard.funtime


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.file.forceDelete
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.databinding.FragmentCommentsBinding
import com.stalmate.user.model.Comment
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.viewmodel.AppViewModel
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DialogFragmentComments(
    var networkViewModel: AppViewModel,
    var funtime: ResultFuntime,
    var callBack: Callback
) :
    DialogFragment(), CommentAdapter.Callback, CommentAdapterNew.Callback {
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
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
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


    //  lateinit var viewModel: CommentViewModel
    lateinit var commentAdapter: CommentAdapterNew
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvList)
        //  viewModel = ViewModelProvider(this)[CommentViewModel::class.java]
        commentAdapter =
            CommentAdapterNew(
                requireContext(),
                this,
                networkViewModel,
                funtime.id,
                this,
                this,
                PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token.toString()
            )
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

        binding.toolbar.tvhead.text = "Comments"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            callBack.funOnCommentDialogDismissed(commentAdapter.commentList.size)
            dismiss()
        }

        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            val view =
                binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (!isLastPage) {
                    // loadMoreItems()
                }
            }
        })

        val emojiconEditText: hani.momanii.supernova_emoji_library.emojiHelper.EmojiconEditText =
            binding.etComment
        val emojiIcon = EmojIconActions(
            this.requireContext(),
            binding.clMain,
            emojiconEditText,
            binding.appCompatImageView7
        )
//        emojiIcon.setUseSystemEmoji(true);
//        binding.etComment.setUseSystemDefault(true)
        emojiIcon.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {}
            override fun onKeyboardClose() {}
        })

        binding.appCompatImageView6.setOnClickListener {
            if (fromCameraCover != null)
                fromCameraCover?.forceDelete()
            val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
            fromCameraCover = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Stalmate"),
                "image_$now.jpg"
            )
            fromCameraCoverUri =
                FileProvider.getUriForFile(
                    this.requireContext().applicationContext,
                    "${this.requireContext().applicationContext.packageName}.provider",
                    fromCameraCover!!
                ).toString()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                fromCameraCoverUri?.toUri()
            )
            launchActivityForImageCaptureFromCamera.launch(cameraIntent)
        }
        binding.appCompatImageView7.setOnClickListener {
            emojiIcon.showPopup()
        }

        binding.tvPOstButton.setOnClickListener {
            if (commentOverId != "") {
                commentAdapter.replyOverComment(
                    binding.etComment.text.toString(),
                    commentOverId,
                    parentPosition,
                    childPosition,
                    isReplyisChildComment,
                    PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token.toString()
                )
            } else {
                //fromCameraCoverUri
                commentAdapter.addComment(
                    binding.etComment.text.toString(),
                    PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token.toString()
                )
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
        Glide.with(requireContext())
            .load(PrefManager.getInstance(requireContext())!!.userDetail.results?.profile_img_1)
            .placeholder(R.drawable.user_placeholder).circleCrop()
            .into(binding.ivUserImage)

        Glide.with(requireContext()).load(funtime.profile_img)
            .placeholder(R.drawable.user_placeholder).circleCrop()
            .into(binding.ivMainUserImage)


        binding.tvComment.text = funtime.text


        /*  val text = "<font color=#000000>${funtime.first_name+" "+funtime.last_name+" "}</font> <font color=#0f53b8>${funtime.text} </font>"
          binding.tvUserName.text= Html.fromHtml(text)*/

        binding.tvUserName.text = "${funtime.first_name + " " + funtime.last_name + " "}"
        binding.tvDate.text = funtime.Created_date

        binding.etComment.setHint(
            "Comment as ${PrefManager.getInstance(requireContext())!!.userDetail.results?.first_name} ${
                PrefManager.getInstance(
                    requireContext()
                )!!.userDetail.results?.last_name
            }"
        )
        hitApi(true)
    }

    var fromCameraCover: File? = null
    var fromCameraCoverUri: String? = ""
    private var launchActivityForImageCaptureFromCamera =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    // start picker to get image for cropping and then use the image in cropping activity
                    fromCameraCoverUri?.toUri()?.let {
                        cropImage.launch(
                            options(
                                uri = fromCameraCoverUri?.toUri(),
                                builder = {
                                    this.setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                                })
                        )
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // User Cancelled the action
                }
                else -> {
                    // Error
                }
            }
        }

    /*Cover Image Picker */
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            val imageFile = File(result.getUriFilePath(requireContext(), true)!!)
            Log.d("imageUrl======", uriContent.toString())
            Log.d("imageUrl======", uriFilePath.toString())

            fromCameraCoverUri = Uri.fromFile(imageFile).toString()

            Glide.with(binding.ivCommentImage.context)
                .load(fromCameraCoverUri)
                .apply(RequestOptions())
                .thumbnail(Glide.with(this.requireContext()).load(fromCameraCoverUri))
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder)
                .into(binding.ivCommentImage)

            binding.ivCommentImage.visibility = View.VISIBLE
            binding.ivDeleteCommentImage.visibility = View.VISIBLE
            binding.ivDeleteCommentImage.setOnClickListener {
                binding.ivCommentImage.visibility = View.GONE
                binding.ivDeleteCommentImage.visibility = View.GONE
                fromCameraCover = null
                fromCameraCoverUri = ""
            }
        } else {
            // an error occurred
            val exception = result.error
        }
    }

    fun hideImageView() {
        binding.ivCommentImage.visibility = View.GONE
        binding.ivDeleteCommentImage.visibility = View.GONE
        fromCameraCover = null
        fromCameraCoverUri = ""
        binding.etComment.setText("")
    }


    fun Calendar.toSimpleDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(time)
    }


    override fun onDestroy() {
        callBack.funOnCommentDialogDismissed(commentAdapter.commentList.size)
        super.onDestroy()
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
        hashmap.put("page", currentPage.toString())
        networkViewModel.getCommentList(
            PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token.toString(),
            hashmap
        )
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
                        it.replies = ArrayList<Comment>()
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
        isReplyisChildComment = isChild
    }

    override fun onClickOnViewMoreReply(shortComment: Comment, position: Int) {
        //  viewReplies(shortComment._id, position)
    }

    override fun onCommentAddedSucessfully() {
        binding.etComment.setText("")
    }

    public interface Callback {
        fun funOnCommentDialogDismissed(commentCount: Int)
    }


}



