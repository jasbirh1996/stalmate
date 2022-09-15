package com.stalmate.user.view.dashboard.HomeFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stalmate.user.R

class FragmentSearchList : Fragment() {

    companion object {
        fun newInstance() = FragmentSearchList()
    }

    private lateinit var viewModel: FragmentSearchListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragment_search_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentSearchListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}