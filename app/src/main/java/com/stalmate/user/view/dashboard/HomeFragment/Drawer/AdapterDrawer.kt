package com.stalmate.user.view.dashboard.HomeFragment.Drawer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemDrawerLayoutBinding
import com.stalmate.user.model.Result
import com.stalmate.user.viewmodel.AppViewModel

class DrawerAdapter(val viewModel: AppViewModel,
                    val context: Context,
                    var callback: Callbackk
) : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    var list = ArrayList<ModelDrawer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_layout, parent, false)
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

  inner  class ViewHolder(var binding : ItemDrawerLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor", "ResourceType")
        fun bind(drawerResponse: ModelDrawer) {

            Glide.with(context).load(drawerResponse.image).into(binding.cardImage)
            binding.tvcardText.text = drawerResponse.text

        }
    }


    public interface Callbackk {
        fun onClickDrawerItem(postId: String)
    }
}