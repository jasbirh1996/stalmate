package com.stalmate.user.view.dashboard.HomeFragment.Drawer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemDrawerLayoutBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.authentication.ActivityAuthentication
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.Friend.FragmentFriend
import com.stalmate.user.viewmodel.AppViewModel

class DrawerAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    var list = ArrayList<ModelDrawer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
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
                    "My Fun times" -> {
                        try {
                            (context as ActivityDashboard).pointToMyFuntime.value = true
                            (context as ActivityDashboard).onBackPressed()
                        } catch (e: ClassCastException) {
                            (context as ActivitySettings).onBackPressed()
                        }
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