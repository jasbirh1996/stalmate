package com.slatmate.user.LoginPage.Otp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.slatmate.user.R
import com.slatmate.user.databinding.FragmentLoginBinding
import com.slatmate.user.databinding.FragmentOTPEnterBinding

class FragmentOTPEnter : Fragment() {

    private lateinit var binding: FragmentOTPEnterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_o_t_p_enter, container, false)




        binding = DataBindingUtil.bind<FragmentOTPEnterBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.hearderText.text =  "Forget Password"

        binding.btnProcess.setOnClickListener {

        }
    }

}