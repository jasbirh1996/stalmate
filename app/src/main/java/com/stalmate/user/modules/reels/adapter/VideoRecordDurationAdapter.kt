package com.stalmate.user.modules.reels.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.viewmodel.AppViewModel


class VideoRecordDurationAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk,
    var type: String,
    var subtype: String
) :
    RecyclerView.Adapter<VideoRecordDurationAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VideoRecordDurationAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_big, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemFriendBigBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: VideoRecordDurationAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemFriendBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {


        }
    }

    fun submitList(feedList: List<User>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }


    fun addToList(feedList: List<User>) {
        val size = list.size
        list.addAll(feedList)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    public interface Callbackk {
        fun onClickOnUpdateFriendRequest(friend: User, status: String)
        fun onClickOnProfile(friend: User)
    }




}