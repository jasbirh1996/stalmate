package com.stalmate.user.view.photoalbum.imageshowindex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemIndexPhotoAlbumShowBinding
import com.stalmate.user.databinding.ItemSuggestedFriendBinding
import com.stalmate.user.model.Albums
import com.stalmate.user.model.User

class AdapterPhotoIndex(private var context : Context) : RecyclerView.Adapter<AdapterPhotoIndex.ViewHolder>() {

    var onList = ArrayList<Albums?>()

   inner class ViewHolder(val binding : ItemIndexPhotoAlbumShowBinding): RecyclerView.ViewHolder(binding.root) {

       fun bind(photo : Albums){

           Glide.with(context)
               .load(photo.files?.replace(".com",".com/"))
               .placeholder(R.drawable.user_placeholder)
               .into(binding.ivIndexPhoto)

       }
    }



    fun addToList(users: List<Albums>) {

        val size = onList.size
        onList.addAll(users)
        val sizeNew = onList.size
        notifyItemRangeChanged(size, sizeNew)
    }
    fun setList(users: List<Albums?>) {
        onList.clear()
        onList.addAll(users)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_index_photo_album_show, parent, false)
        return ViewHolder(DataBindingUtil.bind<ItemIndexPhotoAlbumShowBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onList.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return onList.size
    }
}