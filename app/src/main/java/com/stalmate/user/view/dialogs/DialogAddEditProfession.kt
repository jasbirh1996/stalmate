package com.stalmate.user.view.dialogs


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.stalmate.user.R
import com.stalmate.user.databinding.DialougeAddProfessionBinding
import com.stalmate.user.model.Profession
import com.stalmate.user.viewmodel.AppViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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
    var startCal = Calendar.getInstance()
    var endCal = Calendar.getInstance()
    var startYear = ""
    var endYear = ""
    var startMonth = ""
    var endMonth = ""
    var radioButtonWorking = false


    var isDialogShowing = false
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialouge_add_profession, null)
        binding= DataBindingUtil.bind(view)!!;
        dialog!!.setContentView(binding.root);
        dialog!!.setCancelable(false)
        dialog!!.show()
        isDialogShowing = true

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")

        dialog!!.setCancelable(false)
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(null)

            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setLayout(DialogAddEditProfession.getWidth(context) / 100 * 90, LinearLayout.LayoutParams.WRAP_CONTENT)

            dialog!!.window!!.setDimAmount(0.5f)
            //dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        }

        // Display Selected date in textbox
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            startCal.set(Calendar.YEAR, year)
            startCal.set(Calendar.MONTH, monthOfYear)
            startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                   startYear = year.toString()
                   startMonth = monthOfYear.toString()

                binding.tvCdFrom.text = sdf.format(startCal.time)
            }

        binding.tvCdFrom.setOnClickListener {
            DatePickerDialog(
                context, dateSetListener,
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Display Selected End date in textbox
        val dateEndSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            endCal.set(Calendar.YEAR, year)
            endCal.set(Calendar.MONTH, monthOfYear)
            endCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)

            endYear = year.toString()
            endMonth = monthOfYear.toString()
                binding.tvCdTo.text = sdf.format(endCal.time)
            }

        binding.tvCdTo.setOnClickListener {
            DatePickerDialog(
                context, dateEndSetListener,
                endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH),
                endCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSave.setOnClickListener {
            if (isValid()){


                if (isValidDates(binding.tvCdFrom.text.toString(),binding.tvCdTo.text.toString())){
                    hitAddEditApi()
                }else{
                    makeToast("End Date Must be greater than start date")
                }

              //
                Log.d("=================================", startYear)
                Log.d("=================================", endYear)
                Log.d("=================================", startMonth)
                Log.d("=================================", endMonth)

             /*   if (startYear < endYear){
                    hitAddEditApi()
                }

                if(startYear == endYear){
                    if (startMonth < endMonth){
                        hitAddEditApi()
                    }else {
                        makeToast("End working date should be grater")
                    }
                }*/
            }
        }

        binding.radioButtonCurrentWork.setOnClickListener {
            if (!radioButtonWorking){
                radioButtonWorking = true
                binding.radioButtonCurrentWork.isChecked=true
                binding.tvCdTo.isClickable = false
                binding.viewTo.visibility= View.VISIBLE
            }else{
                radioButtonWorking = false
                binding.radioButtonCurrentWork.isChecked=false
                binding.viewTo.visibility= View.GONE
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        if (isEdit){
            binding.etCompany.setText(profession.company_name)

          if (profession.currently_working_here=="Yes"){
              binding.radioButtonCurrentWork.isChecked=true
              binding.tvCdTo.isClickable = false
              radioButtonWorking = true
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
                makeToast("Please Enter End Date")
                return false
            }
        }

        return true
    }

    fun makeToast(message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    fun isValidDates(startDate:String,endDate:String): Boolean {
        Log.d("klajsdlasd",binding.radioButtonCurrentWork.isChecked.toString())
        if (!binding.radioButtonCurrentWork.isChecked){
            val dateFormat = SimpleDateFormat(
                "dd-MM-yyyy"
            )
            var convertedDate: Date? = Date()
            var convertedDate2 = Date()
            convertedDate = dateFormat.parse(startDate)
            convertedDate2 = dateFormat.parse(endDate)

            Log.d("klajsdlasd",convertedDate2.after(convertedDate).toString())

            return convertedDate2.after(convertedDate)
        }
        return true



    }


}