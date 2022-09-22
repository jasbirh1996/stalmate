package com.stalmate.user.view.photoalbum

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAlbumPhotoIdListingBinding
import com.stalmate.user.databinding.FragmentAlbumPhotoListBinding
import com.stalmate.user.model.Photo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FragmentAlbumPhotoIdListing : BaseFragment(),  PhotoAdapter.Callback {

    private lateinit var binding : FragmentAlbumPhotoIdListingBinding
    var id = ""
    lateinit var photoAdapter: PhotoAdapter
    var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireArguments().getString("id") != null) {
            id = requireArguments().getString("id").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_album_photo_id_listing, container, false)
        binding = DataBindingUtil.bind<FragmentAlbumPhotoIdListingBinding>(view)!!
        photoAdapter = PhotoAdapter(networkViewModel, requireContext(),this)

        Log.d("ajkca", id)

        getAlbumImagelist()


        binding.albumcreateImage.setOnClickListener {
            startCrop()
        }

        return binding.root
    }

    private fun getAlbumImagelist() {

        val hashMap = HashMap<String, String>()
        hashMap["album_id"] = ""
        networkViewModel.photoLiveData(hashMap)
        networkViewModel.photoLiveData.observe(requireActivity()) {
            it.let {
                binding.rvPhoto.layoutManager= GridLayoutManager(context, 4)
                binding.rvPhoto.adapter=photoAdapter
                photoAdapter.submitList(it!!.results)
            }
        }

    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    override fun onClickOnPhoto(photo: Photo, bindingAdapterPosition: Int) {

    }

    /*Cover Image Picker */
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            var uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            imageFile = File(result.getUriFilePath(requireContext(), true)!!)

            updateProfileImageApiHit()

        } else {
            // an error occurred
            val exception = result.error
        }
    }

    private fun updateProfileImageApiHit() {

        val thumbnailBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)

        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData("files",
            imageFile!!.name,
            thumbnailBody
        ) //image[] for multiple image
        val hashMap = HashMap<String, String>()
        hashMap["album_id"] = id
        hashMap["files[]"] = profile_image1.toString()

        networkViewModel.uploadAlbumImageApi(profile_image1)
        networkViewModel.UplodedAlbumImageLiveData.observe(this, Observer {
            it.let {
                makeToast(it!!.message)
                var hashMap = java.util.HashMap<String, String>()
                networkViewModel.getProfileData(hashMap)
                getAlbumImagelist()
            }
        })
    }

}