package com.stalmate.user.view.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemSuggestedFriendBinding
import com.stalmate.user.model.User
import com.stalmate.user.viewmodel.AppViewModel


class SuggestedFriendAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk,
) :
    RecyclerView.Adapter<SuggestedFriendAdapter.FeedViewHolder>(){
    var list = ArrayList<User>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SuggestedFriendAdapter.FeedViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_suggested_friend, parent, false)
        return FeedViewHolder(DataBindingUtil.bind<ItemSuggestedFriendBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: SuggestedFriendAdapter.FeedViewHolder, position: Int) {




        //  holder.bind(list.get(position))
    }
    override fun getItemCount(): Int {
        return 3
    }
    inner class FeedViewHolder(var binding: ItemSuggestedFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: User) {

      /*      binding.buttonFollow.setOnClickListener {
                //   callback.onClickOnUpdateFriendRequest(friend,"Accept")
                updateFriendStatus("add_friend",friend.id, (binding.root.context as? LifecycleOwner)!!)
            }
            binding.root.setOnClickListener {
                callback.onClickOnProfile(friend)
            }


            ImageLoaderHelperGlide.setGlideCorner(context,binding.ivUserImage,friend.url+"/"+friend.img)
            binding.tvUserName.text=friend.first_name
*/

            binding.buttonAddFriend.setOnClickListener {
                updateFriendStatus("add_friend",friend.id, (binding.root.context as? LifecycleOwner)!!)
            }


        }
    }

    fun submitList(feedList: List<User>) {
    /*    list.clear()
        list.addAll(feedList)
        notifyDataSetChanged()*/
    }

    public interface Callbackk {
        fun onClickOnUpdateFriendRequest(friend:User,status: String)
        fun onClickOnProfile(friend:User)
    }



    fun updateFriendStatus(status:String,userId:String,lifecycleOwner: LifecycleOwner) {
        var hashMap = HashMap<String, String>()
        hashMap.put("id_user", userId)

        if (status.equals("add_friend")){
            viewModel.sendFriendRequest("", hashMap)
            viewModel.sendFriendRequestLiveData.observe( lifecycleOwner , Observer {
                it.let {


                }
            })
        }
        /*if (status.equals("follow")){

            viewModel.sendFollowRequest("", hashMap)
            viewModel.followRequestLiveData.observe(lifecycleOwner, Observer {
                it.let {

                }
            })
        }*/
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