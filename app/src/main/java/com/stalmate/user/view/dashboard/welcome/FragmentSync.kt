package com.stalmate.user.view.dashboard.welcome

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.ACCOUNT_SERVICE
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSyncBinding
import com.stalmate.user.utilities.Constants


class FragmentSync(var callback: Callback) : BaseFragment() {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleSyncGoogle.isChecked = isAccountAdded()

        binding.toggleSyncGoogle.setOnCheckedChangeListener { compoundButton, active ->
            if (active) {
                retreiveGoogleContacts()
            }else{
                removeAccount()
            }
        }

        binding.btnNext.setOnClickListener {
            callback.onClickOnNextButtonOnSyncPage()
        }
    }


    fun removeAccount(){
        // Get an instance of the Android account manager
        val accountManager = requireActivity().getSystemService(
            ACCOUNT_SERVICE
        ) as AccountManager

        if (isAccountAdded()){
            var acc=  Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
            accountManager.removeAccountExplicitly(acc)
        }
    }

    fun isAccountAdded():Boolean{

        // Get an instance of the Android account manager
        val accountManager = requireActivity().getSystemService(ACCOUNT_SERVICE) as AccountManager

        for (i in 0 until accountManager.accounts.size){
            if (accountManager.accounts[i].type==Constants.ACCOUNT_TYPE){
                return true
            }
        }
        return false
    }

    private fun retreiveGoogleContacts() {

        mAccount= createSyncAccount(requireActivity())
        var bundle=Bundle()
        bundle.putBoolean("force",true)
        bundle.putBoolean("expedited",true)

        Log.d("asldkjalsda","sync")
        ContentResolver.requestSync(mAccount, "com.stalmate.user", bundle)
    }

    fun createSyncAccount(context: Context): Account {
        showLoader()
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
            Log.d("asdasd","pppooo")
            ContentResolver.setIsSyncable(newAccount, "com.android.contacts", 1)
            ContentResolver.setSyncAutomatically(newAccount, "com.android.contacts", true)

            newAccount
        } else {
            Log.d("asdasd","ppp")
            /*
          * The account exists or some other error occurred. Log this, report it,
          * or handle it internally.
          */
            Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE)
        }
    }

    public interface Callback{
        fun onClickOnNextButtonOnSyncPage()
    }
}