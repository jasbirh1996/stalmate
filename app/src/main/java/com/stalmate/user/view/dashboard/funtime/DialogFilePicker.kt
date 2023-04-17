package com.stalmate.user.view.dashboard.funtime


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.DialogFilePickerBinding
import com.stalmate.user.databinding.FragmentBottomDialogReelsMenuBinding
import com.stalmate.user.viewmodel.AppViewModel


class DialogFilePicker(var callback: Callback, var isFile: Boolean = false) :
    BottomSheetDialogFragment() {
    lateinit var binding: DialogFilePickerBinding
    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
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
        val contentView = View.inflate(context, R.layout.dialog_file_picker, null)
        binding = DataBindingUtil.bind<DialogFilePickerBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        if (isFile == false) {
            binding.buttonFile.visibility = View.GONE
        }
        binding.buttonFile.setOnClickListener {
            callback.onClickOnFilePicker(true)
            dismiss()
        }

        binding.buttonCamera.setOnClickListener {
            callback.onClickOnFilePicker(false)
            dismiss()
        }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    public interface Callback {
        fun onClickOnFilePicker(isFilePicker: Boolean)
    }


}