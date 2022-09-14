package com.stalmate.user.view.profile



import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.otaliastudios.opengl.core.use
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemBlockedUserBinding
import com.stalmate.user.databinding.ItemEducationprofileBinding
import com.stalmate.user.databinding.ItemProfileCoverBinding
import com.stalmate.user.model.CoverImg
import com.stalmate.user.model.Education
import com.stalmate.user.model.ProfileImg
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.viewmodel.AppViewModel

class BlockedUserAdapter(val viewModel: AppViewModel, val context: Context,val callback:Callback)
    : RecyclerView.Adapter<BlockedUserAdapter.AlbumViewHolder>() {

    var list = ArrayList<User>()


    inner class AlbumViewHolder(var binding : ItemBlockedUserBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(response : User){


            binding.tvUserName.text= response.first_name+" "+response.last_name
            ImageLoaderHelperGlide.setGlideCorner(context,binding.ivUserImage,response.img,R.drawable.user_placeholder)


            binding.imageView7.setOnClickListener {
                hitBlockApi(absoluteAdapterPosition,response.id,(binding.root.context as? LifecycleOwner)!!)
            }


            if (list.isEmpty()){
                callback.onListEmpty()
            }

        }


    }

    public interface Callback{
        fun onListEmpty()
    }


    private fun hitBlockApi( position: Int,id:String,owner:LifecycleOwner) {


        val hashMap = HashMap<String, String>()
        hashMap["id_user"] = id

        viewModel.block(hashMap)
        viewModel.blockData.observe( owner, Observer {

            it.let {
                if (it!!.status == true) {

                    list.removeAt(position)
                    notifyItemRemoved(position)
                    if (list.isEmpty()){
                        callback.onListEmpty()
                    }
                }
            }

        })


    }

    fun submitList(albumList: ArrayList<User>) {
        list.clear()
        list.addAll(albumList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_blocked_user, parent, false)
        return AlbumViewHolder(DataBindingUtil.bind<ItemBlockedUserBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }
}