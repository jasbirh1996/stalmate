package com.stalmate.user.commonadapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otaliastudios.opengl.core.use
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.databinding.ItemMultiUserSelectBinding
import com.stalmate.user.databinding.ItemShareWithFriendBinding
import com.stalmate.user.databinding.ItemTaggedUsersBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.dashboard.funtime.FragmentFuntimeTag
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel
import com.stalmate.user.viewmodel.AppViewModel

class ShareWithFriendAdapter(
    val context: Context,var callback : Callback
) :
    RecyclerView.Adapter<ShareWithFriendAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShareWithFriendAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_share_with_friend, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemShareWithFriendBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ShareWithFriendAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemShareWithFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

            binding.tvUserName.text=user.first_name+" "+user.last_name
            ImageLoaderHelperGlide.setGlideCorner(context,binding.userImage,user.img,R.drawable.user_placeholder)

            if (user.isSelected){
                binding.ivChecked.text="Sent"
                binding.ivChecked.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
                binding.ivChecked.background=ContextCompat.getDrawable(context,R.drawable.round_very_small_corner_primary_border)
            }else{
                binding.ivChecked.text="Send"
                binding.ivChecked.setTextColor(ContextCompat.getColor(context,R.color.white))
                binding.ivChecked.background=ContextCompat.getDrawable(context,R.drawable.round_very_small_corner_primary)
            }



            binding.ivChecked.setOnClickListener {
                user.isSelected = !user.isSelected
                if (user.isSelected){
                    callback.onUserSelected(user)
                }

                notifyItemChanged(absoluteAdapterPosition)
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