package com.stalmate.user.view.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityDashboardBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.dashboard.Chat.FragmentChatCall
import com.stalmate.user.view.dashboard.HomeFragment.FragmentHome
import com.stalmate.user.view.dashboard.HomeFragment.FragmentMenu
import com.stalmate.user.view.dashboard.VideoReels.FragmentReels
import com.stalmate.user.view.dashboard.funtime.FragmentFunTime
import com.stalmate.user.view.profile.FragmentProfile

class ActivityDashboard : BaseActivity(), FragmentHome.Callback,
    FragmentMenu.Callback/*, FragmentFunTime.Callbackk*/ {

    companion object {
        private const val ID_HOME = 0
        private const val ID_EXPLORE = 1
        private const val ID_MESSAGE = 2
        private const val ID_NOTIFICATION = 3
        private const val ID_ACCOUNT = 4
    }

    private val TIME_INTERVAL = 2000
    var back_pressed: Long = 0
    private lateinit var binding: ActivityDashboardBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
       if(PrefManager.getInstance(this)?.getPopup("popup").equals("true")){
           WelcomeBackPopup()
           PrefManager.getInstance(this)?.setPopup("popup","false")
        }
        setContentView(binding.root)
        setupBottomBar()
        onNewIntent(intent)
    }

    fun WelcomeBackPopup(){
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.welcomeback_success_poppu,null)
        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        Handler(Looper.getMainLooper()).postDelayed({
            builder.dismiss()
        }, 3000)
        builder.show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.getStringExtra("notificationType") != null) {
            Log.d("casdafgg", intent.getStringExtra("notificationType").toString())
            if (intent.getStringExtra("notificationType") == "newFriendRequest") {
                startActivity(
                    IntentHelper.getOtherUserProfileScreen(this)!!
                        .putExtra("id", intent.getStringExtra("userId").toString())
                )
            } else if (intent.getStringExtra("notificationType") == "funtimeTag") {
                getReelVideoById(intent.getStringExtra("funTimeId").toString())
            }
        }
    }

    var page_count = 0
    var isApiRuning = false
    var handler: Handler? = null
    private fun getReelVideoById(id: String) {
        isApiRuning = true
        val index = 0
        val hashmap = HashMap<String, String>()
        hashmap.put("page", "1")
        hashmap.put("limit", "5")
        hashmap.put("id_user", "")
        hashmap.put("fun_id", id)
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(this) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            if (!it?.results.isNullOrEmpty()) {
                startActivity(
                    IntentHelper.getFullViewReelActivity(this)
                        ?.putExtra("data", it?.results?.get(0))
                )
            }
        }
    }


    private fun setupBottomBar() {
        selectedNavButton(binding.ivHome, binding.ivChat)
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment5, "5").hide(fragment5)
            .commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment4, "4").hide(fragment4)
            .commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment3, "3").hide(fragment3)
            .commit()
//        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(binding.fragmentContainerView.id, fragment1, "1").commit()

        binding.ivHome.setOnClickListener {
            mute(true)
            fm.beginTransaction().hide(active).show(fragment1).commit()
            active = fragment1
            selectedNavButton(binding.ivHome, binding.ivChat)
        }
        binding.ivFuntime.setOnClickListener {
            mute(true)
            /*fm.beginTransaction().hide(active).show(fragment2).commit()
            active = fragment2
            selectedNavButton(binding.ivFuntime, binding.ivHome)*/
            startActivity(IntentHelper.getFullViewReelActivity(this))
        }
        binding.ivCreateFuntime.setOnClickListener {
            startActivity(IntentHelper.getCreateReelsScreen(this))
        }
        binding.ivChat.setOnClickListener {
            mute(true)
            fm.beginTransaction().hide(active).show(fragment3).commit()
            active = fragment3
            selectedNavButton(binding.ivChat, binding.ivHome)
        }
    }

    fun mute(toMute: Boolean) {
        if (toMute) {
            if (active is FragmentHome) {
                Log.d("askldjalsd", "alksdjasd")
                (active as FragmentHome).onPause()
            } else {

            }
        }
    }

    val fragment1: Fragment = FragmentHome(this)
    val fragment2: Fragment = FragmentFunTime()

    //    val fragment3: Fragment = FragmentChatNCallBase()
    val fragment3: Fragment = FragmentChatCall()
    val fragment4: Fragment = FragmentReels()

    //  val fragment5: Fragment = FragmentFriend(this)
    val fragment5: Fragment = FragmentProfile(this)
    val fragmentProfile: FragmentProfile = FragmentProfile(this)
    val fm: FragmentManager = supportFragmentManager
    var active = fragment1

    private val fragmentMenu = FragmentMenu(this)
    val fragment6 = FragmentMenu(this)

    val pointToMyFuntime = MutableLiveData<Boolean>()

    override fun onCLickOnMenuButton() {

    }

    override fun onCLickOnProfileButton() {
        if (fragmentProfile.isAdded) {
            fm.beginTransaction().show(fragmentProfile).commit()
            return
        } else {
            fm.beginTransaction().add(binding.fragmentContainerView.id, fragmentProfile, "6")
                .hide(active)
                .commit()
        }
        active = fragmentProfile
    }

    override fun onScoll(toHide: Boolean) {
        if (toHide) {
            binding.llBottomNavView.visibility = View.GONE
        } else {
            binding.llBottomNavView.visibility = View.VISIBLE
        }
    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        when (active) {
            is FragmentHome -> {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2000)

            }
            is FragmentProfile -> {
                Log.d(";laksd;asd", "removeddd")
                fm.beginTransaction().remove(fragmentProfile).show(fragment1).commit()
                active = fragment1
                Log.d("lkjasdoas", active.javaClass.toString())
            }
            is FragmentMenu -> {
                fm.beginTransaction().remove(fragmentMenu).show(fragment6).commit()
                active = fragment1
            }
            else -> {
                Log.d("lkjasdoas", active.javaClass.toString())
                selectedNavButton(binding.ivHome, binding.ivChat)
                active = fragment1
            }
        }
    }

    fun loadDrawerFragment() {
        val backStateName = fragmentMenu.javaClass.name
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
            //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding.fragmentContainerView.id, fragmentMenu, backStateName)
            ft.commit()
            active = fragmentMenu
        }
    }

    /*override fun onClickBack() {
        selectedNavButton(binding.ivHome, binding.ivChat)
        active = fragment1
    }*/

    override fun onCLickBackButton() {

    }

/*    override fun onClickonFuntimeBackButton() {
       onClickBack()
    }*/
    /**
     * This method will start service to preCache videos from remoteUrl in to Cache Directory
     * So the Player will not reload videos from server if they are already loaded in cache
     */

    fun selectedNavButton(selected: ImageView, unSelected: ImageView) {
        when (selected.id) {
            binding.ivHome.id -> {
                binding.ivHome.setImageResource(R.drawable.btm_home_active)
            }
            binding.ivChat.id -> {
                binding.ivChat.setImageResource(R.drawable.btm_chat_active)
            }
        }
        when (unSelected.id) {
            binding.ivHome.id -> {
                binding.ivHome.setImageResource(R.drawable.btm_home)
            }
            binding.ivChat.id -> {
                binding.ivChat.setImageResource(R.drawable.btm_chat)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return  // no permission
                }
            }
            //Work fro here
        }
    }

    override fun onResume() {
        super.onResume()
        val readImagePermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        val readVideoPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_VIDEO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        val readAudioPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_AUDIO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

        if (
            ContextCompat.checkSelfPermission(
                this,
                readImagePermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                readVideoPermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                readAudioPermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permission not granted
            if (Build.VERSION.SDK_INT <= 29) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_CONTACTS,
                        readImagePermission,
                        readAudioPermission,
                        readVideoPermission,
                    ),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_CONTACTS,
                        readImagePermission,
                        readAudioPermission,
                        readVideoPermission
                    ),
                    1
                )
            }
        } else {
            // Permission has already been granted

        }
    }
}