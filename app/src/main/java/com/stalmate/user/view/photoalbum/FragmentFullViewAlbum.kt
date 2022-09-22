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

class FragmentFullViewAlbum : BaseFragment() {
    var currentIndex = 0
    var type = ""
    var isFreshApi = true

    private lateinit var indexPhotoAdapter: AdapterPhotoIndex
    private lateinit var binding: FragmentFullViewAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireArguments().getString("index") != null) {
            currentIndex = requireArguments().getString("index").toString().toInt()
        }
        if (requireArguments().getString("type") != null) {
            type = requireArguments().getString("type").toString()
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
        currentIndex++
        if (type=="albums_img"){

            val hashMap = HashMap<String, String>()
            hashMap["album_id"] = ""
            networkViewModel.photoLiveData(hashMap)
            networkViewModel.photoLiveData.observe(requireActivity()) {
                it.let {

                    if (it!!.results.isNotEmpty()) {
                        if (isFreshApi) {
                            indexPhotoAdapter.setList(it.results)
                        } else {
                            indexPhotoAdapter.addToList(it.results)
                        }
                    }
                }
            }

        }else {

            val hashMap = HashMap<String, String>()
            hashMap["img_type"] = type
            hashMap["page"] = currentIndex.toString()
            hashMap["limit"] = "1"

            networkViewModel.photoIndexLiveData(hashMap)
            networkViewModel.photoIndexLiveData.observe(viewLifecycleOwner) {
                it.let {


                    if (it!!.results.isNotEmpty()) {

                        if (isFreshApi) {
                            indexPhotoAdapter.setList(it!!.results)
                        } else {
                            indexPhotoAdapter.addToList(it!!.results)
                        }
                    }
                }
            }
        }
    }
}