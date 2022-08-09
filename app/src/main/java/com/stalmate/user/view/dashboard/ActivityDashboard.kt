package com.stalmate.user.view.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityDashboardBinding
import com.stalmate.user.view.dashboard.Chat.ChatCallTabFragment
import com.stalmate.user.view.dashboard.Friend.FragmentFriendList
import com.stalmate.user.view.dashboard.HomeFragment.FragmentHome
import com.stalmate.user.view.dashboard.VideoReels.FragmentReels
import com.stalmate.user.view.dashboard.funtime.FragmentFunTime

class ActivityDashboard : AppCompatActivity() {

    private lateinit var binding : ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.home
        replaceFragment(FragmentHome())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(FragmentHome())
                R.id.funTime -> replaceFragment(FragmentFunTime())
                R.id.chat -> replaceFragment(ChatCallTabFragment())
                R.id.video -> replaceFragment(FragmentReels())
                R.id.friend -> replaceFragment(FragmentFriendList())

                else ->
                {

                }
            }
            true
        }


    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmenTransaction = fragmentManager.beginTransaction()
        fragmenTransaction.replace(R.id.frameLayout, fragment)
        fragmenTransaction.commit()
    }
}