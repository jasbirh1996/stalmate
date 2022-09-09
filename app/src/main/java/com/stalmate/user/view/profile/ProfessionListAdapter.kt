package com.stalmate.user.view.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemEducationprofileBinding
import com.stalmate.user.databinding.ItemGalleryBinding
import com.stalmate.user.model.*
import com.stalmate.user.view.language.AdapterLanguage
import com.stalmate.user.viewmodel.AppViewModel
import java.util.*
import kotlin.collections.ArrayList

class ProfessionListAdapter(val viewModel: AppViewModel, val context: Context, var callback: Callbackk) : RecyclerView.Adapter<ProfessionListAdapter.AlbumViewHolder>() {

    var list = ArrayList<Profession>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionListAdapter.AlbumViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_educationprofile, parent, false)
        return AlbumViewHolder(DataBindingUtil.bind<ItemEducationprofileBinding>(view)!!)

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
        notifyDataSetChanged()
    }


    public interface Callbackk {
        fun onClickItemProfessionDelete(position: Int)
        fun onClickItemProfessionEdit(position: Int)
    }

    inner class AlbumViewHolder(var binding: ItemEducationprofileBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(response : Profession){


          /*  binding.tveducation.text =response.sehool
            binding.tvcource.text = response.branch
            binding.tvcourcetype.text = response.course
*/
            binding.ivDelete.setOnClickListener {
             callback.onClickItemProfessionDelete(list[position].user_id.toInt())
            }

            binding.ivedit.setOnClickListener {
                callback.onClickItemProfessionEdit(list[position].user_id.toInt())
            }

        }
    }
}