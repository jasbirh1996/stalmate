package com.stalmate.user.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemStoryChatBinding
import com.stalmate.user.view.dashboard.Chat.model.StoryModel

class StoryListAdapter(
    private var ChatList: ArrayList<StoryModel>, private var context: Context) :
    RecyclerView.Adapter<StoryListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStoryChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private var viewBinding: ItemStoryChatBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: StoryModel) {
            viewBinding.apply {

                /*Picasso.get()
                    .load(R.drawable.app_logo)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(viewBinding.story)*/
                Glide.with(context)
                    .load(R.drawable.placeholder_filter)
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.app_logo)
                    .into(viewBinding.story)
            }
        }
    }
}