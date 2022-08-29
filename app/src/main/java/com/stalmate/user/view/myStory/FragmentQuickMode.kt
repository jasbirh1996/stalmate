package com.stalmate.user.view.myStory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentQuickModeBinding

class FragmentQuickMode : Fragment() {

    private lateinit var binding : FragmentQuickModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_quick_mode, container, false)
        binding = DataBindingUtil.bind<FragmentQuickModeBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*Common ToolBar SetUp*/
        toolbarSetUp()
    }

    private fun toolbarSetUp() {

        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE

        binding.toolbar.backButtonLeftText.text =  getString(R.string.quite_mode)

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

}