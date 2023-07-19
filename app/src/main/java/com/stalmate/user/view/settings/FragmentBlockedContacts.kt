package com.stalmate.user.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentBlockedContactsBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.profile.BlockedUserAdapter

class FragmentBlockedContacts : BaseFragment() {
    // TODO: Rename and change types of parameters
    private lateinit var blockedUserAdapter: BlockedUserAdapter

    lateinit var binding: FragmentBlockedContactsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        var v = inflater.inflate(R.layout.fragment_blocked_contacts, container, false)
        binding = DataBindingUtil.bind<FragmentBlockedContactsBinding>(v)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.tvhead.text = "Blocked Contacts"

        binding.toolbar.topAppBar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()

        }
        getBlockLIst()

        binding.buttonAdd.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun getBlockLIst() {
        var hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap,prefManager?.access_token.toString())
        hashMap["limit"] = "5"
        hashMap["page"] = "1"
        networkViewModel.getBlockList(hashMap)

        networkViewModel.blockListLiveData.observe(requireActivity(), Observer {
            it.let {
                if (it!!.status) {
                    blockedUserAdapter = BlockedUserAdapter(
                        networkViewModel,
                        requireContext(),
                        object : BlockedUserAdapter.Callback {
                            override fun onListEmpty() {
                                binding.tvNoData.visibility = View.VISIBLE
                            }

                            override fun onItemRemove() {
                                getBlockLIst()
                            }
                        }, prefManager?.access_token.toString()
                    )
                    binding.rvList.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvList.adapter = blockedUserAdapter

                    if (it?.results.isNullOrEmpty()) {
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    blockedUserAdapter.submitList(it?.results as ArrayList<User>)

                }
            }
        })
    }


}