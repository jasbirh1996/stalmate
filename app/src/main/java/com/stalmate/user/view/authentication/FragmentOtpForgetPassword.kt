package com.stalmate.user.view.authentication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentOtpForgetPasswordBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.dashboard.ActivityDashboard


class FragmentOtpForgetPassword : BaseFragment() {

    val DURATION: Long = 2000
    private lateinit var binding: FragmentOtpForgetPasswordBinding
    var email : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_otp_forget_password, container, false)
        email = requireArguments().getString("email").toString()

        binding = DataBindingUtil.bind<FragmentOtpForgetPasswordBinding>(view)!!
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

     /*   startTimer()

        getOtpApiCall()
        */


        binding.btnProcess.setOnClickListener {
            /*Otp Verify Api Call*/

            otpVerifyForgotApiCall()
            

        }
    }


    private fun otpVerifyForgotApiCall() {
        val hashMap = HashMap<String, String>()

        hashMap["email"] = email

        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.otpVerify(hashMap)

        networkViewModel.otpVerifyData.observe(requireActivity()){

            it?.let {

                if (it.status == true){
                    binding.progressBar.visibility = View.GONE
                    val bundle = Bundle()

                   /* bundle.putString("email",email)
                    bundle.putString("otp","1234")*/
                    findNavController().navigate(R.id.fragmentPasswordReset,bundle)

                }
            }

        }
    }

    private fun getOtpApiCall() {
        val hashMap = HashMap<String, String>()

        hashMap["email"] = email

        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.otpVerify(hashMap)

        networkViewModel.otpVerifyData.observe(requireActivity()){

            it?.let {

                if (it.status == true){
                    binding.progressBar.visibility = View.GONE
                }
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun otpVerifyApiCall() {

        val hashMap = HashMap<String, String>()

        hashMap["email"] = email
        hashMap["otp"] = binding.pinView.text.toString()
        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.otpVerify(hashMap)
        networkViewModel.otpVerifyData.observe(requireActivity()){

            it?.let {
                val message = it.message

                if (it.status == true){

                    val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
                    val view = layoutInflater.inflate(R.layout.sign_up_success_poppu,null)
                    builder.setView(view)
                    builder.setCanceledOnTouchOutside(false)

                    PrefManager.getInstance(requireContext())!!.keyIsLoggedIn = true

                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(requireContext(), ActivityDashboard::class.java)
                        startActivity(intent)

                        builder.dismiss()
                        activity?.finish()
                    }, DURATION)
                    builder.show()


                    makeToast(message)
                }else{
                    makeToast(message)
                }

            }
            binding.progressBar.visibility = View.GONE
        }

    }

    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text =  getString(R.string.forget_post)
      //  binding.toolbar.backButtonRightText.visibility = View.GONE

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
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
                    getOtpApiCall()

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