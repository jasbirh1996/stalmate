package com.stalmate.user.modules.reels.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.HorizontalItemReelBinding
import com.stalmate.user.databinding.ItemFullViewReelBinding
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.modules.reels.player.ImageAdapter
import com.stalmate.user.modules.reels.player.holders.ImageReelViewHolder
import com.stalmate.user.modules.reels.player.holders.ReelViewHolder
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.utilities.SeeModetextViewHelper
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.ActivityDashboardNew
import com.stalmate.user.view.dashboard.funtime.*
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import com.stalmate.user.view.dialogs.SuccessDialog
import com.stalmate.user.viewmodel.AppViewModel


class ReelFullViewAdapter(val context: Context, var callback: Callback) :
    ListAdapter<ResultFuntime, ReelViewHolder>(DIFF_CALLBACK) {
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


    public interface Callback {
        fun onClickOnRemoveReel(resultFuntime: ResultFuntime)
        fun onClickOnLikeButtonReel(resultFuntime: ResultFuntime)
        fun onClickOnEditReel(resultFuntime: ResultFuntime)
        fun onClickOnBlockUser(resultFuntime: ResultFuntime)
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
        return VideoReelFullViewHolder(
            ItemFullViewReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {

        if (holder is VideoReelFullViewHolder) {
            handleViewHolder(holder, position)
        } else if (holder is ImageReelViewHolder) {
            handleViewHolder(holder, position)
        }

    }

    private fun handleViewHolder(holder: VideoReelFullViewHolder, position: Int) {
        /*Reset ViewHolder */
        removeImageFromImageView(holder.videoThumbnail)

        holder.customPlayerView.reset()

        /*Set seperate ID for each player view, to prevent it being overlapped by other player's changes*/
        holder.customPlayerView.id = View.generateViewId()

        /*circlular repeatation of items*/
        //  val videoPos = (position % Constants.videoList.size);

        /*Set ratio according to video*/
        //  (holder.videoThumbnail.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = Constants.videoList.get(videoPos).dimension

        /*Set video's direct url*/
        holder.customPlayerView.setVideoUri(Uri.parse(reelList[position].file))


        /*      *//*Set video's thumbnail locally (by drawable), you can set it by remoteUrl too*//*
        val resID: Int = context.getResources().getIdentifier(
            "thumbnail_" + position,
            "drawable",
            context.getPackageName()
        )

        val res: Drawable = context.getResources().getDrawable(resID, null)*/

/*        (context as Activity).runOnUiThread(Runnable {
            //change View Data
        })*/

        val requestOptions = RequestOptions()
        Glide.with(context)
            .load(reelList[position].file)
            .apply(requestOptions)
            .thumbnail(Glide.with(context).load(reelList[position].file))
            .into(holder.videoThumbnail)
        //  holder.videoThumbnail.setImageDrawable(res);

        holder.tvUserName.text = reelList[position].first_name + " " + reelList[position].last_name
        Glide.with(context).load(reelList[position].profile_img)
            .placeholder(R.drawable.profileplaceholder).into(holder.imgUserProfile)
        Glide.with(context).load(reelList[position].sound_image)
            .placeholder(R.drawable.profileplaceholder).into(holder.ivMusicImage)

        holder.likeCount.text = reelList[position].like_count.toString()
        holder.commentCount.text = reelList[position].comment_count.toString()
        //  holder.shareCount.text = reelList[position].share_count.toString()
        if (reelList[position].tag_user.size > 0) {
            holder.layoutTagged.visibility = View.VISIBLE
            holder.tvTaggedPeopleCount.text =
                reelList[position].tag_user.size.toString() + " People Tagged"
            holder.tvTaggedPeopleCount.setOnClickListener {


                var dialogFragmen = FragmentBSTaggedUsers(reelList[position].tag_user)
                dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
            }
        }

        if (!ValidationHelper.isNull(reelList[position].location)) {
            holder.tvLocation.text = reelList[position].location
            holder.tvLocation.visibility = View.VISIBLE
            holder.ivLocation.visibility=View.VISIBLE
        }



        holder.tvStatusDescription.setText(
            Html.fromHtml(
                reelList[position].text,
                Html.FROM_HTML_MODE_COMPACT
            )
        );

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
        holder.buttonLike.setOnClickListener {


            if (reelList[position].isLiked == "Yes") {
                reelList[position].like_count--
                holder.likeCount.text = reelList[position].like_count.toString()
                reelList[position].isLiked = "No"
            } else {
                reelList[position].like_count++
                holder.likeCount.text = reelList[position].like_count.toString()
                reelList[position].isLiked = "Yes"
            }
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


        holder.buttonComment.setOnClickListener {
            var dialogFragmen = DialogFragmentComments(
                (context as BaseActivity).networkViewModel,
                funtimeId = reelList[position].id
            )
            dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
        }
        holder.buttonShare.setOnClickListener {
            var dialogFragmen = DialogFragmentShareWithFriends(
                (context as BaseActivity).networkViewModel,
                reelList[position]
            )
            dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
        }



        holder.ivMenu.setOnClickListener {
            var dialog = BottomDialogFragmentMenuReels(reelList[position].is_my != "YES",
                object : BottomDialogFragmentMenuReels.Callback {
                    override fun onClickOnMenu(typeCode: Int) {
                        when (typeCode) {
                            1 -> {
                                callback.onClickOnEditReel(reelList[position])
                            }

                            2 -> {//delete
                                var dialog = CommonConfirmationDialog(
                                    context,
                                    "Block User",
                                    "Are you sure you want to Delete this post? ",
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
                                context.startActivity(IntentHelper.getReportUserScreen(context))
                            }
                            4 -> {
                                var dialog = CommonConfirmationDialog(
                                    context,
                                    "Block User",
                                    "Are you sure you want to block this user? In future you will not be able to see post or Funtime post of this user.",
                                    "Block User",
                                    "Cancel",
                                    object : CommonConfirmationDialog.Callback {
                                        override fun onDialogResult(isPermissionGranted: Boolean) {
                                            if (isPermissionGranted) {
                                                var dialogSuccess = SuccessDialog(
                                                    context,
                                                    "Success",
                                                    "User Blocked Successfully.",
                                                    "Done",
                                                    object : SuccessDialog.Callback {
                                                        override fun onDialogResult(
                                                            isPermissionGranted: Boolean
                                                        ) {
                                                            if (isPermissionGranted) {

                                                                callback.onClickOnBlockUser(reelList[position])

                                                            }
                                                        }
                                                    })
                                                dialogSuccess.show()

                                            }
                                        }
                                    })
                                dialog.show()
                            }
                        }
                    }
                })
            val manager: FragmentManager =
                (context as AppCompatActivity).getSupportFragmentManager()
            dialog.show(manager, "asdasd")
        }


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

    fun removeReelById(id: String) {
        var position = reelList.indexOfFirst { it.id == id }
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


}