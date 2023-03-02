package com.stalmate.user.view.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemAppSettingBinding
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

class AppSettingAdapter(var appSettingList: ArrayList<AppSettingMenuModel>) :
    RecyclerView.Adapter<AppSettingAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemAppSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appsetting: AppSettingMenuModel) {
           binding.tvUserNameAppSetting.text=appsetting.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAppSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appSettingList[position])
    }

    override fun getItemCount(): Int {
        return appSettingList.size
    }

}
data class AppSettingMenuModel(var name: String)