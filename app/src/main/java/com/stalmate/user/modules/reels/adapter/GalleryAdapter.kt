package com.stalmate.user.modules.reels.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ImageItemSingleBinding
import com.stalmate.user.databinding.ItemGalleryPickBinding
import com.stalmate.user.utilities.ImageLoaderHelperGlide


class GalleryAdapter(
    val context: Context,
    var callback: Callbackk,
) :
    RecyclerView.Adapter<GalleryAdapter.FeedViewHolder>() {
    var old_postion = -1
    var list = ArrayList<GalleryItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GalleryAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_pick, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemGalleryPickBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: GalleryAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemGalleryPickBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GalleryItem) {

            Glide.with(context).load(item.image).into(binding.imageView)
            Log.d("asdasda", item.uri.toString())
            if (item.mediaType == 1) {
                binding.ivmediaType.visibility = View.GONE
            } else {
                binding.ivmediaType.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener {

                callback.onItemSelected(item)
                /*       old_postion=absoluteAdapterPosition
                       notifyItemChanged(old_postion)
       */
            }

            /*     if(old_postion==absoluteAdapterPosition){
                     binding.mainlayout.background=ContextCompat.getDrawable(context,R.drawable.white_small_corner_border)
                 }
                 else
                 {

                     binding.mainlayout.background=ContextCompat.getDrawable(context,R.drawable.primary_small_corner_border)
                 }*/


        }
    }

    fun submitList(feedList: List<GalleryItem>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }


    fun addToList(feedList: List<GalleryItem>) {
        val size = list.size
        list.addAll(feedList)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    public interface Callbackk {
        fun onItemSelected(item: GalleryItem)
    }


}


data class GalleryItem(val image: Bitmap?, val mediaType: Int, val uri: String)