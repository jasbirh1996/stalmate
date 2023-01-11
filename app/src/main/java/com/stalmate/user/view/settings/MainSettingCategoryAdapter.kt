package com.stalmate.user.view.settings
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.MenuSettingItemBinding

import java.io.Serializable

class MainSettingCategoryAdapter(
    val context: Context,
    var callback: Callbackk
) :
    RecyclerView.Adapter<MainSettingCategoryAdapter.SettingCategoryHolder>() {
    var list = ArrayList<SettingMenuModel>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainSettingCategoryAdapter.SettingCategoryHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.menu_setting_item, parent, false)
        return SettingCategoryHolder(DataBindingUtil.bind<MenuSettingItemBinding>(view)!!)
    }

    override fun onBindViewHolder(
        holder: MainSettingCategoryAdapter.SettingCategoryHolder,
        position: Int
    ) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class SettingCategoryHolder(var binding: MenuSettingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(setting: SettingMenuModel) {


            Glide.with(context).load(setting.icon)!!.into(binding.userImage)
                binding.tvUserName.text=setting.name

           binding.root.setOnClickListener {
               callback.onCLickONMenu(setting.name)
           }
            
        }
    }


    fun submitList(settings: List<SettingMenuModel>) {
        list.clear()
        list.addAll(settings)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onCLickONMenu(settingName: String)
    }


}

data class SettingMenuModel(
    var icon: Int,
    var name: String,
) : Serializable



