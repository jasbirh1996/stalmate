package com.stalmate.user.view.photoalbum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentCreateAlbumBinding

class FragmentCreateAlbum : BaseFragment() {

    private lateinit var binding : FragmentCreateAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_create_album, container, false)
        binding = DataBindingUtil.bind<FragmentCreateAlbumBinding>(view)!!

        binding.createAlbumBtn.setOnClickListener {
            if (binding.etalbumname.text!!.isNotEmpty()){
                val hashMap = HashMap<String, String>()
                hashMap["name"] = binding.etalbumname.text.toString()

                networkViewModel.createAlbum(hashMap)
                networkViewModel.createAlbumData.observe(requireActivity()) {

                    it?.let {
                        if (it.status) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }else{
                makeToast(getString(R.string.please_enter_album_name))
            }
        }
        return binding.root
    }

}