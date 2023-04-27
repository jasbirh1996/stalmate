package com.stalmate.user.model

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentBottomsheetSelectThumbnailBinding
import com.stalmate.user.utilities.SpinnerUtil.setSpinner

class SelecteThumbnailBottomSheet(
    val fromCamera: () -> Unit,
    val fromGallery: () -> Unit,
    val fromVideo: () -> Unit,
    val fromDismiss: () -> Unit
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomsheetSelectThumbnailBinding
    var type: String = "video"
    var seconds = 5

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
            View.inflate(context, R.layout.fragment_bottomsheet_select_thumbnail, null)
        binding = DataBindingUtil.bind<FragmentBottomsheetSelectThumbnailBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            binding.ivClose.setOnClickListener {
                fromDismiss()
                dismiss()
            }
            binding.btnCamera.setOnClickListener {
                fromCamera()
                dismiss()
            }
            binding.btnGallery.setOnClickListener {
                fromGallery()
                dismiss()
            }
            binding.btnChooseFromVideo.setOnClickListener {
                fromVideo()
                dismiss()
            }
            binding.btnCancel.setOnClickListener {
                fromDismiss()
                dismiss()
            }
        }
    }
}