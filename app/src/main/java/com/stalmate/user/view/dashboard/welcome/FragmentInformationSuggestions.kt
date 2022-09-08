package com.stalmate.user.view.dashboard.welcome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentInformationSuggestionsBinding
import com.stalmate.user.model.ModelCustumSpinner
import com.stalmate.user.view.singlesearch.ActivitySingleSearch
import com.wedguruphotographer.adapter.CustumSpinAdapter

class FragmentInformationSuggestions : BaseFragment() {

    private lateinit var binding : FragmentInformationSuggestionsBinding



    private lateinit var stateAdapter: CustumSpinAdapter
    private lateinit var cityAdapter: CustumSpinAdapter
    var graduation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =   inflater.inflate(R.layout.fragment_information_suggestions, container, false)
        binding = DataBindingUtil.bind<FragmentInformationSuggestionsBinding>(view)!!

        binding.filledTextGraduation.setOnClickListener {

            startActivityForResult(Intent(requireActivity(), ActivitySingleSearch::class.java).putExtra("TYPE", "graduation"),120)
        }

        binding.filledTextMajor.setOnClickListener {
            startActivityForResult(Intent(requireActivity(), ActivitySingleSearch::class.java).putExtra("TYPE", "major"),120)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode==120){


           val id = data!!.getSerializableExtra("postId").toString()
           val name = data.getSerializableExtra("name").toString()
           val type = data.getSerializableExtra("type").toString()

            Log.d("anjcnkan", type)

            if (type == "graduation") {
                binding.filledTextGraduation.text = name
            }else if (type == "major"){
                binding.filledTextMajor.text = name
            }

        }
    }

    fun isValid() : Boolean{

        if (graduation == ""){
           Toast.makeText(requireActivity(), "enter", Toast.LENGTH_SHORT).show()
            return false
        }/*else if (ValidationHelper.isNull(binding.etMajor.text.toString())){
            makeToast("Please Enter Major")
            return false
        }else if (ValidationHelper.isNull(binding.filledTextCountry.text.toString())){
            makeToast("Please Select Country ")
            return false
        }else if (ValidationHelper.isNull(binding.filledTextCity.text.toString())){
            makeToast("Please Select City ")
            return false
        }*/

        return true
    }


}