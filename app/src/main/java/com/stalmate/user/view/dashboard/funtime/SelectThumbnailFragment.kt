package com.stalmate.user.view.dashboard.funtime

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSelectThumbnailBinding
import com.stalmate.user.videoThumbnails.listener.SeekListener
import com.stalmate.user.videoThumbnails.util.ThumbyUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class SelectThumbnailFragment : BaseFragment() {

    companion object {
        const val EXTRA_THUMBNAIL_POSITION = "EXTRA_THUMBNAIL_POSITION"
        const val EXTRA_URI = "EXTRA_URI"
        fun getStartIntent(uri: String, thumbnailPosition: Long = 0): Bundle {
            val intent = Bundle()
            intent.putString(EXTRA_URI, uri)
            intent.putLong(EXTRA_THUMBNAIL_POSITION, thumbnailPosition)
            return intent
        }
    }

    lateinit var binding: FragmentSelectThumbnailBinding
    private val videoUri: Uri
        get() = arguments?.getString(EXTRA_URI).toString().toUri()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.bind<FragmentSelectThumbnailBinding>(
                inflater.inflate(
                    R.layout.fragment_select_thumbnail,
                    container,
                    false
                )
            )!!
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tBar.tvhead.text = "Select Thumbnail"
        binding.tBar.topAppBar.setOnClickListener {
            findNavController().popBackStack()
        }
        setupVideoContent()
        binding.btnDone.setOnClickListener {
            finishWithData()
        }
    }

    private fun setupVideoContent() {
        binding.viewThumbnail.setDataSource(this.requireContext(), videoUri)
        binding.thumbs.seekListener = seekListener
        binding.thumbs.currentSeekPosition =
            (arguments?.getLong(EXTRA_THUMBNAIL_POSITION, 0L) ?: 0L).toFloat()
        binding.thumbs.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.thumbs.viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.thumbs.uri = videoUri
            }
        })
    }

    private fun finishWithData() {
        (requireActivity() as ActivityFuntimePost).location =
            ((binding.viewThumbnail.getDuration() / 100) * binding.thumbs.currentProgress).toLong() * 1000
        ThumbyUtils.getBitmapAtFrame(
            this.requireContext(),
            videoUri,
            (requireActivity() as ActivityFuntimePost).location,
            200,
            200
        )?.let {
            (requireActivity() as ActivityFuntimePost).getImageUri(it).let {
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

    private val seekListener = object : SeekListener {
        override fun onVideoSeeked(percentage: Double) {
            val duration = binding.viewThumbnail.getDuration()
            binding.viewThumbnail.seekTo((percentage.toInt() * binding.viewThumbnail.getDuration()) / 100)
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

            (requireActivity() as ActivityFuntimePost).mVideoCover.value = Uri.fromFile(imageFile).toString()
            findNavController().popBackStack()
        } else {
            // an error occurred
            val exception = result.error
        }
    }

    fun AppCompatActivity.getImageUri(inImage: Bitmap): Uri {
        val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            this.contentResolver, inImage,
            "IMG_$now", null
        )
        return Uri.parse(path)
    }
}