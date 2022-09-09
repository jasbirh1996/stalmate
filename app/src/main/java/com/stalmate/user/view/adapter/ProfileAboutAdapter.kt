package com.stalmate.user.view.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBinding
import com.stalmate.user.databinding.LayoutProfileDataLinesBinding
import com.stalmate.user.model.AboutProfileLine


import com.stalmate.user.model.Feed
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

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_profile_data_lines, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<LayoutProfileDataLinesBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ProfileAboutAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class FeedViewHolder(var binding: LayoutProfileDataLinesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(about: AboutProfileLine) {

           binding.tvKey.text=about.key
            binding.tvValue.text=" "+about.middle+" "+about.value
            Log.d("asdasd","pp")

            Glide.with(context).load(about.icon).into(binding.icon)

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