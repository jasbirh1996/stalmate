package com.stalmate.user.view.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentChangePasswordBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.ErrorUtil
import java.util.regex.Matcher
import java.util.regex.Pattern


class ChangePasswordFragment : BaseFragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        networkViewModel.mThrowable.observe(this.viewLifecycleOwner) {
            it?.let { it1 ->
                dismissLoader()
                Toast.makeText(
                    this.requireContext(),
                    it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.backChangePassword.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSavePassword.setOnClickListener {
            if (binding.OldPassword.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter old password.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.NewPassword.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter new password.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!isValidPassword(binding.NewPassword.text.toString().trim())) {
                val successdialogBuilder =
                    AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
                val view = layoutInflater.inflate(R.layout.password_validation_error_popup, null)
                successdialogBuilder.setView(view)
                successdialogBuilder.setCanceledOnTouchOutside(true)
                successdialogBuilder.show()
                Toast.makeText(
                    this.requireContext(),
                    "Please enter valid new password.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.ConfirmPassword.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter confirm new password.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.NewPassword.text.toString()
                    .trim() != binding.ConfirmPassword.text.toString().trim()
            ) {
                Toast.makeText(
                    this.requireContext(),
                    "Password and confirm password does not match.",
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

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }
}