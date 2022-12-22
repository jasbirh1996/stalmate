package com.stalmate.user.view.dialogs

import android.content.Context


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil


import com.stalmate.user.R
import com.stalmate.user.databinding.AppCommonAlterDialogBinding
import com.stalmate.user.utilities.ValidationHelper


class CommonConfirmationDialog(
    private val context: Context,var heading:String,var message:String,var buttonPrimary:String,var buttonSecondry:String,
    private val callback: Callback,
)  {
    var isResultFetched=false;
    var binding: AppCommonAlterDialogBinding? = null
    private var dialog: Dialog? = null
    var isDialogShowing = false
        private set
    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.app_common_alter_dialog, null)
        binding = DataBindingUtil.bind(view)
        dialog!!.setContentView(binding!!.getRoot())
        dialog!!.setCancelable(true)
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(null)

            //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                   dialog!!.window!!
                        .setLayout(getWidth(context) / 100 * 90, LinearLayout.LayoutParams.WRAP_CONTENT)

  /*          dialog!!.window!!
                .setLayout( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)*/

        }

        if (ValidationHelper.isNull(heading)){
            binding!!.tvHead.visibility=View.GONE
        }

        binding!!.buttonProceed.setText(buttonPrimary)
        binding!!.buttonClose.setText(buttonSecondry)
        binding!!.tvHead.setText(heading)
        binding!!.tvMessage.setText(message)
        binding!!.buttonClose.setOnClickListener {
            callback.onDialogResult(isPermissionGranted = false)
            dismiss()
        }
        binding!!.buttonProceed.setOnClickListener {
            callback.onDialogResult(isPermissionGranted = true)
            dismiss()
        }

        dialog!!.show()
        isDialogShowing = true
    }

    fun dismiss() {
        isDialogShowing = false
        dialog!!.dismiss()
    }

    interface Callback {
        fun onDialogResult(isPermissionGranted:Boolean)
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
