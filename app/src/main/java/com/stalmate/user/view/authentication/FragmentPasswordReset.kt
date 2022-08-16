package com.stalmate.user.view.authentication


import android.annotation.SuppressLint
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
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentPasswordResetBinding
import com.stalmate.user.utilities.ValidationHelper

class FragmentPasswordReset : BaseFragment() {

    private lateinit var binding: FragmentPasswordResetBinding
    var email: String = ""
    var otp: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_password_reset, container, false)
        binding = DataBindingUtil.bind<FragmentPasswordResetBinding>(view)!!
        email = requireArguments().getString("email").toString()
        otp = requireArguments().getString("otp").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Common ToolBar SetUp*/
        toolbarSetUp()
        binding.btnLogin.setOnClickListener {
            if (isValid()) {
                forgetPasswordApiCall()
            }
        }


        binding.etPassword.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etPassword.text!!.isEmpty()){
                    binding.appCompatImageView17.visibility = View.GONE
                }else {
                    binding.appCompatImageView17.visibility = View.VISIBLE
                }

            }

            override fun beforeTextChanged(s: CharSequence,start: Int,count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (binding.etConfirmPassword.text!!.isEmpty()){
                    binding.appCompatImageView18.visibility = View.GONE
                }else {
                    binding.appCompatImageView18.visibility = View.VISIBLE

                }


            }

            override fun beforeTextChanged(s: CharSequence,start: Int,count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }


    private fun forgetPasswordApiCall() {

        val hashMap = HashMap<String, String>()

        hashMap["email"] = email
        hashMap["otp"] = otp
        hashMap["password"] = binding.etPassword.text.toString()

        showLoader()
        networkViewModel.otpVerify(hashMap)
        networkViewModel.otpVerifyData.observe(requireActivity()) {

            it?.let {
                val message = it.message

                if (it.status == true) {

                    dismissLoader()
                    findNavController().navigate(R.id.fragmentLogin)

                    makeToast(message)
                } else {
                    makeToast(message)
                }

            }
            dismissLoader()
        }

    }

    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text = getString(R.string.reset_password)
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.backButtonRightText.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun isValid():Boolean {

        if(binding.etPassword.text!!.isEmpty() || binding.etConfirmPassword.text!!.isEmpty()) {
            makeToast("Please Enter Password")
            return false
        } else if(ValidationHelper.isValidPassword(binding.etPassword.text.toString())) {
            makeToast(getString(R.string.password_error_toast))
            return false
        } else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            makeToast(getString(R.string.password_not_match))
            return false
        }

        return true
    }




}