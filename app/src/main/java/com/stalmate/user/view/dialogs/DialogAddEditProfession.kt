package com.stalmate.user.view.dialogs


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.stalmate.user.R
import com.stalmate.user.databinding.DialougeAddProfessionBinding
import com.stalmate.user.model.Profession
import com.stalmate.user.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*


class DialogAddEditProfession(
    private val context: Context,
    var profession: Profession,
    var viewModel: AppViewModel,
    var isEdit:Boolean,
    var callbackk: Callbackk
) {
    private var dialog: Dialog? = null
    private lateinit var binding: DialougeAddProfessionBinding
    var isDialogShowing = false
        private set

    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialouge_add_profession, null)
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
        var cal = Calendar.getInstance()
        // Display Selected date in textbox
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tvCdFrom.text = sdf.format(cal.time)

            }

        binding.tvCdFrom.setOnClickListener {
            DatePickerDialog(
                context, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Display Selected End date in textbox
        val dateEndSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tvCdTo.text = sdf.format(cal.time)

            }

        binding.tvCdTo.setOnClickListener {
            DatePickerDialog(
                context, dateEndSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSave.setOnClickListener {
            if (isValid()){
                hitAddEditApi()
            }
        }

        binding.radioButtonCurrentWork.setOnCheckedChangeListener { p0, p1 ->
            binding.tvCdTo.isClickable = false
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        if (isEdit){
            binding.etCompany.setText(profession.company_name)

          if (profession.currently_working_here=="Yes"){
              binding.radioButtonCurrentWork.isChecked=true
              binding.tvCdTo.isClickable = false
          }

            binding.tvCdTo.setText(profession.to)
            binding.tvCdFrom.setText(profession.from)
            binding.etDesignation.setText(profession.designation)

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

     interface  Callbackk{
        fun onSuccessfullyEditedProfession(profession: Profession)
    }

    fun hitAddEditApi(){


        val hashMap = HashMap<String, String>()

        if (isEdit){
            hashMap["id"] =profession._id
        }

        profession.to=binding.tvCdTo.text.toString()
        profession.from=binding.tvCdFrom.text.toString()
        profession.company_name=binding.etCompany.text.toString()
        profession.designation=binding.etDesignation.text.toString()
        profession.currently_working_here=if (binding.radioButtonCurrentWork.isChecked) "Yes" else "No"


        hashMap["company_name"] =profession.company_name
        hashMap["currently_working_here"] =profession.currently_working_here
        hashMap["to"] =profession.to
        hashMap["from"] = profession.from
        hashMap["designation"] = profession.designation

        viewModel.addUpdateProfessionData(hashMap)
        viewModel.addUpdateProfessionLiveData.observe( (binding.root.context as? LifecycleOwner)!!){
            it?.let {
                if (it.status){
                   callbackk.onSuccessfullyEditedProfession(profession)
                    dismiss()
                }
            }
        }
    }
    fun isValid():Boolean{
        if (binding.etCompany.text.isEmpty()){
            makeToast("Please Enter Company Name")
            return false
        }else if (binding.etDesignation.text.isEmpty()){
            makeToast("Please Enter Desigantion")
            return false
        }else if (binding.tvCdFrom.text.isEmpty()){
            makeToast("Please Enter Starting Date")
            return false
        }else if (!binding.radioButtonCurrentWork.isChecked){

            if (binding.tvCdTo.text.isEmpty()){
                makeToast("Please Enter Work Status")
                return false
            }
        }


        return true
    }




    fun makeToast(message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

}