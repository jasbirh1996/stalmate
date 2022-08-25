package com.stalmate.user.Helper

import android.content.Context
import android.content.Intent
import com.stalmate.user.modules.reels.activity.ActivityVideoRecorder
import com.stalmate.user.view.authentication.FragmentOTPEnter
import com.stalmate.user.view.profile.ActivityFollowersFollowingScreen
import com.stalmate.user.view.profile.ActivityOtherUserProfile
import com.stalmate.user.view.profile.ActivityProfile
import com.stalmate.user.view.profile.ActivityProfileEdit

public class IntentHelper {

    companion object{
        fun getFragmentOTPEnter(context: Context?): Intent? {
            return Intent(context, FragmentOTPEnter::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        fun getProfileScreen(context: Context?): Intent? {
            return Intent(context, ActivityProfile::class.java)
               .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        fun getOtherUserProfileScreen(context: Context?): Intent? {
            return Intent(context, ActivityOtherUserProfile::class.java)
            /*   .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)*/
        }
        fun getProfileEditScreen(context: Context?): Intent? {
            return Intent(context, ActivityProfileEdit::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        fun getCreateReelsScreen(context: Context?): Intent? {
            return Intent(context, ActivityVideoRecorder::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        fun getFollowersFollowingScreen(context: Context?): Intent? {
            return Intent(context, ActivityFollowersFollowingScreen::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

    }


}