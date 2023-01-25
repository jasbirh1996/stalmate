package com.stalmate.user.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemCallsUserBinding
import com.stalmate.user.view.dashboard.Chat.model.CallsModel

class CallsAdapter(private var Calls: ArrayList<CallsModel>, private var context: Context) :
    RecyclerView.Adapter<CallsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCallsUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private var viewBinding: ItemCallsUserBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: CallsModel) {
            viewBinding.apply {
                viewBinding.userName.text = item.userName
                viewBinding.tvCallCount.text = item.callCount
                viewBinding.tvLastMsgTiming.text = item.dateTime
                when (item.inOutCall) {
                    "0" -> {
                        viewBinding.imSeenMsgUnSeen.visibility = View.VISIBLE
                        viewBinding.callReceive.visibility = View.GONE
                    }
                    "1" -> {
                        viewBinding.imSeenMsgUnSeen.visibility = View.GONE
                        viewBinding.callReceive.visibility = View.VISIBLE
                    }
                }
                when (item.callVideo) {
                    "0" -> {
                        viewBinding.iconCall.visibility = View.VISIBLE
                        viewBinding.iconVideoCall.visibility = View.GONE
                    }
                    "1" -> {
                        viewBinding.iconCall.visibility = View.GONE
                        viewBinding.iconVideoCall.visibility = View.VISIBLE
                    }
                }
                /*Picasso.get()
                    .load(R.drawable.app_logo)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(viewBinding.userImage)*/

                Glide.with(context)
                    .load(R.drawable.placeholder_filter)
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.app_logo)
                    .into(viewBinding.userImage)

            }
        }
    }
}