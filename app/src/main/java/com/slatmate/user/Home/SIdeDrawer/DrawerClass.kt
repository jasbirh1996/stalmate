package com.slatmate.user.Home.SIdeDrawer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.slatmate.user.R
import com.slatmate.user.databinding.SideDrawerLayoutBinding

class DrawerClass : AppCompatActivity() , AdapterDrawer.ItemClickListener {

    var adapter: AdapterDrawer? = null

    lateinit var binding : SideDrawerLayoutBinding

    // data to populate the RecyclerView with
    var dataName = arrayOf(
        "Posts",
        "Pages",
        "My Stories",
        "Groups",
        "Events",
        "Memories",
        "Albums",
        "Create Categories",
        "Fun Times",
        "Share App",
        "Settings",
        "Quite Mode",
        "Saved/Favourite",
        "Logout"
    )

    // data to populate the RecyclerView with
    var dataIcon = arrayOf(
        "Posts",
        "Pages",
        "My Stories",
        "Groups",
        "Events",
        "Memories",
        "Albums",
        "Create Categories",
        "Fun Times",
        "Share App",
        "Settings",
        "Quite Mode",
        "Saved/Favourite",
        "Logout"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_drawer_layout);
    }

    override fun onItemClick(view: View?, position: Int) {

    }


}