package com.stalmate.user.view.dashboard.funtime

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentFuntimePrivacyOptionsBinding
import com.stalmate.user.utilities.Constants


class FragmentFuntimePrivacyOptions : Fragment() {
    var selectedType=""
    lateinit var binding: FragmentFuntimePrivacyOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        var view = inflater.inflate(R.layout.fragment_funtime_privacy_options, container, false)

        binding = DataBindingUtil.bind<FragmentFuntimePrivacyOptionsBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        proceed(Constants.PRIVACY_TYPE_MY_FOLLOWER)
        binding.layoutMyFollower.setOnClickListener {
            proceed(Constants.PRIVACY_TYPE_MY_FOLLOWER)
        }

        binding.layoutPublic.setOnClickListener {
            proceed(Constants.PRIVACY_TYPE_PUBLIC)
        }

        binding.layoutSpecific.setOnClickListener {
            proceed(Constants.PRIVACY_TYPE_SPECIFIC)
        }
        binding.layoutPrivate.setOnClickListener {
            proceed(Constants.PRIVACY_TYPE_PRIVATE)
        }
        binding.buttonOk.setOnClickListener {
            var bundle = Bundle()
            bundle.putString(SELECT_PRIVACY, selectedType)
            setFragmentResult(
                SELECT_PRIVACY, bundle
            )
            findNavController().popBackStack()
        }

        binding.toolbar.tvhead.text = "Privacy"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun proceed(type: String) {
        selectedType=type
        selectedOption(type)



    }



    fun selectedOption(type: String){
       when(type){
           Constants.PRIVACY_TYPE_MY_FOLLOWER->{
               binding.layoutMyFollower.background=ContextCompat.getDrawable(requireContext(),R.drawable.primary_small_corner_border)
               binding.layoutPrivate.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutPublic.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutSpecific.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
           }
           Constants.PRIVACY_TYPE_PRIVATE->{
               binding.layoutMyFollower.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutPrivate.background=ContextCompat.getDrawable(requireContext(),R.drawable.primary_small_corner_border)
               binding.layoutPublic.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutSpecific.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
           }
           Constants.PRIVACY_TYPE_PUBLIC->{
               binding.layoutMyFollower.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutPrivate.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutPublic.background=ContextCompat.getDrawable(requireContext(),R.drawable.primary_small_corner_border)
               binding.layoutSpecific.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
           }
           Constants.PRIVACY_TYPE_SPECIFIC->{
               binding.layoutMyFollower.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutPrivate.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutPublic.background=ContextCompat.getDrawable(requireContext(),R.drawable.white_small_corner_border)
               binding.layoutSpecific.background=ContextCompat.getDrawable(requireContext(),R.drawable.primary_small_corner_border)
           }
       }
    }




}