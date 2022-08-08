package com.stalmate.user.view.authentication


import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R

import com.stalmate.user.base.BaseFragment

import com.stalmate.user.databinding.FragmentsignupBinding
import com.stalmate.user.utilities.ValidationHelper
import java.util.*




class FragmentSignUp : BaseFragment() {
    private lateinit var binding: FragmentsignupBinding

    private var GANDER : String = ""
    val c = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
//        binding.etDOB.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragmentsignup, container, false)
        binding = DataBindingUtil.bind(view)!!
//        binding.etDOB.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

        var cal = Calendar.getInstance()

     /*   val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.etDOB.text = sdf.format(cal.time)

            }


        // Display Selected date in textbox
        binding.etDOB.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }*/
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

        binding.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck){
                GANDER = "1"
                binding.rdmale.setChecked(true)
                binding.rdFamel.setChecked(false)
                binding.rdOthers.setChecked(false)
            }
        }

        binding.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck){
                GANDER = "2"
                binding.rdmale.setChecked(false)
                binding.rdFamel.setChecked(true)
                binding.rdOthers.setChecked(false)
            }
        }


        binding.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck){
                GANDER = "3"
                binding.rdmale.setChecked(false)
                binding.rdFamel.setChecked(false)
                binding.rdOthers.setChecked(true)
            }
        }


        binding.btnCrateAccount.setOnClickListener {
            /*if (isValid()) {
                createAccountApiCall()
            }*/

            findNavController().navigate(R.id.fragmentOTPEnter)
        }
    }


    fun isValid():Boolean{

       if (ValidationHelper.isNull(binding.etName.text.toString())){

           makeToast(getString(R.string.first_name_toast))
           return false
       }else
           if (ValidationHelper.isNull(binding.etLastName.text.toString())){
           makeToast(getString(R.string.last_name_toast))
       }else
           if (!isValidEmail(binding.etEmail.text.toString())){
            makeToast(getString(R.string.email_error_toast))
            return false;
        }else
            if(!binding.rdmale.isChecked && !binding.rdFamel.isChecked && !binding.rdOthers.isChecked){
            makeToast(getString(R.string.select_gendar_error))
               return false
            }else
                if(ValidationHelper.isValidPassword(binding.etPassword.text.toString())){
                makeToast(getString(R.string.password_error_toast))
                return false
            }else
                if (!binding.tmcheckbox.isChecked){
                makeToast(getString(R.string.accept_tnc))
            }
        return true
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /*private fun createAccountApiCall() {

        var gander_name : String = ""
        if (GANDER=="1"){
            gander_name = "Male"
        }else if (GANDER == "2"){
            gander_name = "Female"
        }else if(GANDER == "3"){
            gander_name = "Others"
        }

        val hashMap = HashMap<String, String>()
        hashMap["email"] =binding.etEmail.text.toString()
        hashMap["password"] =binding.etPassword.text.toString()
        hashMap["first_name"] =binding.etName.text.toString()
        hashMap["last_name"] =binding.etLastName.text.toString()
        hashMap["gender"] =gander_name
        hashMap["schoolandcollege"] =binding.etschoolcollege.text.toString()
        hashMap["dob"] =binding.etDOB.text.toString()
        hashMap["device_id"] = ""
        hashMap["device_token"] = App.getInstance().firebaseToken.toString()
        hashMap["device_type"] = "android"
        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.registration(hashMap)
        networkViewModel.registerData.observe(requireActivity()){

            it?.let {
                val message = it.message

                if (it.status == true){
                    PrefManager.getInstance(requireContext())!!.userDetail=it
                    PrefManager.getInstance(requireContext())!!.keyIsLoggedIn = true
                    findNavController().navigate(com.slatmate.user.R.id.fragmentOTPEnter)

                    makeToast(message)
                }else{
                    makeToast(message)
                }
            }
            binding.progressBar.visibility = View.GONE
        }
    }*/




    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text =  getString(R.string.sign_up)
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.backButtonRightText.visibility = View.GONE
        binding.toolbar.menuChat.visibility = View.VISIBLE

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

}