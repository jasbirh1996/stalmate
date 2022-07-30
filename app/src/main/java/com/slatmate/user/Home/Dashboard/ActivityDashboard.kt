package com.slatmate.user.Home.Dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.slatmate.user.Home.Dashboard.Chat.ChatCallTabFragment
import com.slatmate.user.Home.Dashboard.Friend.FragmentFriendList
import com.slatmate.user.Home.Dashboard.HomeFragment.FragmentHome
import com.slatmate.user.Home.Dashboard.VideoReels.FragmentReels
import com.slatmate.user.Home.Dashboard.funtime.FragmentFunTime
import com.slatmate.user.R
import com.slatmate.user.databinding.ActivityDashboardBinding

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