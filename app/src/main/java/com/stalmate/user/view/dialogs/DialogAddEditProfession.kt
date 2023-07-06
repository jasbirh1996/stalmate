package com.stalmate.user.view.dialogs


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.SystemClock
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
import com.stalmate.user.view.profile.ActivityProfileEdit
import com.stalmate.user.viewmodel.AppViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class DialogAddEditProfession(
    private val context: Context,
    var profession: Profession,
    var viewModel: AppViewModel,
    var isEdit: Boolean,
    var callbackk: Callbackk
) {
    private var dialog: Dialog? = null
    private lateinit var binding: DialougeAddProfessionBinding
    var startYear = ""
    var endYear = ""
    var startMonth = ""
    var endMonth = ""
    var isDialogShowing = false
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialouge_add_profession, null)
        binding = DataBindingUtil.bind(view)!!;
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
            dialog!!.window!!.setLayout(
                DialogAddEditProfession.getWidth(context) / 100 * 90,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            dialog!!.window!!.setDimAmount(0.5f)
            //dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        }

        val startCal = Calendar.getInstance()
        // Display Selected date in textbox
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                startCal.set(Calendar.YEAR, year)
                startCal.set(Calendar.MONTH, monthOfYear)
                startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                startYear = year.toString()
                startMonth = monthOfYear.toString()

                binding.tvCdFrom.setText(sdf.format(startCal.time))

                startTimeStamp =
                    (SimpleDateFormat("dd-MM-yyyy").parse(binding.tvCdFrom.text.toString())?.time
                        ?: 0)
            }

        binding.tvCdFrom.setOnClickListener {
            DatePickerDialog(
                context, dateSetListener,
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val endCal = Calendar.getInstance()
        // Display Selected End date in textbox
        val dateEndSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                endCal.set(Calendar.YEAR, year)
                endCal.set(Calendar.MONTH, monthOfYear)
                endCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                endYear = year.toString()
                endMonth = monthOfYear.toString()
                binding.tvCdTo.setText(sdf.format(endCal.time))

                endTimeStamp =
                    (SimpleDateFormat("dd-MM-yyyy").parse(binding.tvCdTo.text.toString())?.time
                        ?: 0)
            }

        binding.tvCdTo.setOnClickListener {
            DatePickerDialog(
                context, dateEndSetListener,
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        var mLastClickTime = 0L
        binding.btnSave.setOnClickListener {
            // mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            // do your magic here
            if (isValid()) {
                if (isValidDates(
                        binding.tvCdFrom.text.toString(),
                        binding.tvCdTo.text.toString()
                    )
                ) {
                    hitAddEditApi()
                } else {
                    makeToast("End Date Must be greater than start date")
                }
            }
        }

        binding.radioButtonCurrentWork.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.tvCdTo.visibility = View.GONE
            } else {
                binding.tvCdTo.visibility = View.VISIBLE
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        if (isEdit) {
            startTimeStamp = (SimpleDateFormat("dd-MM-yyyy").parse(profession.from)?.time ?: 0)
            endTimeStamp = (SimpleDateFormat("dd-MM-yyyy").parse(profession.to)?.time ?: 0)

            binding.etDesignation.setText(profession.designation)
            binding.etCompany.setText(profession.company_name)
            binding.tvCdFrom.setText(profession.from)
            binding.tvCdTo.setText(profession.to)
            binding.radioButtonCurrentWork.isChecked = (profession.currently_working_here == "Yes")
            if (binding.radioButtonCurrentWork.isChecked) {
                binding.tvCdTo.visibility = View.GONE
            } else {
                binding.tvCdTo.visibility = View.VISIBLE
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

    interface Callbackk {
        fun onSuccessfullyEditedProfession(profession: Profession)
    }

    fun hitAddEditApi() {


        val hashMap = HashMap<String, String>()

        if (isEdit) {
            hashMap["id"] = profession._id
        }

        profession.to = binding.tvCdTo.text.toString()
        profession.from = binding.tvCdFrom.text.toString()
        profession.company_name = binding.etCompany.text.toString()
        profession.designation = binding.etDesignation.text.toString()
        profession.currently_working_here =
            if (binding.radioButtonCurrentWork.isChecked) "Yes" else "No"


        hashMap["company_name"] = profession.company_name
        hashMap["currently_working_here"] = profession.currently_working_here
        hashMap["to"] = profession.to
        hashMap["from"] = profession.from
        hashMap["designation"] = profession.designation

        viewModel.addUpdateProfessionData(
            (context as ActivityProfileEdit)?.prefManager?.access_token.toString(),hashMap)
        viewModel.addUpdateProfessionLiveData.observe((binding.root.context as? LifecycleOwner)!!) {
            it?.let {
                if (it.status) {
                    callbackk.onSuccessfullyEditedProfession(profession)
                    dismiss()
                }
            }
        }
    }

    fun isValid(): Boolean {
        if (binding.etCompany.text.isEmpty()) {
            makeToast("Please Enter Company Name")
            return false
        } else if (binding.etDesignation.text.isEmpty()) {
            makeToast("Please Enter Desigantion")
            return false
        } else if (binding.tvCdFrom.text.isEmpty()) {
            makeToast("Please Enter Starting Date")
            return false
        } else if (!binding.radioButtonCurrentWork.isChecked) {
            if (binding.tvCdTo.text.isEmpty()) {
                makeToast("Please Enter End Date")
                return false
            }
        }

        return true
    }

    fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    var startTimeStamp: Long = 0
    var endTimeStamp: Long = 0

    fun isValidDates(startDate: String, endDate: String): Boolean {
        if (!binding.radioButtonCurrentWork.isChecked) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            var convertedDate: Date? = Date()
            var convertedDate2 = Date()
            convertedDate = dateFormat.parse(startDate)
            convertedDate2 = dateFormat.parse(endDate)
            return convertedDate2.after(convertedDate)
        }
        return true
    }
}