package com.stalmate.user.view.singlesearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.LayoutSingleSearchBinding
import com.stalmate.user.utilities.ValidationHelper
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


      //  hitSearchListApi(Type)

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.itemView.setOnClickListener {
            finishFragment("", searchData)
        }

    }
    private fun hitSearchListApi(type : String) {
        /*SetUp Search Adapter*/
        searchAdapter = SingleSearchAdapter(networkViewModel, Type,requireContext(),this )
        binding.rvList.adapter=searchAdapter


        val hashMap = HashMap<String, String>()

        hashMap["search"] = searchData

        if (type == "graduation") {
            networkViewModel.searchLiveData(hashMap, search = searchData)
            networkViewModel.searchLiveData.observe(this) {
                it?.let {

                    val stateList: ArrayList<ResultSearch> = ArrayList<ResultSearch>()
                    if (it.results.isEmpty()) {
                        binding.itemView.visibility = View.VISIBLE
                        searchAdapter.submitList(stateList)
                    } else {

                        searchAdapter.submitList(it.results)
                    }
                }
            }

        }else if(type == "major"){
            networkViewModel.searchBranchLiveData(hashMap, search =searchData)
            networkViewModel.searchBranchLiveData.observe(this) {
                it?.let {

                    val stateList: ArrayList<ResultSearch> = ArrayList<ResultSearch>()
                    if (it.results.isEmpty()) {
                        binding.itemView.visibility = View.VISIBLE
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



    var searchData=""
    fun search(searchData:String){
       this.searchData=searchData
        if (!ValidationHelper.isNull(searchData)){
            hitSearchListApi(Type)
            Log.d("asdasdasd","notempty")
            binding.rvList.visibility=View.VISIBLE
            binding.itemView.visibility=View.GONE
        }else{
            binding.tvValue.text ="You Want Add"+ " "+searchData
            binding.itemView.visibility=View.VISIBLE
            binding.rvList.visibility=View.GONE

            Log.d("asdasdasd","empty")
        }
    }







}