package com.stalmate.user.modules.contactSync

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.dashboard.ActivityDashboardNew


class Authenticator(context: Context) : AbstractAccountAuthenticator(context) {
    private val mContext: Context
    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String, authTokenType: String, requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle {
        val intent = Intent(mContext, ActivityDashboardNew::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account, options: Bundle
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse, account: Account,
        authTokenType: String, options: Bundle
    ): Bundle {
        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE)
        return result
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse, account: Account,
        authTokenType: String, options: Bundle
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse, account: Account,
        features: Array<String>
    ): Bundle? {
        return null
    }

    init {
        mContext = context
    }
}