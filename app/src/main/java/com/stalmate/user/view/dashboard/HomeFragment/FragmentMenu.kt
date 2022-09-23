package com.stalmate.user.view.dashboard.HomeFragment


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.BubbleGradient
import com.igalata.bubblepicker.model.PickerItem
import com.igalata.bubblepicker.rendering.BubblePicker


import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentMenuBinding
import com.stalmate.user.databinding.SideDrawerLayoutBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.HomeFragment.Drawer.DrawerAdapter
import com.stalmate.user.view.dashboard.HomeFragment.Drawer.ModelDrawer


class FragmentMenu : BaseFragment(),  DrawerAdapter.Callbackk {

    lateinit var drawerAdapter: DrawerAdapter
    val data = ArrayList<ModelDrawer>()
    lateinit var binding: FragmentMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentMenuBinding>(inflater.inflate(R.layout.fragment_menu, container, false))!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawerAdapter = DrawerAdapter(networkViewModel, requireContext(),this)

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

    }

    override fun onClickDrawerItem(postId: String) {
    }

}
