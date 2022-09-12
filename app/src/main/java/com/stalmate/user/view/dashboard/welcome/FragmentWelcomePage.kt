package com.stalmate.user.view.dashboard.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentInformationSuggestionsBinding
import com.stalmate.user.databinding.FragmentWelcomePageBinding

class FragmentWelcomePage : Fragment() {

    private lateinit var binding : FragmentWelcomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_welcome_page, container, false)
        binding = DataBindingUtil.bind<FragmentWelcomePageBinding>(view)!!

        Glide.with(requireActivity()).asGif().load( R.raw.welcome_gif).into(binding.appCompatImageView11)

        return binding.root
    }

}