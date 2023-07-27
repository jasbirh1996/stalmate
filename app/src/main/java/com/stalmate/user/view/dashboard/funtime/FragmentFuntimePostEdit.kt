package com.stalmate.user.view.dashboard.funtime



import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFuntimePostEditBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_ID
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel
import com.stalmate.user.view.singlesearch.ActivitySingleSearch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.stream.Collectors

class FragmentFuntimePostEdit : BaseFragment(), FriendAdapter.Callbackk {
    private var mPlayer: ExoPlayer? = null
    lateinit var binding: FragmentFuntimePostEditBinding
    var taggedPeople = ArrayList<User>()
    lateinit var tagPeopleViewModel: TagPeopleViewModel
    var mVideo = ""
    var city = ""
    var selectedPrivacy = "Public"
    var country = ""
    var mAudioId = ""

    //    var ActivityDashboard : ActivityDashboard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (!::binding.isInitialized) {
            binding = DataBindingUtil.bind<FragmentFuntimePostEditBinding>(
                inflater.inflate(
                    R.layout.fragment_funtime_post_edit,
                    container,
                    false
                )
            )!!
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("asdasdgkn", "xxxxxx")
        tagPeopleViewModel = ViewModelProvider(requireActivity()).get(TagPeopleViewModel::class.java)

        if (requireActivity().intent.getStringExtra(ActivityFilter.EXTRA_VIDEO) != null) {
            mVideo = requireActivity().intent.getStringExtra(ActivityFilter.EXTRA_VIDEO)!!
        }

        if ((requireActivity() as ActivityFuntimePost).isEdit) {
            Log.d("aklsjdasd", "oooooo")
            var funtime = (requireActivity() as ActivityFuntimePost).funtime
            Log.d("aklsjdasd", funtime.tag_user?.size.toString())
            mVideo = funtime.file.toString()
            binding.editor.html=funtime.text


        }



        binding.toolbar.tvhead.text = ""
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()

        }
        setUpEditorbuttons()
        updateButtons()
        try {
            Log.d("asdasdasd", "hjkjh")
            Log.d("asdasdasd", requireActivity().intent.getStringExtra(EXTRA_SONG_ID).toString())
        } catch (e: java.lang.Exception) {

        }
/*
        val bitmap = ThumbnailUtils.createVideoThumbnail(mVideo, MediaStore.Video.Thumbnails.MICRO_KIND)
        Glide.with(requireActivity()).load(bitmap).into(binding.thumbnail)
*/


        Glide.with(requireContext())
            .load(mVideo)
            /*  .apply(requestOptions)*/
            .thumbnail(Glide.with(requireContext()).load(mVideo))
            .into(binding.thumbnail);

        binding.buttonPost.setOnClickListener {
            if ( (requireContext() as ActivityFuntimePost).isEdit){
                editPost()
            }else{
                apiPostReel(File(mVideo))
            }
        }

        tagPeopleViewModel.tagModelLiveData.observe(viewLifecycleOwner) {
            if (it.taggedPeopleList.isNotEmpty() && it.policy==Constants.PRIVACY_TYPE_PUBLIC) {
                taggedPeople = it.taggedPeopleList
            }
        }



    }




    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }

/*    override fun onDestroy() {
        super.onDestroy()
        mPlayer!!.stop(true)
        mPlayer!!.playWhenReady = false
        mPlayer!!.release()
        mPlayer = null
    }*/


    private fun apiPostReel(file: File) {
        showLoader()
        var commaSeparatedStr = ""
        if (taggedPeople.isNotEmpty()) {
            commaSeparatedStr = taggedPeople
                .stream()
                .map {
                    it.id
                }
                .collect(Collectors.joining(","))
        }

        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())
        val thumbnailBody: RequestBody = RequestBody.create("video/*".toMediaTypeOrNull(), file)
        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
            "file",
            file.name,
            thumbnailBody
        ) //image[] for multiple image
        var data = ""
        if (!ValidationHelper.isNull(binding.editor.html)) {
            data = binding.editor.html.toString()
        }
        networkViewModel.postReel(
            access_token = prefManager?.access_token.toString(),
            file = profile_image1,
            cover_image = null,
            file_type = getRequestBody(".mp4"),
            text = getRequestBody(data),
            tag_id = getRequestBody(commaSeparatedStr),
            sound_id = getRequestBody(requireActivity().intent.getStringExtra(EXTRA_SONG_ID).toString()),
            location = getRequestBody(city + ", " + country),
            privacy = getRequestBody(selectedPrivacy),
            privacy_data = getRequestBody(""),
            deviceId = getRequestBody(""),
            deviceToken = getRequestBody("")
        )
        networkViewModel.postReelLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.let {
                dismissLoader()
                if (it!!.status == true) {
                    val intent = Intent(context, ActivityDashboard::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    requireContext().startActivity(intent)
                    (context as Activity).finishAffinity()
                }
            }
        })
    }

    var isBoldActive = false
    var isItalicActive = false
    var isUnderlineActive = false

    fun setUpEditorbuttons() {
        binding.ivBold.setOnClickListener {
            isBoldActive = !isBoldActive
            updateButtons()
            binding.editor.setBold();
        }
        binding.ivItalic.setOnClickListener {
            isItalicActive = !isItalicActive
            updateButtons()
            binding.editor.setItalic();
        }
        binding.ivUnderLine.setOnClickListener {
            isUnderlineActive = !isUnderlineActive
            updateButtons()
            binding.editor.setUnderline();
        }
    }

    fun updateButtons() {

        if (isBoldActive) {
            binding.ivBold.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_post_bold
                )
            )
        } else {
            binding.ivBold.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_post_bold_gray
                )
            )
        }

        if (isItalicActive) {
            binding.ivItalic.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_post_ittalic_blue
                )
            )
        } else {
            binding.ivItalic.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_post_ittalic
                )
            )
        }

        if (isUnderlineActive) {
            binding.ivUnderLine.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_post_underline_blue
                )
            )
        } else {
            binding.ivUnderLine.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_post_underline
                )
            )
        }

    }



    fun editPost(){
        val hashmap = HashMap<String, String>()
        hashmap.put("id", (requireActivity() as ActivityFuntimePost).funtime.id.toString())
        hashmap.put("is_delete", "0")
        var data = ""
        if (!ValidationHelper.isNull(binding.editor.html)) {
            data = binding.editor.html.toString()
        }
        hashmap.put("text", data)
        //hashmap.put("comment_status", "true")
        networkViewModel.funtimUpdate(prefManager?.access_token.toString(),hashmap)
        networkViewModel.funtimeUpdateLiveData.observe(viewLifecycleOwner, Observer {
            it.let {



                if (it!!.status) {
                    val intent = Intent(context, ActivityDashboard::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    requireContext().startActivity(intent)
                    (context as Activity).finishAffinity()
                }

            }
        })
    }



}
