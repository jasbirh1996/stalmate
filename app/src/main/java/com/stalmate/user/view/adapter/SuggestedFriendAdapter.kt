package com.stalmate.user.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemSuggestedFriendBinding
import com.stalmate.user.model.ModelFriend
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_ADD_FRIEND
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_CANCEL_FRIEND_REQUEST
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_REMOVE_FROM_SUGGESTIONS
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel


class SuggestedFriendAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk,
) :
    RecyclerView.Adapter<SuggestedFriendAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SuggestedFriendAdapter.FeedViewHolder {

        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggested_friend, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemSuggestedFriendBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: SuggestedFriendAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemSuggestedFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {




            binding.root.setOnClickListener {
                callback.onClickOnProfile(friend)
            }

            ImageLoaderHelperGlide.setGlideCorner(
                context,
                binding.ivUserImage,
                friend.url + "/" + friend.img
            )
            binding.tvUserName.text = friend.first_name

            Log.d("asdasdasd", friend.url + "/" + friend.img)

            binding.buttonAddFriend.setOnClickListener {
                updateFriendStatus(
                    TYPE_USER_ACTION_ADD_FRIEND,
                    friend.id,
                    (binding.root.context as? LifecycleOwner)!!,
                    bindingAdapterPosition
                )
            }


            binding.buttonRemove.setOnClickListener {


                updateFriendStatus(
                    TYPE_USER_ACTION_REMOVE_FROM_SUGGESTIONS,
                    friend.id,
                    (binding.root.context as? LifecycleOwner)!!,
                    bindingAdapterPosition
                )





                friend.isFriendRemovedFromSuggestion = 1
                notifyItemChanged(bindingAdapterPosition)
            }
            binding.buttonAfterAction.setOnClickListener {
                if (friend.isFriendRemovedFromSuggestion != 1) {
                    updateFriendStatus(
                        Constants.TYPE_USER_ACTION_CANCEL_FRIEND_REQUEST,
                        friend.id,
                        (binding.root.context as? LifecycleOwner)!!,
                        bindingAdapterPosition
                    )
                }
            }


            if (friend.isFriend == 1 || friend.isFriendRemovedFromSuggestion == 1) {
                binding.buttonAddFriend.visibility = View.GONE
                binding.buttonRemove.visibility = View.GONE
                binding.buttonAfterAction.visibility = View.VISIBLE
            } else {
                binding.buttonAddFriend.visibility = View.VISIBLE
                binding.buttonRemove.visibility = View.VISIBLE
                binding.buttonAfterAction.visibility = View.GONE
            }




            if (friend.isFriendRemovedFromSuggestion == 1) {
                binding.tvAfterbuttonText.text = "Removed"
            }
            if (friend.isFriend == 1) {
                binding.tvAfterbuttonText.text = "Friend Request Sent"
            }


        }
    }

    fun submitList(feedList: List<User>) {
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnUpdateFriendRequest(friend: User, status: String)
        fun onClickOnProfile(friend: User)
    }

    fun updateFriendStatus(
        status: String,
        userId: String,
        lifecycleOwner: LifecycleOwner,
        bindingAdapterPosition: Int
    ) {
        var hashMap = HashMap<String, String>()
        hashMap.put("id_user", userId)
        hashMap["type"] = status
        if (status.equals(TYPE_USER_ACTION_ADD_FRIEND) || status.equals(
                TYPE_USER_ACTION_CANCEL_FRIEND_REQUEST
            )
        ) {
            viewModel.sendFriendRequest("", hashMap)
            viewModel.sendFriendRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {
                    if (list[bindingAdapterPosition].isFriend == 0) {
                        list[bindingAdapterPosition].isFriend = 1
                    } else {
                        list[bindingAdapterPosition].isFriend = 0
                    }
                    notifyItemChanged(bindingAdapterPosition)

                }
            })
        } else if (status.equals(TYPE_USER_ACTION_REMOVE_FROM_SUGGESTIONS)) {

            viewModel.removeUserFromSuggestion(hashMap)
            viewModel.removeUserFromSuggestionLiveData.observe(lifecycleOwner, Observer {
                it.let {

                    list.removeAt(bindingAdapterPosition)
                    notifyItemRemoved(bindingAdapterPosition)

                }
            })
        }
    }


}