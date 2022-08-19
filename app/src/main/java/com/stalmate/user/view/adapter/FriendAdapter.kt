package com.stalmate.user.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class FriendAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) :
    RecyclerView.Adapter<FriendAdapter.FeedViewHolder>(){
    var list = ArrayList<User>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FriendAdapter.FeedViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemFriendBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: FriendAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }
    override fun getItemCount(): Int {
        return list.size
    }
    inner class FeedViewHolder(var binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {
            binding.buttonFollow.setOnClickListener {
              callback.onClickOnUpdateFriendRequest(friend,"Accept")
            }
            binding.root.setOnClickListener {
                callback.onClickOnProfile(friend)
            }


            ImageLoaderHelperGlide.setGlideCorner(context,binding.ivUserImage,friend.url+"/"+friend.img)
            binding.tvUserName.text=friend.first_name


        }
    }






    fun submitList(feedList: List<User>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnUpdateFriendRequest(friend:User,status: String)
        fun onClickOnProfile(friend:User)
    }









}