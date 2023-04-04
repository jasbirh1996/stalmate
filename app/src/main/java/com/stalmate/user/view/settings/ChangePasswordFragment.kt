package com.stalmate.user.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentChangePasswordBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings


class ChangePasswordFragment : BaseFragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkViewModel.changePasswordResponse.observe(this.viewLifecycleOwner) {
            dismissLoader()
            Toast.makeText(
                this.requireContext(),
                "Password updated successfully!",
                Toast.LENGTH_SHORT
            ).show()
            (requireActivity() as ActivitySettings).onBackPressed()
        }

        binding.btnSavePassword.setOnClickListener {
            if (binding.OldPassword.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter old password",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.NewPassword.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter new password",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.ConfirmPassword.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter confirm new password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showLoader()
                networkViewModel.changePassword(
                    access_token = prefManager?.access_token.toString(),
                    password_old = binding.OldPassword.text.toString().trim(),
                    password_new = binding.NewPassword.text.toString().trim(),
                    password_confirm = binding.ConfirmPassword.text.toString().trim()
                )
            }
        }
    }
}