package com.stalmate.user.view.dashboard.funtime


import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemReelByAudioBinding


class ReelVideosByAudioAdapter(
    val context: Context, var callback: Callback, var showData: Boolean
) : RecyclerView.Adapter<ReelVideosByAudioAdapter.ViewHolder>() {

    var list = ArrayList<ResultFuntime>()

    inner class ViewHolder(var binding: ItemReelByAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(video: ResultFuntime) {
            val requestOptions = RequestOptions()
            Glide.with(context)
                .load(video.file)
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(video.file))
                .into(binding.ivMusicImage);
            // new DownloadImage(YourImageView).execute("Your URL");
            //  Glide.with(context).load(SeeModetextViewHelper.retriveVideoFrameFromVideo(video.file)).into(binding.ivMusicImage)
            binding.root.setOnClickListener {
                callback.onClickOnReel(video)
            }

            if (showData) {
                binding.tvViews.visibility= View.VISIBLE
                binding.ivPlay.visibility= View.VISIBLE
            } else {
                binding.tvViews.visibility= View.GONE
                binding.ivPlay.visibility= View.GONE
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_reel_by_audio, parent, false)
        return ViewHolder(DataBindingUtil.bind<ItemReelByAudioBinding>(view)!!)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("lakjsdasd", "asdlasd")
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun addToList(users: List<ResultFuntime>) {
        val size = list.size
        list.addAll(users)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    fun submitList(users: List<ResultFuntime>) {
        Log.d("laksjdlasd", users.size.toString())
        list.clear()
        list.addAll(users)
        notifyDataSetChanged()
    }

    public interface Callback {
        fun onClickOnReel(reel: ResultFuntime)
    }

}


class DownloadImage(bmImage: ImageView) :
    AsyncTask<String?, Void?, Bitmap?>() {
    var bmImage: ImageView

    init {
        this.bmImage = bmImage as ImageView
    }

    protected override fun doInBackground(vararg urls: String?): Bitmap? {
        var myBitmap: Bitmap? = null
        var mMRetriever: MediaMetadataRetriever? = null
        try {
            mMRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14) mMRetriever.setDataSource(
                urls[0],
                HashMap<String, String>()
            ) else mMRetriever.setDataSource(
                urls[0]
            )
            myBitmap = mMRetriever.getFrameAtTime()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (mMRetriever != null) {
                mMRetriever.release()
            }
        }
        return myBitmap
    }

    protected override fun onPostExecute(result: Bitmap?) {
        bmImage.setImageBitmap(result)
    }
}