package com.stalmate.user.view.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemProfileFriendBinding

import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class ProfileFriendAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
) :
    RecyclerView.Adapter<ProfileFriendAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileFriendAdapter.FeedViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile_friend, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemProfileFriendBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: ProfileFriendAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class FeedViewHolder(var binding: ItemProfileFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {


            val radius = context.resources.getDimension(R.dimen.dp_15)
            binding.ivUserImage.setShapeAppearanceModel(binding.ivUserImage.getShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED,radius)
                .build());


/*            binding.ivUserImage.setShapeAppearanceModel(binding.ivUserImage.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCornerSizes(20f)
                    .build());*/

                binding.root.setOnClickListener {
                    callback.onClickOnProfile(friend)
                }
            ImageLoaderHelperGlide.setGlide(context,binding.ivUserImage,friend.img,R.drawable.user_placeholder)

            binding.tvUserName.text=friend.first_name



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


    fun submitList(feedList: List<User>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnProfile(user: User)
    }


}