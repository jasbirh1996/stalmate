package com.stalmate.user.view.profile

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.stalmate.user.databinding.DialogAlertAccessContactsBinding

class FragmentAlertDialogAccessContacts(var callback: Callback) : DialogFragment() {
    private var binding: DialogAlertAccessContactsBinding? = null

    interface Callback {
        fun onCLickONAccessButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAlertAccessContactsBinding.inflate(layoutInflater)
        dialog?.setCancelable(false)
        listener()
        return binding!!.root
    }

    private fun listener() {
        binding?.buttonAllowAccess?.setOnClickListener {
            dismiss()
            callback.onCLickONAccessButton()
        }
        binding?.buttonDontAllowAccess?.setOnClickListener {
            dialog?.dismiss()
        }
    }

    companion object {
        fun getWidth(context: Context): Int {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }
}