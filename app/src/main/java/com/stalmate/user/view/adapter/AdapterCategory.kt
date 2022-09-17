package com.stalmate.user.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemLanguageLayoutBinding
import com.stalmate.user.model.Category
import com.stalmate.user.viewmodel.AppViewModel

class AdapterCategory(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk

) : RecyclerView.Adapter<AdapterCategory.LanguageViewHolder>() {
    var row_index = 0
    var list = ArrayList<Category>()

    private var checkedPosition = 0

    fun submitList(categoryList: List<Category>) {
        list.clear()
        list.addAll(categoryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCategory.LanguageViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_language_layout, parent, false)
        return LanguageViewHolder(DataBindingUtil.bind(view)!!)

    }

    override fun onBindViewHolder(holder: AdapterCategory.LanguageViewHolder, position: Int) {

        holder.bind(list.get(position))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface Callbackk {
        fun onClickIntrastedItem(data : ArrayList<Category>)
    }


    inner class LanguageViewHolder(var binding: ItemLanguageLayoutBinding): RecyclerView.ViewHolder(binding.root){

        @SuppressLint("ResourceAsColor", "ResourceType")
        fun bind(categoryResponse: Category) {

            if (list.get(position).isSelected){
                //list.get(position).isSelected = true
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
               // list.get(position).isSelected = false
            }
            binding.item.text = categoryResponse.name

            binding.itemLayout.setOnClickListener {

                if(list.get(position).isSelected){
                    list.get(position).isSelected=false
                }else{
                    list.get(position).isSelected=true


                }

                if (checkedPosition != getAdapterPosition()) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                }

                row_index = position

                callback.onClickIntrastedItem(list)

                notifyItemChanged(absoluteAdapterPosition)

            }

        }
    }

    fun getSelected(): Category? {
        return if (checkedPosition != -1) {
            list.get(checkedPosition)
        } else null
    }

    /*fun  getAllSelectedItem(position: String, name : String){

        for (i in 0 until list.size) {

            if (list[i].isSelected) {


                selectedItemList.add(position, name)


            }
        }

    }*/

}

