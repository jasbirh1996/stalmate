package com.stalmate.user.modules.reels.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivitySongPickerBinding
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker
import com.stalmate.user.view.dashboard.funtime.AdapterFunTime
import com.stalmate.user.view.dashboard.funtime.AdapterFunTimeMusic

import java.io.File




const val EXTRA_SONG_FILE = "song_file"
const val EXTRA_SONG_ID = "song_id"
const val  EXTRA_SONG_NAME = "song_name"
const val EXTRA_SONG_COVER = "song_cover"
const val EXTRA_SONG_DURATION = "duration"


class ActivitySongPicker : BaseActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivitySongPickerBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_song_picker)!!
        setUpNavigation()

        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? = fragmentManager.findFragmentById(R.id.nav_host_fragment)



    }


    fun setUpNavigation() {
        navController=findNavController(R.id.nav_host_fragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}