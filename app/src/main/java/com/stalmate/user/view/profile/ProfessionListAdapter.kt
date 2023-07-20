package com.stalmate.user.view.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemProfessionProfileBinding
import com.stalmate.user.model.*
import com.stalmate.user.viewmodel.AppViewModel
import java.util.Collections.list
import kotlin.collections.ArrayList

class ProfessionListAdapter(
    val viewModel: AppViewModel,
    val context: Context,
    var callback: Callbackk
    ) : RecyclerView.Adapter<ProfessionListAdapter.AlbumViewHolder>() {

    var list = ArrayList<Profession>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfessionListAdapter.AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profession_profile, parent, false)
        return AlbumViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(albumList: List<Profession>) {
        list.clear()
        list.addAll(albumList)
    }

    fun addToList(feedList:Profession) {
        val size = list.size
        list.add(feedList)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }


    public interface Callbackk {
        fun onClickItemProfessionEdit(position: Profession, index: Int)
    }

    inner class AlbumViewHolder(var binding: ItemProfessionProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profession: Profession) {


            binding.tvComapny.text = profession.company_name
            binding.tvgesignation.text = profession.designation
            binding.tvFrom.text = profession.from

            if (profession.currently_working_here=="Yes"){
                binding.tvTo.text = "current working here"
            }else {
                binding.tvTo.text = profession.to
            }

            binding.ivDelete.setOnClickListener {
                deleteProfessoin(
                    profession._id,
                    bindingAdapterPosition,
                    (binding.root.context as? LifecycleOwner)!!
                )
            }

            binding.ivedit.setOnClickListener {
                callback.onClickItemProfessionEdit(list[bindingAdapterPosition], bindingAdapterPosition)
            }
        }
    }

    fun deleteProfessoin(id: String, position: Int, lifecycleObserver: LifecycleOwner) {
        val hashMap = HashMap<String, String>()
        hashMap["id"] = id
        hashMap["is_delete"] = "1"

        viewModel.addUpdateProfessionData((context as ActivityProfileEdit)?.prefManager?.access_token.toString(),hashMap)
        viewModel.addUpdateProfessionLiveData.observe(lifecycleObserver) {
            it?.let {
                if (it.status) {
                    list.removeAt(position)
                    notifyItemRemoved(position)

                }
            }
        }
    }
}