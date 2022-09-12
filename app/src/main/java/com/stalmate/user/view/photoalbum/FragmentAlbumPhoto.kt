package com.stalmate.user.view.photoalbum

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAlbumListingBinding
import com.stalmate.user.databinding.FragmentAlbumPhotoBinding
import com.stalmate.user.view.language.AdapterLanguage

class FragmentAlbumPhoto : BaseFragment() {

    private lateinit var binding : FragmentAlbumPhotoBinding
    lateinit var feedAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_album_photo, container, false)
        binding = DataBindingUtil.bind<FragmentAlbumPhotoBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Common ToolBar SetUp*/
        toolbarSetUp()
        val id = requireArguments().getString("id").toString()

        feedAdapter = PhotoAdapter(networkViewModel, requireContext())
        binding.rvPhoto.adapter=feedAdapter

        val hashMap = HashMap<String, String>()

        hashMap["album_id"] = id

        networkViewModel.photoLiveData(hashMap)
        networkViewModel.photoLiveData.observe(requireActivity()) {
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        }
    }



    private fun toolbarSetUp() {
        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text = getString(R.string.albums_photo)
        binding.toolbar.menuChat.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}