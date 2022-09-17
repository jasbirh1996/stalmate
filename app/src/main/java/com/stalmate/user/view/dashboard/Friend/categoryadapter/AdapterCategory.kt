package com.stalmate.user.view.dashboard.Friend.categoryadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemCategoryLayoutBinding
import com.stalmate.user.view.dashboard.Friend.categorymodel.CategoryResponse
import com.stalmate.user.viewmodel.AppViewModel
import java.util.HashMap

class AdapterCategory(val viewModel: AppViewModel,
                      val context: Context,
                      var callback: Callbackk
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    var list = ArrayList<CategoryResponse>()

   inner class CategoryViewHolder(val binding : ItemCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root){

       @SuppressLint("ResourceAsColor", "ResourceType")
       fun bind(categoryResponse: CategoryResponse) {

        binding.tvCategoryName.text = categoryResponse.name

           binding.iveditCategory.setOnClickListener {
               callback.onClickEditItem(list[bindingAdapterPosition], bindingAdapterPosition)
           }

           binding.ivDelete.setOnClickListener {

               onClickItemDelete(categoryResponse.id, bindingAdapterPosition, (binding.root.context as? LifecycleOwner)!!)
           }

       }
    }

    fun submitList(languageList: List<CategoryResponse>) {
        list.clear()
        list.addAll(languageList)
        notifyDataSetChanged()
    }

    public interface Callbackk {
        fun onClickEditItem(categoryResponse: CategoryResponse, index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_layout, parent, false)
        return CategoryViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun onClickItemDelete(id: String, position: Int, lifecycleObserver: LifecycleOwner) {
        val hashMap = HashMap<String, String>()

        hashMap["id"] = id
        hashMap["is_delete"] = "1"

        viewModel.updateFriendCategoryData(hashMap)
        viewModel.updateFriendCategoryLiveData.observe(lifecycleObserver){
            it?.let {
                if (it.status == true){
                    list.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }


}