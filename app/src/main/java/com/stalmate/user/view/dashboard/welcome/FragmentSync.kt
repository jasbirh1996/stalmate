package com.stalmate.user.view.dashboard.welcome

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.content.Context.ACCOUNT_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSyncBinding
import com.stalmate.user.modules.contactSync.SyncService
import com.stalmate.user.utilities.Constants


class FragmentSync : BaseFragment() {
    var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS
    )
    var WRITE_REQUEST_CODE = 100
    lateinit var syncBroadcastreceiver: SyncBroadcasReceiver
    private lateinit var mAccount: Account
    lateinit var binding: FragmentSyncBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_sync, container, false)
        binding = DataBindingUtil.bind<FragmentSyncBinding>(view)!!
        binding.btnNext.text = "Done"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var permissionArray = arrayOf(Manifest.permission.READ_CONTACTS)
        if (isPermissionGranted(permissionArray, requireContext())) {
            Log.d("alskjdasd", ";aosjldsad")
            requireActivity().startService(
                Intent(requireActivity(), SyncService::class.java)
            )
        }
        val filter = IntentFilter()
        filter.addAction(Constants.ACTION_SYNC_COMPLETED)
        requireActivity().requestPermissions(permissions, WRITE_REQUEST_CODE)

        syncBroadcastreceiver = SyncBroadcasReceiver()
        requireActivity().registerReceiver(syncBroadcastreceiver, filter)
        binding.toggleSyncGoogle.isChecked = isAccountAdded()

        binding.toggleSyncGoogle.setOnCheckedChangeListener { compoundButton, active ->
            if (active) {
                retreiveGoogleContacts()
            } else {
                removeAccount()
            }
        }

        binding.btnNext.setOnClickListener {
            //callback.onClickOnNextButtonOnSyncPage()

            /*    startActivity(
                    IntentHelper.getSearchScreen(requireContext())
                )*/
            findNavController().popBackStack()
        }
    }


    fun removeAccount() {
        // Get an instance of the Android account manager
        val accountManager = requireActivity().getSystemService(
            ACCOUNT_SERVICE
        ) as AccountManager

        if (isAccountAdded()) {
            var acc = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
            ContentResolver.cancelSync(acc, "com.android.contacts");
            accountManager.removeAccountExplicitly(acc)
        }
    }

    fun isAccountAdded(): Boolean {

        // Get an instance of the Android account manager
        val accountManager = requireActivity().getSystemService(ACCOUNT_SERVICE) as AccountManager

        for (i in 0 until accountManager.accounts.size) {
            if (accountManager.accounts[i].type == Constants.ACCOUNT_TYPE) {
                return true
            }
        }
        return false
    }

    private fun retreiveGoogleContacts() {

        mAccount = createSyncAccount(requireActivity())

        Log.d("asldkjalsda", "sync")
        ContentResolver.cancelSync(mAccount, "com.android.contacts");
        ContentResolver.cancelSync(mAccount, "com.android.contacts");

        Handler(Looper.getMainLooper()).postDelayed(Runnable {


            // Set sync for this account.
            // Set sync for this account.
            val extras = Bundle()
/*        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true)*/
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.setIsSyncable(
                mAccount,
                "com.android.contacts",
                1
            ) // Mandatory since 3.1
            // ContentResolver.setSyncAutomatically(mAccount, "com.android.contacts", false)
            ContentResolver.requestSync(mAccount, "com.android.contacts", extras)

            // ContentResolver.addPeriodicSync(mAccount, "com.stalmate.user", extras, POLL_FREQUENCY)


        }, 500)
        showLoader()
    }

    fun createSyncAccount(context: Context): Account {

        // Create the account type and default account
        val newAccount = Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        // Get an instance of the Android account manager
        val accountManager = context.getSystemService(ACCOUNT_SERVICE) as AccountManager
        /*
        * Add the account and account type, no password or user data
        * If successful, return the Account object, otherwise report an error.
        */
        return if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
           * If you don't set android:syncable="true" in
           * in your <provider> element in the manifest,
           * then call context.setIsSyncable(account, AUTHORITY, 1)
           * here.
           */

            ContentResolver.setIsSyncable(newAccount, "com.android.contacts", 1)
            ContentResolver.setSyncAutomatically(newAccount, "com.android.contacts", true)
            newAccount
        } else {
            Log.d("asdasd", "ppp")
            /*
          * The account exists or some other error occurred. Log this, report it,
          * or handle it internally.
          */
            Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        }
    }

    public interface Callback {
        fun onClickOnNextButtonOnSyncPage()
    }

    inner class SyncBroadcasReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            if (p1!!.action == Constants.ACTION_SYNC_COMPLETED) {
                dismissLoader()
                Log.d("==========wew", "wwwwwwwwwwww=====121=====wwwwwwwwwwwwww")
                makeToast("Synced")
                if (p1.extras!!.getString("contacts") != null) {
                    Log.d("==========wew", "wwwwwwwwwwwwwwwwwwwwwww11www")
                    startActivity(
                        IntentHelper.getSearchScreen(requireContext())!!
                            .putExtra("contacts", p1.extras!!.getString("contacts").toString())
                    )
                }
            }
        }
    }

    override fun onDestroy() {
      //  requireActivity().unregisterReceiver(syncBroadcastreceiver)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()


    }


}