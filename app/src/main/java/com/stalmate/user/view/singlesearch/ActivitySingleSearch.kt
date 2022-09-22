package com.stalmate.user.view.singlesearch

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
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

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var fragment = supportFragmentManager.findFragmentById(binding.frame.id)
                if (p0 != null) {
                    if (fragment is FragmentSingleSearch) {
                        fragment.search(binding.etSearch.text.toString())
                    } else if (fragment is FragmentPlaceAutoComplete) {
                        fragment.search(binding.etSearch.text.toString())
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


        if (intent.getStringExtra("type").toString() == "graduation") {

            loadFragment(FragmentSingleSearch(intent.getStringExtra("type").toString()))
        } else if (intent.getStringExtra("type").toString() == "major") {
            loadFragment(FragmentSingleSearch(intent.getStringExtra("type").toString()))

        } else if (intent.getStringExtra("type").toString() == "autoCompleteCountries") {
            loadFragment(FragmentPlaceAutoComplete(TypeFilter.REGIONS))
        } else if (intent.getStringExtra("type").toString() == "autoCompleteCities") {
            loadFragment(FragmentPlaceAutoComplete(TypeFilter.CITIES))
        }

        binding.etSearch.requestFocus()
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput( binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun loadFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.add(binding!!.frame.id, fragment, fragmentTag)
            //  ft.addToBackStack(backStateName)
            ft.commit()
        }

    }

}