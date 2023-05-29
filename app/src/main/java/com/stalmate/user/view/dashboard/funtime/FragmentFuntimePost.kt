package com.stalmate.user.view.dashboard.funtime

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anggrayudi.storage.file.forceDelete
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFuntimePostBinding
import com.stalmate.user.model.SelecteThumbnailBottomSheet
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.utils.RealPathUtil
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.funtime.SelectThumbnailFragment.Companion.getStartIntent
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel
import com.stalmate.user.view.singlesearch.ActivitySingleSearch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors


class FragmentFuntimePost : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var binding: FragmentFuntimePostBinding
    var taggedPeople = ArrayList<User>()
    lateinit var tagPeopleViewModel: TagPeopleViewModel
    var mediaUri = ""
    private var isImage: Boolean = false
    private var isEdit: Boolean = false
    var city = ""
    var selectedPrivacy = "Public"
    var country = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!::binding.isInitialized) {
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

    private var launchActivityForImagePickFromGallery =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            result.data?.data?.let {
                if (it.toString().isNotEmpty()) {
                    // start picker to get image for cropping and then use the image in cropping activity
                    cropImage.launch(
                        options(
                            uri = it,
                            builder = {
                                this.setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                            }
                        )
                    )
                }
            }
        }

    private var launchActivityForImageCaptureFromCamera =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    // start picker to get image for cropping and then use the image in cropping activity
                    cropImage.launch(
                        options(
                            uri = (requireActivity() as ActivityFuntimePost).mVideoCover.value?.toUri(),
                            builder = {
                                this.setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                            })
                    )
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

            (requireActivity() as ActivityFuntimePost).mVideoCover.value =
                Uri.fromFile(imageFile).toString()
        } else {
            // an error occurred
            val exception = result.error
        }
    }


    private var fromCameraCover: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tagPeopleViewModel = ViewModelProvider(requireActivity())[TagPeopleViewModel::class.java]
        isEdit = (requireActivity() as ActivityFuntimePost).isEdit
        isImage = (requireActivity() as ActivityFuntimePost).isImage
        mediaUri = (requireActivity() as ActivityFuntimePost).videoUri.toString()

        binding.editor.setOnTextChangeListener {
            if (it.isNullOrEmpty()) {
                binding.llHint.visibility = View.VISIBLE
            } else {
                binding.llHint.visibility = View.GONE
            }
        }

        setUpEditorbuttons()
        updateButtons()

        val thumbnailDialog = SelecteThumbnailBottomSheet(
            fromCamera = {
                if (fromCameraCover != null)
                    fromCameraCover?.forceDelete()
                val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
                fromCameraCover = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Stalmate"),
                    "image_$now.jpg"
                )
                (requireActivity() as ActivityFuntimePost).mVideoCover.value =
                    FileProvider.getUriForFile(
                        this.requireContext().applicationContext,
                        "${this.requireContext().applicationContext.packageName}.provider",
                        fromCameraCover!!
                    ).toString()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    (requireActivity() as ActivityFuntimePost).mVideoCover.value?.toUri()
                )
                launchActivityForImageCaptureFromCamera.launch(cameraIntent)
            },
            fromGallery = {
                val pickerIntent = Intent(Intent.ACTION_PICK)
                pickerIntent.type = "image/*"
                pickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                pickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                launchActivityForImagePickFromGallery.launch(pickerIntent)
            },
            fromVideo = {
                findNavController().navigate(
                    R.id.action_fragmentFuntimePost_to_fragmentSelectThumbnail,
                    getStartIntent(
                        uri = mediaUri,
                        thumbnailPosition = (requireActivity() as ActivityFuntimePost).location
                    )
                )
            },
            fromDismiss = {}
        )

        if (isImage)
            binding.llSelectCover.visibility = View.GONE
        else {
            binding.llSelectCover.visibility = View.VISIBLE
            binding.llSelectCover.setOnClickListener {
                if (thumbnailDialog.isAdded) {
                    return@setOnClickListener
                }
                thumbnailDialog.show(
                    (requireActivity() as ActivityFuntimePost).supportFragmentManager,
                    thumbnailDialog.tag
                )
            }
        }

        if (isEdit) {
            val funtime = (requireActivity() as ActivityFuntimePost).funtime
            mediaUri = funtime.file.toString()
            binding.editor.html = funtime.text
            binding.buttonPost.text = "Ok"
        }

        binding.toolbar.tvhead.text = "Create Funtime Post"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutTagPeople.setOnClickListener { findNavController().navigate(R.id.action_fragmentFuntimePost_to_fragmentFuntimeTag) }

        Glide.with(requireContext())
            .load(mediaUri)
            .thumbnail(Glide.with(requireContext()).load(mediaUri))
            .placeholder(R.drawable.profileplaceholder)
            .error(R.drawable.profileplaceholder)
            .into(binding.thumbnail)

        binding.buttonPost.setOnClickListener {
            if (isEdit) {
                editPost()
            } else {
                apiPostReel()
            }
        }

        tagPeopleViewModel.tagModelLiveData.observe(viewLifecycleOwner) {
            if (it.taggedPeopleList.isNotEmpty()) {
                binding.tvPeopleCount.text = it.taggedPeopleList.size.toString() + " People"
                binding.tvPeopleCount.visibility = View.VISIBLE
                taggedPeople = it.taggedPeopleList
            } else {
                binding.tvPeopleCount.visibility = View.GONE
            }
            setPolicyOnUi(it.policy)
        }

        binding.layoutAddLocation.setOnClickListener {
            val intentt = Intent(requireContext(), ActivitySingleSearch::class.java)
            intentt.putExtra("type", "autoCompleteCountries")
            startActivityForResult(intentt, 121)
        }

        binding.layoutPrivacy.setOnClickListener {
            setFragmentResultListener(SELECT_PRIVACY) { key, bundle ->
                clearFragmentResultListener(requestKey = SELECT_PRIVACY)
                selectedPrivacy = bundle.getString(SELECT_PRIVACY) as String
                setPolicyOnUi(selectedPrivacy)
            }
            findNavController().navigate(R.id.action_fragmentFuntimePost_to_FragmentFuntimePrivacyOptions)
        }

        (requireActivity() as ActivityFuntimePost).mVideoCover.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it)
                .thumbnail(Glide.with(requireContext()).load(it))
                .placeholder(R.drawable.profileplaceholder)
                .error(R.drawable.profileplaceholder)
                .into(binding.thumbnail)
        }
    }


    fun setPolicyOnUi(selectedPrivacyParameter: String) {
        when (selectedPrivacyParameter) {
            Constants.PRIVACY_TYPE_MY_FOLLOWER -> {
                binding.tvPrivacyData.text = "My Followers"

            }
            Constants.PRIVACY_TYPE_PRIVATE -> {
                binding.tvPrivacyData.text = "Private"
            }
            Constants.PRIVACY_TYPE_PUBLIC -> {
                binding.tvPrivacyData.text = "Public"
            }
            Constants.PRIVACY_TYPE_SPECIFIC -> {
                binding.tvPrivacyData.text = "Specific Friends"
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 121) {
            city = data!!.getSerializableExtra("city").toString()
            country = data.getSerializableExtra("country").toString()
            binding.tvAddLocation.text = city + "," + country
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

    private fun String.getRequestBody(): RequestBody =
        RequestBody.create("text/plain".toMediaTypeOrNull(), this)

    private fun File.getMultipartBody(
        keyName: String,
        type: String
    ): MultipartBody.Part? {
        return try {
            MultipartBody.Part.createFormData(
                keyName,
                this.name,
                this.asRequestBody(type.toMediaTypeOrNull())
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun apiPostReel() {
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

        val mediaUriRealPath =
            RealPathUtil.getRealPath(this.requireContext(), mediaUri.toUri()).toString()


        val mediaFile = try {
            File(
                mediaUriRealPath
            ).getMultipartBody(
                keyName = "file",
                type = (requireActivity() as ActivityFuntimePost).mimeType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        val mediaFileThumIcon =
            try {
                File(
                    if (isImage)
                        mediaUriRealPath
                    else
                        RealPathUtil.getRealPath(
                            this.requireContext(),
                            (requireActivity() as ActivityFuntimePost).mVideoCover.value.toString()
                                .toUri()
                        ).toString()
                ).getMultipartBody(
                    keyName = "thum_icon",
                    type = "image/*"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        val mediaFileCover =
            try {
                File(
                    if (isImage)
                        mediaUriRealPath
                    else
                        RealPathUtil.getRealPath(
                            this.requireContext(),
                            (requireActivity() as ActivityFuntimePost).mVideoCover.value.toString()
                                .toUri()
                        ).toString()
                ).getMultipartBody(
                    keyName = "cover_image",
                    type = "image/*"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        var data = ""
        if (!ValidationHelper.isNull(binding.editor.html)) {
            data = binding.editor.html.toString()
        }

        networkViewModel.postReel(
            access_token = prefManager?.access_token.toString(),
            file = mediaFile,
            thum_icon = mediaFileThumIcon,
            cover_image = mediaFileCover,
            file_type = (requireActivity() as ActivityFuntimePost).mimeType.getRequestBody(),
            text = data.getRequestBody(),
            tag_id = commaSeparatedStr.getRequestBody(),
            sound_id = "none".getRequestBody(),
            location = "$city, $country".getRequestBody(),
            privacy = selectedPrivacy.getRequestBody(),
            privacy_data = "none".getRequestBody(),
            deviceId = "12345".getRequestBody(),
            deviceToken = "54321".getRequestBody()
        )
        networkViewModel.mThrowable.observe(viewLifecycleOwner) {
            dismissLoader()
            Toast.makeText(this.requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        networkViewModel.postReelLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dismissLoader()
            it.let {
                if (it?.status == true) {
                    val intent = Intent(context, ActivityDashboard::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    requireContext().startActivity(intent)
                    (context as Activity).finishAffinity()
                }
            }
        })
    }

    private var isBoldActive = false
    private var isItalicActive = false
    private var isUnderlineActive = false

    private fun setUpEditorbuttons() {
        binding.ivBold.setOnClickListener {
            isBoldActive = !isBoldActive
            updateButtons()
            binding.editor.setBold()
        }
        binding.ivItalic.setOnClickListener {
            isItalicActive = !isItalicActive
            updateButtons()
            binding.editor.setItalic()
        }
        binding.ivUnderLine.setOnClickListener {
            isUnderlineActive = !isUnderlineActive
            updateButtons()
            binding.editor.setUnderline()
        }
    }

    private fun updateButtons() {
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


    private fun editPost() {
        val hashmap = HashMap<String, String>()
        hashmap["id"] = (requireActivity() as ActivityFuntimePost).funtime.id.toString()
        hashmap["is_delete"] = "0"
        var data = ""
        if (!ValidationHelper.isNull(binding.editor.html)) {
            data = binding.editor.html.toString()
        }
        hashmap["text"] = data
        networkViewModel.funtimUpdate(hashmap)
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

const val SELECT_PRIVACY = "type"