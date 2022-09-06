package com.stalmate.user.view.photoalbum

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemGalleryBinding
import com.stalmate.user.model.Result
import com.stalmate.user.model.ResultResponse
import com.stalmate.user.view.language.AdapterLanguage
import com.stalmate.user.viewmodel.AppViewModel

class AlbumAdapter(val viewModel: AppViewModel, val context: Context,  var callback: Callbackk) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    var list = ArrayList<ResultResponse>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.AlbumViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return AlbumViewHolder(DataBindingUtil.bind<ItemGalleryBinding>(view)!!)

    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(list.get(position))


    }

    override fun getItemCount(): Int {

        return list.size
    }
    fun submitList(albumList: List<ResultResponse>) {
        list.clear()
        list.addAll(albumList)
        notifyDataSetChanged()
    }



    public interface Callbackk {
        fun onClickItem(postId: String)
    }

    inner class AlbumViewHolder(var binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(feed : ResultResponse){

            Glide.with(context).load(R.drawable.profileplaceholder)
                .placeholder(R.drawable.profileplaceholder)
                .into(binding.imAlbumImage)


            binding.tvAlbumName.text = feed.name

            binding.photoLayout.setOnClickListener {
                callback.onClickItem(list[position].id)
            }



        }
    }
}