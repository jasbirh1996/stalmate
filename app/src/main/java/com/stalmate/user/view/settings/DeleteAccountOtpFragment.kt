package com.stalmate.user.view.settings

import android.content.Intent
import android.os.Bundle
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
import com.stalmate.user.view.authentication.ActivityAuthentication

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
                requireActivity().showToast("Your account has been deleted successfully.")
                startActivity(
                    Intent(
                        this.requireActivity(),
                        ActivityAuthentication::class.java
                    )
                )
                requireActivity().finishAffinity()
            }
        }
    }
}