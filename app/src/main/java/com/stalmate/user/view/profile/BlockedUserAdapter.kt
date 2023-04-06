package com.stalmate.user.view.profile


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ItemBlockedUserBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.settings.ActivityBlockContacts
import com.stalmate.user.viewmodel.AppViewModel

class BlockedUserAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    val callback: Callback,
    val accessToken: String
) : RecyclerView.Adapter<BlockedUserAdapter.AlbumViewHolder>() {

    var list = ArrayList<User>()


    inner class AlbumViewHolder(var binding: ItemBlockedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(response: User) {
            binding.tvUserName.text = response.first_name + " " + response.last_name
            Glide.with(context).load(response.img).circleCrop()
                .placeholder(R.drawable.user_placeholder).into(binding.ivUserImage)
            // ImageLoaderHelperGlide.setGlideCorner(context,binding.ivUserImage,response.img,R.drawable.user_placeholder)
            binding.imageView7.setOnClickListener {
                hitBlockApi(
                    bindingAdapterPosition,
                    response.id,
                    (binding.root.context as? LifecycleOwner)!!
                )
            }
        }
    }

    interface Callback {
        fun onListEmpty()
        fun onItemRemove()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hitBlockApi(position: Int, id: String, owner: LifecycleOwner) {
        /*val hashMap = HashMap<String, String>()
        hashMap["id_user"] = id
        viewModel.block(hashMap)*/

        viewModel.block(
            access_token = accessToken,
            _id = id
        )
        viewModel.blockData.observe(owner) {
            it.let {
                list.removeAt(position)
                notifyItemRemoved(position)
                /*   notifyDataSetChanged()*/
                callback.onItemRemove()
                if (list.isEmpty()) {
                    callback.onListEmpty()
                }
            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(albumList: List<User>) {
        list.clear()
        list.addAll(albumList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_blocked_user, parent, false)
        return AlbumViewHolder(DataBindingUtil.bind<ItemBlockedUserBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //This two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}