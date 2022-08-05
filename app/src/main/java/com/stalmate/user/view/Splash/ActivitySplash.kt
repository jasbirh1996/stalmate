package com.stalmate.user.view.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.stalmate.user.view.OnBoarding.ActivityOnBoardingScreen
import com.stalmate.user.R

class ActivitySplash : AppCompatActivity() {
    val SPLASH_DURATION: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(Intent(applicationContext, ActivityOnBoardingScreen::class.java))
            finish()
        }, SPLASH_DURATION)
    }
}