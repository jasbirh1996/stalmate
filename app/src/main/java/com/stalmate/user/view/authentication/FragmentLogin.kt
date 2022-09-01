package com.stalmate.user.view.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentLoginBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.utilities.ValidationHelper.isValidEmail
import com.stalmate.user.view.dashboard.ActivityDashboard

class FragmentLogin : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)
        binding = DataBindingUtil.bind<FragmentLoginBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*click on page */
       binding.forgetPassword.setOnClickListener {
           findNavController().navigate(R.id.fragmentForgetPassword)

       }

        binding.btnLogin.setOnClickListener {
            if (isValid()){

                hitLoginApi()
            }
        }

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (isValidEmail(binding.etEmail.text.toString())){
                    binding.appCompatImageView12.visibility = View.VISIBLE
                }else{
                    binding.appCompatImageView12.visibility = View.GONE
                }

            }

            override fun beforeTextChanged(s: CharSequence,start: Int,count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })


//        binding.etPassword.addTextChangedListener(object : TextWatcher {
//            @SuppressLint("ResourceAsColor")
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                if (binding.etPassword.text!!.isEmpty()){
//                    binding.appCompatImageView17.visibility = View.GONE
//                }else {
//                    binding.appCompatImageView17.visibility = View.VISIBLE
//                }
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence,start: Int,count: Int,after: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable) {
//
//            }
//        })


        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.fragmentSignUp)
        }
    }

    private fun hitLoginApi() {

        val hashMap = HashMap<String, String>()
        hashMap["email"] =binding.etEmail.text.toString()
        hashMap["password"] =binding.etPassword.text.toString()
        hashMap["deviceID"] = ""
        hashMap["deviceToken"] = App.getInstance().firebaseToken.toString()
        hashMap["deviceType"] = "android"

        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.login(hashMap)
        networkViewModel.loginData.observe(requireActivity()){

            it?.let {
                val message = it.message

                if (it.status){

                    PrefManager.getInstance(requireContext())!!.userDetailLogin = it
                    binding.progressBar.visibility = View.GONE
                    PrefManager.getInstance(requireContext())!!.keyIsLoggedIn = true
                    Log.d("token=======", PrefManager.getInstance(App.getInstance())!!.userDetailLogin.results.toString())

                    val intent = Intent(requireContext(), ActivityDashboard::class.java)
                    startActivity(intent)
                    makeToast(message)

                }else{

                    binding.progressBar.visibility = View.GONE
                    makeToast(message)

                }
            }
        }
    }



    fun isValid():Boolean{
        if (ValidationHelper.isNull(binding.etEmail.text.toString())){
            makeToast(getString(R.string.email_error_toast))
            return false;
        }else
        if (!isValidEmail(binding.etEmail.text.toString())){
            makeToast(getString(R.string.please_enter_valid_email))
            return false;
        }else if (ValidationHelper.isNull(binding.etPassword.text.toString())) {
            makeToast(getString(R.string.password_error_toast))
            return false
        }

        return true
    }
}