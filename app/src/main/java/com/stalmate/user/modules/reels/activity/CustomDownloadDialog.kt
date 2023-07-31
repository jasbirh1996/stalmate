package com.stalmate.user.modules.reels.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.databinding.CustomDownloadDialogBinding

class CustomDownloadDialog(
    private val mContext: Context, private val message: String,
    private val mListener: DownloadListener
) : Dialog(mContext) {

    var binding: CustomDownloadDialogBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_download_dialog, null)
        binding = DataBindingUtil.bind(view)
        setContentView(binding?.root!!)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window!!.attributes)

        lp.width = (mContext.resources.displayMetrics.widthPixels * 0.90).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.7f
        window!!.attributes = lp

        binding?.btnCancel?.setOnClickListener {
            mListener.eventListener()
            dismiss()
        }
    }

    fun setMessage(message: String) {
        if (message == "👍") {
            binding?.tvMessage?.text = "Processing..."
            binding?.rlCancel?.visibility = View.GONE
        } else {
            binding?.tvMessage?.text = "Wait a while, It's being downloaded!"
            binding?.rlCancel?.visibility = View.VISIBLE
        }
        binding?.tvFilesNo?.text = message
    }

    interface DownloadListener {
        fun eventListener()
    }
}