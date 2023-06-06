package com.stalmate.user.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemHomeStoryBinding
import com.stalmate.user.model.Feed

import com.stalmate.user.viewmodel.AppViewModel

class UserHomeStoryAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) :
    RecyclerView.Adapter<UserHomeStoryAdapter.FeedViewHolder>() {
    var list = ArrayList<Feed>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UserHomeStoryAdapter.FeedViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_story, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemHomeStoryBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: UserHomeStoryAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class FeedViewHolder(var binding: ItemHomeStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Feed) {


/*            binding.ivUserImage.setShapeAppearanceModel(binding.ivUserImage.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCornerSizes(20f)
                    .build());*/

            binding.root.setOnClickListener {
                context.startActivity(IntentHelper.getStoryActivity(context))
            }


        }

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
        fun onClickOnProfile(user: Feed)
    }


}