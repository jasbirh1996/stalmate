package com.stalmate.user.view.adapter



import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemSquareVideoViewBinding
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

class SavedGridVideoAdapter(
    val context: Context
) :
    RecyclerView.Adapter<SavedGridVideoAdapter.VideoViewHolder>() {
    var list = ArrayList<ResultFuntime>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SavedGridVideoAdapter.VideoViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_square_video_view, parent, false)
        return VideoViewHolder(DataBindingUtil.bind<ItemSquareVideoViewBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: SavedGridVideoAdapter.VideoViewHolder, position: Int) {
        Log.d("lasjkdasd","hjghkhuk")
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class VideoViewHolder(var binding: ItemSquareVideoViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(funtime: ResultFuntime) {
            Log.d("lasjkdasd",funtime.file)
            val requestOptions = RequestOptions()
            Glide.with(context)
                .load(funtime.file)
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(funtime.file))
                .into(binding.image);
            // new DownloadImage(YourImageView).execute("Your URL");
            //  Glide.with(context).load(SeeModetextViewHelper.retriveVideoFrameFromVideo(video.file)).into(binding.ivMusicImage)


        }
    }

    fun addToList(users: List<ResultFuntime>) {
        val size = list.size
        list.addAll(users)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }
    fun submitList(users: List<ResultFuntime>) {
        Log.d("aljdasd",users.size.toString())
        list.clear()
        list.addAll(users)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnVideo()
    }


}
