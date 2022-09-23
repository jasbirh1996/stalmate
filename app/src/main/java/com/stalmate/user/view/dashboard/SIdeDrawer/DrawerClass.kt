package com.stalmate.user.view.dashboard.SIdeDrawer

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.SideDrawerLayoutBinding
import com.stalmate.user.view.dashboard.HomeFragment.Drawer.DrawerAdapter
import com.stalmate.user.view.dashboard.HomeFragment.Drawer.ModelDrawer


class DrawerClass : BaseActivity() , DrawerAdapter.Callbackk {

    lateinit var drawerAdapter: DrawerAdapter

    lateinit var binding : SideDrawerLayoutBinding

    val data = ArrayList<ModelDrawer>()

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_drawer_layout);
        drawerAdapter = DrawerAdapter(networkViewModel, this,this)

        data.add(ModelDrawer(R.drawable.ic_menu_posts, "Posts"))
        data.add(ModelDrawer(R.drawable.ic_menu_pages, "Pages"))
        data.add(ModelDrawer(R.drawable.ic_menu_mystories, "My stories"))
        data.add(ModelDrawer(R.drawable.ic_menu_groups, "Posts"))
        data.add(ModelDrawer(R.drawable.ic_menu_events, "Events"))
        data.add(ModelDrawer(R.drawable.ic_menu_memories, "Memories"))
        data.add(ModelDrawer(R.drawable.ic_menu_albums, "Albums"))
        data.add(ModelDrawer(R.drawable.ic_menu_create_categories, "Create categories"))
        data.add(ModelDrawer(R.drawable.ic_menu_funtimes, "Fun time"))
        data.add(ModelDrawer(R.drawable.ic_menu_shareapp, "Share App"))
        data.add(ModelDrawer(R.drawable.ic_menu_settings, "Settings"))
        data.add(ModelDrawer(R.drawable.ic_menu_quite_mode, "Quite mode"))
        data.add(ModelDrawer(R.drawable.ic_menu_saved, "Saved favourite"))
        data.add(ModelDrawer(R.drawable.ic_menu_logout, "Logout"))


        /*binding.rvSideMenu.adapter=drawerAdapter
        drawerAdapter.submitList(data)*/
    }

    override fun onClickDrawerItem(postId: String) {

    }


}