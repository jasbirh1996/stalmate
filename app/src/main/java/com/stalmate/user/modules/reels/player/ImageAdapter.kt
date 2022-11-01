package com.stalmate.user.modules.reels.player

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.stalmate.user.databinding.ImageItemSingleBinding
import com.stalmate.user.modules.reels.player.holders.ImageViewHolder
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

import kotlin.random.Random



class ImageAdapter(val context: Context, val parentPosition: Int) :
    ListAdapter<ResultFuntime, ImageViewHolder>(DIFF_CALLBACK) {

    companion object {
        /** Mandatory implementation inorder to use "ListAdapter" - new JetPack component" **/
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResultFuntime>() {
            override fun areItemsTheSame(oldItem: ResultFuntime, newItem: ResultFuntime): Boolean {
                return false;// oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ResultFuntime, newItem: ResultFuntime): Boolean {
                return false;//oldItem == newItem
            }

        }

        public val TOTAL_IMAGES = 4;
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        return ImageViewHolder(
            ImageItemSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        /* Show any random image from drawable*/
        val posToPick = (parentPosition+position)%7;
     //   holder.imageView.setImageDrawable(holder.itemView.context.getResource("image_"+posToPick));

    }

    override fun getItemCount(): Int {
        return TOTAL_IMAGES;
    }

    fun removeImageFromImageView(imageView: ImageView) {
        try {
            imageView.background = null
            imageView.setImageDrawable(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        val rand = Random(System.nanoTime())
        return (start..end).random(rand)
    }
}