package com.stalmate.user.view.authentication

import android.content.Intent
import android.os.Bundle
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
import java.util.HashMap

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
                    PrefManager.getInstance(requireContext())!!.userDetailLogin= it

                    PrefManager.getInstance(requireContext())!!.keyIsLoggedIn = true
                    val intent = Intent(requireContext(), ActivityDashboard::class.java)
                    startActivity(intent)

                    makeToast(message)
                }else{
                    makeToast(message)
                }

            }


        }


    }

    fun isValid():Boolean{

        if (!isValidEmail(binding.etEmail.text.toString())){
            makeToast(getString(R.string.email_error_toast))
            return false;
        }else if (ValidationHelper.isValidPassword(binding.etPassword.text.toString())) {
            makeToast(getString(R.string.password_error_toast))
            return false
        }

        return true
    }
}