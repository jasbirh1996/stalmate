package com.stalmate.user.model

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentBottomsheetUpdateUsernameBinding
import com.stalmate.user.view.profile.ActivityProfileEdit

class UpdateUsernameBottomSheet(
    val usernameUpdated: () -> Unit
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomsheetUpdateUsernameBinding
    var isUsedUsername = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    //dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView =
            View.inflate(context, R.layout.fragment_bottomsheet_update_username, null)
        binding = DataBindingUtil.bind<FragmentBottomsheetUpdateUsernameBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            binding.etUsername.setText(arguments?.getString("etUsername"))
            binding.ivUsername.visibility = View.VISIBLE
            binding.errorTaken.visibility = View.GONE

            binding.etUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (binding.etUsername.text.toString().isNotEmpty()) {
                        (requireContext() as ActivityProfileEdit).networkViewModel.checkIfOldUsername(
                            user_name = binding.etUsername.text.toString()
                        )
                    } else {
                        binding.ivUsername.visibility = View.GONE
                        binding.errorTaken.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            (requireContext() as ActivityProfileEdit).networkViewModel.checkIfOldUsernameLiveData.observe(
                requireActivity()
            ) {
                it?.reponse?.let {
                    if (it.name_status == true) {
                        binding.ivUsername.visibility = View.VISIBLE
                        binding.errorTaken.visibility = View.GONE
                        isUsedUsername = false
                    } else {
                        isUsedUsername = true
                        binding.ivUsername.visibility = View.GONE
                        binding.errorTaken.visibility = View.VISIBLE
                    }
                }
            }

            (requireContext() as ActivityProfileEdit).networkViewModel.changeUsernameLiveData.observe(
                this
            ) {
                Toast.makeText(
                    requireContext(),
                    "Username updated successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                usernameUpdated()
                dismiss()
            }

            (requireContext() as ActivityProfileEdit).networkViewModel.mThrowable.observe(this) {
                it?.let {
                    if (it.contains("day", true)) {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding.ivClose.setOnClickListener {
                dismiss()
            }
            binding.btnUpdate.setOnClickListener {
                if (binding.etUsername.text.toString().isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.user_name_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (isUsedUsername) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.user_name_valid_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    (requireContext() as ActivityProfileEdit).networkViewModel.changeUsername(
                        access_token = (requireContext() as ActivityProfileEdit).prefManager?.access_token.toString(),
                        user_name = binding.etUsername.text.toString()
                    )
                }
            }
            binding.btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}