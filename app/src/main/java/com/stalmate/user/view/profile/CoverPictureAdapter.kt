package com.stalmate.user.view.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemEducationprofileBinding
import com.stalmate.user.databinding.ItemProfileCoverBinding
import com.stalmate.user.model.CoverImg
import com.stalmate.user.model.Education
import com.stalmate.user.model.ProfileImg
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class CoverPictureAdapter(val viewModel: AppViewModel, val context: Context, var callback: CoverPictureAdapter.Callbackk)
    : RecyclerView.Adapter<CoverPictureAdapter.AlbumViewHolder>() {

    var list = ArrayList<CoverImg>()


    public interface Callbackk {
        fun onClickItemEdit(position: CoverImg, index: Int)
    }

    inner class AlbumViewHolder(var binding : ItemProfileCoverBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(response : CoverImg){



            ImageLoaderHelperGlide.setGlideCorner(context,binding.ivImage,response.img)
        }


    }

    fun submitList(albumList: List<CoverImg>) {
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