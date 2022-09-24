package com.stalmate.user.view.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemEducationprofileBinding
import com.stalmate.user.databinding.ItemProfileCoverBinding
import com.stalmate.user.model.AlbumImage
import com.stalmate.user.model.Albums
import com.stalmate.user.model.Education
import com.stalmate.user.model.Photo

import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class SelfProfileAlbumAdapter(val viewModel: AppViewModel, val context: Context, val type : String)
    : RecyclerView.Adapter<SelfProfileAlbumAdapter.AlbumViewHolder>() {

    var list = ArrayList<Albums>()

    inner class AlbumViewHolder(var binding : ItemProfileCoverBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(response : Albums){
            ImageLoaderHelperGlide.setGlideCorner(context,binding.ivImage,response.img,R.drawable.user_placeholder)

            binding.ivImage.setOnClickListener {

          context.startActivity(IntentHelper.getPhotoGalleryAlbumScreen(context)!!.putExtra("viewType", "viewPhotoListing")
                    .putExtra("albumId", response.id)
                )
            }
        }
    }

    fun submitList(albumList: ArrayList<Albums>) {
        list.clear()
        list.addAll(albumList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_cover, parent, false)
        return AlbumViewHolder(DataBindingUtil.bind<ItemProfileCoverBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(list.get(position))
    }
    override fun getItemCount(): Int {
        return list.size
    }
}