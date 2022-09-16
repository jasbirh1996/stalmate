package com.stalmate.user.view.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBinding
import com.stalmate.user.databinding.ItemProfileAboutDataBinding
import com.stalmate.user.databinding.LayoutProfileDataLinesBinding
import com.stalmate.user.model.AboutProfileLine


import com.stalmate.user.model.Feed
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.viewmodel.AppViewModel

class ProfileAboutAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) :
    RecyclerView.Adapter<ProfileAboutAdapter.FeedViewHolder>() {
    var list = ArrayList<AboutProfileLine>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileAboutAdapter.FeedViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_about_data, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemProfileAboutDataBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ProfileAboutAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))



    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemProfileAboutDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(about: AboutProfileLine) {


            if (!ValidationHelper.isNull(about.key) && !ValidationHelper.isNull(about.middle)) {
                binding.tvValue.text = " " + about.middle + " " + about.value
                binding.tvKey.text = about.key
                binding.tvKey.visibility = View.VISIBLE
            } else if (ValidationHelper.isNull(about.key) && ValidationHelper.isNull(about.middle)) {
                binding.tvValue.text = about.value
                binding.tvKey.visibility = View.GONE
            } else if (!ValidationHelper.isNull(about.key) && ValidationHelper.isNull(about.middle)) {
                binding.tvValue.text = " " + about.value
                binding.tvKey.text = about.key
                binding.tvKey.visibility = View.VISIBLE
            }
          /*  Glide.with(context)
                .load(about.icon)
                .into(binding.iconn);*/

            binding.iconn.setImageResource(about.icon)
        }
    }

    fun submitList(feedList: List<AboutProfileLine>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnViewComments(postId: Int)
    }


}