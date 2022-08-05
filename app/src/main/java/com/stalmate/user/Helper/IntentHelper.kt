package com.stalmate.user.Helper

import android.content.Context
import android.content.Intent
import com.stalmate.user.view.authentication.FragmentOTPEnter

public class IntentHelper {

    fun getFragmentOTPEnter(context: Context?): Intent? {
        return Intent(context, FragmentOTPEnter::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

}