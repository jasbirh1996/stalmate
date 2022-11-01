package com.stalmate.user.commonadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.databinding.ItemTaggedUsersBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.dashboard.funtime.FragmentFuntimeTag
import com.stalmate.user.viewmodel.AppViewModel

class TaggedUsersAdapter(
    val viewModel: FragmentFuntimeTag.TagPeopleViewModel,
    val context: Context, var isToSelect:Boolean,var callback : Callback
) :
    RecyclerView.Adapter<TaggedUsersAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TaggedUsersAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tagged_users, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemTaggedUsersBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: TaggedUsersAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemTaggedUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

            binding.ivRemove.setOnClickListener {
                list.removeAt(absoluteAdapterPosition)
                viewModel.taggedPeopleLiveData.postValue(list)

            }
            if (isToSelect){
                binding.ivRemove.visibility= View.GONE
                binding.root.setOnClickListener {
                    callback.onUserSelected(user)
                }
            }else{
                binding.ivRemove.visibility= View.VISIBLE
            }
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

public interface Callback{
    fun onUserSelected(user: User)
}



}