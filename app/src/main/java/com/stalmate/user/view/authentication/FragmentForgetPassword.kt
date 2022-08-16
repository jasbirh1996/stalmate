package com.stalmate.user.view.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentForgetPasswordBinding
import com.stalmate.user.utilities.ValidationHelper

class FragmentForgetPassword : BaseFragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_forget_password, container, false)
        binding = DataBindingUtil.bind<FragmentForgetPasswordBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()


        /*Click on page */

        binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.fragmentLogin)
        }

        binding.btnProcessOtp.setOnClickListener {
//            findNavController().navigate(R.id.fragmentPasswordReset)

            if (isValid()){
                requestOtpApi()
            }
        }


        binding.etEmail.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (ValidationHelper.isValidEmail(binding.etEmail.text.toString())){
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
    }




    private fun requestOtpApi() {
        val hashMap = HashMap<String, String>()
        hashMap["email"] =binding.etEmail.text.toString()
        showLoader()

        networkViewModel.otpVerify(hashMap)
        networkViewModel.otpVerifyData.observe(requireActivity()){

            it?.let {
                val message = it.message

                if (it.status == true){
                    dismissLoader()
                    val bundle = Bundle()
                    bundle.putString("email", binding.etEmail.text.toString())
                    bundle.putString("layout", "ForgetPassword")
                    findNavController().navigate(R.id.fragmentOTPEnter,bundle)

                    makeToast(message)
                }else{
                    makeToast(message)
                }

            }


        }


    }

    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text =  getString(R.string.forget_post)
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.backButtonRightText.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }




    fun isValid():Boolean{
        if (ValidationHelper.isNull(binding.etEmail.text.toString())){
            makeToast(getString(R.string.email_error_toast))
            return false;
        }else
            if (!ValidationHelper.isValidEmail(binding.etEmail.text.toString())){
                makeToast(getString(R.string.please_enter_valid_email))
                return false;
            }

        return true
    }


}