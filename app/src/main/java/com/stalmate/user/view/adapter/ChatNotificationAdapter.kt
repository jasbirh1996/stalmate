package com.stalmate.user.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemRecentBinding
import com.stalmate.user.view.dashboard.Chat.model.ChatNotificationModel

class ChatNotificationAdapter(private var ChatNotificationList: ArrayList<ChatNotificationModel>, private var context: Context) :
    RecyclerView.Adapter<ChatNotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ChatNotificationList[position])
    }

    override fun getItemCount(): Int {
        return ChatNotificationList.size
    }

    //This two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private var viewBinding: ItemRecentBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: ChatNotificationModel) {
            viewBinding.apply {
                viewBinding.userName.text = item.userName
                viewBinding.timeHrs.text = item.timeHrs

                Glide.with(context)
                    .load(R.drawable.placeholder_filter)
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.app_logo)
                    .into(viewBinding.userImage)
            }
        }
    }
}

