package com.stalmate.user.view.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.stalmate.user.R
import com.stalmate.user.databinding.DialogFragmentLoaderBinding

class DialogFragmentLoader(var fragment: Fragment, var title: String) : DialogFragment() {
    private lateinit var backStateName: String
    private lateinit var binding: DialogFragmentLoaderBinding
    private lateinit var callbackk:Callbackk

    public fun addCallback(callbackk: Callbackk){
        this.callbackk=callbackk
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView =layoutInflater.inflate(R.layout.dialog_fragment_loader, null)
        val dialog=object : Dialog(requireContext()) {
            override fun onBackPressed() {
                if (childFragmentManager.backStackEntryCount==1){
                    closeDialog()

                }else{
                    childFragmentManager.popBackStack()
                }
            }
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.bind(dialogView)!!
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        if (dialog.window != null) {
            dialog.window!!.setStatusBarColor(ContextCompat.getColor(requireContext(),R.color.white));
            dialog.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.color.white
                )
            )
            /*   dialog.window!!.setLayout(
                   DialogAddEditEmergencyContacts.getWidth(
                       context
                   ) / 100 * 90, LinearLayout.LayoutParams.WRAP_CONTENT
               )*/
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // This flag is required to set otherwise the setDimAmount method will not show any effect

            dialog.window!!.setDimAmount(0.5f) //0 for no dim to 1 for full dim

            dialog.getWindow()!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            );
        }


/*        binding.toolbar.topAppBar.menu.get(0).isVisible=false*/
        loadFragment(fragment)

        return dialog
    }


    public fun  clearAll(){
        for (i in 1 until childFragmentManager.backStackEntryCount) {
            childFragmentManager.popBackStack()
        }
    }


    private fun closeDialog() {
        try {
            callbackk.onDismissDialogOnFragmentLoader()
        }catch (e: Exception){

        }

        dialog!!.dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    public fun loadFragment(fragment: Fragment) {
        Log.d("lasdasd","as;kdasd")
        backStateName = fragment.javaClass.simpleName
        val manager: FragmentManager = childFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            //    ft.setTransition(Tra));
            ft.add(binding.frame.getId(), fragment, backStateName)
            ft.addToBackStack(backStateName);
            ft.commit()
        }
    }

    public interface Callbackk{
        fun onDismissDialogOnFragmentLoader()
    }
}