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
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.OnBoarding.ActivityOnBoardingScreen
import com.stalmate.user.view.authentication.ActivityAuthentication
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.ActivityDashboardNew

class ActivitySplash : BaseActivity() {
    val SPLASH_DURATION: Long = 1000
    private lateinit var prefManager: PrefManager
    private lateinit var context: ActivitySplash
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_splash)
        context = this
        prefManager = PrefManager(context)
        Handler(Looper.getMainLooper()).postDelayed({
            if (prefManager.keyIsLoggedIn) {
                startActivity(Intent(applicationContext, ActivityDashboardNew::class.java))
                finish()
            } else {
                if (prefManager.keyIsOldusere) {
                    startActivity(
                        Intent(
                            context,
                            ActivityAuthentication::class.java
                        ).putExtra("screen", "login")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                } else {
                    startActivity(Intent(applicationContext, ActivityOnBoardingScreen::class.java))
                }

                finish()
            }
            finish()
        }, SPLASH_DURATION)
    }


}