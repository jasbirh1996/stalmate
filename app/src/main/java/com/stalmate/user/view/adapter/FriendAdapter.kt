package com.stalmate.user.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.model.User
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.Constants.TYPE_ALL_FOLLOWERS_FOLLOWING
import com.stalmate.user.utilities.Constants.TYPE_MY_FRIENDS
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_ADD_FRIEND
import com.stalmate.user.utilities.Constants.TYPE_USER_ACTION_FOLLOW
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.viewmodel.AppViewModel

class FriendAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk,
    var type: String,
    var subtype: String
) :
    RecyclerView.Adapter<FriendAdapter.FeedViewHolder>() {
    var list = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FriendAdapter.FeedViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_big, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemFriendBigBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: FriendAdapter.FeedViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class FeedViewHolder(var binding: ItemFriendBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {
            setupViewsForAdapter(binding,friend,bindingAdapterPosition)
        }
    }

    fun submitList(feedList: List<User>) {
        list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()
    }


    fun addToList(feedList: List<User>) {
        val size = list.size
        list.addAll(feedList)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    public interface Callbackk {
        fun onClickOnUpdateFriendRequest(friend: User, status: String)
        fun onClickOnProfile(friend: User)
    }


    fun updateFriendStatus(
        status: String,
        userId: String,
        lifecycleOwner: LifecycleOwner,
        binding: ItemFriendBigBinding,
        position: Int
    ) {
        var hashMap = HashMap<String, String>()
        hashMap.put("id_user", userId)
        if (status.equals(Constants.TYPE_USER_ACTION_ADD_FRIEND)) {
            viewModel.sendFriendRequest("", hashMap)
            viewModel.sendFriendRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {
                    if (list[position].isFriend==0){
                        if  (!ValidationHelper.isNull(list[position].request_status)){
                            if (list[position].request_status == Constants.FRIEND_CONNECTION_STATUS_PENDING) {
                                list[position].request_status=""
                            }
                        }else{
                            list[position].request_status=Constants.FRIEND_CONNECTION_STATUS_PENDING
                        }
                    }else{
                        list[position].isFriend=1
                    }

                    notifyItemChanged(position)
                }
            })
        }

        if (status.equals(TYPE_USER_ACTION_FOLLOW)) {
            viewModel.sendFollowRequest("", hashMap)
            viewModel.followRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {

                    if (list[position].isFollowed == 1) {
                        list[position].isFollowed = 0
                    } else {
                        list[position].isFollowed = 1
                    }
                    notifyItemChanged(position)

                }
            })
        }

        if (status.equals(Constants.TYPE_USER_ACTION_ACCEPT_FRIEND_REQUEST)) {
            hashMap["type"] = "Accept"
            viewModel.updateFriendRequest(hashMap)
            viewModel.updateFriendRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {
                    binding.buttonAccept.text = "Accepted"
                    binding.ivDelete.visibility=View.GONE
                }
            })
        }
        if (status.equals(Constants.TYPE_USER_ACTION_DELETE_FRIEND_REQUEST)) {
            hashMap["type"] = "Delete"
            viewModel.updateFriendRequest(hashMap)
            viewModel.updateFriendRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {
                    binding.buttonAccept.text = "Accepted"
                }
            })
        }
    }


    fun setupViewsForAdapter(
        binding: ItemFriendBigBinding,
        friend: User,
        bindingAdapterPosition: Int
    ) {

        Log.d("asdasdauiou",type+subtype)
        if (type.equals(Constants.TYPE_FRIEND_REQUEST)) {
            binding.layoutButtons.visibility = View.GONE
            binding.buttonAccept.visibility  = View.VISIBLE
            binding.layoutFriendRequestExtra.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE
            setupButtonColor("Confirm", true, binding.buttonAccept)
            binding.buttonAccept.visibility=View.VISIBLE
            binding.buttonOuterFollow.visibility=View.GONE

            binding.tvMutualFirnds.text="Mutual Friends : 7"
            binding.tvMessge.text="Hi How are you? I am \n Stanley from yoga class"



        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS)) {
            binding.layoutButtons.visibility = View.VISIBLE
            binding.buttonAccept.visibility = View.GONE
            binding.layoutFriendRequestExtra.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
            setupButtonColor("Follow", false, binding.buttonFollow)
            setupButtonColor("Send Friend Request", true, binding.buttonFriend)
           binding.buttonFriend.visibility=View.VISIBLE
            binding.buttonAccept.visibility=View.GONE
            binding.buttonOuterFollow.visibility=View.GONE
        } else if (type.equals(Constants.TYPE_FRIEND_SUGGESTIONS_FOLLOWERS)) {
            binding.layoutButtons.visibility = View.VISIBLE
            binding.buttonAccept.visibility = View.GONE
            binding.layoutFriendRequestExtra.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
//            binding.buttonFriend.visibility=View.GONE
            binding.buttonFollow.visibility = View.GONE


        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWING)) {
            binding.layoutButtons.visibility = View.VISIBLE
            binding.buttonAccept.visibility = View.GONE
            binding.layoutFriendRequestExtra.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
            setupButtonColor("Following", false, binding.buttonFollow)
            setupButtonColor("Friends", true, binding.buttonFriend)
            binding.buttonFriend.visibility=View.VISIBLE

            binding.buttonOuterFollow.visibility=View.GONE

        } else if (type.equals(Constants.TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_FRIEND_FOLLOWER)) {
            binding.layoutButtons.visibility = View.VISIBLE
            binding.buttonAccept.visibility = View.GONE
            binding.layoutFriendRequestExtra.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
            setupButtonColor("Friends", true, binding.buttonFriend)
            setupButtonColor("Following", false, binding.buttonFollow)
            binding.buttonFriend.visibility=View.VISIBLE
            Log.d("asdasdasdfsdf",type+subtype)

            binding.buttonOuterFollow.visibility=View.GONE
        } else if (type.equals(TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWERS)) {
            binding.layoutButtons.visibility = View.GONE
            binding.buttonAccept.visibility = View.GONE
            binding.layoutFriendRequestExtra.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
           // setupButtonColor("Following", false, binding.buttonFollow)
            binding.buttonFriend.visibility=View.VISIBLE
            Log.d("asdasda",type+subtype)
            binding.buttonOuterFollow.visibility=View.VISIBLE

        } else if (type.equals(TYPE_ALL_FOLLOWERS_FOLLOWING) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWINGS)) {
            binding.layoutButtons.visibility = View.GONE
            binding.buttonAccept.visibility = View.GONE
            binding.layoutFriendRequestExtra.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
            binding.buttonOuterFollow.visibility=View.VISIBLE
            Log.d("asdasdafgh",type+subtype)
         //   setupButtonColor("Following", false, binding.buttonFollow)
//            binding.buttonFriend.visibility=View.GONE
        }







        if (friend.isFollowed == 1) {
            binding.buttonFollow.text = "Following"
        } else {
            binding.buttonFollow.text = "Follow"
        }

        if (friend.isFollowed == 1) {
            binding.buttonOuterFollow.text = "Following"
        } else {
            binding.buttonOuterFollow.text = "Follow"
        }


        if (friend.request_status.equals(Constants.FRIEND_CONNECTION_STATUS_PENDING)) {
            binding.buttonFriend.text="Friend Request Sent"
        } else {
            binding.buttonFriend.text="Send Friend Request"
        }



        if (type.equals(TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWERS)) {
            if (friend.isFriend == 1) {
                binding.buttonFriend.text = "Unfriend"
            } else {
                binding.buttonFriend.text = "Friend"
            }

        }


        if (type.equals(TYPE_MY_FRIENDS) && subtype.equals(Constants.TYPE_USER_TYPE_FOLLOWINGS)) {
            if (friend.isFriend == 1) {
                binding.buttonFriend.text = "Unfriend"
            } else {
                binding.buttonFriend.text = "Friend"
            }
        }






        binding.buttonFriend.setOnClickListener {
            updateFriendStatus(
                TYPE_USER_ACTION_ADD_FRIEND,
                friend.id,
                (binding.root.context as? LifecycleOwner)!!,
                binding,
                bindingAdapterPosition
            )
        }


        binding.buttonFollow.setOnClickListener {
            updateFriendStatus(
                TYPE_USER_ACTION_FOLLOW,
                friend.id,
                (binding.root.context as? LifecycleOwner)!!,
                binding,
                bindingAdapterPosition
            )
        }

        binding.root.setOnClickListener {
            callback.onClickOnProfile(friend)
        }
        binding.buttonAccept.setOnClickListener {
            //  callback.onClickOnProfile(friend)
            updateFriendStatus(
                Constants.TYPE_USER_ACTION_ACCEPT_FRIEND_REQUEST,
                friend.id,
                (binding.root.context as? LifecycleOwner)!!,
                binding,
                bindingAdapterPosition
            )
        }


        binding.buttonOuterFollow.setOnClickListener {
            //  callback.onClickOnProfile(friend)
            updateFriendStatus(
                Constants.TYPE_USER_ACTION_FOLLOW,
                friend.id,
                (binding.root.context as? LifecycleOwner)!!,
                binding,
                bindingAdapterPosition
            )
        }
        
        binding.ivDelete.setOnClickListener {
            updateFriendStatus(
                Constants.TYPE_USER_ACTION_DELETE_FRIEND_REQUEST,
                friend.id,
                (binding.root.context as? LifecycleOwner)!!,
                binding,
                bindingAdapterPosition
            )
            //  callback.onClickOnProfile(friend)
        }
        ImageLoaderHelperGlide.setGlideCorner(
            context,
            binding.ivUserImage,
             friend.img,
            R.drawable.user_placeholder
        )

        binding.tvUserName.text = friend.first_name
        if (friend.profile_data[0].profession.isNotEmpty()){
            binding.tvLineOne.text = friend.profile_data[0].profession[0].designation
            binding.tvLineOne.visibility = View.VISIBLE
        }else  if (friend.profile_data[0].education.isNotEmpty()){
            binding.tvLineOne.text = friend.profile_data[0].education[0].sehool
            binding.tvLineOne.visibility = View.VISIBLE
        }

        if (friend.mutual_friend.isNotEmpty()){
            binding.tvMutualFirnds.text = friend.mutual_friend
            binding.tvMutualFirnds.visibility = View.VISIBLE
        }

        if (!ValidationHelper.isNull(friend.profile_data[0].home_town)){
            binding.tvLineTwo.text = friend.profile_data[0].home_town
            binding.tvLineTwo.visibility = View.VISIBLE

        }


    }


    fun setupButtonColor(text: String, isPrimary: Boolean, view: Button): Button {
        view.setText(text)
        if (isPrimary) {
            view.background =
                ContextCompat.getDrawable(context, R.drawable.primary_button_background)
            view.setTextColor(context.getColor(R.color.white))
            return view
        }
        view.background = ContextCompat.getDrawable(
            context,
            R.drawable.large_round_corner_light_primary_border_light_gray_filled
        )
        view.setTextColor(context.getColor(R.color.colorPrimary))
        return view
    }



}