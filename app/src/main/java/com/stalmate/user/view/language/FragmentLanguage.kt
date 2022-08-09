package com.stalmate.user.view.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentLanguageBinding


class FragmentLanguage : BaseFragment(), AdapterLanguage.Callbackk {

    private lateinit var binding : FragmentLanguageBinding
    lateinit var feedAdapter: AdapterLanguage



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_language, container, false)
        binding = DataBindingUtil.bind<FragmentLanguageBinding>(view)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

        binding.progressBar.visibility = View.VISIBLE
        feedAdapter = AdapterLanguage(networkViewModel, requireContext(),this )
        binding.rvLanguage.adapter=feedAdapter
        binding.rvLanguage.layoutManager= GridLayoutManager(requireContext(), 3 )

        networkViewModel.languageLiveData("", HashMap())
        networkViewModel.languageLiveData.observe(requireActivity()) {
            it.let {
                binding.progressBar.visibility = View.GONE
                feedAdapter.submitList(it!!.results)
            }
        }
        setupData()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.fragmentLogin)
        }

    }

    private fun setupData() {

    }


    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.text = getString(R.string.choose_language)
        binding.toolbar.back.visibility = View.GONE
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.backButtonRightText.visibility = View.GONE
        binding.toolbar.menuChat.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }



    override fun onClickLanguageItem(postId: String) {
        makeToast( "abhay")
    }

}