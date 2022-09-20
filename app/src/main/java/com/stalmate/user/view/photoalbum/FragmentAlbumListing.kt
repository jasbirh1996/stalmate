package com.stalmate.user.view.photoalbum

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAlbumListingBinding
import com.stalmate.user.databinding.FragmentLoginBinding

class FragmentAlbumListing : BaseFragment(), AlbumAdapter.Callbackk {
    private lateinit var binding: FragmentAlbumListingBinding
    private lateinit var feedAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_album_listing, container, false)

        binding = DataBindingUtil.bind<FragmentAlbumListingBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoader()
        /*Common ToolBar SetUp*/
        toolbarSetUp()

        setUpAdapter()

        binding.btnAddAlbum.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            val viewGroup = view.findViewById<ViewGroup>(android.R.id.content)
            val dialogView: View = LayoutInflater.from(context).inflate(R.layout.create_album_layout, viewGroup, false)
            builder.setView(dialogView)
            val alertDialog = builder.create()


            var btnubmit  = dialogView.findViewById<TextView>(R.id.btnSubmit)
            btnubmit.setOnClickListener {

                var albumName = dialogView.findViewById<EditText>(R.id.etAlbumName)

                if (!albumName.text.isEmpty()) {

                    val hashMap = HashMap<String, String>()
                    hashMap["name"] = albumName.text.toString()

                    showLoader()
                    networkViewModel.createAlbum(hashMap)
                    networkViewModel.createAlbumData.observe(requireActivity()) {

                        it?.let {
                            val message = it.message

                            if (it.status) {

                                dismissLoader()
                                alertDialog.dismiss()
                                makeToast(it.message)
                                setUpAdapter()
                            }
                        }
                    }
                }else{
                    makeToast("Please Add Album Name")
                }
            }

            alertDialog.show()
            alertDialog.setCancelable(true)

        }
    }



    private fun setUpAdapter() {
        feedAdapter = AlbumAdapter(networkViewModel, requireContext(), this)
        binding.rvAlbum.adapter=feedAdapter
        binding.rvAlbum.layoutManager= LinearLayoutManager(requireContext())


        networkViewModel.albumLiveDatas("", HashMap())

        networkViewModel.albumLiveData.observe(requireActivity()) {
            it.let {
                dismissLoader()
                feedAdapter.submitList(it!!.results)
            }
        }
    }


    private fun toolbarSetUp() {
        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text = getString(R.string.albums)
        binding.toolbar.menuChat.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onClickItem(postId: String) {

        val bundle = Bundle()
        bundle.putString("id", postId)
        findNavController().navigate(R.id.fragmentAlbumPhoto, bundle)
    }


}