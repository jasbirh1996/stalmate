package com.stalmate.user.view.authentication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentOTPEnterBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.dashboard.ActivityDashboard


class FragmentOTPEnter : BaseFragment() {
    val DURATION: Long = 2000
    private lateinit var binding: FragmentOTPEnterBinding

    var email : String = ""
    var forgetPasswordScreen : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_o_t_p_enter, container, false)
        binding = DataBindingUtil.bind<FragmentOTPEnterBinding>(view)!!
        email = requireArguments().getString("email").toString()
        forgetPasswordScreen = requireArguments().getBoolean("Boolean")
        Log.d("akjsdad",email)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

        startTimer()

        getOtpApiCall()



        binding.btnProcess.setOnClickListener {
            /*Otp Verify Api Call*/

            if (email.isEmpty()) {
                otpVerifyApiCall()
            }else{
                otpVerifyForgotApiCall()
            }

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

                    bundle.putString("email",email)
                    bundle.putString("otp","1234")
                    findNavController().navigate(R.id.fragmentPasswordReset,bundle)

                }
            }

        }
    }

    private fun getOtpApiCall() {
        val hashMap = HashMap<String, String>()

        hashMap["email"] = PrefManager.getInstance(requireContext())!!.userDetail.results.get(0).email.toString()

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

        hashMap["email"] = PrefManager.getInstance(requireContext())!!.userDetail.results.get(0).email.toString()
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
        binding.toolbar.backButtonRightText.visibility = View.GONE

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