package com.stalmate.user.view.dashboard.funtime

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemFriendBigBinding
import com.stalmate.user.databinding.ItemFunTimeBinding
import com.stalmate.user.databinding.ItemLanguageLayoutBinding
import com.stalmate.user.model.Result
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.viewmodel.AppViewModel
import eightbitlab.com.blurview.RenderScriptBlur

class AdapterFunTime(val viewModel: AppViewModel,
                     val context: Context,
                     ) : RecyclerView.Adapter<AdapterFunTime.ViewHolder>() {

    var list = ArrayList<ResultFuntime>()

    inner class ViewHolder(var binding : ItemFunTimeBinding): RecyclerView.ViewHolder(binding.root) {

        fun  bind(funtimeResponse : ResultFuntime){
            var radius = 20f

            var decorView: View = (context as Activity?)!!.window.getDecorView();

            // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
            var rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
            var windowBackground = decorView.background
            binding.blurViewTab.setupWith(rootView, RenderScriptBlur(context))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)



            binding.tvUserName.text = funtimeResponse.first_name+ " " + funtimeResponse.last_name

            if (funtimeResponse.file_type=="Video"){

                binding.pvExoplayer.visibility = View.VISIBLE

            }else {
                Glide.with(context).load(funtimeResponse.file).into(binding.shapeableImageView)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_fun_time, parent, false)
        return ViewHolder(DataBindingUtil.bind<ItemFunTimeBinding>(view)!!)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun submitList(funtimeList: List<ResultFuntime>) {
        list.clear()
        list.addAll(funtimeList)
        notifyDataSetChanged()
    }



}