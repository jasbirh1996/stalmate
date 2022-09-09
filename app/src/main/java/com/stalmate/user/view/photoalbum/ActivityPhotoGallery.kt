package com.stalmate.user.view.photoalbum

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityPhotoGalleryBinding
import com.stalmate.user.databinding.CreateAlbumLayoutBinding


class ActivityPhotoGallery : BaseActivity() {

    private lateinit var binding : ActivityPhotoGalleryBinding
    private lateinit var bindingPop : CreateAlbumLayoutBinding

    lateinit var navController: NavController

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this , R.layout.activity_photo_gallery)
        setUpNavigation()


    }

    fun setUpNavigation() {
        navController=findNavController(R.id.nav_host_fragment)
    }

}