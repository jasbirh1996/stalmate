package com.stalmate.user.view.photoalbum

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.*
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityPhotoGalleryBinding


class ActivityPhotoGallery : BaseActivity() {

    private lateinit var binding : ActivityPhotoGalleryBinding

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
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.albumnavigation)
        if (intent.getStringExtra("viewType")!=null && intent.getStringExtra("viewType")=="viewNormal"){
            graph.setStartDestination(R.id.fragmentAlbumPhoto)
 /*           val arguments=NavArgument.Builder().setDefaultValue("Hello").build()
            graph.addArgument("",arguments)*/
            if (intent.getStringExtra("albumId")!=null){
                graph.addArgument("albumId",NavArgument.Builder().setDefaultValue(intent.getStringExtra("albumId")!!.toString()).build())
            }else{
                graph.addArgument("albumId",NavArgument.Builder().setDefaultValue("").build())

            }


            if (intent.getStringExtra("type")!=null){
                graph.addArgument("type",NavArgument.Builder().setDefaultValue(intent.getStringExtra("type")!!.toString()).build())
            }

            navController.graph=graph
           // navController.navigate(R.id.fragmentAlbumPhoto)
        }

        if (intent.getStringExtra("viewType")!=null && intent.getStringExtra("viewType")=="viewFullScreen"){
            graph.setStartDestination(R.id.fragmentAlbumFullView)

            graph.addArgument("albumId",NavArgument.Builder().setDefaultValue(intent.getStringExtra("albumId")!!.toString()).build())

            if (intent.getStringExtra("imageId")!=null){
                graph.addArgument("imageId",NavArgument.Builder().setDefaultValue(intent.getStringExtra("imageId")!!.toString()).build())
            }else{
                graph.addArgument("imageId",NavArgument.Builder().setDefaultValue("").build())

            }




            navController.graph=graph

        }

        if (intent.getStringExtra("viewType")!=null && intent.getStringExtra("viewType")=="viewPhotoListing"){
            graph.setStartDestination(R.id.fragmentAlbumPhotoListInGrid)
            Log.d("askldjasd","aosjdasd")
            graph.addArgument("albumId",NavArgument.Builder().setDefaultValue(intent.getStringExtra("albumId")!!.toString()).build())
            graph.addArgument("imageId",NavArgument.Builder().setDefaultValue("").build())
            navController.graph=graph
        }

    }


 /*   override fun onBackPressed() {
        Navigation.findNavController(binding.root).popBackStack()
    }*/
}