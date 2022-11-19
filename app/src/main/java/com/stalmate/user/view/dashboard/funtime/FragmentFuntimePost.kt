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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFuntimePostBinding
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

class FragmentFuntimePost : BaseFragment(), FriendAdapter.Callbackk {
    private var mPlayer: ExoPlayer? = null
    lateinit var binding: FragmentFuntimePostBinding
    var taggedPeople= ArrayList<User>()
    lateinit var tagPeopleViewModel: TagPeopleViewModel
    var mVideo = ""
    var city=""
    var selectedPrivacy=""
    var country=""
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

        if (!::binding.isInitialized){
            binding = DataBindingUtil.bind<FragmentFuntimePostBinding>(
                inflater.inflate(
                    R.layout.fragment_funtime_post,
                    container,
                    false
                )
            )!!
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("asdasdgkn","xxxxxx")
        tagPeopleViewModel= ViewModelProvider(requireActivity()).get(TagPeopleViewModel::class.java)
        mVideo = requireActivity().intent.getStringExtra(ActivityFilter.EXTRA_VIDEO)!!

        binding.toolbar.tvhead.text=""
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()

        }
        setUpEditorbuttons()
        updateButtons()
        try {
            Log.d("asdasdasd","hjkjh")
            Log.d("asdasdasd",requireActivity().intent.getStringExtra(EXTRA_SONG_ID).toString())
        }catch (e:java.lang.Exception){

        }
        binding.layoutTagPeople.setOnClickListener { findNavController().navigate(R.id.action_fragmentFuntimePost_to_fragmentFuntimeTag) }
/*
        val bitmap = ThumbnailUtils.createVideoThumbnail(mVideo, MediaStore.Video.Thumbnails.MICRO_KIND)
        Glide.with(requireActivity()).load(bitmap).into(binding.thumbnail)
*/


        Glide.with(requireContext())
            .load(mVideo)
          /*  .apply(requestOptions)*/
            .thumbnail(Glide.with(requireContext()).load(mVideo))
            .into(binding.thumbnail);

        binding.buttonPost.setOnClickListener { apiPostReel(File(mVideo)) }
        tagPeopleViewModel.getTaggedPeopleList().observe(viewLifecycleOwner) {
            Log.d("asdasdasd","apsdkpasd")
            Log.d("asdasdasd",it.size.toString())
          if (it.isNotEmpty()){
              binding.tvPeopleCount.text=it.size.toString()+" People"
              binding.tvPeopleCount.visibility=View.VISIBLE
              taggedPeople=it
          }
        }

        binding.layoutAddLocation.setOnClickListener {
            var intentt=Intent(requireContext(), ActivitySingleSearch::class.java)
            intentt.putExtra("type","autoCompleteCountries")
            startActivityForResult(intentt,121)
        }

        binding.layoutPrivacy.setOnClickListener {
            setFragmentResultListener(SELECT_PRIVACY) { key, bundle ->
                clearFragmentResultListener(requestKey = SELECT_PRIVACY)
                selectedPrivacy= bundle.getString(SELECT_PRIVACY) as String
                when(selectedPrivacy){
                    Constants.PRIVACY_TYPE_MY_FOLLOWER->{
                        binding.tvPrivacyData.text="My Followers"

                    }
                    Constants.PRIVACY_TYPE_PRIVATE->{
                        binding.tvPrivacyData.text="Private"
                    }
                    Constants.PRIVACY_TYPE_PUBLIC->{
                        binding.tvPrivacyData.text="Public"
                    }
                    Constants.PRIVACY_TYPE_SPECIFIC->{
                        binding.tvPrivacyData.text="Specific Friends"
                    }

                }







            }
            findNavController().navigate(R.id.action_fragmentFuntimePost_to_FragmentFuntimePrivacyOptions)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode==121){
            city=data!!.getSerializableExtra("city").toString()
            country=data.getSerializableExtra("country").toString()
            binding.tvAddLocation.text=city+","+country
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
        var commaSeparatedStr=""
      if (taggedPeople.isNotEmpty()){
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
        var data=""
        if (!ValidationHelper.isNull(binding.editor.html)){
            data=binding.editor.html.toString()
        }
Log.d(";lasjd;asd",selectedPrivacy)
      networkViewModel.postReel(
            profile_image1,
            getRequestBody(".mp4"),
            getRequestBody(data),
            getRequestBody(commaSeparatedStr),
          getRequestBody(requireActivity().intent.getStringExtra(EXTRA_SONG_ID).toString()),
            getRequestBody(city+", "+country),
            getRequestBody(selectedPrivacy),
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

  var isBoldActive=false
    var isItalicActive=false
    var isUnderlineActive=false

    fun setUpEditorbuttons(){
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

    fun updateButtons(){

        if (isBoldActive){
           binding.ivBold.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_post_bold) )
        }else{
            binding.ivBold.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_post_bold_gray) )
        }

        if (isItalicActive){
            binding.ivItalic.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_post_ittalic_blue) )
        }else{
            binding.ivItalic.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_post_ittalic) )
        }

        if (isUnderlineActive){
            binding.ivUnderLine.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_post_underline_blue) )
        }else{
            binding.ivUnderLine.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.ic_post_underline) )
        }

    }




}

const val SELECT_PRIVACY="type"