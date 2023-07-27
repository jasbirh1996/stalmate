package com.stalmate.user.modules.reels.player

import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.HorizontalItemReelBinding
import com.stalmate.user.databinding.ItemReelBinding
import com.stalmate.user.modules.reels.player.holders.ImageReelViewHolder
import com.stalmate.user.modules.reels.player.holders.ReelViewHolder
import com.stalmate.user.modules.reels.player.holders.VideoReelViewHolder
import com.stalmate.user.utilities.SeeModetextViewHelper
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.DialogFragmentComments
import com.stalmate.user.view.dashboard.funtime.DialogFragmentShareWithFriends
import com.stalmate.user.view.dashboard.funtime.ResultFuntime


class ReelAdapter(val context: Context, var callback: Callback) :
    ListAdapter<ResultFuntime, ReelViewHolder>(DIFF_CALLBACK) {
    var isMuted = false
    var reelList = ArrayList<ResultFuntime>()

    companion object {
        /** Mandatory implementation inorder to use "ListAdapter" - new JetPack component" **/
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResultFuntime>() {
            override fun areItemsTheSame(oldItem: ResultFuntime, newItem: ResultFuntime): Boolean {
                return false;// oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ResultFuntime,
                newItem: ResultFuntime
            ): Boolean {
                return false;//oldItem == newItem
            }
        }
        const val FEED_TYPE_VIDEO = 1;
        const val FEED_TYPE_IMAGES_MULTIPLE = 2;
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("akljsdasd", "asdkasd")
        if (true) {
            return FEED_TYPE_VIDEO;
        } else {
            return FEED_TYPE_IMAGES_MULTIPLE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {

        if (viewType == FEED_TYPE_IMAGES_MULTIPLE) {
            return ImageReelViewHolder(
                HorizontalItemReelBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
        return VideoReelViewHolder(
            ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {

        if (holder is VideoReelViewHolder) {
            handleViewHolder(holder, position)
        } else if (holder is ImageReelViewHolder) {
            handleViewHolder(holder, position)
        }
    }

    private fun handleViewHolder(holder: VideoReelViewHolder, position: Int) {
        /*Reset ViewHolder */
        removeImageFromImageView(holder.videoThumbnail)

        holder.isVideo = !reelList[position].isImage()

        /*Set seperate ID for each player view, to prevent it being overlapped by other player's changes*/
        holder.customPlayerView.id = View.generateViewId()

        /*circlular repeatation of items*/
        //  val videoPos = (position % Constants.videoList.size);

        /*Set ratio according to video*/
        //  (holder.videoThumbnail.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = Constants.videoList.get(videoPos).dimension

        /*Set video's direct url*/
        holder.customPlayerView.setVideoUri(Uri.parse(reelList[position].file))


        holder.customPlayerView.setOnClickListener {
            callback.onClickOnFullView(reelList[position])
        }
        if (!ValidationHelper.isNull(reelList[position].location)) {
            holder.tvLocation.text = reelList[position].location
            holder.tvLocation.visibility = View.VISIBLE
            holder.ivLocation.visibility = View.VISIBLE
        }

        val requestOptions = RequestOptions()
        Glide.with(context)
            .load(reelList[position].thum_icon)
            .apply(requestOptions)
            .thumbnail(Glide.with(context).load(reelList[position].thum_icon))
            .into(holder.videoThumbnail)


        holder.tvUserName.text = reelList[position]!!.first_name + " " + reelList[position]!!.last_name
        Glide.with(context).load(reelList[position].profile_img)
            .placeholder(R.drawable.profileplaceholder).into(holder.imgUserProfile)

        holder.likeCount.text = reelList[position]!!.like_count.toString()
        holder.commentCount.text = reelList[position]!!.comment_count.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvStatusDescription.setText(
                Html.fromHtml(
                    reelList[position]!!.text,
                    Html.FROM_HTML_MODE_COMPACT
                )
            );
        } else {
            holder.tvStatusDescription.setText(Html.fromHtml(reelList[position]!!.text));
        }
        if (holder.tvStatusDescription.getText().toString()
                .split(System.getProperty("line.separator")).size > 2
        ) {
            SeeModetextViewHelper.makeTextViewResizable(
                holder.tvStatusDescription,
                2,
                "more",
                true
            );
        }

        holder.likeButton.setOnClickListener {
            if (reelList[position].isLiked == "Yes") {
                reelList[position].isLiked = "No"
                reelList[position].like_count = (reelList[position].like_count?:0)-1
                holder.likeIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_funtime_post_like_unfill
                    )
                )

            } else {
                reelList[position].isLiked = "Yes"
                reelList[position].like_count = (reelList[position].like_count?:0)+1
                holder.likeIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_funtime_post_like_fill
                    )
                )
            }
            holder.likeCount.text = reelList[position].like_count.toString()
            callback.onClickOnLikeButtonReel(reelList[position])
        }
        holder.shareShareCount.setText(reelList[position].share_count.toString())
        holder.shareButton.setOnClickListener {
            callback.onClickOnShareReel(reelList[position])
            val dialogFragmen = DialogFragmentShareWithFriends(
                (context as BaseActivity).networkViewModel,
                reelList[position], object : DialogFragmentShareWithFriends.CAllback {
                    override fun onTotalShareCountFromDialog(count: Int) {
                        reelList[position].share_count = (reelList[position].share_count?:0) + count
                        holder.shareShareCount.setText(reelList[position].share_count.toString())
                    }
                }
            )
            dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
        }
        holder.commentButton.setOnClickListener {
            val dialogFragmen = DialogFragmentComments(
                (context as BaseActivity).networkViewModel,
                reelList[position],
                object : DialogFragmentComments.Callback {
                    override fun onCommentCount(commentCount: Int) {
                        holder.commentCount.text = commentCount.toString()
                        reelList[position].comment_count = commentCount
                        holder.customPlayerView.getPlayer()!!.play()
                    }

                    override fun onDismiss() {

                    }

                    override fun onShow() {

                    }
                })

            holder.customPlayerView.getPlayer()?.pause()
            dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
        }
        holder.soundIcon.setOnClickListener {
            if (holder.customPlayerView.getPlayer()?.volume == 0f) {
                holder.customPlayerView.getPlayer()?.volume =
                    holder.customPlayerView.getPlayer()!!.deviceVolume.toFloat()
                holder.soundIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_sound_on
                    )
                )
            } else {
                holder.customPlayerView.getPlayer()!!.volume = 0f
                holder.soundIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_sound_off
                    )
                )
            }
        }
        holder.tvStoryPostTime.text = reelList[position].Created_date
        if (reelList[position].isLiked == "Yes") {
            holder.likeIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_funtime_post_like_fill
                )
            )
        } else {
            holder.likeIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_funtime_post_like_unfill
                )
            )
        }
    }


    private fun handleViewHolder(holder: ImageReelViewHolder, position: Int) {
        /* Set adapter (items are being used inside adapter, you can setup in your own way*/
        val ReelAdapter = ImageAdapter(holder.itemView.context, position)
        holder.recyclerViewImages.adapter = ReelAdapter
    }


    public interface Callback {
        fun onClickOnRemoveReel(resultFuntime: ResultFuntime)
        fun onClickOnLikeButtonReel(resultFuntime: ResultFuntime)
        fun onClickOnEditReel(resultFuntime: ResultFuntime)
        fun onClickOnBlockUser(resultFuntime: ResultFuntime)
        fun onClickOnFullView(resultFuntime: ResultFuntime)
        fun onClickOnShareReel(resultFuntime: ResultFuntime)
    }


    override fun getItemCount(): Int {
        Log.d("akljsdasd", reelList.size.toString())
        return reelList.size;
    }

    fun removeImageFromImageView(imageView: ImageView) {
        try {
            imageView.background = null
            imageView.setImageDrawable(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setList(feedList: ArrayList<ResultFuntime>) {
        reelList.clear()
        reelList.addAll(feedList)
        notifyDataSetChanged()
    }


    fun addToList(feedList: ArrayList<ResultFuntime>) {
        val size = reelList.size
        reelList.addAll(feedList)
        val sizeNew = reelList.size
        notifyItemRangeChanged(size, sizeNew)
    }


    fun updateList(upatedReelList: ArrayList<ResultFuntime>) {
        for (i in 0 until reelList.size) {
            upatedReelList.forEach { newItem ->
                if (reelList[i].id == newItem.id) {
                    reelList.set(i, newItem)
                    notifyItemChanged(i)
                }
            }
        }
    }

    fun blockUserFromList(userId: String) {

        for (i in 0 until reelList.size) {
            if (reelList[i].user_id == userId) {
                reelList.removeAt(i)
                notifyItemRemoved(i)
            }
        }
    }
}