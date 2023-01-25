package com.stalmate.user.view.dashboard.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.databinding.FragmentCallsBinding
import com.stalmate.user.view.adapter.CallsAdapter
import com.stalmate.user.view.dashboard.Chat.model.CallsModel

class FragmentCalls : Fragment() {
    private lateinit var _binding: FragmentCallsBinding
    private val binding get() = _binding
    private lateinit var callsListAdapter: CallsAdapter
    private var callsArrayList = ArrayList<CallsModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallsBinding.inflate(inflater, container, false)
        callsListListener()
        return binding.root
    }

    private fun callsListListener() {
        callsArrayList.clear()
        for (i in 0 until 5) {
            val callsModel = CallsModel()
            callsModel.userName = "Gopichand"
            callsModel.callCount = "(4)"
            callsModel.dateTime = "Today, 10:00 AM"
            callsModel.inOutCall="1"
            callsModel.callVideo="1"
            callsArrayList.add(callsModel)
        }
        for (i in 0 until 3) {
            val callsModel = CallsModel()
            callsModel.userName = "Vaibhav Nayak"
            callsModel.callCount = "(2)"
            callsModel.dateTime = "Yesterday, 10:00 AM"
            callsModel.inOutCall="0"
            callsModel.callVideo="0"
            callsArrayList.add(callsModel)
        }
        callsListAdapter = CallsAdapter(callsArrayList, requireActivity())
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCallsUsers.layoutManager = linearLayoutManager
        binding.rvCallsUsers.adapter = callsListAdapter
    }
}