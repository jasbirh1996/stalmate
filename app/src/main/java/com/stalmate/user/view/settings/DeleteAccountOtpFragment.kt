package com.stalmate.user.view.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.c2m.storyviewer.utils.showToast
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentOTPEnterDeleteAccountBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.authentication.ActivityAuthentication
import com.stalmate.user.view.dialogs.SuccessDialog

class DeleteAccountOtpFragment : BaseFragment() {

    private lateinit var binding: FragmentOTPEnterDeleteAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_o_t_p_enter_delete_account, container, false)
        binding = DataBindingUtil.bind<FragmentOTPEnterDeleteAccountBinding>(view)!!


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.tvhead.text = "Delete my account"
        binding.toolbar.topAppBar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.otpCountDown.setOnClickListener {
            Toast.makeText(
                this.requireContext(),
                "Otp sent to your registered email successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnProcess.setOnClickListener {
            if (binding.pinView.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(this.requireContext(), "Please enter otp", Toast.LENGTH_SHORT).show()
            } else if (binding.pinView.text.toString().trim() != "1234") {
                Toast.makeText(
                    this.requireContext(),
                    "Please enter correct otp",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val d = SuccessDialog(
                    context = this@DeleteAccountOtpFragment.requireContext(),
                    heading = "Success",
                    message = "Your stalemate account has been temporary deleted.\n\nYour account will be deleted permanently after 30 days but if you want to continue, login again within 30 days any time.",
                    buttonPrimary = "",
                    callback = object : SuccessDialog.Callback {
                        override fun onDialogResult(isPermissionGranted: Boolean) {
                            PrefManager.getInstance(this@DeleteAccountOtpFragment.requireContext())?.keyIsLoggedIn =
                                false
                            startActivity(
                                Intent(
                                    context,
                                    ActivityAuthentication::class.java
                                ).putExtra("screen", "login")
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                            requireActivity().finishAffinity()
                        }
                    },
                    icon = R.drawable.baseline_check_circle_24, isAutoDismiss = true
                )
                d.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    d.dismiss()
                    PrefManager.getInstance(this.requireContext())?.keyIsLoggedIn = false
                    startActivity(
                        Intent(
                            context,
                            ActivityAuthentication::class.java
                        ).putExtra("screen", "login")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                    requireActivity().finishAffinity()
                }, 3000)
            }
        }
    }
}