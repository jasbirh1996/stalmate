package com.slatmate.user.view.Authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.slatmate.user.view.Dashboard.ActivityDashboard
import com.slatmate.user.R
import com.slatmate.user.databinding.FragmentLoginBinding

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)
        binding = DataBindingUtil.bind<FragmentLoginBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*click on page */
       binding.forgetPassword.setOnClickListener {
           findNavController().navigate(R.id.fragmentForgetPassword)

       }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.fragmentOTPEnter)
           /* val intent = Intent(context, ActivityDashboard::class.java)
            startActivity(intent)*/
        }


        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.fragmentSignUp)
        }
    }




}