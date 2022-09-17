package com.stalmate.user.view.singlesearch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemSearchBinding
import com.stalmate.user.model.Result
import com.stalmate.user.viewmodel.AppViewModel

class SearchAdapter(val viewModel: AppViewModel,
                    val context: Context,
                    var callback: Callbackk

) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var list = ArrayList<ResultSearch>()

    fun submitList(searchList: List<ResultSearch>) {
        list.clear()
        list.addAll(searchList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding : ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(searchResponse :ResultSearch){

            binding.tvSearchName.text = searchResponse.name

            binding.tvSearchName.setOnClickListener {
                callback.onClickSearchItem(searchResponse.id, searchResponse.name)
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return ViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    public interface Callbackk {
        fun onClickSearchItem(id: String, name: String)
    }

}
