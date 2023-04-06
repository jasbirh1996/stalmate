package com.stalmate.user.model


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentBottomsheetContactUsBinding
import com.stalmate.user.utilities.SpinnerUtil.setSpinner
import java.util.regex.Pattern


class ContactUsBottomSheet(
    val onCaptureAfterNthSeconds: (category: String, topic: String, details: String) -> Unit,
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomsheetContactUsBinding
    var type: String = "video"
    var seconds = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottomsheet_contact_us, null)
        binding = DataBindingUtil.bind<FragmentBottomsheetContactUsBinding>(contentView)!!
        dialog.getWindow()!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            var category = ""
            var topic = ""
            //resources.getStringArray(R.array.month).indexOf(selectedMonth) + 1
            binding.spinnerCategory.setSpinner(
                listFromResources = R.array.contact_us_category,
                setSelection = 1,
                onItemSelectedListener = {
                    category = binding.spinnerCategory.selectedItem.toString()
                }
            )
            binding.spinnerTopic.setSpinner(
                listFromResources = R.array.contact_us_topic,
                setSelection = 1,
                onItemSelectedListener = {
                    topic = binding.spinnerTopic.selectedItem.toString()
                }
            )
            binding.buttonTrimDone.setOnClickListener {
                onCaptureAfterNthSeconds(
                    category,
                    topic,
                    binding.etDetailedReason.text.toString()
                )
                dismiss()
            }
        }
    }
}
