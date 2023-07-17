package com.stalmate.user.commonadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemActualTaggedUsersBinding
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.dashboard.funtime.TaggedUser

class ActualTaggedUserAdapter(
    val context: Context,var callback : Callback
) :
    RecyclerView.Adapter<ActualTaggedUserAdapter.FeedViewHolder>() {
    var list = ArrayList<TaggedUser>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ActualTaggedUserAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_actual_tagged_users, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemActualTaggedUsersBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ActualTaggedUserAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemActualTaggedUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: TaggedUser) {
            binding.tvUserName.text=user.first_name+" "+user.last_name
            ImageLoaderHelperGlide.setGlideCorner(context,binding.userImage,user.profile_img_1,R.drawable.user_placeholder)

            binding.root.setOnClickListener {
                callback.onUserSelected(user)
            }
        }
    }

    fun submitList(feedList: List<TaggedUser>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }


    fun addToList(feedList: List<TaggedUser>) {
        val size = list.size
        list.addAll(feedList)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    public interface Callback{
        fun onUserSelected(user: TaggedUser)
    }



}