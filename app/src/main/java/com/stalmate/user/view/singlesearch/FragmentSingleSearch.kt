package com.stalmate.user.view.singlesearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.LayoutSingleSearchBinding
import java.util.HashMap

class FragmentSingleSearch(var Type:String) : BaseFragment(), SingleSearchAdapter.Callbackk {

    private lateinit var binding : LayoutSingleSearchBinding
    private lateinit var searchAdapter: SingleSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.bind<LayoutSingleSearchBinding>(inflater.inflate(R.layout.layout_single_search, container, false))!!
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAdd.setOnClickListener {
            finishFragment("", binding.etSearch.text.toString())
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (binding.etSearch.text.toString().isNotEmpty()){
                    hitSearchListApi(Type)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
    private fun hitSearchListApi(type : String) {
        /*SetUp Search Adapter*/
        searchAdapter = SingleSearchAdapter(networkViewModel, Type,requireContext(),this )
        binding.rvList.adapter=searchAdapter


        val hashMap = HashMap<String, String>()

        hashMap["search"] = binding.etSearch.text.toString()

        if (type == "graduation") {
            networkViewModel.searchLiveData(hashMap, search = binding.etSearch.text.toString())
            networkViewModel.searchLiveData.observe(this) {
                it?.let {

                    val stateList: ArrayList<ResultSearch> = ArrayList<ResultSearch>()
                    if (it.results.isEmpty()) {

                        var state = ResultSearch(id = "0", name = "No Result Found")
                        binding.buttonAdd.visibility = View.VISIBLE
                        stateList.add(state)
                        searchAdapter.submitList(stateList)

                    } else {
                        binding.buttonAdd.visibility = View.GONE
                        searchAdapter.submitList(it.results)
                    }
                }
            }

        }else if(type == "major"){
            networkViewModel.searchBranchLiveData(hashMap, search = binding.etSearch.text.toString())
            networkViewModel.searchBranchLiveData.observe(this) {
                it?.let {

                    val stateList: ArrayList<ResultSearch> = ArrayList<ResultSearch>()
                    if (it.results.isEmpty()) {

                        var state = ResultSearch(id = "0", name = "No Result Found")
                        stateList.add(state)
                        searchAdapter.submitList(stateList)
                    } else {
                        searchAdapter.submitList(it.results)
                    }
                }
            }
        }
    }

    override fun onClickSearchItem(id: String, name: String) {
        finishFragment(id, name)

    }



    fun finishFragment(id: String, name: String){
        var bundle=Intent()
        bundle.putExtra("id",id)
        bundle.putExtra("name",name)
        bundle.putExtra("type",Type)
     requireActivity().setResult(Activity.RESULT_OK,bundle)
        requireActivity().finish()
    }










}