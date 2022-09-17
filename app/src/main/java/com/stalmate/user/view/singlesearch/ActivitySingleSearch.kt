package com.stalmate.user.view.singlesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivitySingleSearchBinding
import com.stalmate.user.view.dashboard.welcome.FragmentPlaceAutoComplete

class ActivitySingleSearch : AppCompatActivity() {
    lateinit var binding: ActivitySingleSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_search)


        if (intent.getStringExtra("type").toString() == "graduation") {

        loadFragment(FragmentSingleSearch(intent.getStringExtra("type").toString()))
        } else if (intent.getStringExtra("type").toString() == "major") {
            loadFragment(FragmentSingleSearch(intent.getStringExtra("type").toString()))

        } else if (intent.getStringExtra("type").toString() == "autoCompleteCountries") {
            loadFragment(FragmentPlaceAutoComplete(TypeFilter.REGIONS))
        }
        else if (intent.getStringExtra("type").toString() == "autoCompleteCities") {
            loadFragment(FragmentPlaceAutoComplete(TypeFilter.CITIES))
        }


    }


    private fun loadFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(binding!!.frame.id, fragment, fragmentTag)
            ft.addToBackStack(backStateName)
            ft.commit()
        }

    }
}