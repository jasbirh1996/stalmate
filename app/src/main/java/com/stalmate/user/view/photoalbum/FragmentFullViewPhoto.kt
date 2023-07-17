package com.stalmate.user.view.photoalbum


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFullViewAlbumBinding
import com.stalmate.user.view.photoalbum.imageshowindex.AdapterPhotoIndex

class FragmentFullViewPhoto : BaseFragment() {
    var currentIndex = 0
    var imageId = ""
    var albumId = ""
    var isFreshApi = true

    private lateinit var indexPhotoAdapter: AdapterPhotoIndex
    private lateinit var binding: FragmentFullViewAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireArguments().getString("albumId") != null) {
            albumId = requireArguments().getString("albumId").toString()
        }
        if (requireArguments().getString("imageId") != null) {
            imageId = requireArguments().getString("imageId").toString()
        }
        indexPhotoAdapter = AdapterPhotoIndex(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_full_view_album, container, false)
        binding = DataBindingUtil.bind<FragmentFullViewAlbumBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/

        binding.viewpager.adapter = indexPhotoAdapter
        hitphotoListApi()

        binding.back.setOnClickListener {
            requireActivity().finish()

           findNavController().popBackStack()
        }


        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (indexPhotoAdapter.onList.isNotEmpty()) {
                    if (binding.viewpager.currentItem == binding.viewpager.adapter?.itemCount?.minus(1)
                    ) {
                        isFreshApi = false
                        hitphotoListApi()
                    }
                }
            }
        })
    }

    private fun hitphotoListApi() {
        val hashMap = HashMap<String, String>()
        hashMap["album_id"] = albumId
        hashMap["limit"]="1"
        if (!isFreshApi) {
            currentIndex++
            hashMap["page"] = currentIndex.toString()
        }else{
            hashMap["img_id"] = imageId
        }


        networkViewModel.getAlbumPhotos(hashMap)
        networkViewModel.photoLiveData.observe(requireActivity()) {
            it.let {
                if (!it?.results.isNullOrEmpty()) {
                    if (isFreshApi) {
                        currentIndex= it?.position?:0
                        it?.results?.let { it1 -> indexPhotoAdapter.setList(it1) }
                    } else {
                        it?.results?.let { it1 -> indexPhotoAdapter.addToList(it1) }
                    }
                }
            }
        }
    }
}