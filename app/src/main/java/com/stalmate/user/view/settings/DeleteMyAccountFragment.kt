package com.stalmate.user.view.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c2m.storyviewer.utils.showToast
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentDeleteMyAccountBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.view.authentication.ActivityAuthentication


class DeleteMyAccountFragment : BaseFragment() {

    private var _binding: FragmentDeleteMyAccountBinding? = null
    private val binding get() = _binding!!
    private var otp: String = ""

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
        networkViewModel.sendOtpResponse.observe(this.viewLifecycleOwner) {
            if (it?.reponse != null) {
                otp = it.reponse.otp.toString()
            }
        }
        networkViewModel.deleteMyAccountResponse.observe(this.viewLifecycleOwner) {
            dismissLoader()
            if (it?.message?.contains("Invlid", true) == false) {
                requireActivity().showToast("Your account has been deleted successfully.")
                startActivity(Intent(this.requireActivity(), ActivityAuthentication::class.java))
                requireActivity().finishAffinity()
            }
        }
        binding.ChangeCountryName.setOnCountryChangeListener {
            binding.tvChangeFlage.setText(binding.ChangeCountryName.selectedCountryEnglishName.toString())
        }
        binding.tvChangeFlage.setOnClickListener {
            binding.ChangeCountryName.launchCountrySelectionDialog()
        }
        binding.btnDeleteMyAccount.setOnClickListener {
            if (otp.isNullOrEmpty()) {
                requireActivity().showToast("Please wait for otp!")
            } else if (binding.tvChangeNumber.text.toString().isNullOrEmpty()) {
                requireActivity().showToast("Please enter your registered mobile number.")
            } else if (binding.etEmail.text.toString().isNullOrEmpty()) {
                requireActivity().showToast("Please enter your email.")
            } else if (binding.tvChangeFlage.text.toString().trim().isNullOrEmpty()) {
                requireActivity().showToast("Please select your country.")
            } else {
                showLoader()
                networkViewModel.deleteMyAccount(
                    access_token = prefManager?.access_token.toString(),
                    number = binding.tvChangeNumber.text.toString().trim(),
                    email = binding.etEmail.text.toString(),
                    number_c_code = binding.ChangeCCp.selectedCountryCode,
                    otp = otp,
                    notify_contact = true
                )
            }
        }
    }
}