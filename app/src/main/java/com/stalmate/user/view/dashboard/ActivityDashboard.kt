package com.stalmate.user.view.dashboard


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.simform.custombottomnavigation.Model
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityDashboardBinding
import com.stalmate.user.view.dashboard.Chat.FragmentChatNCallBase
import com.stalmate.user.view.dashboard.Friend.FragmentFriend
import com.stalmate.user.view.dashboard.HomeFragment.FragmentHome
import com.stalmate.user.view.dashboard.HomeFragment.FragmentMenu
import com.stalmate.user.view.dashboard.VideoReels.FragmentReels
import com.stalmate.user.view.dashboard.funtime.FragmentFunTime


class ActivityDashboard : AppCompatActivity(), FragmentHome.Callback {

    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomBar()
        onNewIntent(intent)
        loadDrawerFragment(FragmentMenu())
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
        if (intent!!.getStringExtra("notificationType") != null) {
            startActivity(
                IntentHelper.getOtherUserProfileScreen(this)!!
                    .putExtra("id", intent.getStringExtra("userId").toString())
            )
        }
        super.onNewIntent(intent)
    }


    fun setupBottomBar() {
        binding.bottomNavigationView.selectedItemId = R.id.home
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment5, "5").hide(fragment5)
            .commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment4, "4").hide(fragment4)
            .commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment3, "3").hide(fragment3)
            .commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment2, "2").hide(fragment2)
            .commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment1, "1").commit()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fm.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                }
                R.id.funTime -> {

                    fm.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                }
                R.id.chat -> {

                    fm.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3
                }
                R.id.video -> {
                    fm.beginTransaction().hide(active).show(fragment4).commit()
                    active = fragment4
                }
                R.id.friend -> {
                    fm.beginTransaction().hide(active).show(fragment5).commit()
                    active = fragment5
                }

                else -> {

                }
            }
            true
        }

    }


    val fragment1: Fragment = FragmentHome(this)
    val fragment2: Fragment = FragmentFunTime()
    val fragment3: Fragment = FragmentChatNCallBase()
    val fragment4: Fragment = FragmentReels()
    val fragment5: Fragment = FragmentFriend()
    val fm: FragmentManager = supportFragmentManager
    var active = fragment1
    private fun setBottomNavigationInNormalWay(savedInstanceState: Bundle?) {

        //tvSelected.typeface = Typeface.createFromAsset(assets, "fonts/SourceSansPro-Regular.ttf")

        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: 2

        binding.bottomNavigation.apply {

            // If you don't pass activeIndex then by pass 0 here or call setSelectedIndex function only
            // setSelectedIndex()        // It will take 0 by default
            setSelectedIndex(activeIndex)

            add(
                Model(
                    icon = R.drawable.ic_botm_menu_funtime,
                    id = ID_HOME,
                    text = R.string.chat,
                )
            )
            add(
                Model(
                    icon = R.drawable.ic_botm_menu_chat_inactive,
                    id = ID_EXPLORE,
                    text = R.string.chat,
                    count = 0
                )
            )
            add(
                Model(
                    R.drawable.ic_botm_menu_home_inactive,
                    id = ID_MESSAGE,
                    text = R.string.chat,
                    count = 0
                )
            )
            add(
                Model(
                    R.drawable.ic_botm_menu_video_inactive,
                    id = ID_NOTIFICATION,
                    text = R.string.chat,
                    count = 0
                )
            )
            add(
                Model(
                    R.drawable.ic_botm_menu_friends_inactive,
                    id = ID_ACCOUNT,
                    text = R.string.chat,
                    count = 0
                )
            )

            // If you want to change count
            //  setCount(ID_NOTIFICATION, R.string.count_update)

            setOnShowListener {
                val name = when (it.id) {
                    ID_HOME -> "Home"
                    ID_EXPLORE -> "Explore"
                    ID_MESSAGE -> "Message"
                    ID_NOTIFICATION -> "Notification"
                    ID_ACCOUNT -> "Account"
                    else -> ""
                }

                /*      val bgColor = when (it.id) {
                          ID_HOME -> ContextCompat.getColor(this@ActivityDashboard, R.color.color_home_bg)
                          ID_EXPLORE -> ContextCompat.getColor(
                              this@ActivityDashboard,
                              R.color.color_favorite_bg
                          )
                          ID_MESSAGE -> ContextCompat.getColor(this@ActivityDashboard, R.color.color_chat_bg)
                          ID_NOTIFICATION -> ContextCompat.getColor(
                              this@ActivityDashboard,
                              R.color.color_notification_bg
                          )
                          ID_ACCOUNT -> ContextCompat.getColor(
                              this@ActivityDashboard,
                              R.color.color_profile_bg
                          )
                          else -> ContextCompat.getColor(this@ActivityDashboard, R.color.colorPrimary)
                      }*/

                //  binding.lnrLayout.setBackgroundColor(bgColor)
            }

            setOnClickMenuListener {
                val name = when (it.id) {
                    ID_HOME -> "HOME"
                    ID_EXPLORE -> "EXPLORE"
                    ID_MESSAGE -> "MESSAGE"
                    ID_NOTIFICATION -> "NOTIFICATION"
                    ID_ACCOUNT -> "ACCOUNT"
                    else -> ""
                }
            }

            setOnReselectListener {
                Toast.makeText(context, "item ${it.id} is reselected.", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onCLickOnMenuButton() {
        toggleDrawer()
    }

    private fun toggleDrawer() {

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            toggleDrawer()
        } else {
           super.onBackPressed()
        }

    }





    private fun loadDrawerFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(binding.frameDrawer.id, fragment, fragmentTag)

           // ft.addToBackStack(backStateName)

            ft.commit()
        }
    }

}