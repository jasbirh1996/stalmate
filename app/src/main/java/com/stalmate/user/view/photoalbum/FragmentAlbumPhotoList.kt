package com.stalmate.user.view.photoalbum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAlbumPhotoListBinding
import com.stalmate.user.model.Photo

class FragmentAlbumPhotoList : BaseFragment(), PhotoAdapter.Callback {

    private lateinit var binding : FragmentAlbumPhotoListBinding
    lateinit var feedAdapter: PhotoAdapter
    var id=""
    var type=""
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Common ToolBar SetUp*/
        toolbarSetUp()

        if (requireArguments().getString("id")!=null){
           id = requireArguments().getString("id").toString()

            val hashMap = HashMap<String, String>()
            hashMap["album_id"] = id
            networkViewModel.photoLiveData(hashMap)
            networkViewModel.photoLiveData.observe(requireActivity()) {
                it.let {
                    feedAdapter.submitList(it!!.results)
                }
            }
        }
        if (requireArguments().getString("type")!=null){
            type = requireArguments().getString("type").toString()
            hitphotoListApi(type)
        }
        feedAdapter = PhotoAdapter(networkViewModel, requireContext(),this)
        binding.rvPhoto.adapter=feedAdapter

    }



    private fun toolbarSetUp() {
        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text = getString(R.string.albums_photo)
        binding.toolbar.menuChat.visibility = View.GONE
        binding.toolbar.back.setOnClickListener {
          findNavController().popBackStack()
        }
    }


    private fun hitphotoListApi(type: String) {


        val hashMap = HashMap<String, String>()
        hashMap["img_type"] = type
        hashMap["page"] = "1"
        hashMap["limit"] = "25"

        networkViewModel.photoIndexLiveData(hashMap)
        networkViewModel.photoIndexLiveData.observe(viewLifecycleOwner) {
            it.let {

                if (it!!.results.isNotEmpty()) {
                    feedAdapter.submitList(it!!.results)
                }
            }

        }
    }

    override fun onClickOnPhoto(photo: Photo, bindingAdapterPosition: Int) {
        val bundle = Bundle()
        bundle.putString("index", bindingAdapterPosition.toString())
        bundle.putString("type", type)
      findNavController().navigate(R.id.action_fragmentAlbumListing_to_fragmentAlbumFullView,bundle)
    }

}