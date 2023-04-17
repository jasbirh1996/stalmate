package com.stalmate.user.view.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.c2m.storyviewer.utils.showToast
import com.google.android.material.snackbar.Snackbar
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentDeleteMyAccountBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.ErrorUtil
import com.stalmate.user.view.authentication.ActivityAuthentication


class DeleteMyAccountFragment : BaseFragment() {

    private var _binding: FragmentDeleteMyAccountBinding? = null
    private val binding get() = _binding!!
    private var otp: String = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_delete_my_account, container, false)
        _binding = FragmentDeleteMyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        networkViewModel.sendOtp(access_token = prefManager?.access_token.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkViewModel.mThrowable.observe(this.viewLifecycleOwner) {
            it?.let { it1 ->
                dismissLoader()
                Toast.makeText(this.requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        networkViewModel.sendOtpResponse.observe(this.viewLifecycleOwner) {
            if (it?.reponse != null) {
                //otp = it.reponse.otp.toString()
            }
        }
        networkViewModel.deleteMyAccountResponse.observe(this.viewLifecycleOwner) {
            dismissLoader()
            if (it?.message?.contains("Invlid", true) == false) {
                findNavController().navigate(R.id.action_deleteMyAccountFragment_to_otpAccountDelete)
            }
        }
        binding.backDeleteAccount.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDeleteMyAccount.setOnClickListener {
            if (otp.isNullOrEmpty()) {
                requireActivity().showToast("Please wait for otp!")
            } else if (binding.etEmail.text.toString().isNullOrEmpty()) {
                requireActivity().showToast("Please enter your email.")
            } else {
                showLoader()
                networkViewModel.deleteMyAccount(
                    access_token = prefManager?.access_token.toString(),
                    email = binding.etEmail.text.toString(),
                    otp = otp,
                    notify_contact = true
                )
            }
        }
    }
}