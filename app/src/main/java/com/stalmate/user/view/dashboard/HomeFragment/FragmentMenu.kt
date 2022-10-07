package com.stalmate.user.view.dashboard.HomeFragment


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily

import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.BubbleGradient
import com.igalata.bubblepicker.model.PickerItem
import com.igalata.bubblepicker.rendering.BubblePicker


import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentMenuBinding
import com.stalmate.user.databinding.SideDrawerLayoutBinding
import com.stalmate.user.model.AboutProfileLine
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.adapter.ProfileAboutAdapter
import com.stalmate.user.view.authentication.ActivityAuthentication
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.HomeFragment.Drawer.DrawerAdapter
import com.stalmate.user.view.dashboard.HomeFragment.Drawer.ModelDrawer
import com.stalmate.user.view.language.AdapterLanguage
import com.stalmate.user.view.profile.ProfileAlbumImageAdapter
import com.stalmate.user.view.profile.SelfProfileAlbumAdapter


class FragmentMenu(var callback : Callback) : BaseFragment(),  DrawerAdapter.Callbackk {

    lateinit var drawerAdapter: DrawerAdapter
    val data = ArrayList<ModelDrawer>()
    lateinit var binding: FragmentMenuBinding
    lateinit var userData: User

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

        binding.btnBack.setOnClickListener {
            callback.onCLickBackButton()
        }

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

        binding.tvUserName.text = PrefManager.getInstance(App.getInstance())!!.userDetail.results[0].first_name + " " +  PrefManager.getInstance(App.getInstance())!!.userDetail.results[0].last_name

        drawerAdapter = DrawerAdapter(networkViewModel, requireContext(),this )
        binding.rvMenu.adapter = drawerAdapter
        drawerAdapter.submitList(data)

        getUserProfileData()

    }

    public interface Callback{
        fun onCLickBackButton()
    }

    override fun onClickDrawerItem(postId: String) {
    }


    fun getUserProfileData() {
        var hashMap = HashMap<String, String>()
        networkViewModel.getProfileData(hashMap)
        networkViewModel.profileLiveData.observe(requireActivity(), Observer {
            it.let {
                userData = it!!.results
                setUpAboutUI()
                PrefManager.getInstance(requireContext())!!.userProfileDetail = it

            }
        })
    }

    fun setUpAboutUI() {

        binding.tvUserName.text = userData.first_name+ " " +userData.last_name

        if (userData.profile_data[0].profession.isNotEmpty()){
            binding.workStatus.visibility = View.VISIBLE
        }
        if (userData.profile_data[0].location.isNotEmpty()){
            binding.locationStatus.visibility = View.VISIBLE
        }
        Glide.with(requireContext()).load(userData.cover_img1)
            .placeholder(R.drawable.user_placeholder)
            .into(binding.userCoverImage)
        Glide.with(requireContext()).load(userData.profile_img1)
            .placeholder(R.drawable.user_placeholder)
            .into(binding.userProfileImage)

    }

}
