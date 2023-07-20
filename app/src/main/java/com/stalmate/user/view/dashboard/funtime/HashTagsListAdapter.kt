package com.stalmate.user.view.dashboard.funtime

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.databinding.ItemHashTagBinding
import com.stalmate.user.model.HashTagsListResponse

class HashTagsListAdapter(
    val hashTagsListResponse: ArrayList<HashTagsListResponse.Result?>,
    val onCLick: (hashTag: String) -> Unit
) :
    RecyclerView.Adapter<HashTagsListAdapter.HashTagsListViewHolder>() {

    class HashTagsListViewHolder(val view: ItemHashTagBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(item: HashTagsListResponse.Result) {
            view.tvHashTag.setText(Html.fromHtml(
                item.name,
                Html.FROM_HTML_MODE_COMPACT
            ))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashTagsListViewHolder {
        return HashTagsListViewHolder(
            DataBindingUtil.bind<ItemHashTagBinding>(
                LayoutInflater.from(parent.context).inflate(R.layout.item_hash_tag, null, false)
            )!!
        )
    }

    override fun getItemCount(): Int = (hashTagsListResponse.size ?: 0)

    override fun onBindViewHolder(holder: HashTagsListViewHolder, position: Int) {
        hashTagsListResponse[position]?.let { hashTag ->
            holder.bind(hashTag)
            holder.itemView.setOnClickListener {
                onCLick(hashTag.name.toString())
            }
        }
    }
}
