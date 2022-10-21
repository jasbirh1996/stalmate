package com.stalmate.user.view.dashboard.funtime

import android.app.Activity
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.TaggedUsersAdapter
import com.stalmate.user.databinding.FragmentFuntimePostBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.modules.reels.utils.VideoUtil
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboard
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FragmentFuntimePost : BaseFragment(), FriendAdapter.Callbackk {
    private var mPlayer: ExoPlayer? = null
    lateinit var binding: FragmentFuntimePostBinding
    lateinit var peopleAdapter: TaggedUsersAdapter
    var mVideo = ""
    var mAudioId = ""
//    var activityDashboard : ActivityDashboard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFuntimePostBinding>(
            inflater.inflate(
                R.layout.fragment_funtime_post,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVideo = requireActivity().intent.getStringExtra(ActivityFilter.EXTRA_VIDEO)!!
    /*    peopleAdapter = TaggedUsersAdapter(taggedPeopleViewModel, requireContext())*/
        binding.layoutTagPeople.setOnClickListener { findNavController().navigate(R.id.action_fragmentFuntimePost_to_fragmentFuntimeTag) }
        val bitmap = ThumbnailUtils.createVideoThumbnail(mVideo, MediaStore.Video.Thumbnails.MICRO_KIND)
        Glide.with(requireActivity()).load(bitmap).into(binding.thumbnail)
        binding.buttonPost.setOnClickListener { apiPostReel(File(mVideo)) }


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
        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

        val thumbnailBody: RequestBody = RequestBody.create("video/*".toMediaTypeOrNull(), file)
        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
            "file",
            file.name,
            thumbnailBody
        ) //image[] for multiple image
        networkViewModel.postReel(
            profile_image1,
            getRequestBody(".mp4"),
            getRequestBody(binding.etPostData.text.toString()),
            getRequestBody(""),
            getRequestBody("Noida"),
            getRequestBody(""),
            getRequestBody(""),
            getRequestBody(""),
            getRequestBody("")

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
}