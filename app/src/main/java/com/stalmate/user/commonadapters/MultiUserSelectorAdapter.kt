package com.stalmate.user.commonadapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otaliastudios.opengl.core.use
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.databinding.ItemMultiUserSelectBinding
import com.stalmate.user.databinding.ItemTaggedUsersBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.dashboard.funtime.FragmentFuntimeTag
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel
import com.stalmate.user.viewmodel.AppViewModel

class MultiUserSelectorAdapter(
    val viewModel:TagPeopleViewModel,
    val context: Context
) :
    RecyclerView.Adapter<MultiUserSelectorAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MultiUserSelectorAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_multi_user_select, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemMultiUserSelectBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: MultiUserSelectorAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemMultiUserSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {

            binding.tvUserName.text=user.first_name+" "+user.last_name
            binding.tvUserId.text= user.id
            ImageLoaderHelperGlide.setGlideCorner(context,binding.userImage,user.img,R.drawable.user_placeholder)

            if (user.isSelected){
                binding.ivCheckImahe.visibility=View.VISIBLE
            }else{
                binding.ivCheckImahe.visibility=View.GONE
            }



            binding.ivChecked.setOnClickListener {
                user.isSelected = !user.isSelected


            /*    if (user.isSelected){
                    viewModel.addToList(user)
                }else{
                    viewModel.removeFromList(user)
                }*/
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




}