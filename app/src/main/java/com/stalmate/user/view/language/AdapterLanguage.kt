package com.stalmate.user.view.language

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemLanguageLayoutBinding
import com.stalmate.user.model.Feed
import com.stalmate.user.model.Result
import com.stalmate.user.viewmodel.AppViewModel

class AdapterLanguage(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk

) : RecyclerView.Adapter<AdapterLanguage.LanguageViewHolder>() {

    var list = ArrayList<Result>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterLanguage.LanguageViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_language_layout, parent, false)
        return LanguageViewHolder(DataBindingUtil.bind<ItemLanguageLayoutBinding>(view)!!)

    }

    override fun onBindViewHolder(holder: AdapterLanguage.LanguageViewHolder, position: Int) {
        holder.bind(list.get(position))



    }

    override fun getItemCount(): Int {
        return list.size
    }

    public interface Callbackk {
        fun onClickLanguageItem(postId: String)
    }

    fun submitList(languageList: List<Result>) {
        list.clear()
        list.addAll(languageList)
        notifyDataSetChanged()
    }

    inner class LanguageViewHolder(var binding: ItemLanguageLayoutBinding): RecyclerView.ViewHolder(binding.root){

        @SuppressLint("ResourceAsColor", "ResourceType")
        fun bind(feed: Result) {

            binding.item.text = feed.name

            binding.itemLayout.setOnClickListener {
               callback.onClickLanguageItem(list[position].id)

            }
        }
    }
}