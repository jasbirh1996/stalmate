package com.stalmate.user.view.dashboard.funtime
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemCommentBinding
import com.stalmate.user.model.Comment
import com.stalmate.user.utilities.TimesAgo2
import com.stalmate.user.viewmodel.AppViewModel
import java.util.HashMap

class ChildCommentAdapter(
    var context: Context,
    var callBack: Callback,
    var funTimeId: String,
   var networkViewModel: AppViewModel,
   var lifecyclerOwner: LifecycleOwner
) : RecyclerView.Adapter< ChildCommentAdapter.CommentViewHolder>() {
    var commentList = ArrayList<Comment>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }


    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun initBinding(binding: ItemCommentBinding, shortComment: Comment) {

            binding.tvDate.text = "${TimesAgo2.covertTimeToText(shortComment.Created_date,true)}"
            binding.tvUserName.text = "${shortComment.first_name} ${shortComment.last_name}"
            binding.tvReply.text = "Reply"
            binding.tvLikesCount.text = "0 Likes"
            binding.tvComment.text = shortComment.comment
            Glide.with(context).load(shortComment.profile_img).circleCrop()
                .into(binding.ivUserImage)

            binding.tvReply.setOnClickListener {
                callBack.onClickOnReply(shortComment, bindingAdapterPosition)
            }

            /* binding.tvReply.setOnClickListener {
                    commentListener.onSendComment(
                        "author",
                        "message",
                        shortComment.level, shortComment.parentId ?: shortComment._id,bindingAdapterPosition
                    )
                }*/

/*
            if (shortComment.child_count == 0) {
                binding.btnMore.visibility = View.GONE
                binding.view15.visibility = View.GONE
            } else {
                binding.btnMore.visibility = View.VISIBLE
                binding.btnMore.text = "Show ${shortComment.child_count} replies"
                binding.view15.visibility = View.VISIBLE
            }*/
            binding.btnMore.visibility=View.GONE
            binding.view15.visibility=View.GONE


            if (shortComment.isLiked=="Yes"){
                binding.ivHearIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.like_heart
                    )
                )
            }else{
                binding.ivHearIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_profile_heart_icon
                    )
                )
            }


            binding.ivHearIcon.setOnClickListener {
                likeComment(shortComment._id,bindingAdapterPosition)
            }

        }

        fun bind(shortComment: Comment) {
            initBinding(binding, shortComment)
        }



    }

    object ShortCommentDiffUtil : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }


    public interface Callback {
        fun onClickOnReply(shortComment: Comment, childPosition: Int)
        fun onClickOnViewMoreReply(shortComment: Comment, position: Int)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun addToList(comments: List<Comment>) {
        val size = commentList.size
        Log.d("lasjkdasdss",commentList.size.toString())
        commentList.addAll(comments)
        val sizeNew = commentList.size
        notifyItemRangeChanged(size, sizeNew)
    }
    fun submitList(comments: List<Comment>) {
        commentList.clear()
        commentList.addAll(comments)
        notifyDataSetChanged()
    }


/*
    fun addComment(data: String) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funTimeId)
        hashmap.put("comment", data)
        hashmap.put("id", "")
        hashmap.put("comment_id", "")
        hashmap.put("is_delete", "0")

        networkViewModel.addComment(hashmap)
        networkViewModel.addCommentLiveData.observe(lifecyclerOwner) {
            it.let {

                if (it!!.status) {
                    */
/*      viewModel.addComment(
                              Comment(
                                  it.results._id,
                                  Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                                  "test",
                                  data,
                                  first_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name,
                                  last_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name,
                                  child_count = it.results.child_count,
                                  profile_img = it.results.profile_img
                              )
                          )*//*


                }


            }
        }
    }
    fun replyOverComment(data: String, commentId: String) {

        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funTimeId)
        hashmap.put("comment", data)
        hashmap.put("id", "")
        hashmap.put("comment_id", commentId)
        hashmap.put("is_delete", "0")

        networkViewModel.addComment(hashmap)
        networkViewModel.addCommentLiveData.observe(lifecyclerOwner) {
            it.let {

                if (it!!.status) {
                    */
/*    viewModel.addComment(
                            Comment(
                                it.results._id,
                                Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                                "test",
                                data,
                                parentId = it.results.parentId ?: it.results._id,
                                first_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name,
                                last_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name,
                                child_count = it.results.child_count,
                                profile_img = it.results.profile_img
                            )
                        )*//*


                }
            }
        }
    }
*/
fun likeComment(commentId: String,position: Int) {

    var hashmap = HashMap<String, String>()
    hashmap.put("funtime_id", funTimeId)
    hashmap.put("comment_id", commentId)

    networkViewModel.likeComment(hashmap)
    networkViewModel.likeCommentLiveData.observe(lifecyclerOwner) { mainIt ->
        run {
            if (mainIt!!.status) {


                if (commentList[position].isLiked=="Yes"){
                    commentList[position].isLiked="No"
                }else{
                    commentList[position].isLiked="Yes"
                }
                notifyItemChanged(position)




            }
        }

    }
}


}

