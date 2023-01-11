package com.stalmate.user.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.R

import com.stalmate.user.databinding.ItemFriendBinding
import com.stalmate.user.databinding.ItemSquarePhotoBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.Constants.TYPE_ALL_FOLLOWERS_FOLLOWING
import com.stalmate.user.utilities.Constants.TYPE_MY_FRIENDS
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_ADD_FRIEND
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_FOLLOW
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.ResultMusic
import com.stalmate.user.viewmodel.AppViewModel

class SavedGridMusicAdapter(
    val context: Context
) :
    RecyclerView.Adapter<SavedGridMusicAdapter.MusicVideHolder>() {
    var list = ArrayList<ResultMusic>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SavedGridMusicAdapter.MusicVideHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_square_photo, parent, false)
        return MusicVideHolder(DataBindingUtil.bind<ItemSquarePhotoBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: SavedGridMusicAdapter.MusicVideHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MusicVideHolder(var binding: ItemSquarePhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(music: ResultMusic) {
            val requestOptions = RequestOptions()
            Glide.with(context)
                .load(music.image)
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(music.image))
                .into(binding.image);
            // new DownloadImage(YourImageView).execute("Your URL");
            //  Glide.with(context).load(SeeModetextViewHelper.retriveVideoFrameFromVideo(video.file)).into(binding.ivMusicImage)

        }
    }

    fun addToList(users: List<ResultMusic>) {
        val size = list.size
        list.addAll(users)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }
    fun submitList(users: List<ResultMusic>) {
        list.clear()
        list.addAll(users)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnMusic()
    }


}
