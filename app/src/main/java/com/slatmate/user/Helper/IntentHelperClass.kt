package com.slatmate.user.Helper

import android.content.Context
import android.content.Intent
import com.slatmate.user.LoginPage.Otp.FragmentOTPEnter

public class IntentHelperClass {

    fun getFragmentOTPEnter(context: Context?): Intent? {
        return Intent(context, FragmentOTPEnter::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

}