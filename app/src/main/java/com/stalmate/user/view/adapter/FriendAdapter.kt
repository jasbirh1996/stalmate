package com.stalmate.user.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.Constants.TYPE_ALL_FOLLOWERS_FOLLOWING
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_FOLLOW
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class FriendAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk,
    var type:String,
    var subtype:String) :
    RecyclerView.Adapter<FriendAdapter.FeedViewHolder>(){
    var list = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FriendAdapter.FeedViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_big, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemFriendBigBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: FriendAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemFriendBigBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {
            if (friend.isFollowed==1){
                binding.buttonFollow.text="Unfollow"
            }else{
                binding.buttonFollow.text="Follow"
            }

            setupViewsForAdapter(binding)
            binding.buttonFollow.setOnClickListener {
                updateFriendStatus(TYPE_USER_ACTION_FOLLOW,friend.id, (binding.root.context as? LifecycleOwner)!!,binding,bindingAdapterPosition)
            }

            binding.buttonSendFriendRequest.setOnClickListener {
                updateFriendStatus(Constants.TYPE_USER_ACTION_ADD_FRIEND,friend.id, (binding.root.context as? LifecycleOwner)!!,binding,bindingAdapterPosition)
            }

            binding.root.setOnClickListener {
                callback.onClickOnProfile(friend)
            }

            binding.buttonConfirm.setOnClickListener {
              //  callback.onClickOnProfile(friend)
                updateFriendStatus(Constants.TYPE_USER_ACTION_ACCEPT_FRIEND_REQUEST,friend.id, (binding.root.context as? LifecycleOwner)!!,binding,bindingAdapterPosition)
            }
            binding.ivDelete.setOnClickListener {
                updateFriendStatus(Constants.TYPE_USER_ACTION_DELETE_FRIEND_REQUEST,friend.id, (binding.root.context as? LifecycleOwner)!!,binding,bindingAdapterPosition)
                //  callback.onClickOnProfile(friend)
            }
            ImageLoaderHelperGlide.setGlideCorner(context,binding.ivUserImage,friend.url+"/"+friend.img)
            binding.tvUserName.text=friend.first_name


        }
    }

    fun submitList(feedList: List<User>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickOnUpdateFriendRequest(friend:User,status: String)
        fun onClickOnProfile(friend:User)
    }



    fun updateFriendStatus(status:String,userId:String,lifecycleOwner: LifecycleOwner,binding:ItemFriendBigBinding,position: Int) {
        var hashMap = HashMap<String, String>()
        hashMap.put("id_user", userId)

        if (status.equals(Constants.TYPE_USER_ACTION_ADD_FRIEND)){
            viewModel.sendFriendRequest("", hashMap)
            viewModel.sendFriendRequestLiveData.observe( lifecycleOwner , Observer {
                it.let {
                    binding.buttonSendFriendRequest.text = "Request Sent"
                }
            })
        }

        if (status.equals(TYPE_USER_ACTION_FOLLOW)){
            viewModel.sendFollowRequest("", hashMap)
            viewModel.followRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {

                    if (list[position].isFollowed==1){
                        list[position].isFollowed=0
                    }else{
                        list[position].isFollowed=1
                    }
                    notifyItemChanged(position)

                }
            })
        }

        if (status.equals(Constants.TYPE_USER_ACTION_ACCEPT_FRIEND_REQUEST)){
            hashMap["type"] = "Accept"
            viewModel.updateFriendRequest(hashMap)
            viewModel.updateFriendRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {
                    binding.buttonConfirm.text = "Accepted"
                }
            })
        }
        if (status.equals(Constants.TYPE_USER_ACTION_DELETE_FRIEND_REQUEST)){
            hashMap["type"] = "Delete"
            viewModel.updateFriendRequest(hashMap)
            viewModel.updateFriendRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {
                    binding.buttonConfirm.text = "Accepted"
                }
            })
        }
    }


    fun setupViewsForAdapter(binding:ItemFriendBigBinding) {



        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {
            binding.layoutButtons.visibility= View.GONE
            binding.buttonConfirm.visibility=View.VISIBLE
            binding.layoutFriendRequestExtra.visibility=View.VISIBLE
            binding.ivDelete.visibility=View.VISIBLE
            setupButtonColor("Confirm",true,binding.buttonConfirm)

        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
            binding.layoutButtons.visibility= View.VISIBLE
            binding.buttonConfirm.visibility=View.GONE
            binding.layoutFriendRequestExtra.visibility=View.GONE
            binding.ivDelete.visibility=View.GONE
            setupButtonColor("Follow",false,binding.buttonFollow)
            setupButtonColor("Send Friend Request",true,binding.buttonSendFriendRequest)

//            binding.buttonFriend.visibility=View.GONE


        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS_FOLLOWERS)) {
            binding.layoutButtons.visibility= View.VISIBLE
            binding.buttonConfirm.visibility=View.GONE
            binding.layoutFriendRequestExtra.visibility=View.GONE
            binding.ivDelete.visibility=View.GONE
            setupButtonColor("Send Friend Request",true,binding.buttonSendFriendRequest)

//            binding.buttonFriend.visibility=View.GONE
            binding.buttonFollow.visibility=View.GONE


        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWING)) {
            binding.layoutButtons.visibility= View.VISIBLE
            binding.buttonConfirm.visibility=View.GONE
            binding.layoutFriendRequestExtra.visibility=View.GONE
            binding.ivDelete.visibility=View.GONE
            setupButtonColor("Following",false,binding.buttonFollow)
            setupButtonColor("Friends",true,binding.buttonSendFriendRequest)
//            binding.buttonFriend.visibility=View.GONE

        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWER)) {
            binding.layoutButtons.visibility= View.VISIBLE
            binding.buttonConfirm.visibility=View.GONE
            binding.layoutFriendRequestExtra.visibility=View.GONE
            binding.ivDelete.visibility=View.GONE
            setupButtonColor("Friends",true,binding.buttonSendFriendRequest)
            setupButtonColor("Following",false,binding.buttonFollow)
//            binding.buttonFriend.visibility=View.GONE
        }




        else if (type.equals(TYPE_ALL_FOLLOWERS_FOLLOWING) && type.equals(Constants.TYPE_USER_TYPE_FOLLOWERS)) {
            binding.layoutButtons.visibility= View.VISIBLE
            binding.buttonConfirm.visibility=View.GONE
            binding.layoutFriendRequestExtra.visibility=View.GONE
            binding.ivDelete.visibility=View.GONE
            setupButtonColor("Friends",true,binding.buttonSendFriendRequest)
            setupButtonColor("Following",false,binding.buttonFollow)
//            binding.buttonFriend.visibility=View.GONE
        }

        else if (type.equals(TYPE_ALL_FOLLOWERS_FOLLOWING) && type.equals(Constants.TYPE_USER_TYPE_FOLLOWINGS)) {
            binding.layoutButtons.visibility= View.VISIBLE
            binding.buttonConfirm.visibility=View.GONE
            binding.layoutFriendRequestExtra.visibility=View.GONE
            binding.ivDelete.visibility=View.GONE
            setupButtonColor("Friends",true,binding.buttonSendFriendRequest)
            setupButtonColor("Following",false,binding.buttonFollow)
//            binding.buttonFriend.visibility=View.GONE
        }

    }


    fun setupButtonColor(text:String,isPrimary: Boolean,view:Button):Button{
        view.setText(text)
        if (isPrimary){
            view.background=ContextCompat.getDrawable(context,R.drawable.primary_button_background)
            view.setTextColor(context.getColor(R.color.white))
            return  view
        }
        view.background=ContextCompat.getDrawable(context,R.drawable.large_round_corner_light_primary_border_light_gray_filled)
        view.setTextColor(context.getColor(R.color.colorPrimary))
        return  view
    }










}