package com.stalmate.user.commonadapters


import android.content.Context
import android.text.Html
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
        holder.bind(list.get(position), callBackAdapterFeed, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class FeedViewHolder(var binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feed: ResultFuntime, callBackAdapterFeed: Callbackk?, position: Int) {
            try {
                if (feed.user_id == ((context as ActivityDashboard).prefManager?._id)) {
                    binding.appCompatTextView5.visibility = View.INVISIBLE
                } else {
                    if (feed.isFollowing == "No") {
                        binding.appCompatTextView5.text = "Follow"
                        binding.appCompatTextView5.visibility = View.VISIBLE
                        binding.appCompatTextView5.setOnClickListener {
                            feed.isFollowing = "Yes"
                            binding.appCompatTextView5.text = "Sent follow request"
                            callBackAdapterFeed?.onClickOnFollowButtonReel(feed)
                            notifyDataSetChanged()
                        }
                    } else {
                        binding.appCompatTextView5.setOnClickListener {
                            feed.isFollowing = "No"
                            binding.appCompatTextView5.text = "Follow"
                            callBackAdapterFeed?.onClickOnFollowButtonReel(feed)
                            notifyDataSetChanged()
                        }
                        binding.appCompatTextView5.text = "Sent follow request"
                        binding.appCompatTextView5.visibility = View.VISIBLE
                    }
                }
                val requestOptionsMe = RequestOptions()
                Glide.with(binding.appCompatImageView5.context)
                    .load((context).prefManager?.profile_img_1)
                    .apply(requestOptionsMe)
                    .thumbnail(
                        Glide.with(context)
                            .load((context).prefManager?.profile_img_1)
                    )
                    .placeholder(R.drawable.image)
                    .error(R.drawable.image)
                    .into(binding.ivButtonMenu1)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val requestOptions = RequestOptions()
            Glide.with(binding.appCompatImageView5.context)
                .load(feed.file)
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(feed.file))
                .placeholder(R.drawable.image)
                .error(R.drawable.image)
                .into(binding.appCompatImageView5)

            val requestOptions1 = RequestOptions()
            Glide.with(binding.userProfileImage.context)
                .load(feed.profile_img)
                .apply(requestOptions1)
                .thumbnail(Glide.with(context).load(feed.profile_img))
                .placeholder(R.drawable.image)
                .error(R.drawable.image)
                .into(binding.userProfileImage)

            binding.tvUserName.text = feed.first_name + " " + feed.last_name

            binding.likeCount.text = feed.like_count.toString() + " Likes"
            binding.commentCount.text = feed.comment_count.toString() + " Comments"
            binding.shareCount.text = "${feed.share_count} Shares"
            binding.appCompatTextView6.text = feed.Created_date

            if (!feed.topcomment.isNullOrEmpty()) {
                binding.tvDes.text = feed.topcomment?.get(0)?.comment.toString()
                feed.topcomment?.get(0)?.user_id?.let {
                    binding.tvUserName1.text = (it.first_name + " " + it.last_name)
                }
                if (!feed.topcomment?.get(0)?.comment_image.isNullOrEmpty() && feed.topcomment?.get(0)?.new_comment_image.isNullOrEmpty()) {
                    binding.ivCommentImage.visibility = View.VISIBLE
                    binding.ivDeleteCommentImage.visibility = View.GONE
                    Glide.with(binding.ivCommentImage.context)
                        .load(feed.topcomment?.get(0)?.comment_image)
                        .apply(RequestOptions())
                        .thumbnail(Glide.with(context).load(feed.topcomment?.get(0)?.comment_image))
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(binding.ivCommentImage)
                } else if (!feed.topcomment?.get(0)?.new_comment_image.isNullOrEmpty()) {
                    binding.ivCommentImage.visibility = View.VISIBLE
                    binding.ivDeleteCommentImage.visibility = View.VISIBLE
                    Glide.with(binding.ivCommentImage.context)
                        .load(feed.topcomment?.get(0)?.new_comment_image)
                        .apply(RequestOptions())
                        .thumbnail(
                            Glide.with(context).load(feed.topcomment?.get(0)?.new_comment_image)
                        )
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(binding.ivCommentImage)
                    binding.ivDeleteCommentImage.setOnClickListener {
                        callBackAdapterFeed?.onCaptureImage(feed, -1)
                        list[position].topcomment?.get(0)?.new_comment_image = ""
                        notifyDataSetChanged()
                    }
                } else {
                    binding.ivCommentImage.visibility = View.GONE
                    binding.ivDeleteCommentImage.visibility = View.GONE
                }
            }else{
                binding.ivCommentImage.visibility = View.GONE
                binding.ivDeleteCommentImage.visibility = View.GONE
            }

//            binding.shareCount.text = feed.share_count.toString()

            if (feed.file_type.contains("image")) {
                binding.ivPlay.visibility = View.GONE
            } else {
                binding.ivPlay.visibility = View.VISIBLE
            }

            binding.tvPostDescription.text = Html.fromHtml(
                feed.text,
                Html.FROM_HTML_MODE_COMPACT
            )

            if (binding.tvPostDescription.text.toString()
                    .split(System.getProperty("line.separator")).size > 2
            ) {
                SeeModetextViewHelper.makeTextViewResizable(
                    binding.tvPostDescription,
                    2,
                    "more",
                    true
                )
            }

            binding.appCompatImageView5.setOnClickListener {
                callBackAdapterFeed?.onCLickItem(feed)
            }

            binding.shareIcon.setOnClickListener {
                val dialogFragmen = DialogFragmentShareWithFriends(
                    (context as BaseActivity).networkViewModel,
                    feed, object : DialogFragmentShareWithFriends.CAllback {
                        override fun onTotalShareCountFromDialog(count: Int) {
                            feed.share_count = feed.share_count + count
                            binding.shareCount.text = "${feed.share_count} Shares"
                            feed.isDataUpdated = true
                        }
                    }
                )
                dialogFragmen.show((context as AppCompatActivity).supportFragmentManager, "")
            }

            if (feed.isLiked == "Yes") {
                binding.likeIcon.setImageResource(R.drawable.liked)
            } else {
                binding.likeIcon.setImageResource(R.drawable.like)
            }

            binding.likeIcon.setOnClickListener {
                if (feed.isLiked == "Yes") {
                    feed.like_count--
                    binding.likeCount.text = feed.like_count.toString()
                    feed.isLiked = "No"
                    binding.likeIcon.setImageResource(R.drawable.like)
                } else {
                    feed.like_count++
                    binding.likeCount.text = feed.like_count.toString()
                    feed.isLiked = "Yes"
                    binding.likeIcon.setImageResource(R.drawable.liked)
                }
                feed.isDataUpdated = true
                callBackAdapterFeed?.onClickOnLikeButtonReel(feed)
            }
            val dialogFragmentComment = DialogFragmentComments(
                (context as BaseActivity).networkViewModel,
                feed,
                object : DialogFragmentComments.Callback {
                    override fun funOnCommentDialogDismissed(commentCount: Int) {
                        binding.commentCount.text = "$commentCount Comments"
                        feed.comment_count = commentCount
                        notifyDataSetChanged()
                    }
                })
            binding.commentIcon.setOnClickListener {
                dialogFragmentComment.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    ""
                )
            }

            val emojiIcon = EmojIconActions(
                (context as AppCompatActivity),
                binding.root,
                binding.etSearch,
                binding.appCompatImageView7
            )
//        emojiIcon.setUseSystemEmoji(true);
//        binding.etComment.setUseSystemDefault(true)
            emojiIcon.setKeyboardListener(object : EmojIconActions.KeyboardListener {
                override fun onKeyboardOpen() {}
                override fun onKeyboardClose() {}
            })

            binding.appCompatImageView7.setOnClickListener {
                emojiIcon.showPopup()
            }

            binding.appCompatImageView6.setOnClickListener {
                callBackAdapterFeed?.onCaptureImage(feed, position)
            }

            binding.ivSend.setOnClickListener {
                callBackAdapterFeed?.onSendComment(feed, binding.etSearch.text.toString())
                binding.etSearch.setText("")
            }

            /*val disposable4 = RxTextView.textChangeEvents(binding.etSearch)
                .skipInitialValue()
                .debounce(400, TimeUnit.MILLISECONDS)
                .map { it.text().toString() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }*/
/*

            val config = reactionConfig(context) {
                reactions {
                    resId    { com.stalmate.user.R.drawable.ic_fb_like }
                    resId    { R.drawable.ic_fb_love }
                    resId    { R.drawable.ic_fb_laugh }
                    reaction { R.drawable.ic_fb_wow scale ImageView.ScaleType.FIT_XY }
                    reaction { R.drawable.ic_fb_sad scale ImageView.ScaleType.FIT_XY }
                    reaction { R.drawable.ic_fb_angry scale ImageView.ScaleType.FIT_XY }
                }
            }



            val popup = ReactionPopup(
                context,
                ReactionsConfigBuilder(context)
                    .withReactions(
                        intArrayOf(
                            R.drawable.ic_fb_like,
                            R.drawable.ic_fb_love,
                            R.drawable.ic_fb_laugh,
                            R.drawable.ic_fb_wow,
                            R.drawable.ic_fb_sad,
                            R.drawable.ic_fb_angry
                        )
                    )
                    .build())


            popup.reactionSelectedListener = { position: Int ->

                Log.d("Reactions", "Selection position=$position")
                position != 3
            }


     */
/*       binding.like.setOnLongClickListener {
                Log.d("Reactions", "Selection position")
                popup.showAsDropDown(binding.likeIcon)
                return@setOnLongClickListener true
            }*//*






            binding.like.setOnTouchListener(object : View.OnTouchListener {
               @SuppressLint("ClickableViewAccessibility")
               var firsttouch=0.0;
              var  isup=false;
                var  millistotouch=400;
                override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                    if(motionEvent!!.action==MotionEvent.ACTION_DOWN){
                        firsttouch = System.currentTimeMillis().toDouble();
                        Handler().postDelayed(Runnable {
                            if (!isup) {
                                //
                                // Do your long click work
                                //
                                popup.onTouch(binding.like, motionEvent)
                            }
                            else
                            {
                                firsttouch=0.0;
                                isup=false;
                            }

                        }, millistotouch.toLong())
                        return true
                    }

                    if (motionEvent!!.action==MotionEvent.ACTION_UP){
                        if ((System.currentTimeMillis()-firsttouch)<millistotouch)
                        {
                            isup=true;
                            //
                            // Do your short click work
                            //
                        }
                    }


                    return false
                } });

*/


        }

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
    }


}