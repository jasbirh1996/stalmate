package com.stalmate.user.view.dashboard

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityDashboardBinding
import com.stalmate.user.modules.reels.player.Constants
import com.stalmate.user.modules.reels.player.VideoPreLoadingService
import com.stalmate.user.view.dashboard.Chat.FragmentChatNCallBase
import com.stalmate.user.view.dashboard.Friend.FragmentFriend
import com.stalmate.user.view.dashboard.HomeFragment.FragmentHome
import com.stalmate.user.view.dashboard.HomeFragment.FragmentMenu
import com.stalmate.user.view.dashboard.VideoReels.FragmentReels
import com.stalmate.user.view.dashboard.funtime.FragmentFunTime

class ActivityDashboard : BaseActivity(), FragmentHome.Callback , FragmentFriend.Callbackk, FragmentMenu.Callback/*, FragmentFunTime.Callbackk*/{
    private val TIME_INTERVAL = 2000
    var back_pressed: Long = 0
    private lateinit var binding: ActivityDashboardBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomBar()
       onNewIntent(intent)

        /*loadDrawerFragment(FragmentMenu())*/
        //  setBottomNavigationInNormalWay(savedInstanceState)
    }

    companion object {
        private const val ID_HOME = 0
        private const val ID_EXPLORE = 1
        private const val ID_MESSAGE = 2
        private const val ID_NOTIFICATION = 3
        private const val ID_ACCOUNT = 4
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
       if (intent!!.getStringExtra("notificationType") != null) {
           Log.d("casdafgg",intent.getStringExtra("notificationType").toString())


           if (intent.getStringExtra("notificationType")=="newFriendRequest"){
               startActivity(
                   IntentHelper.getOtherUserProfileScreen(this)!!
                       .putExtra("id", intent.getStringExtra("userId").toString())
               )

           }else if (intent.getStringExtra("notificationType")=="funtimeTag"){
               getReelVideoById(intent.getStringExtra("funTimeId").toString())
           }


       }

    }

    var page_count = 0
    var isApiRuning = false
    var handler: Handler? = null
    private fun getReelVideoById(id:String) {
        isApiRuning = true
        val index = 0
        var hashmap = HashMap<String, String>()
        hashmap.put("page", "1")
        hashmap.put("limit", "5")
        hashmap.put("id_user", "")
        hashmap.put("fun_id", id)
        networkViewModel.funtimeLiveData(hashmap)
        networkViewModel.funtimeLiveData.observe(this) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            Log.d("========", "empty")
            if (it!!.results.isNotEmpty()) {
                startActivity(IntentHelper.getFullViewReelActivity(this)!!.putExtra("data",it!!.results[0]))
            }
        }
    }



    fun setupBottomBar() {
        binding.bottomNavigationView.selectedItemId = R.id.home

        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment5, "5").hide(fragment5).commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment4, "4").hide(fragment4).commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment1, "1").commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    mute(true)
                    fm.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1


                }
                R.id.funTime -> {
                    mute(false)
                    fm.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2


                }
                R.id.chat -> {
                    mute(true)
                    fm.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3

                }
                R.id.video -> {
                    mute(true)
                    fm.beginTransaction().hide(active).show(fragment4).commit()
                    active = fragment4

                }
                R.id.friend -> {
                    mute(true)
                    fm.beginTransaction().hide(active).show(fragment5).commit()
                    active = fragment5

                }
                else -> {
                }
            }
            true
        }
    }



fun mute(toMute:Boolean){
   if (toMute){
       if (active is FragmentFunTime){
           Log.d("askldjalsd","alksdjasd")
           (active as FragmentFunTime).pauseMusic()
       }else{
           
       }
   }
}


    val fragment1: Fragment = FragmentHome(this)
    val fragment2: Fragment = FragmentFunTime()
    val fragment3: Fragment = FragmentChatNCallBase()
    val fragment4: Fragment = FragmentReels()
    val fragment5: Fragment = FragmentFriend(this)
    val fm: FragmentManager = supportFragmentManager
    var active = fragment1

    override fun onCLickOnMenuButton() {
        toggleDrawer()
    }

    private fun toggleDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            loadDrawerFragment(FragmentMenu(this))
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            toggleDrawer()
        }


        else if (active is FragmentHome){
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

        }
            else{
                onClickBack()
            }


    }

    private fun loadDrawerFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding.frameDrawer.id, fragment, fragmentTag)
            ft.commit()
        }
    }

    override fun onClickBack() {
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    override fun onCLickBackButton() {
        toggleDrawer()
    }

/*    override fun onClickonFuntimeBackButton() {
       onClickBack()
    }*/
    /**
     * This method will start service to preCache videos from remoteUrl in to Cache Directory
     * So the Player will not reload videos from server if they are already loaded in cache
     */



}