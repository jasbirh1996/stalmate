package com.stalmate.user.view.dashboard.Friend.categoryadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.slatmate.user.model.Results
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemCategoryLayoutBinding
import com.stalmate.user.view.dashboard.Friend.categorymodel.CategoryResponse
import com.stalmate.user.viewmodel.AppViewModel

class AdapterCategory(val viewModel: AppViewModel,
                      val context: Context,
                      var callback: Callbackk
) : RecyclerView.Adapter<AdapterCategory.ViewHolder>() {

    var list = ArrayList<CategoryResponse>()

   inner class ViewHolder(val binding : ItemCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root){

       @SuppressLint("ResourceAsColor", "ResourceType")
       fun bind(categoryResponse: CategoryResponse) {

        binding.tvCategoryName.text = categoryResponse.name

           binding.iveditCategory.setOnClickListener {

           }

           binding.ivDelete.setOnClickListener {

           }

       }
    }

    fun submitList(languageList: List<CategoryResponse>) {
        list.clear()
        list.addAll(languageList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickItem(id: String, name : String)
        fun onClickDeleteItem(id: String, name : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_layout, parent, false)
        return ViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
       return list.size
    }
}