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
import com.stalmate.user.model.Education
import com.stalmate.user.model.ModelCustumSpinner
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.profile.EducationListAdapter
import com.stalmate.user.view.singlesearch.ActivitySingleSearch
import com.wedguruphotographer.adapter.CustumSpinAdapter

class FragmentInformationSuggestions(val callback: Callbackk ) : BaseFragment() {

    private lateinit var binding : FragmentInformationSuggestionsBinding

    var graduation = ""
    var majorText = ""
    var name = ""
    var graduationId = ""
    var majorTextId = ""
    var type = ""
    var country = ""
    var state = ""
    var city = ""

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



           name = data!!.getSerializableExtra("name").toString()
           type = data.getSerializableExtra("type").toString()

            Log.d("anjcnkan", type)
            Log.d("anjcnkan", name)

            if (type == "graduation") {
                binding.filledTextGraduation.text = name
                graduationId = data!!.getSerializableExtra("postId").toString()
            }else if (type == "major"){
                binding.filledTextMajor.text = name
                majorTextId = data!!.getSerializableExtra("postId").toString()
            }
        }
    }

    interface Callbackk {
        fun onCallBackData(graducation : String, graducationId: String , major : String, majorId : String, country : String , state : String, city : String )
    }

    fun isValid() : Boolean{

            if (ValidationHelper.isNull(binding.filledTextGraduation.text.toString())) {
                makeToast("Please Enter Graduation Field")
                return false
            } else if (ValidationHelper.isNull(binding.filledTextMajor.text.toString())) {
                makeToast("Please Enter Major")
                return false
            } else if (ValidationHelper.isNull(binding.filledTextCountry.text.toString())) {
                makeToast("Please Select Country ")
                return false
            } else if (ValidationHelper.isNull(binding.filledTextState.text.toString())) {
                makeToast("Please Select State ")
                return false
            } else if (ValidationHelper.isNull(binding.filledTextCity.text.toString())) {
                makeToast("Please Select City ")
                return false
            }

        callback.onCallBackData(
            binding.filledTextGraduation.text.toString(),
            graduationId,
            binding.filledTextMajor.text.toString(),
            majorTextId,
            binding.filledTextCountry.text.toString(),
            binding.filledTextState.text.toString(),
            binding.filledTextCity.text.toString()
        )

        return true

    }


}