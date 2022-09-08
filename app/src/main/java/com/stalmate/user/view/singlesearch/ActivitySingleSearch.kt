package com.stalmate.user.view.singlesearch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivitySingleSearchBinding
import com.stalmate.user.view.language.AdapterLanguage
import java.util.HashMap

class ActivitySingleSearch : BaseActivity(), SearchAdapter.Callbackk, SearchUnivercityAdapter.Callbackk {

    private lateinit var binding : ActivitySingleSearchBinding

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchUnivercityAdapter: SearchUnivercityAdapter

    var Type : String =""

    override fun onClick(viewId: Int, view: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this, R.layout.activity_single_search)

        /*here get type for hit search api*/

        Type = intent.getSerializableExtra("TYPE").toString()

        Log.d("tancknak", Type.toString())

        binding.ivBack.setOnClickListener {
            onBackPressed()
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
        searchAdapter = SearchAdapter(networkViewModel, this,this )
        binding.rvSearch.adapter=searchAdapter

        val hashMap = HashMap<String, String>()

        hashMap["search"] = binding.etSearch.text.toString()

        if (type == "graduation") {
            networkViewModel.searchLiveData(hashMap, search = binding.etSearch.text.toString())
            networkViewModel.searchLiveData.observe(this) {
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

        }else if(type == "major"){
            networkViewModel.searchBranchLiveData(hashMap, search = binding.etSearch.text.toString())
            networkViewModel.searchBranchLiveData.observe(this) {
                it?.let {

                    val stateList: ArrayList<ResultSearch> = ArrayList<ResultSearch>()
                    if (it.results.isEmpty()) {

                        var state = ResultSearch(id = "0", name = "No Result Found")
                        stateList.add(state)
                        searchUnivercityAdapter.submitList(stateList)
                    } else {
                        searchAdapter.submitList(it.results)
                    }
                }
            }
        }
    }

    override fun onClickSearchItem(postId: String, name: String) {


        var intent =Intent()
        intent.putExtra("postId",postId)
        intent.putExtra("name",name)
        intent.putExtra("type",Type)
        setResult(Activity.RESULT_OK,intent)
        finish()

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onClickSearchUnivercityItem(id: String, name: String) {


        var intent =Intent()
        intent.putExtra("postId",id)
        intent.putExtra("name",name)
        intent.putExtra("type",Type)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}