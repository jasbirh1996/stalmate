package com.slatmate.user.view.language

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.slatmate.user.R
import com.slatmate.user.base.BaseActivity
import com.slatmate.user.base.BaseFragment
import com.slatmate.user.commonadapters.AdapterFeed
import com.slatmate.user.databinding.ActivityProfileBinding
import com.slatmate.user.databinding.FragmentLanguageBinding
import com.slatmate.user.databinding.FragmentLoginBinding
import java.util.Observer

class FragmentLanguage : BaseFragment(), AdapterFeed.Callbackk {

    private lateinit var binding : FragmentLanguageBinding
    lateinit var feedAdapter: AdapterFeed



    override fun onCreate(savedInstanceState: Bundle?) {g
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

        feedAdapter = AdapterFeed(networkViewModel, requireContext(), this)
        binding.rvLanguage.adapter=feedAdapter
        binding.rvLanguage.layoutManager= GridLayoutManager(requireContext(), 3 )

        networkViewModel.getFeedList("", HashMap())
        networkViewModel.feedLiveData.observe(requireActivity()) {
            it.let {
                feedAdapter.submitList(it!!.results)
            }
        }
        setupData()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(com.slatmate.user.R.id.fragmentLogin)
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

    override fun onClickOnViewComments(postId: Int) {

    }

}