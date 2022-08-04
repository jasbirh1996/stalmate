package com.slatmate.user.Helper

import android.content.Context
import android.content.Intent
import com.slatmate.user.view.Authentication.FragmentOTPEnter

public class IntentHelper {

    fun getFragmentOTPEnter(context: Context?): Intent? {
        return Intent(context, FragmentOTPEnter::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

}