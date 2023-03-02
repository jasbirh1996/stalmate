package com.stalmate.user.view.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.databinding.ItemAboutSettingBinding
import com.stalmate.user.databinding.ItemAppSettingBinding


class AboutSettingAdapter(var context: Context,var aboutUsSettingList: ArrayList<AboutUsSettingMenuModel>) :
    RecyclerView.Adapter<AboutSettingAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemAboutSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(aboutussetting: AboutUsSettingMenuModel, context: Context) {
            binding.aboutUsSetting.text=aboutussetting.name
            Glide.with(context).load(aboutussetting.image).into(binding.ivAbout)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAboutSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(aboutUsSettingList[position],context)
    }

    override fun getItemCount(): Int {
        return aboutUsSettingList.size
    }

}
data class AboutUsSettingMenuModel(var name: String,var image:Int)