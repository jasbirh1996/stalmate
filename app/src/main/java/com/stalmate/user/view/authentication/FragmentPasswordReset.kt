package com.stalmate.user.view.authentication


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.stalmate.user.databinding.ResetSuccessPoppuBinding
import com.stalmate.user.databinding.SignUpSuccessPoppuBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.ActivityDashboard
import java.util.regex.Matcher
import java.util.regex.Pattern

class FragmentPasswordReset : BaseFragment() {

    private lateinit var binding: FragmentPasswordResetBinding
    private lateinit var bindingdialog : ResetSuccessPoppuBinding

    var email: String = ""
    var otp: String = ""
    val DURATION: Long = 2000
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


//        binding.etPassword.addTextChangedListener(object : TextWatcher {
//            @SuppressLint("ResourceAsColor")
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                /*if (!binding.etPassword.text!!.isEmpty() && isValidPassword(binding.etPassword.text.toString().trim())){
//
//                    binding.appCompatImageView17.visibility = View.VISIBLE
//                }else {
//                    binding.appCompatImageView17.visibility = View.GONE
//
//                }*/
//                binding.appCompatImageView17.visibility = View.VISIBLE
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence,start: Int,count: Int,after: Int) {
//                binding.appCompatImageView17.visibility = View.VISIBLE
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                binding.appCompatImageView17.visibility = View.VISIBLE
//
//            }
//        })

//        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
//            @SuppressLint("ResourceAsColor")
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                if (!binding.etConfirmPassword.text!!.isEmpty() && isValidPassword(binding.etPassword.text.toString().trim())){
//
//                    binding.appCompatImageView18.visibility = View.VISIBLE
//                }else {
//                    binding.appCompatImageView18.visibility = View.GONE
//
//                }
//
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


        binding.appCompatTextView3.setOnClickListener {
            findNavController().navigate(R.id.fragmentLogin)
        }
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

                    val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
                    val view = layoutInflater.inflate(R.layout.reset_success_poppu,null)

                    builder.setView(view)
                    builder.setCanceledOnTouchOutside(false)
                   bindingdialog = DataBindingUtil.bind(view)!!


                    bindingdialog.btnLogin.setOnClickListener {
                        findNavController().navigate(R.id.fragmentLogin)
                        builder.dismiss()
                    }
                    builder.show()


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
        binding.toolbar.backButtonLeftText.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun isValid():Boolean {

        if(binding.etPassword.text!!.isEmpty() || binding.etConfirmPassword.text!!.isEmpty()) {
            makeToast(getString(R.string.please_enter_password))
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


    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }



}