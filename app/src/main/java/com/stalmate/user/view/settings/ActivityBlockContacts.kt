package com.stalmate.user.view.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.FragmentBlockedContactsBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.profile.BlockedUserAdapter

class ActivityBlockContacts : BaseActivity() {
    private lateinit var binding: FragmentBlockedContactsBinding
    private lateinit var blockedUserAdapter: BlockedUserAdapter

    override fun onClick(viewId: Int, view: View?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBlockedContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.toolbar.tvhead.text = "Blocked Contacts"

        binding.toolbar.topAppBar.setNavigationOnClickListener {
            finish()
        }
        getBlockLIst()

        binding.buttonAdd.setOnClickListener {
            finish()
        }
    }

    private fun getBlockLIst() {
        var hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap)
        hashMap["limit"] = "5"
        hashMap["page"] = "1"
        networkViewModel.getBlockList(hashMap)
        networkViewModel.blockListLiveData.observe(this) {
            it.let {
                if (it!!.status) {
                    blockedUserAdapter = BlockedUserAdapter(
                        networkViewModel,
                        applicationContext,
                        object : BlockedUserAdapter.Callback {
                            override fun onListEmpty() {
                                binding.tvNoData.visibility = View.VISIBLE
                            }

                            override fun onItemRemove() {

                            }
                        }, accessToken = prefManager?.access_token.toString())
                    binding.rvList.layoutManager = LinearLayoutManager(this)
                    binding.rvList.adapter = blockedUserAdapter

                    if (it.results.isEmpty()) {
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    blockedUserAdapter.submitList(it.results as ArrayList<User>)
                }
            }
        }
    }
}
