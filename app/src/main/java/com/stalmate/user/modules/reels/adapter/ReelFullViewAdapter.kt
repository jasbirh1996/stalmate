package com.stalmate.user.modules.reels.adapter

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.HorizontalItemReelBinding
import com.stalmate.user.databinding.ItemFullViewReelBinding
import com.stalmate.user.modules.reels.activity.ActivityFullViewReels
import com.stalmate.user.modules.reels.player.ImageAdapter
import com.stalmate.user.modules.reels.player.holders.ImageReelViewHolder
import com.stalmate.user.modules.reels.player.holders.ReelViewHolder
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.utilities.SeeModetextViewHelper
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.*
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import com.stalmate.user.view.dialogs.SuccessDialog


class ReelFullViewAdapter(
    val context: Context,
    var callback: Callback,
    val userId: String
) :
    ListAdapter<ResultFuntime, ReelViewHolder>(DIFF_CALLBACK) {
    var reelList = ArrayList<ResultFuntime>()
    private var imageLoader: ImageLoader? = null
    private var volumeForAll = 0f

    init {
        if (imageLoader == null) {
            val imageLoaderConfiguration = ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .writeDebugLogs() // Remove for release app
                .build()
            imageLoader = ImageLoader.getInstance()
            imageLoader?.init(imageLoaderConfiguration)
        }
    }

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


    public interface Callback {
        fun onClickOnRemoveReel(resultFuntime: ResultFuntime)
        fun onClickOnLikeButtonReel(resultFuntime: ResultFuntime)
        fun onClickOnEditReel(resultFuntime: ResultFuntime)
        fun onClickOnBlockUser(resultFuntime: ResultFuntime)
        fun downloadThisFuntime(resultFuntime: ResultFuntime)
    }

    override fun getItemViewType(position: Int): Int {
        if (!reelList[position].isImage()) {
            return FEED_TYPE_VIDEO;
        } else {
            return FEED_TYPE_VIDEO;//return FEED_TYPE_IMAGES_MULTIPLE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        return if (viewType == FEED_TYPE_IMAGES_MULTIPLE) {
            return ImageReelViewHolder(
                HorizontalItemReelBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else {
            VideoReelFullViewHolder(
                ItemFullViewReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        if (holder is VideoReelFullViewHolder) {
            handleViewHolder(holder, position)
        } else if (holder is ImageReelViewHolder) {
            handleViewHolder(holder, position)
        }
    }

    private fun handleViewHolder(holder: VideoReelFullViewHolder, position: Int) {
        imageLoader?.loadImage(reelList[position].file, object : SimpleImageLoadingListener() {
            override fun onLoadingStarted(imageUri: String?, view: View?) {
                holder.progressBarBuffering.visibility = View.VISIBLE
                holder.videoThumbnail.visibility = View.GONE
            }

            override fun onLoadingComplete(
                imageUri: String?,
                view: View?,
                loadedImage: Bitmap?
            ) {
                holder.progressBarBuffering.visibility = View.GONE
                loadedImage?.let {
                    holder.videoThumbnail.visibility = View.VISIBLE
                    holder.videoThumbnail.setImage(ImageSource.bitmap(it))
                    Palette.from(it).generate { palette ->
                        palette?.let { it1 ->
                            setUpInfoBackgroundColor(
                                holder.videoLayout,
                                it1
                            )
                        }
                    }
                }
            }
        })

        if (reelList[position].isImage()) {
            holder.isVideo = false
            holder.ivSoundButton.visibility = View.GONE
            holder.customPlayerView.visibility = View.GONE
            //holder.videoThumbnail.visibility = View.GONE
            //LoadImage here also
        } else {
            //video/mp4
            holder.ivSoundButton.visibility = View.VISIBLE
            holder.customPlayerView.visibility = View.VISIBLE
            holder.videoThumbnail.visibility = View.GONE
            try {
                holder.isVideo = true
                //Set seperate ID for each player view, to prevent it being overlapped by other player's changes
//                holder.customPlayerView.id = View.generateViewId()
                holder.customPlayerView.setVideoUri(Uri.parse(reelList[position].file))

                holder.ivSoundButton.setOnClickListener {
                    holder.ivSoundButton.animate().alpha(1f).setDuration(1000).start()
                    volumeForAll = if (volumeForAll == 0f) 1f else 0f
                    holder.ivSoundButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.ivSoundButton.context,
                            if (volumeForAll == 0f) R.drawable.ic_sound_off else R.drawable.ic_sound_on
                        )
                    )
                    holder.ivSoundButton.postDelayed({
                        holder.ivSoundButton.animate().alpha(0f).setDuration(1000).start()
                    }, 1000)
                    holder.customPlayerView.getPlayer()?.volume = volumeForAll
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.tvUserName.text = reelList[position].first_name + " " + reelList[position].last_name
        Glide.with(context).load(reelList[position].profile_img)
            .placeholder(R.drawable.profileplaceholder).into(holder.imgUserProfile)
        holder.imgUserProfile.setOnClickListener {
            context.startActivity(
                IntentHelper.getOtherUserProfileScreen(this.context)!!
                    .putExtra("id", reelList[position].user_id)
            )
        }
        Glide.with(context).load(reelList[position].sound_image)
            .placeholder(R.drawable.profileplaceholder).into(holder.ivMusicImage)

        holder.likeCount.text = reelList[position].like_count.toString()
        holder.commentCount.text = reelList[position].comment_count.toString()
        holder.shareCount.text = reelList[position].share_count.toString()
        if (!reelList[position].tag_user.isNullOrEmpty()) {
            holder.layoutTagged.visibility = View.VISIBLE
            holder.tvTaggedPeopleCount.text =
                (reelList[position].tag_user?.size ?: 0).toString() + " People Tagged"
            holder.tvTaggedPeopleCount.setOnClickListener {
                reelList[position].tag_user?.let {
                    var dialogFragmen = FragmentBSTaggedUsers(it)
                    dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
                }
                reelList[position].isDataUpdated = true
            }
        }

        if (!reelList[position].location.toString().isNullOrEmpty()) {
            holder.tvLocation.text = reelList[position].location
            holder.tvLocation.visibility = View.VISIBLE
            holder.ivLocation.visibility = View.VISIBLE
        }

        if (!reelList[position].text.isNullOrEmpty()) {
            holder.tvStatusDescription.visibility = View.VISIBLE
            holder.tvStatusDescription.text = Html.fromHtml(
                reelList[position].text,
                Html.FROM_HTML_MODE_COMPACT
            )
            try {
                if ((holder.tvStatusDescription.text.toString().length > 80)) {
                    SeeModetextViewHelper.makeTextViewResizable(
                        holder.tvStatusDescription,
                        1,
                        "more",
                        true
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.tvStatusDescription.visibility = View.GONE
        }

        if (reelList[position].isLiked == "Yes") {
            holder.ivLikeIcon.setImageResource(R.drawable.ic_funtime_slidepost_liked_icon)
        } else {
            holder.ivLikeIcon.setImageResource(R.drawable.ic_funtime_slidepost_like_icon)
        }

        holder.buttonLike.setOnClickListener {
            if (reelList[position].isLiked == "Yes") {
                reelList[position].like_count = (reelList[position].like_count ?: 0) - 1
                holder.likeCount.text = reelList[position].like_count.toString()
                reelList[position].isLiked = "No"
                holder.ivLikeIcon.setImageResource(R.drawable.ic_funtime_slidepost_like_icon)
            } else {
                reelList[position].like_count = (reelList[position].like_count ?: 0) + 1
                holder.likeCount.text = reelList[position].like_count.toString()
                reelList[position].isLiked = "Yes"
                holder.ivLikeIcon.setImageResource(R.drawable.ic_funtime_slidepost_liked_icon)
            }
            reelList[position].isDataUpdated = true
            callback.onClickOnLikeButtonReel(reelList[position])
        }

        /* val timesAg = TimesAgo2.covertTimeToText(item.Created_date, true)*/
        if (!ValidationHelper.isNull(reelList[position].sound_name)) {
            holder.layoutMusic.visibility = View.VISIBLE
            holder.layoutMusic.setOnClickListener {

                context.startActivity(
                    IntentHelper.getReelListByAudioScreen(context)!!
                        .putExtra("data", reelList[position])
                )
            }
            holder.tvMusic.text = reelList[position].sound_name
            holder.tvMusicArtist.text = reelList[position].artist_name
        }


        holder.tvStoryPostTime.text = reelList[position].Created_date
        holder.buttonAdd.setOnClickListener {
            context.startActivity(IntentHelper.getCreateReelsScreen(context))
        }
        val dialogFragmentComment = DialogFragmentCommentWithVideo(
            (context as BaseActivity).networkViewModel,
            reelList[position], object : DialogFragmentCommentWithVideo.Callback {
                override fun funOnCommentDialogDismissed(commentCount: Int) {
                    reelList[position].isDataUpdated = true
                    holder.commentCount.text = commentCount.toString()
                    if (!reelList[position].isImage())
                        holder.customPlayerView.getPlayer()?.play()
                }
            }
        )
        holder.buttonComment.setOnClickListener {
            if (!reelList[position].isImage())
                holder.customPlayerView.getPlayer()?.pause()
            dialogFragmentComment.show(
                (context as ActivityFullViewReels).supportFragmentManager,
                ""
            )
        }
        if (reelList[position].comment_status.equals("on", true) ||
            reelList[position].comment_status.equals("true", true) ||
            reelList[position].comment_status.equals("1", true)
        ) {
            holder.buttonComment.visibility = View.VISIBLE
        } else {
            holder.buttonComment.visibility = View.GONE
        }
        val dialogFragmentShares = DialogFragmentShareWithFriends(
            (context as BaseActivity).networkViewModel,
            reelList[position], object : DialogFragmentShareWithFriends.CAllback {
                override fun onTotalShareCountFromDialog(count: Int) {
                    reelList[position].share_count = (reelList[position].share_count ?: 0) + count
                    holder.shareCount.setText("${reelList[position].share_count}")
                    reelList[position].isDataUpdated = true
                }

            }
        )
        holder.buttonShare.setOnClickListener {
            dialogFragmentShares.show((context as AppCompatActivity).supportFragmentManager, "")
        }

        holder.ivMenu.setOnClickListener {
            val dialog = BottomDialogFragmentMenuReels((reelList[position].user_id != userId),
                (context as BaseActivity).networkViewModel,
                reelList[position],
                object : BottomDialogFragmentMenuReels.Callback {
                    override fun onClickOnMenu(typeCode: Int, commentMode: Boolean?) {
                        when (typeCode) {
                            1 -> {//Edit
                                callback.onClickOnEditReel(reelList[position])
                            }

                            2 -> {//delete
                                val dialog = CommonConfirmationDialog(
                                    context,
                                    "Delete Funtime",
                                    "Are you sure you want to Delete this funtime? ",
                                    "Delete",
                                    "Cancel",
                                    object : CommonConfirmationDialog.Callback {
                                        override fun onDialogResult(isPermissionGranted: Boolean) {
                                            if (isPermissionGranted) {
                                                callback.onClickOnRemoveReel(reelList[position])
                                            }
                                        }
                                    })
                                dialog.show()
                            }
                            3 -> {
                                //Report user's funtime only
                                context.startActivity(
                                    IntentHelper.getReportUserScreen(context)!!
                                        .putExtra("id", reelList[position].id)
                                )
                            }
                            4 -> {
                                //Block user
                                val dialog = CommonConfirmationDialog(
                                    context,
                                    "Block User",
                                    "Are you sure you want to block this user? In future you will not be able to see post or Funtime post of this user.",
                                    "Block User",
                                    "Cancel",
                                    object : CommonConfirmationDialog.Callback {
                                        override fun onDialogResult(isPermissionGranted: Boolean) {
                                            if (isPermissionGranted) {
                                                callback.onClickOnBlockUser(reelList[position])
                                            }
                                        }
                                    })
                                dialog.show()
                            }
                            5 -> {//Download
                                callback.downloadThisFuntime(reelList[position])
                            }
                            6 -> {//Comment settings
                                Toast.makeText(
                                    it.context,
                                    if (commentMode == true) "Comment is On" else "Comment is Off",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (commentMode == true) {
                                    holder.buttonComment.visibility = View.VISIBLE
                                } else {
                                    holder.buttonComment.visibility = View.GONE
                                }
                            }
                            7 -> {
                                Toast.makeText(
                                    it.context,
                                    "Sharing coming soon.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })

            val manager: FragmentManager =
                (context as AppCompatActivity).getSupportFragmentManager()
            dialog.show(manager, "asdasd")
        }
    }

    override fun onViewAttachedToWindow(holder: ReelViewHolder) {
        try {
            if (holder is VideoReelFullViewHolder)
                if (holder.isVideo) {
                    holder.customPlayerView.getPlayer()?.volume = volumeForAll
                    holder.ivSoundButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.ivSoundButton.context,
                            if (volumeForAll == 0f) R.drawable.ic_sound_off else R.drawable.ic_sound_on
                        )
                    )
                    holder.ivSoundButton.animate().alpha(1f).setDuration(1000).start()
                    holder.ivSoundButton.postDelayed({
                        holder.ivSoundButton.animate().alpha(0f).setDuration(1000).start()
                    }, 1000)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onViewAttachedToWindow(holder)
    }

    private fun handleViewHolder(holder: ImageReelViewHolder, position: Int) {
        /* Set adapter (items are being used inside adapter, you can setup in your own way*/
        val ReelFullViewAdapter = ImageAdapter(holder.itemView.context, position)
        holder.recyclerViewImages.adapter = ReelFullViewAdapter
    }

    override fun getItemCount(): Int {
        Log.d("akljsdasd", reelList.size.toString())
        return reelList.size;
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

    fun removeReelById(id: String) {
        val position = reelList.indexOfFirst { it.id == id }
        reelList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun likeReelById(id: String) {
        /*       var position=  reelList.indexOfFirst { it.id== id}
               if (reelList[position].isLiked == "Yes"){
                   reelList[position].like_count--
                   reelList[position].isLiked = "No"
               }else{
                   reelList[position].like_count++
                   reelList[position].isLiked = "Yes"
               }
               notifyItemChanged(position)*/
    }

    fun blockUserFromList(position: Int) {

        //   reelList.removeAt(position)
        //   notifyItemRemoved(position)
        notifyDataSetChanged()
        /*    var listsize=reelList.size
            var deletionCount=0
            for (i in 0 until listsize) {
                Log.d("lkajsdlasd",i.toString())
                if (reelList[i].user_id == userId) {
                    deletionCount++
                    Log.d("lkajsdlasd",listsize.toString())
                    reelList.removeAt(i)
                    listsize--
                }
            }
           // notifyItemRangeRemoved(0,deletionCount)
            notifyItemRangeChanged(0, getItemCount());*/
    }

    private fun setUpInfoBackgroundColor(cl: ConstraintLayout, palette: Palette) {
        val swatch = getMostPopulousSwatch(palette)
        if (swatch != null) {
            val endColor = swatch.rgb
            cl.setBackgroundColor(endColor)
        } else {
            val defaultColor = ContextCompat.getColor(cl.context, R.color.pinklight)
            cl.setBackgroundColor(defaultColor)
        }
    }

    private fun getMostPopulousSwatch(palette: Palette?): Palette.Swatch? {
        var mostPopulous: Palette.Swatch? = null
        if (palette != null) {
            for (swatch in palette.swatches) {
                if (mostPopulous == null || swatch.population > mostPopulous.population) {
                    mostPopulous = swatch
                }
            }
        }
        return mostPopulous
    }
}