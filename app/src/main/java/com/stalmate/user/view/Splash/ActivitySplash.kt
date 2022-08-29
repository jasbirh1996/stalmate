package com.stalmate.user.view.Splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.stalmate.user.R
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.OnBoarding.ActivityOnBoardingScreen
import com.stalmate.user.view.dashboard.ActivityDashboard

class ActivitySplash : AppCompatActivity() {
    val SPLASH_DURATION: Long = 2000
   private lateinit var prefManager: PrefManager
    private  lateinit var context: ActivitySplash

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_splash)
        context = this
        prefManager = PrefManager(context)
        Handler(Looper.getMainLooper()).postDelayed({
            if (prefManager.keyIsLoggedIn) {
                startActivity(Intent(applicationContext, ActivityDashboard::class.java))
                finish()
            } else {
                startActivity(Intent(applicationContext, ActivityOnBoardingScreen::class.java))
                finish()
            }
            finish()



        }, SPLASH_DURATION)
    }
}