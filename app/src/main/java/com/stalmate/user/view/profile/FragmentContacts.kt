package com.stalmate.user.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentContactsBinding
import com.stalmate.user.view.adapter.spinner.ContactsAdapter
import com.stalmate.user.view.profile.staticmodel.ModelContacts

class FragmentContacts(var callbackProfileEdit: FragmentProfileEdit.CAllback) : BaseFragment() {
    private lateinit var _binding: FragmentContactsBinding
    private lateinit var contactsAdapter: ContactsAdapter
    private var contactsArrayList = ArrayList<ModelContacts>()
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        binding.appbarContacts.topAppBar.setNavigationOnClickListener{
           callbackProfileEdit.onClickBackPress()
        }
        binding.appbarContacts.tvhead.text = "Contacts"
        contactsListListener()
        return binding.root
    }

    private fun contactsListListener() {
        contactsArrayList.clear()
        for (i in 0 until 4) {
            val modelContacts = ModelContacts()
            modelContacts.userName = "Gopichand"
            modelContacts.mutualFriends = "Mutual friends: 7"
            modelContacts.status = "0"
            contactsArrayList.add(modelContacts)
        }
        for (i in 0 until 3) {
            val modelContacts = ModelContacts()
            modelContacts.userName = "Vaibhav Nayak"
            modelContacts.mutualFriends = "Mutual friends: 2"
            modelContacts.status = "1"
            contactsArrayList.add(modelContacts)
        }
        for (i in 0 until 2) {
            val modelContacts = ModelContacts()
            modelContacts.userName = "Vaibhav Nayak"
            modelContacts.mutualFriends = "Mutual friends: 2"
            modelContacts.status = "1"
            contactsArrayList.add(modelContacts)
        }
        for (i in 0 until 2) {
            val modelContacts = ModelContacts()
            modelContacts.userName = "Vaibhav Nayak"
            modelContacts.mutualFriends = "Mutual friends: 2"
            modelContacts.status = "0"
            contactsArrayList.add(modelContacts)
        }
        contactsAdapter = ContactsAdapter(contactsArrayList, requireActivity())
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvContacts.layoutManager = linearLayoutManager
        binding.rvContacts.adapter = contactsAdapter
    }
}