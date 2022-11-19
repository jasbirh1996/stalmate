package com.stalmate.user.modules.reels.adapter

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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.HorizontalItemReelBinding
import com.stalmate.user.databinding.ItemFullViewReelBinding
import com.stalmate.user.modules.reels.player.ImageAdapter
import com.stalmate.user.modules.reels.player.holders.ImageReelViewHolder
import com.stalmate.user.modules.reels.player.holders.ReelViewHolder
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.utilities.SeeModetextViewHelper
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.BottomDialogFragmentMenuReels
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import com.stalmate.user.view.dialogs.SuccessDialog


class ReelFullViewAdapter(val context: Context) :
    ListAdapter<ResultFuntime, ReelViewHolder>(DIFF_CALLBACK) {
    var reelList = ArrayList<ResultFuntime>()
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
        const val FEED_TYPE_VIDEO = 1;
        const val FEED_TYPE_IMAGES_MULTIPLE = 2;
    }


    public interface Callback{
        fun onClickOnReelMenu(type:Int)
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("akljsdasd","asdkasd")
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

        holder.tvUserName.text= reelList[position].first_name+ " " +reelList[position].last_name
        Glide.with(context).load(reelList[position].profile_img).placeholder(R.drawable.profileplaceholder).into(holder.imgUserProfile)
        Glide.with(context).load(reelList[position].sound_image).placeholder(R.drawable.profileplaceholder).into(holder.ivMusicImage)

        holder.likeCount.text = reelList[position].like_count.toString()
        holder.commentCount.text = reelList[position].comment_count.toString()
      //  holder.shareCount.text = reelList[position].share_count.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvStatusDescription.setText(Html.fromHtml(reelList[position]!!.text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvStatusDescription.setText(Html.fromHtml(reelList[position]!!.text));
        }

        if (holder.tvStatusDescription.getText().toString().split(System.getProperty("line.separator")).size>2){
            SeeModetextViewHelper.makeTextViewResizable(
                holder.tvStatusDescription,
                2,
                "more",
                true
            );
        }
        holder.buttonLike.setOnClickListener {
            // likeApiHit()
        }

        /* val timesAg = TimesAgo2.covertTimeToText(item.Created_date, true)*/
        if (!ValidationHelper.isNull(reelList[position].sound_name)){
            holder.layoutMusic.visibility=View.VISIBLE
            holder.layoutMusic.setOnClickListener {

                context.startActivity(IntentHelper.getReelListByAudioScreen(context)!!.putExtra("data",reelList[position]))
            }
            holder.tvMusic.text=reelList[position].sound_name
            holder.tvMusicArtist.text=reelList[position].artist_name
        }


        holder.tvStoryPostTime.text = reelList[position].Created_date
        holder.buttonAdd.setOnClickListener {
            context.startActivity(IntentHelper.getCreateReelsScreen(context))
        }


        holder.buttonComment.setOnClickListener {

        }


        holder.ivMenu.setOnClickListener {




         var dialog=   BottomDialogFragmentMenuReels(reelList[position].is_my!="YES",object :BottomDialogFragmentMenuReels.Callback{
                override fun onClickOnMenu(typeCode: Int) {
                    when(typeCode){
                        1->{
                            context.startActivity(IntentHelper.getCreateFuntimePostScreen(context))
                        }

                        2->{//delete
                           var dialog= CommonConfirmationDialog(context,"Block User","Are you sure you want to Delete this post? ","Delete","Cancel",object :CommonConfirmationDialog.Callback{
                                override fun onDialogResult(isPermissionGranted: Boolean) {
                                    if (isPermissionGranted){
                                         reelList.removeAt(position)
                                        notifyItemRemoved(position)
                                    }
                                }
                            })
                            dialog.show()
                        }
                        3->{
                            context.startActivity(IntentHelper.getReportUserScreen(context))
                        }
                        4->{
                            var dialog= CommonConfirmationDialog(context,"Block User","Are you sure you want to block this user? In future you will not be able to see post or Funtime post of this user.","Block User","Cancel",object :CommonConfirmationDialog.Callback{
                                override fun onDialogResult(isPermissionGranted: Boolean) {
                                    if (isPermissionGranted){
                                        reelList.removeAt(position)
                                        notifyItemRemoved(position)

                                        var dialogSuccess= SuccessDialog(context,"Success","User Blocked Successfully.","Done",object :SuccessDialog.Callback{
                                            override fun onDialogResult(isPermissionGranted: Boolean) {
                                                if (isPermissionGranted){



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
            dialog.show(manager,"asdasd")
        }





    }

    private fun handleViewHolder(holder: ImageReelViewHolder, position: Int) {

        /* Set adapter (items are being used inside adapter, you can setup in your own way*/
        val ReelFullViewAdapter = ImageAdapter(holder.itemView.context, position)
        holder.recyclerViewImages.adapter = ReelFullViewAdapter


    }

    override fun getItemCount(): Int {
        Log.d("akljsdasd",reelList.size.toString())
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

    /*  private fun likeApiHit() {
          var hashmap = HashMap<String, String>()
          hashmap.put("funtime_id", item!!.id)
          networkViewModel.funtimeLiveLikeUnlikeData(hashmap)
          networkViewModel.funtimeLiveLikeUnlikeData.observe(viewLifecycleOwner){
              if (it!!.message=="Liked") {
                  binding.like.text = it.like_count.toString()
                  binding.likeIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_funtime_post_like_fill))
              }else{
                  binding.like.text = it.like_count.toString()
              }
          }
      }*/


}