package com.stalmate.user.view.photoalbum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAlbumPhotoListBinding
import com.stalmate.user.model.Photo

class FragmentAlbumPhotoList : BaseFragment(), PhotoAdapter.Callback, AlbumAdapter.Callbackk {

    private lateinit var binding : FragmentAlbumPhotoListBinding
    lateinit var photoAdapter: PhotoAdapter
    private lateinit var albumAdapter: AlbumAdapter

    var albumId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_album_photo_list, container, false)
        binding = DataBindingUtil.bind<FragmentAlbumPhotoListBinding>(view)!!

        binding.photoTabs.setBackgroundColor(R.drawable.button_album_select_unselect_background);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Common ToolBar SetUp*/
        toolbarSetUp()
        albumAdapter = AlbumAdapter(networkViewModel, requireContext(), this)
        /*button color*/
        binding.photoTabs.setBackgroundColor(getResources().getColor(R.color.app_color))
        binding.photoAlbum.setTextColor(resources.getColor(R.color.white))
        binding.photoalbumImage.setImageResource(R.drawable.ic_album_photo_white);

        binding.albumtab.setBackgroundColor(getResources().getColor(R.color.white))
        binding.albumfoldertext.setTextColor(resources.getColor(R.color.black))
        binding.albumsfolder.setImageResource(R.drawable.ic_album_album_gray);


        binding.PhotoCard.setOnClickListener {
            binding.photoTabs.setBackgroundColor(getResources().getColor(R.color.app_color))
            binding.photoAlbum.setTextColor(resources.getColor(R.color.white))
            binding.photoalbumImage.setImageResource(R.drawable.ic_album_photo_white)
            hitphotoListApi()
            binding.albumtab.setBackgroundColor(getResources().getColor(R.color.white))
            binding.albumfoldertext.setTextColor(resources.getColor(R.color.black))
            binding.albumsfolder.setImageResource(R.drawable.ic_album_album_gray);
            binding.createAlbumbtn.visibility=View.GONE
        }

        binding.albumPhotCard.setOnClickListener {
            binding.createAlbumbtn.visibility = View.VISIBLE
            binding.albumtab.setBackgroundColor(getResources().getColor(R.color.app_color))
            binding.albumfoldertext.setTextColor(resources.getColor(R.color.white))
            binding.albumsfolder.setImageResource(R.drawable.ic_album_album_white);
            setUpAdapter()
            binding.photoTabs.setBackgroundColor(getResources().getColor(R.color.white))
            binding.photoAlbum.setTextColor(resources.getColor(R.color.black))
            binding.photoalbumImage.setImageResource(R.drawable.ic_album_photo_gray)
        }

        if (requireArguments().getString("albumId")!=null){
            albumId = requireArguments().getString("albumId").toString()
            hitphotoListApi()
        }
        if (requireArguments().getString("type")!=null){
            if (requireArguments().getString("type").toString()=="albums"){
                binding.albumPhotCard.performClick()
            }
        }
        photoAdapter = PhotoAdapter(networkViewModel, requireContext(),this)

        binding.createAlbumbtn.setOnClickListener {
            findNavController().navigate(R.id.fragmentCreateAlbum)
        }



    }

    private fun toolbarSetUp() {
        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text = getString(R.string.albums_photo)
        binding.toolbar.menuChat.visibility = View.GONE
        binding.toolbar.back.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun hitphotoListApi() {
        val hashMap = HashMap<String, String>()
        hashMap["album_id"] = albumId
        networkViewModel.getAlbumPhotos(hashMap)
        networkViewModel.photoLiveData.observe(requireActivity()) {
            it.let {
                binding.rvPhoto.layoutManager= GridLayoutManager(context, 4)
                binding.rvPhoto.adapter=photoAdapter
                photoAdapter.submitList(it!!.results)
            }
        }
    }



    private fun setUpAdapter() {
        networkViewModel.albumLiveDatas("", HashMap())
        networkViewModel.albumLiveData.observe(requireActivity()) {
            it.let {
                binding.rvPhoto.layoutManager= GridLayoutManager(context, 3)
                binding.rvPhoto.adapter=albumAdapter
                albumAdapter.submitList(it!!.results)
            }
        }
    }

    override fun onClickItem(postId: String) {
        val bundle = Bundle()
        Log.d("ajkshdkasd",postId)
        bundle.putString("albumId", postId)
        findNavController().navigate(R.id.action_fragmentAlbumPhoto_to_fragmentAlbumPhotoIdListing,bundle)

    }

    override fun onClickOnPhoto(photo: Photo) {

        val bundle = Bundle()
        bundle.putString("imageId", photo.id)
        bundle.putString("albumId", albumId)
        findNavController().navigate(R.id.action_fragmentAlbumListing_to_fragmentAlbumFullView,bundle)

    }

}