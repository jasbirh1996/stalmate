package com.slatmate.user.LoginPage.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.slatmate.user.R
import com.slatmate.user.databinding.FragmentLoginBinding
import com.slatmate.user.databinding.FragmentSignUpBinding

class FragmentSignUp : Fragment() {
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_sign_up, container, false)

        binding = DataBindingUtil.bind<FragmentSignUpBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.hearderText.text = "Sign Up"

        binding.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck){
                binding.rdmale.setChecked(true)
                binding.rdFamel.setChecked(false)
                binding.rdOthers.setChecked(false)
            }
        }

        binding.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck){
                binding.rdmale.setChecked(false)
                binding.rdFamel.setChecked(true)
                binding.rdOthers.setChecked(false)
            }
        }


        binding.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck){
                binding.rdmale.setChecked(false)
                binding.rdFamel.setChecked(false)
                binding.rdOthers.setChecked(true)
            }
        }
    }

}