package com.stalmate.user.commonadapters


import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.text.Html
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding2.widget.RxTextView
/*import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions*/
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ItemFeedBinding
import com.stalmate.user.modules.reels.player.InstaLikePlayerView
import com.stalmate.user.utilities.SeeModetextViewHelper
import com.stalmate.user.view.dashboard.ActivityDashboard
import com.stalmate.user.view.dashboard.funtime.DialogFragmentComments
import com.stalmate.user.view.dashboard.funtime.DialogFragmentShareWithFriends
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.viewmodel.AppViewModel
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AdapterFeed(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: FragmentActivity,
    var callBackAdapterFeed: AdapterFeed.Callbackk? = null
) :
    RecyclerView.Adapter<AdapterFeed.FeedViewHolder>() {
    var list = ArrayList<ResultFuntime>()
    var isMuted = false

    fun likeReelById() {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdapterFeed.FeedViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemFeedBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: AdapterFeed.FeedViewHolder, position: Int) {
        holder.itemView.apply {
            val feed = list[position]
            try {
                if (feed.user_id == ((context as ActivityDashboard).prefManager?._id)) {
                    holder.appCompatTextView5.visibility = View.INVISIBLE
                } else {
                    if (feed.isFollowing == "No") {
                        holder.appCompatTextView5.text = "Follow"
                        holder.appCompatTextView5.visibility = View.VISIBLE
                        holder.appCompatTextView5.setOnClickListener {
                            feed.isFollowing = "Yes"
                            holder.appCompatTextView5.text = "Sent follow request"
                            callBackAdapterFeed?.onClickOnFollowButtonReel(feed)
                            notifyDataSetChanged()
                        }
                    } else {
                        holder.appCompatTextView5.setOnClickListener {
                            feed.isFollowing = "No"
                            holder.appCompatTextView5.text = "Follow"
                            callBackAdapterFeed?.onClickOnFollowButtonReel(feed)
                            notifyDataSetChanged()
                        }
                        holder.appCompatTextView5.text = "Sent follow request"
                        holder.appCompatTextView5.visibility = View.VISIBLE
                    }
                }
                val requestOptionsMe = RequestOptions()
                Glide.with(holder.appCompatImageView5.context)
                    .load(((context as ActivityDashboard).prefManager?.profile_img_1))
                    .apply(requestOptionsMe)
                    .thumbnail(
                        Glide.with(context)
                            .load(((context as ActivityDashboard).prefManager?.profile_img_1))
                    )
                    .placeholder(R.drawable.image)
                    .error(R.drawable.image)
                    .into(holder.ivButtonMenu1)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val requestOptions1 = RequestOptions()
            Glide.with(holder.userProfileImage.context)
                .load(feed.profile_img)
                .apply(requestOptions1)
                .thumbnail(Glide.with(context).load(feed.profile_img))
                .placeholder(R.drawable.image)
                .error(R.drawable.image)
                .into(holder.userProfileImage)

            holder.tvUserName.text = feed.first_name + " " + feed.last_name

            holder.likeCount.text = feed.like_count.toString() + " Likes"
            holder.commentCount.text = feed.comment_count.toString() + " Comments"
            holder.shareCount.text = "${feed.share_count} Shares"
            holder.appCompatTextView6.text = feed.Created_date

            if (!feed.topcomment.isNullOrEmpty()) {
                if (!feed.topcomment?.get(0)?.comment.isNullOrEmpty()) {
                    holder.tvDes.visibility = View.VISIBLE
                    holder.tvDes.text = feed.topcomment?.get(0)?.comment.toString()
                } else {
                    holder.tvDes.visibility = View.GONE
                }
                if (feed.topcomment?.get(0)?.user_id != null) {
                    feed.topcomment?.get(0)?.user_id?.let {
                        if (!it.first_name.isNullOrEmpty()) {
                            holder.tvUserName1.visibility = View.VISIBLE
                            holder.tvUserName1.text = (it.first_name + " " + it.last_name)
                        } else {
                            holder.tvUserName1.visibility = View.GONE
                        }
                    }
                } else {
                    holder.tvUserName1.visibility = View.GONE
                }
                if (!feed.topcomment?.get(0)?.comment_image.isNullOrEmpty() && feed.topcomment?.get(
                        0
                    )?.new_comment_image.isNullOrEmpty()
                ) {
                    holder.ivCommentImage.visibility = View.VISIBLE
                    holder.ivDeleteCommentImage.visibility = View.GONE
                    Glide.with(holder.ivCommentImage.context)
                        .load(feed.topcomment?.get(0)?.comment_image)
                        .apply(RequestOptions())
                        .thumbnail(Glide.with(context).load(feed.topcomment?.get(0)?.comment_image))
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(holder.ivCommentImage)
                } else if (!feed.topcomment?.get(0)?.new_comment_image.isNullOrEmpty()) {
                    holder.ivCommentImage.visibility = View.VISIBLE
                    holder.ivDeleteCommentImage.visibility = View.VISIBLE
                    Glide.with(holder.ivCommentImage.context)
                        .load(feed.topcomment?.get(0)?.new_comment_image)
                        .apply(RequestOptions())
                        .thumbnail(
                            Glide.with(context).load(feed.topcomment?.get(0)?.new_comment_image)
                        )
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(holder.ivCommentImage)
                    holder.ivDeleteCommentImage.setOnClickListener {
                        callBackAdapterFeed?.onCaptureImage(feed, -1)
                        list[position].topcomment?.get(0)?.new_comment_image = ""
                        notifyDataSetChanged()
                    }
                } else {
                    holder.ivCommentImage.visibility = View.GONE
                    holder.ivDeleteCommentImage.visibility = View.GONE
                }
            } else {
                holder.ivCommentImage.visibility = View.GONE
                holder.ivDeleteCommentImage.visibility = View.GONE
            }

//            holder.shareCount.text = feed.share_count.toString()

            if (!feed.text.isNullOrEmpty()) {
                holder.tvPostDescription.visibility = View.VISIBLE
                holder.tvPostDescription.text = Html.fromHtml(
                    feed.text,
                    Html.FROM_HTML_MODE_COMPACT
                )
                if (holder.tvPostDescription.text.toString()
                        .split(System.getProperty("line.separator")).size > 2
                ) {
                    SeeModetextViewHelper.makeTextViewResizable(
                        holder.tvPostDescription,
                        2,
                        "more",
                        true
                    )
                }
            } else {
                holder.tvPostDescription.visibility = View.GONE
            }

            holder.appCompatImageView5.setOnClickListener {
                callBackAdapterFeed?.onCLickItem(feed)
            }

            holder.shareIcon.setOnClickListener {
                val dialogFragmen = DialogFragmentShareWithFriends(
                    (context as BaseActivity).networkViewModel,
                    feed, object : DialogFragmentShareWithFriends.CAllback {
                        override fun onTotalShareCountFromDialog(count: Int) {
                            feed.share_count = feed.share_count + count
                            holder.shareCount.text = "${feed.share_count} Shares"
                            feed.isDataUpdated = true
                        }
                    }
                )
                dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
            }

            if (feed.isLiked == "Yes") {
                holder.likeIcon.setImageResource(R.drawable.liked)
            } else {
                holder.likeIcon.setImageResource(R.drawable.like)
            }

            holder.likeIcon.setOnClickListener {
                if (feed.isLiked == "Yes") {
                    feed.like_count--
                    holder.likeCount.text = feed.like_count.toString()
                    feed.isLiked = "No"
                    holder.likeIcon.setImageResource(R.drawable.like)
                } else {
                    feed.like_count++
                    holder.likeCount.text = feed.like_count.toString()
                    feed.isLiked = "Yes"
                    holder.likeIcon.setImageResource(R.drawable.liked)
                }
                feed.isDataUpdated = true
                callBackAdapterFeed?.onClickOnLikeButtonReel(feed)
            }
            val dialogFragmentComment = DialogFragmentComments(
                (context as BaseActivity).networkViewModel,
                feed,
                object : DialogFragmentComments.Callback {
                    override fun funOnCommentDialogDismissed(commentCount: Int) {
                        holder.commentCount.text = "$commentCount Comments"
                        feed.comment_count = commentCount
                        notifyDataSetChanged()
                    }

                    override fun onCancel() {
                        callBackAdapterFeed?.hideCommentOverlay(feed, position)
                    }
                })
            holder.commentIcon.setOnClickListener {
                callBackAdapterFeed?.showCommentOverlay(feed, position)
                dialogFragmentComment.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    ""
                )
            }

            val emojiIcon = EmojIconActions(
                (context as AppCompatActivity),
                holder.appCompatImageView7.rootView,
                holder.etSearch,
                holder.appCompatImageView7
            )
//        emojiIcon.setUseSystemEmoji(true);
//        holder.etComment.setUseSystemDefault(true)
            emojiIcon.setKeyboardListener(object : EmojIconActions.KeyboardListener {
                override fun onKeyboardOpen() {}
                override fun onKeyboardClose() {}
            })

            holder.appCompatImageView7.setOnClickListener {
                emojiIcon.showPopup()
            }

            holder.appCompatImageView6.setOnClickListener {
                callBackAdapterFeed?.onCaptureImage(feed, position)
            }

            holder.ivSend.setOnClickListener {
                callBackAdapterFeed?.onSendComment(feed, holder.etSearch.text.toString())
                holder.etSearch.setText("")
            }


            if (feed.file_type.equals("0", true)) {
                holder.customPlayerView.visibility = View.GONE
                holder.ivSoundButton.visibility = View.GONE
                holder.ivPlay.visibility = View.GONE
                holder.appCompatImageView5.visibility = View.VISIBLE
                val requestOptions = RequestOptions()
                Glide.with(holder.appCompatImageView5.context)
                    .load(feed.file)
                    .apply(requestOptions)
                    .thumbnail(Glide.with(context).load(feed.file))
                    .placeholder(R.drawable.image)
                    .error(R.drawable.image)
                    .into(holder.appCompatImageView5)
            } else {
                //video/mp4
                holder.appCompatImageView5.background = null
                holder.appCompatImageView5.setImageDrawable(null)

                holder.customPlayerView.visibility = View.VISIBLE
                holder.ivSoundButton.visibility = View.VISIBLE
                holder.ivPlay.visibility = View.GONE
                holder.appCompatImageView5.visibility = View.GONE

                try {
                    /*holder.customPlayerView.reset()
//                    Set seperate ID for each player view, to prevent it being overlapped by other player's changes
                    holder.customPlayerView.id = View.generateViewId()*/
//                    Set video's direct url
                    holder.customPlayerView.setVideoUri(Uri.parse(feed.file))

                    holder.customPlayerView.setOnClickListener {
                        callBackAdapterFeed?.onCLickItem(feed)
                    }

                    holder.ivSoundButton.setOnClickListener {
                        if (holder.customPlayerView.getPlayer()?.volume == 0f) {
                            holder.customPlayerView.getPlayer()?.volume =
                                holder.customPlayerView.getPlayer()?.deviceVolume?.toFloat() ?: 1f
                            holder.ivSoundButton.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_sound_on
                                )
                            )
                        } else {
                            holder.customPlayerView.getPlayer()?.volume = 0f
                            holder.ivSoundButton.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_sound_off
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    inner class FeedViewHolder(var binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val customPlayerView = binding.feedPlayerView
        val ivSoundButton = binding.ivSoundButton
        val appCompatImageView5 = binding.appCompatImageView5
        val appCompatImageView6 = binding.appCompatImageView6
        val appCompatImageView7 = binding.appCompatImageView7
        val appCompatTextView5 = binding.appCompatTextView5
        val appCompatTextView6 = binding.appCompatTextView6
        val ivPlay = binding.ivPlay
        val ivSend = binding.ivSend
        val etSearch = binding.etSearch
        val commentIcon = binding.commentIcon
        val commentCount = binding.commentCount
        val likeCount = binding.likeCount
        val likeIcon = binding.likeIcon
        val shareIcon = binding.shareIcon
        val shareCount = binding.shareCount
        val tvPostDescription = binding.tvPostDescription
        val ivDeleteCommentImage = binding.ivDeleteCommentImage
        val ivCommentImage = binding.ivCommentImage
        val ivButtonMenu1 = binding.ivButtonMenu1
        val tvUserName = binding.tvUserName
        val tvUserName1 = binding.tvUserName1
        val userProfileImage = binding.userProfileImage
        val tvDes = binding.tvDes
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun x() {

    }


/*
    private fun likeUnlikeApi(
        position: Int, feed: Feed
    ) {
        val hashMap = HashMap<String, String>()
        hashMap["token"] =
            PrefManager.getInstance(context)!!.userDetail.token
        hashMap["post_id"] = feed.id
        if (feed.like == 0) {
            hashMap["like"] = "1"
        } else {
            hashMap["like"] = "0"
        }
        RestClient.getInst().likeUnlikeFeed(hashMap).enqueue(object : Callback<ModelSuccess> {
            override fun onResponse(call: Call<ModelSuccess>, response: Response<ModelSuccess>) {
                if (response.body()!!.result) {
                    if (feed.like == 0) {
                        feed.like = 1
                        feed.likeCount = (feed.likeCount.toInt() + 1).toString()
                    } else {
                        feed.like = 0
                        feed.likeCount = (feed.likeCount.toInt() - 1).toString()
                    }
                    viewModel.update(feed, position)
                } else {
                }
            }

            override fun onFailure(call: Call<ModelSuccess>, t: Throwable) {}
        })
    }


    private fun followUnfollowApi(
        position: Int, feed: Feed
    ) {
        val hashMap = HashMap<String, String>()
        hashMap["token"] =
            PrefManager.getInstance(context)!!.userDetail.token
        hashMap["postUserID"] = feed.user_id
        if (feed.already_follow == "No") {
            hashMap["follow"] = "Yes"
        } else {
            hashMap["follow"] = "No"
        }
        RestClient.getInst().followUnfollowUser(hashMap).enqueue(object : Callback<ModelSuccess> {
            override fun onResponse(call: Call<ModelSuccess>, response: Response<ModelSuccess>) {
                if (response.body()!!.result) {
                    if (feed.already_follow == "No") {
                        feed.already_follow = "Yes"
                    } else {
                        feed.already_follow = "No"
                    }
                    viewModel.update(feed, position)
                } else {
                }
            }

            override fun onFailure(call: Call<ModelSuccess>, t: Throwable) {}
        })
    }
*/


    fun submitList(feedList: List<ResultFuntime>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    fun addToList(feedList: ArrayList<ResultFuntime>) {
        val size = list.size
        list.addAll(feedList)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    public interface Callbackk {
        fun onClickOnViewComments(postId: Int)
        fun onCLickItem(item: ResultFuntime)

        fun onClickOnLikeButtonReel(feed: ResultFuntime)
        fun onClickOnFollowButtonReel(feed: ResultFuntime)
        fun onSendComment(feed: ResultFuntime, comment: String)
        fun onCaptureImage(feed: ResultFuntime, position: Int)
        fun showCommentOverlay(feed: ResultFuntime, position: Int)
        fun hideCommentOverlay(feed: ResultFuntime, position: Int)
    }


}