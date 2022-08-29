package com.stalmate.user.view.myStory

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentQuickModeBinding
import com.stalmate.user.databinding.FragmentSchaduleQuickModeBinding

class FragmentSchaduleQuickMode : Fragment() {

    private lateinit var binding : FragmentSchaduleQuickModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_schadule_quick_mode, container, false)

        binding = DataBindingUtil.bind<FragmentSchaduleQuickModeBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun toolbarSetUp() {

        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text =  getString(R.string.quite_mode)
        binding.toolbar.menuChat.visibility = View.VISIBLE
        binding.toolbar.menuChat.setImageDrawable((getResources().getDrawable(R.drawable.ic_quitemode_tick))

      /*  binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }*/
    }

}