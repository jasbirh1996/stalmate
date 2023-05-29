package com.stalmate.user.view.dashboard.HomeFragment.Drawer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemDrawerLayoutBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.authentication.ActivityAuthentication
import com.stalmate.user.view.dashboard.Friend.FragmentFriend
import com.stalmate.user.viewmodel.AppViewModel

class DrawerAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    var list = ArrayList<ModelDrawer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_layout, parent, false)
        return ViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }


    fun submitList(languageList: List<ModelDrawer>) {
        list.clear()
        list.addAll(languageList)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(var binding: ItemDrawerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor", "ResourceType")
        fun bind(drawerResponse: ModelDrawer) {
            Glide.with(context).load(drawerResponse.image).into(binding.cardImage)
            binding.tvcardText.text = drawerResponse.text

            binding.card.setOnClickListener {
                when (drawerResponse.text) {
                    "My Funtime" -> {
                        context.startActivity(IntentHelper.getFullViewReelActivity(context))
                    }
                    "My Friends" -> {
                        context.startActivity(Intent(context, FragmentFriend::class.java))
                    }
                    "Settings" -> {
                        context.startActivity(IntentHelper.getSettingScreen(context))
                    }
                    "Saved/Favourite" -> {
                        context.startActivity(IntentHelper.getSaveFavouriteFuntimeScreen(context))
                    }
                    "Logout" -> {
                        PrefManager.getInstance(context)!!.keyIsLoggedIn = false
                        context.startActivity(
                            Intent(
                                context,
                                ActivityAuthentication::class.java
                            ).putExtra("screen", "login")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        (context as Activity).finishAffinity()
                    }
                }
            }
        }
    }

    interface Callbackk {
        fun onClickDrawerItem(postId: String)
    }
}