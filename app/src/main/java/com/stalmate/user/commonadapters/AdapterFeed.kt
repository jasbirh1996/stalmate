package com.stalmate.user.commonadapters



import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFeedBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.viewmodel.AppViewModel


class AdapterFeed(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) :
    RecyclerView.Adapter<AdapterFeed.FeedViewHolder>() {
    var list = ArrayList<Feed>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdapterFeed.FeedViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemFeedBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: AdapterFeed.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class FeedViewHolder(var binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feed: Feed) {


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


     /*       binding.like.setOnLongClickListener {
                Log.d("Reactions", "Selection position")
                popup.showAsDropDown(binding.likeIcon)
                return@setOnLongClickListener true
            }*/





            binding.like.setOnTouchListener(object : View.OnTouchListener {
               @SuppressLint("ClickableViewAccessibility")
               var firsttouch=0.0;
              var   isup=false;
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




        }

    }

    fun x(){

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


    fun submitList(feedList: List<Feed>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnViewComments(postId: Int)
    }








}