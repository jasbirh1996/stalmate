package com.stalmate.user.view.dashboard.funtime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.databinding.DataBindingUtil
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentFunTimeBinding
import com.stalmate.user.view.language.AdapterLanguage
import eightbitlab.com.blurview.RenderScriptBlur


class FragmentFunTime() : BaseFragment() {

    lateinit var binding: FragmentFunTimeBinding

    lateinit var adapterFunTime: AdapterFunTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFunTimeBinding>(inflater.inflate(R.layout.fragment_fun_time, container, false))!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterFunTime = AdapterFunTime(networkViewModel, requireContext())
        binding.rvRecyclerView.adapter = adapterFunTime

       /* callback.onClickHideBottom()*/

        binding.ivAddButton.setOnClickListener {
            startActivity(IntentHelper.getCreateReelsScreen(requireActivity()))

//            startActivity(Intent(context, ActivityfuntimeRandom::class.java))
        }

        var hashmap = HashMap<String, String>()
        hashmap.put("page", "1")
        networkViewModel.funtimeLiveData(hashmap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner) {

            it.let {
                adapterFunTime.submitList(it!!.results)
                Log.d("=============", it!!.results.size.toString())


            }
        }
    }

    public interface Callbackk {
        fun onClickHideBottom()
    }

}