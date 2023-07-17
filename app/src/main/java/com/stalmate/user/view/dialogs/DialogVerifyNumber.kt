package com.stalmate.user.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.DialogueNumberVerifyBinding
import com.stalmate.user.databinding.DialougeAddEducationBinding
import com.stalmate.user.model.Education
import com.stalmate.user.model.Profession
import com.stalmate.user.viewmodel.AppViewModel
import java.util.*

class DialogVerifyNumber(
    private val context: Context,
    var viewModel: AppViewModel,
    var number : String,
    var callback : Callbackk
   ) {
    private var dialog: Dialog? = null
    private lateinit var binding: DialogueNumberVerifyBinding
    var isDialogShowing = false


    fun show() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialogue_number_verify, null)
        binding= DataBindingUtil.bind(view)!!;
        dialog!!.setContentView(binding.root);
        dialog!!.setCancelable(false)
        dialog!!.show()
        isDialogShowing = true

        startTimer()

        dialog!!.setCancelable(false)
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(null)

            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setLayout(DialogAddEditProfession.getWidth(context) / 100 * 90, LinearLayout.LayoutParams.WRAP_CONTENT)

            dialog!!.window!!.setDimAmount(0.5f)
            //dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        }

        binding.tventeredNumber.text = "Enter OTP Code sent to"+" "+"+"+number


        binding.btnCloseDialogue.setOnClickListener {
            dismiss()

        }

        binding.btnprocess.setOnClickListener {
            if (isValid()){
                hitNumberVerifyApi()
            }
        }
    }

    fun isValid():Boolean{
        if (binding.pinView.text.toString() != "1234"){
            makeToast("Please Enter Valid OTP")
            return false
        }
        return true
    }


    fun makeToast(message:String){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }


    fun hitNumberVerifyApi(){

        val hashMap = HashMap<String, String>()

        hashMap["number"] =number
        hashMap["otp"] = binding.pinView.text.toString()

        viewModel.numberVerify((context as BaseActivity).prefManager?.access_token.toString(),hashMap)
        viewModel.numberVerifyData.observe( (binding.root.context as? LifecycleOwner)!!){
            it?.let {
                if (it.status == true){
                    makeToast(it.message.toString())
                    dismiss()
                    callback.onSuccessFullyAddNumber()
                }
            }
        }
    }

    public interface Callbackk {
        fun onSuccessFullyAddNumber()

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

    private fun startTimer() {
        val timeDuration = 30000L
        val interval = 1000L
        val timer = object : CountDownTimer(timeDuration, interval) {
            override fun onTick(millisUntilFinished: Long) {
                binding.otpCountDown.text = formatSeconds(millisUntilFinished)

            }

            override fun onFinish() {
                binding.otpCountDown.visibility = View.GONE
                binding.otpResent.visibility = View.VISIBLE
                binding.otpResent.setOnClickListener {
                    startTimer()
                    binding.otpResent.visibility = View.GONE
                    binding.otpCountDown.visibility = View.VISIBLE
//                    getOtpApiCall()

                }
            }
        }

        timer.start()
    }

    fun formatSeconds(timeInSeconds: Long): String {
        val secondsLeft = timeInSeconds / 1000
        val ss = if (secondsLeft < 10) "0$secondsLeft" else "" + secondsLeft
        return "00:$ss"
    }

}