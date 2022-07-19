package com.slatmate.user.LoginPage.Otp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.slatmate.user.R
import com.slatmate.user.databinding.FragmentForgetPasswordBinding
import com.slatmate.user.databinding.FragmentLoginBinding

class FragmentForgetPassword : Fragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_forget_password, container, false)
        binding = DataBindingUtil.bind<FragmentForgetPasswordBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*Click on page */

        binding.toolbar.hearderText.text = "Forget Password"



        binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.fragmentLogin)
        }

        binding.btnProcessOtp.setOnClickListener {
            findNavController().navigate(R.id.fragmentOTPEnter)
        }




    }




}