package com.slatmate.user.view.authentication

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
import com.example.mechanicforyoubusiness.utilities.PrefManager
import com.slatmate.user.R
import com.slatmate.user.base.BaseFragment
import com.slatmate.user.databinding.FragmentOTPEnterBinding
import com.slatmate.user.view.OnBoarding.ActivityOnBoardingScreen
import com.slatmate.user.view.dashboard.ActivityDashboard


class FragmentOTPEnter : BaseFragment() {
    val DURATION: Long = 2000
    private lateinit var binding: FragmentOTPEnterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(com.slatmate.user.R.layout.fragment_o_t_p_enter, container, false)
        binding = DataBindingUtil.bind<FragmentOTPEnterBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

        startTimer()

        binding.btnProcess.setOnClickListener {
            /*Otp Verify Api Call*/
//            otpVerifyApiCall()


            val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
            val view = layoutInflater.inflate(R.layout.sign_up_success_poppu,null)
            builder.setView(view)
            builder.setCanceledOnTouchOutside(false)
            builder.show()

            Handler(Looper.getMainLooper()).postDelayed({

                val intent = Intent(context, ActivityDashboard::class.java)
                startActivity(intent)
                requireActivity().finish()
            }, DURATION)

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
                    val intent = Intent(context, ActivityDashboard::class.java)
                    startActivity(intent)
                    requireActivity().finish()
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
        binding.toolbar.toolBarCenterText.text =  getString(com.slatmate.user.R.string.forget_post)
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
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