package com.stalmate.user.view.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemCallsUserBinding
import com.stalmate.user.databinding.ItemContactsBinding
import com.stalmate.user.view.dashboard.Chat.model.CallsModel
import com.stalmate.user.view.profile.staticmodel.ModelContacts

class ContactsAdapter(private var Calls: ArrayList<ModelContacts>, private var context: Context) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Calls[position])
    }

    override fun getItemCount(): Int {
        return Calls.size
    }

    //This two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private var viewBinding: ItemContactsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: ModelContacts) {
            viewBinding.apply {
                viewBinding.userName.text = item.userName
                viewBinding.mutalFriends.text = item.mutualFriends
                when (item.status) {
                    "0" -> {
                        viewBinding.addFriend.visibility = View.VISIBLE
                        viewBinding.invite.visibility = View.GONE
                    }
                    "1" -> {
                        viewBinding.addFriend.visibility = View.GONE
                        viewBinding.invite.visibility = View.VISIBLE
                    }
                }
                Glide.with(context)
                    .load(R.drawable.placeholder_filter)
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.app_logo)
                    .into(viewBinding.userImage)

            }
        }
    }
}