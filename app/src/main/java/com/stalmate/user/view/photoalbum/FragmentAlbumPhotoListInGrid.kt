package com.stalmate.user.view.photoalbum



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAlbumPhotoListInGridBinding
import com.stalmate.user.model.Albums
import java.io.File

class FragmentAlbumPhotoListInGrid : BaseFragment(),  PhotoAdapter.Callback {

    private lateinit var binding : FragmentAlbumPhotoListInGridBinding
    var albumId = ""
    lateinit var photoAdapter: PhotoAdapter
    var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireArguments().getString("albumId") != null) {
            albumId = requireArguments().getString("albumId").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_album_photo_list_in_grid, container, false)
        binding = DataBindingUtil.bind<FragmentAlbumPhotoListInGridBinding>(view)!!
        photoAdapter = PhotoAdapter(networkViewModel, requireContext(),this)
        getAlbumImagelist()


        binding.toolbar.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    private fun getAlbumImagelist() {

        val hashMap = HashMap<String, Any>()
//        hashMap["album_id"] = albumId
//        hashMap["album_id"] = id
        hashMap["user_id"] = prefManager?._id.toString()
        hashMap["limit"] = 100
        hashMap["page"] = 1
        networkViewModel.getAlbumPhotos(prefManager?.access_token.toString(),hashMap)
        networkViewModel.photoLiveData.observe(requireActivity()) {
            it.let {
                binding.rvPhoto.layoutManager= GridLayoutManager(context, 4)
                binding.rvPhoto.adapter=photoAdapter
                photoAdapter.submitList(it!!.results)
            }
        }
    }

    override fun onClickOnPhoto(photo: Albums) {
        val bundle = Bundle()
        bundle.putString("imageId", photo.id)
        bundle.putString("albumId", albumId)
        findNavController().navigate(R.id.action_fragmentAlbumPhotoListInGrid_to_fragmentAlbumFullView,bundle)
    }
}