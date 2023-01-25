package com.stalmate.user.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemChatUserBinding
import com.stalmate.user.view.dashboard.Chat.model.ChatModel

class ChatListAdapter(
    private var ChatList: ArrayList<ChatModel>, private var context: Context
) :
    RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ChatList[position])
    }

    //Return the size of users list & total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return ChatList.size
    }

    //This two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private var viewBinding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: ChatModel) {
            viewBinding.apply {
                viewBinding.tvUserName.text=item.userName
                viewBinding.tvLastMsg.text=item.lastMessage
                viewBinding.tvLastMsgTiming.text=item.lastMsgTiming

                /*Picasso.get()
                    .load(R.drawable.app_logo)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(viewBinding.userImage)*/

                 Glide.with(context)
                     .load(R.drawable.placeholder_filter)
                     .placeholder(R.drawable.app_logo)
                     .error(R.drawable.app_logo)
                     .into(viewBinding.userImage)
            }
        }
    }
}