package com.stalmate.user.modules.reels.photo_editing


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentBottomsheetRangeBinding


class Counter(
    val onCaptureAfterNthSeconds: (type: String, duration: Int) -> Unit,
    val onRangeDialogDismiss: () -> Unit
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomsheetRangeBinding
    var type: String = "video"
    var seconds = 5

    override fun onCancel(dialog: DialogInterface) {
        onRangeDialogDismiss()
        super.onCancel(dialog)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                onRangeDialogDismiss()
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottomsheet_range, null)
        binding = DataBindingUtil.bind<FragmentBottomsheetRangeBinding>(contentView)!!
        dialog.getWindow()!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        binding.buttonTrimDone.setOnClickListener {
            onCaptureAfterNthSeconds(type, seconds)
            onRangeDialogDismiss()
            dismiss()
        }


        binding.radioGroupType.setOnCheckedChangeListener { group, checkedId ->
            run {
                when (checkedId) {
                    R.id.radioVideo -> {
                        type = "video"
                    }
                    R.id.radioPhoto -> {
                        type = "photo"
                    }
                }
            }
        }
        binding.radioGroupDuration.setOnCheckedChangeListener { group, checkedId ->
            run {
                when (checkedId) {
                    R.id.radio5 -> {
                        seconds = 5
                    }
                    R.id.radio10 -> {
                        seconds = 10
                    }
                    R.id.radio15 -> {
                        seconds = 15
                    }
                }
            }
        }
    }
}
