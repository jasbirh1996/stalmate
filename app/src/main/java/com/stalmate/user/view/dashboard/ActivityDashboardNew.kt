package com.stalmate.user.view.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityDashboardNewBinding
import com.stalmate.user.view.dashboard.HomeFragment.FragmentDashboard
import com.stalmate.user.view.dashboard.HomeFragment.FragmentHome
import com.stalmate.user.view.dashboard.HomeFragment.FragmentMenu
import com.stalmate.user.view.dashboard.funtime.FragmentFunTime

class ActivityDashboardNew : BaseActivity(), FragmentMenu.Callback {
    lateinit var binding:ActivityDashboardNewBinding

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil. setContentView(this,R.layout.activity_dashboard_new)
        drawerLayout= findViewById(R.id.drawerLayout)
        loadFragment(fragmentDashboard)
    //    onNewIntent(intent)
    }

    private fun loadFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            //fragment not in back stack, create it.
            Log.d("laksdasd","alsjdasd")
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding.frame.id, fragment, fragmentTag)
            ft.addToBackStack(fragmentTag)

            ft.commit()
        }
    }

    fun onClickOnFuntime(){
        loadFragment(fragmentFuntime)
    }
    val fragmentFuntime: Fragment = FragmentFunTime()
    val fragmentDashboard: Fragment = FragmentDashboard()


    override fun onBackPressed() {
     var currentVisibleFragment=  supportFragmentManager.findFragmentById(binding.frame.id)
        if (currentVisibleFragment !is FragmentDashboard){
            supportFragmentManager.popBackStack()
        }else{
            super.onBackPressed()
        }
    }

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
                startActivity(
                    IntentHelper.getFullViewReelActivity(this)!!.putExtra("data", it!!.results[0])
                )
            }
        }
    }

    lateinit var drawerLayout: DrawerLayout
    public fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            loadDrawerFragment(FragmentMenu(this))
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }
    private fun loadDrawerFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding.frameDrawer.id, fragment, fragmentTag)
            ft.commit()
        }
    }

    override fun onCLickBackButton() {
        onBackPressed()
    }
}