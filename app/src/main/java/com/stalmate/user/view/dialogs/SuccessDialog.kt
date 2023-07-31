package com.stalmate.user.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.databinding.AppCommonAlterDialogBinding
import com.stalmate.user.databinding.AppCommonSuccessDialogBinding


class SuccessDialog(
    private val context: Context,
    var heading: String,
    var message: String,
    var buttonPrimary: String,
    private val callback: Callback,
    var icon: Int = -1,
    var isAutoDismiss: Boolean = false
) {
    var binding: AppCommonSuccessDialogBinding? = null
    private var dialog: Dialog? = null
    var isDialogShowing = false
        private set

    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.app_common_success_dialog, null)
        binding = DataBindingUtil.bind(view)
        dialog!!.setContentView(binding!!.getRoot())
        dialog!!.setCancelable(false)
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(null)

            //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!
                .setLayout(getWidth(context) / 100 * 90, LinearLayout.LayoutParams.WRAP_CONTENT)

            /*          dialog!!.window!!
                          .setLayout( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)*/

        }
        if (icon != -1) {
            binding!!.ivImage.setImageResource(icon)
        }
        if (buttonPrimary.isNullOrEmpty()) {
            binding!!.buttonProceed.visibility = View.GONE
        }
        binding!!.buttonProceed.setText(buttonPrimary)
        binding!!.tvHead.setText(heading)
        binding!!.tvMessage.setText(message)
        if (!isAutoDismiss)
            binding!!.cvCard.setOnClickListener {
                callback.onDialogResult(isPermissionGranted = true)
                dismiss()
            }
        if (!isAutoDismiss)
            binding!!.buttonProceed.setOnClickListener {
                callback.onDialogResult(isPermissionGranted = true)
                dismiss()
            }
        dialog?.show()
        isDialogShowing = true
    }

    fun dismiss() {
        isDialogShowing = false
        dialog?.dismiss()
    }

    interface Callback {
        fun onDialogResult(isPermissionGranted: Boolean)
    }

    companion object {
        fun getWidth(context: Context): Int {
            val displayMetrics = DisplayMetrics()
            val windowmanager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowmanager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }
}