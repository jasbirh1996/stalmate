package com.stalmate.user.modules.reels.player
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.HorizontalItemReelBinding
import com.stalmate.user.databinding.ItemReelBinding
import com.stalmate.user.modules.reels.player.holders.ImageReelViewHolder
import com.stalmate.user.modules.reels.player.holders.ReelViewHolder
import com.stalmate.user.modules.reels.player.holders.VideoReelViewHolder
import com.stalmate.user.utilities.SeeModetextViewHelper
import com.stalmate.user.view.dashboard.funtime.ResultFuntime


class ReelAdapter(val context: Context) :
    ListAdapter<ResultFuntime, ReelViewHolder>(DIFF_CALLBACK) {
    var reelList = ArrayList<ResultFuntime>()
    lateinit var instaLikePlayerView:InstaLikePlayerView
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
       try {
           instaLikePlayerView=holder.customPlayerView
       }catch (e:Exception){

       }
        holder.customPlayerView.reset()

        /*Set seperate ID for each player view, to prevent it being overlapped by other player's changes*/
        holder.customPlayerView.id = View.generateViewId()

        /*circlular repeatation of items*/
      //  val videoPos = (position % Constants.videoList.size);

        /*Set ratio according to video*/
      //  (holder.videoThumbnail.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = Constants.videoList.get(videoPos).dimension

        /*Set video's direct url*/
        holder.customPlayerView.setVideoUri(Uri.parse(reelList[position].file))


        holder.customPlayerView.setOnClickListener {
            context.startActivity(IntentHelper.getFullViewReelActivity(context)!!.putExtra("data",reelList[position]))
        }


  /*      *//*Set video's thumbnail locally (by drawable), you can set it by remoteUrl too*//*
        val resID: Int = context.getResources().getIdentifier(
            "thumbnail_" + position,
            "drawable",
            context.getPackageName()
        )

        val res: Drawable = context.getResources().getDrawable(resID, null)*/
        val requestOptions = RequestOptions()
        Glide.with(context)
            .load(reelList[position].file)
            .apply(requestOptions)
            .thumbnail(Glide.with(context).load(reelList[position].file))
            .into(holder.videoThumbnail)
      //  holder.videoThumbnail.setImageDrawable(res);

        holder.tvUserName.text= reelList[position]!!.first_name+ " " +reelList[position]!!.last_name
        Glide.with(context).load(reelList[position]!!.profile_img).placeholder(R.drawable.profileplaceholder).into(holder.imgUserProfile)

        holder.like.text = reelList[position]!!.like_count.toString()
        holder.comment.text = reelList[position]!!.comment_count.toString()

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

        holder.like.setOnClickListener {
           // likeApiHit()
        }

        /* val timesAg = TimesAgo2.covertTimeToText(item!!.Created_date, true)*/
        holder.tvStoryPostTime.text = reelList[position]!!.Created_date


    }

    private fun handleViewHolder(holder: ImageReelViewHolder, position: Int) {

        /* Set adapter (items are being used inside adapter, you can setup in your own way*/
        val ReelAdapter = ImageAdapter(holder.itemView.context, position)
        holder.recyclerViewImages.adapter = ReelAdapter


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