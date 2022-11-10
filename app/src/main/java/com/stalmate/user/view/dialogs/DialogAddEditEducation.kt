package com.stalmate.user.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.stalmate.user.R
import com.stalmate.user.databinding.DialougeAddEducationBinding
import com.stalmate.user.model.Education
import com.stalmate.user.model.Profession
import com.stalmate.user.viewmodel.AppViewModel
import java.util.*

class DialogAddEditEducation(
    private val context: Context,
    var education : Education,
    var viewModel: AppViewModel,
    var isEdit:Boolean,
    var callbackk: Callbackk
) {
    private var dialog: Dialog? = null
    private lateinit var binding: DialougeAddEducationBinding
    var isDialogShowing = false
        private set

    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialouge_add_education, null)
        binding= DataBindingUtil.bind(view)!!;
        dialog!!.setContentView(binding.root);
        dialog!!.setCancelable(false)
        dialog!!.show()
        isDialogShowing = true

        dialog!!.setCancelable(false)
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(null)

            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setLayout(DialogAddEditProfession.getWidth(context) / 100 * 90, LinearLayout.LayoutParams.WRAP_CONTENT)

            dialog!!.window!!.setDimAmount(0.5f)
            //dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        }


        binding.btnSave.setOnClickListener {
            if (isValid()){
                hitAddEditApi()
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        if (isEdit){
            binding.etGraduation.setText(education.sehool)
            binding.etBachlore.setText(education.course)
            binding.etBachloreType.setText(education.branch)
        }
    }

     interface  Callbackk{
        fun onSuccessfullyEditedEducation(education: Education)
    }

    fun isValid():Boolean{
        if (binding.etGraduation.text.isEmpty()){
            makeToast("Please Enter University")
            return false
        }else if (binding.etBachlore.text.isEmpty()){
            makeToast("Please Enter Bachelor")
            return false
        }else if (binding.etBachloreType.text.isEmpty()){
            makeToast("Please Enter bachelor")
            return false
        }
        return true
    }


    fun makeToast(message:String){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }


    fun hitAddEditApi(){

        val hashMap = HashMap<String, String>()

        if (isEdit){
            hashMap["id"] =education._id
        }

        education.sehool=binding.etGraduation.text.toString()
        education.course=binding.etBachlore.text.toString()
        education.branch=binding.etBachloreType.text.toString()


        hashMap["sehool"] =education.sehool
        hashMap["branch"] = education.course
        hashMap["course"] =education.branch

        viewModel.educationData(hashMap)
        viewModel.educationData.observe( (binding.root.context as? LifecycleOwner)!!){
            it?.let {
                if (it.status){
                    callbackk.onSuccessfullyEditedEducation(education)
                    dismiss()
                }
            }
        }
    }

    fun dismiss() {
        isDialogShowing = false
        dialog!!.dismiss()
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