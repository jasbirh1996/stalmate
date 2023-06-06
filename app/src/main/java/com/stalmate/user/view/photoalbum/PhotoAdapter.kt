package com.stalmate.user.view.photoalbum

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemPhotoLayoutBinding
import com.stalmate.user.model.Photo
import com.stalmate.user.viewmodel.AppViewModel

class PhotoAdapter(val viewModel: AppViewModel, val context: Context,var callback:Callback
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>(){




    var list = ArrayList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_layout, parent, false)
        return ViewHolder(DataBindingUtil.bind<ItemPhotoLayoutBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
       return  list.size
    }


    fun submitList(photoList: List<Photo>) {
        list.clear()
        list.addAll(photoList)
        notifyDataSetChanged()
    }

    fun clearList(photoList: List<Photo>) {
        list.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var binding : ItemPhotoLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor", "ResourceType")
        fun bind(feed: Photo) {


            Glide.with(context).load(feed.files.replace(".com",".com/"))
                .placeholder(R.drawable.profileplaceholder)
                .into(binding.image)

            binding.image.setOnClickListener {
                callback.onClickOnPhoto(feed)
            }


        }
    }

    public interface Callback{
        fun onClickOnPhoto(photo: Photo)
    }

}