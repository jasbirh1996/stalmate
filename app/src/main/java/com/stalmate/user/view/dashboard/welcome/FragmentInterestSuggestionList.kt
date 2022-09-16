package com.stalmate.user.view.dashboard.welcome


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.igalata.bubblepicker.rendering.BubblePicker


import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentInterestSuggestionListBinding
import com.stalmate.user.view.adapter.AdapterCategory


class FragmentInterestSuggestionList : BaseFragment(), AdapterCategory.Callbackk{

    lateinit var adapterCategory: AdapterCategory
    lateinit var binding: FragmentInterestSuggestionListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentInterestSuggestionListBinding>(
            inflater.inflate(
                R.layout.fragment_interest_suggestion_list,
                container,
                false
            )
        )!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterCategory = AdapterCategory(networkViewModel, requireContext(),this)
        binding.rvIntrast.adapter=adapterCategory
        binding.rvIntrast.layoutManager= GridLayoutManager(requireContext(), 3)
        networkViewModel.getCategoryList("", HashMap())
        networkViewModel.categoryLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                adapterCategory.submitList(it!!.results)
            }
        })
    }


    override fun onClickIntrastedItem(postId: String, lang: String) {



    }


}


