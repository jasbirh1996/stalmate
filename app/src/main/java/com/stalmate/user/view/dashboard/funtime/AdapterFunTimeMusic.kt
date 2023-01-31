package com.stalmate.user.view.dashboard.funtime

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemMusicLayoutBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class AdapterFunTimeMusic(
    val viewModel: AppViewModel,
    val context: Context, var isBig: Boolean, var callback: Callback
) : RecyclerView.Adapter<AdapterFunTimeMusic.ViewHolder>() {

    var list = ArrayList<ResultMusic>()

    inner class ViewHolder(var binding: ItemMusicLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(funtimeMusicResponse: ResultMusic) {
            var radius = 20f
            var decorView: View = (context as Activity?)!!.window.getDecorView();

            ImageLoaderHelperGlide.setGlideCorner(
                context,
                binding.ivMusicImage,
                funtimeMusicResponse.image,
                R.drawable.user_placeholder
            )
            binding.tvMusicName.text = funtimeMusicResponse.sound_name
            binding.tvMusicDescription.text = funtimeMusicResponse.artist_name



            binding.root.setOnClickListener {
                callback.onSongSelected(funtimeMusicResponse)
            }


            binding.ivFAvouriteButton.setOnClickListener {
                callback.onClickOnFavouriteMusicButton(funtimeMusicResponse)
            }

            if (funtimeMusicResponse.isSave == "Yes") {
                binding.ivFAvouriteButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_audio_saved
                    )
                )
            } else {
                binding.ivFAvouriteButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.audio_unsave
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_music_layout, parent, false)
        return ViewHolder(DataBindingUtil.bind<ItemMusicLayoutBinding>(view)!!)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
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

    public interface Callback {
        fun onSongSelected(song: ResultMusic)
        fun onClickOnFavouriteMusicButton(song: ResultMusic)
    }

    public fun removeMusicFromList(song: ResultMusic) {
        var position = list.indexOfFirst { it.id == song.id }
        list.removeAt(position)
        notifyItemRemoved(position)

    }

    public fun updateSaveStatusList(song: ResultMusic) {
        var position = list.indexOfFirst { it.id == song.id }
        Log.d("lakjsdasd", list.get(position).isSave)
        if (list[position].isSave == "Yes") {
            list[position].isSave = "No"
        } else {
            list[position].isSave = "Yes"
        }
        notifyItemChanged(position)
    }


}