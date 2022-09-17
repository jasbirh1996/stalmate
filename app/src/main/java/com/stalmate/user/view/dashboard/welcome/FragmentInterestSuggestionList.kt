package com.stalmate.user.view.dashboard.welcome


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentInterestSuggestionListBinding
import com.stalmate.user.model.Category
import com.stalmate.user.model.SelectedList
import com.stalmate.user.view.adapter.AdapterCategory


class FragmentInterestSuggestionList : BaseFragment(), AdapterCategory.Callbackk{

    lateinit var adapterCategory: AdapterCategory
    lateinit var binding: FragmentInterestSuggestionListBinding

    var datass = ""

    var list = ArrayList<Category>()


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
                list.addAll(it!!.results)
                adapterCategory.submitList(list)
            }
        })

    }

    override fun onClickIntrastedItem(data: ArrayList<Category>) {

        datass = data.toString()

    }


    fun isvalid() : Boolean
    {
        if (datass.isEmpty()){
            makeToast("ajkcnackn")
            return false
        }
        return true
    }



   fun getSelectedDAta(): ArrayList<String> {
       return adapterCategory.getSelected()
    }

}


