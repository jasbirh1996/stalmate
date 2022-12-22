package com.stalmate.user.view.dashboard.funtime

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.databinding.ItemCommentBinding
import com.stalmate.user.model.Comment

class CommentAdapter(private val commentListener: CommentClickListener,var context:Context,var callBack: Callback)
    : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(ShortCommentDiffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

        private fun initBinding(binding: ItemCommentBinding, shortComment: Comment,toShowhead: Boolean) {

            binding.tvDate.text = "${shortComment.date}"
            binding.tvUserName.text="${shortComment.first_name} ${shortComment.last_name}"
            binding.tvReply.text="Reply"
            binding.tvLikesCount.text="0 Likes"
            binding.tvComment.text=shortComment.comment
            Glide.with(context).load(shortComment.profile_img).circleCrop().into(binding.ivUserImage)


            binding.tvReply.setOnClickListener {

                callBack.onClickOnReply(shortComment,bindingAdapterPosition)
            }

            binding.btnMore.setOnClickListener {
                shortComment.isExpanded=true
                callBack.onClickOnViewMoreReply(shortComment,bindingAdapterPosition)
            }

     /*       if(shortComment.replies.isNotEmpty()  && shortComment.level==1){
                createNestedComment(binding, shortComment,bindingAdapterPosition)
            }*/


            if (!toShowhead){
                binding.btnMore.visibility= View.GONE
                binding.view15.visibility=View.GONE
            }else{
                binding.btnMore.visibility= View.VISIBLE
                binding.btnMore.text = "Show ${shortComment.child_count} replies"
                binding.view15.visibility=View.VISIBLE
            }




        }

        fun bind(shortComment: Comment) {
            initBinding(binding, shortComment, true)
        }












        private fun createNestedComment(
            binding: ItemCommentBinding,
            shortComment: Comment,
            parentPosition: Int
        ) {

            binding.btnMore.isVisible = true

/*
            if (shortComment.isExpanded){
                binding.btnMore.text = "Hide replies"
                Log.d("aklsjdasd","expanded")
                binding.llReplies.removeAllViews()
                shortComment.replies.forEach { nestedComment ->
                    val newComment = ItemCommentBinding.inflate(LayoutInflater.from(binding.root.context), null, false)
                    initBinding(newComment, nestedComment, false)
                    binding.llReplies.addView(newComment.root)
                }
                binding.llReplies.isVisible = true



            }else{
                binding.btnMore.text = "Show ${shortComment.replies.size} replies"
                Log.d("aklsjdasd","collapsed")
                binding.llReplies.removeAllViews()
                binding.llReplies.isVisible = false
            }
*/


            binding.btnMore.setOnClickListener {
               shortComment.isExpanded=!shortComment.isExpanded
                notifyItemChanged(bindingAdapterPosition)
            }

        }









    }

    object ShortCommentDiffUtil: DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }

    open class CommentClickListener(
        private val sendComment: (author: String, message: String, level: Int, parentId: String?,position:Int) -> Unit
    ) {
        fun onSendComment(author: String, message: String, level: Int, parentId: String? = null,position:Int) =
            sendComment(author, message, level, parentId,position)
    }

    public interface Callback{
        fun onClickOnReply(shortComment:Comment,position:Int)
        fun onClickOnViewMoreReply(shortComment:Comment,position:Int)
    }

}