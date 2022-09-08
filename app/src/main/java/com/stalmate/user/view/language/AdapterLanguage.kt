package com.stalmate.user.view.language

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemLanguageLayoutBinding
import com.stalmate.user.model.Result
import com.stalmate.user.viewmodel.AppViewModel

class AdapterLanguage(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk

) : RecyclerView.Adapter<AdapterLanguage.LanguageViewHolder>() {
    var row_index = 0
    var list = ArrayList<Result>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterLanguage.LanguageViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_language_layout, parent, false)
        return LanguageViewHolder(DataBindingUtil.bind(view)!!)

    }

    override fun onBindViewHolder(holder: AdapterLanguage.LanguageViewHolder, position: Int) {

        holder.bind(list.get(position))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    public interface Callbackk {
        fun onClickLanguageItem(postId: String, lang : String)
    }

    fun submitList(languageList: List<Result>) {
        list.clear()
        list.addAll(languageList)
        notifyDataSetChanged()
    }

    inner class LanguageViewHolder(var binding: ItemLanguageLayoutBinding): RecyclerView.ViewHolder(binding.root){

        @SuppressLint("ResourceAsColor", "ResourceType")
        fun bind(languageResponse: Result) {

            binding.item.text = languageResponse.name

            binding.itemLayout.setOnClickListener {

                row_index = position
                notifyDataSetChanged()

               callback.onClickLanguageItem(list[position].id, list[position].name)

            }


            if (row_index == position) {
                binding.item.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.language_select_background_blue
                    )
                )
                binding.item.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.item.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.language_select_background
                    )
                )
                binding.item.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

        }
    }
}