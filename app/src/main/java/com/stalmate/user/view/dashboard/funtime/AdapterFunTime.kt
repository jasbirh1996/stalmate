package com.stalmate.user.view.dashboard.funtime

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.databinding.ItemFunTimeBinding
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.viewmodel.AppViewModel

class AdapterFunTime(val viewModel: AppViewModel,
                     val context: Context,
                     var callback: FriendAdapter.Callbackk) : RecyclerView.Adapter<AdapterFunTime.ViewHolder>() {


    inner class ViewHolder(var binding : ItemFunTimeBinding): RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       /* var view = LayoutInflater.from(parent.context).inflate(R.layout.item_fun_time, parent, false)
        return ViewHolder(DataBindingUtil.bind<ItemFunTimeBinding>(view)!!)*/
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}