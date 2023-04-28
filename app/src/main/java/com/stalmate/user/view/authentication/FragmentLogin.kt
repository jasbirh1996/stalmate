package com.stalmate.user.view.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.c2m.storyviewer.utils.showToast
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentLoginBinding
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.CustumEditText
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.utilities.ValidationHelper.isValidEmail


class FragmentLogin : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private var exit = false
    /*override fun onBackPressed() {
        if (exit) {
            requireActivity().finish() // finish activity
        } else {
            Toast.makeText(
                requireActivity(), "Press Back again to Exit.",
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed(Runnable { exit = false }, 3 * 1000)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

/*        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               requireActivity().finish()
            }
        })*/

        binding.btnLogin.setOnClickListener {
            if (isValid()) {
                hitLoginApi()
            }
        }

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isValidEmail(binding.etEmail.text.toString())) {
                    binding.appCompatImageView12.visibility = View.VISIBLE
                } else {
                    binding.appCompatImageView12.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.createAccount.setOnClickListener {
            findNavController().apply {
                navigate(R.id.action_fragmentLogin_to_fragmentSignUp)
            }
        }

        CustumEditText.setup(binding.filledTextEmail, binding.etEmail)
        CustumEditText.setup(binding.filledTextPassword, binding.etPassword)
    }


    private fun hitLoginApi() {
        Constants.TYPE_ALL_FOLLOWERS_FOLLOWING
        val hashMap = HashMap<String, String>()
        hashMap[Constants.PARAMETER_EMAIL] = binding.etEmail.text.toString()
        hashMap["password"] = binding.etPassword.text.toString()
        hashMap["deviceID"] = ""
        hashMap["deviceToken"] = App.getInstance().firebaseToken.toString()
        hashMap["deviceType"] = "android"

        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.login(hashMap)
        networkViewModel.mThrowable.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this.requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        networkViewModel.loginData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            it?.let {
                val message = it.message
                if (it.status) {
                    PrefManager.getInstance(requireContext())?.keyIsLoggedIn = true
                    PrefManager.getInstance(requireContext())?.userDetail = it
                    PrefManager.getInstance(App.getInstance())
                        ?.setStringValue(
                            key = "language",
                            value = if (!it.results?.language.isNullOrEmpty()) it.results?.language else "English"
                        )
                    PrefManager.getInstance(App.getInstance())
                        ?.setStringValue(
                            key = "country",
                            value = if (!it.results?.country.isNullOrEmpty()) it.results?.country else "IN"
                        )
                    App.getInstance().setupApis()
                    if (PrefManager.getInstance(requireContext())?.userDetail?.results?.is_account_recover == true) {
                        makeToast("Welcome Back!!")
                    } else {
                        makeToast(message)
                    }
                    startActivity(IntentHelper.getDashboardScreen(context))
                    requireActivity().finish()
                } else {
                    makeToast(message)
                }
            }
        }
    }


    private fun isValid(): Boolean {
        if (ValidationHelper.isNull(binding.etEmail.text.toString())) {
            makeToast(getString(R.string.email_error_toast))
            return false;
        } else if (!isValidEmail(binding.etEmail.text.toString())) {
            makeToast(getString(R.string.please_enter_valid_email))
            return false;
        } else if (ValidationHelper.isNull(binding.etPassword.text.toString())) {
            makeToast(getString(R.string.password_error_toast))
            return false
        }
        return true
    }


}