package com.stalmate.user.view.dashboard.funtime


import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemCommentBinding
import com.stalmate.user.model.Comment
import com.stalmate.user.utilities.TimesAgo2
import com.stalmate.user.viewmodel.AppViewModel

class CommentAdapterNew(
    var context: Context, var callback: Callback,
    var networkViewModel: AppViewModel, var funTimeId: String, var lifecyclerOwner: LifecycleOwner
) : RecyclerView.Adapter<CommentAdapterNew.CommentViewHolder>() {
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


    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root),
        ChildCommentAdapter.Callback {
        fun bind(shortComment: Comment) {
         //   binding.tvDate.text = "${TimesAgo2.covertTimeToText(shortComment.Created_date,true)}"
            binding.tvDate.text = "${shortComment.Created_date}"
            binding.tvUserName.text = "${shortComment.first_name} ${shortComment.last_name}"
            val text = "<font color=#000000>${shortComment.first_name+" "} ${shortComment.last_name}</font> <font color=#0f53b8>${shortComment.comment} </font>"
            binding.tvUserName.text=Html.fromHtml(text)

            binding.tvReply.text = "Reply"
            binding.tvLikesCount.text = "0 Likes"
            binding.tvComment.text = shortComment.comment
            Glide.with(context).load(shortComment.profile_img).circleCrop()
                .into(binding.ivUserImage)
            binding.tvReply.setOnClickListener {
                callback.onClickOnReply(shortComment, bindingAdapterPosition,0,false)
            }
            binding.tvLikesCount.setText(shortComment.like_count.toString()+" likes")
            binding.btnMore.setOnClickListener {
                if (shortComment.isExpanded) {
                    Log.d("a;ksdasd", "laskjdlasd")
                    shortComment.isExpanded = false
                    binding.btnMore.text = "Hide replies"
                    commentList[bindingAdapterPosition].replies.clear()

                    notifyItemChanged(bindingAdapterPosition)
                } else {
                    shortComment.isExpanded = true
                    viewReplies(shortComment, bindingAdapterPosition)
                }
            }

            var commentRepliedAdapter = ChildCommentAdapter(context, this, funTimeId,networkViewModel,lifecyclerOwner)
            commentRepliedAdapter.addToList(shortComment.replies)
            binding.rvChildReplies.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvChildReplies.adapter = commentRepliedAdapter

            /* binding.tvReply.setOnClickListener {
                    commentListener.onSendComment(
                        "author",
                        "message",
                        shortComment.level, shortComment.parentId ?: shortComment._id,bindingAdapterPosition
                    )
                }*/


            if (shortComment.child_count == 0) {
                binding.btnMore.visibility = View.GONE
                binding.view15.visibility = View.GONE
            } else {
                binding.btnMore.visibility = View.VISIBLE
                binding.btnMore.text = "Show ${shortComment.child_count} replies"
                binding.view15.visibility = View.VISIBLE

                if (shortComment.isExpanded) {
                    binding.btnMore.text = "Hide replies"
                } else {
                    binding.btnMore.text = "Show ${shortComment.child_count} replies"
                }

            }



            if (shortComment.isLiked=="Yes"){
                binding.ivHearIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.heart_filled
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

            binding.tvLikesCount.setText(shortComment.like_count.toString()+" likes")




            binding.ivHearIcon.setOnClickListener {
                likeComment(shortComment._id,bindingAdapterPosition)
            }

        }


        override fun onClickOnReply(shortComment: Comment, childPosition: Int) {
            callback.onClickOnReply(shortComment,bindingAdapterPosition, childPosition,true)
        }

        override fun onClickOnViewMoreReply(shortComment: Comment, position: Int) {
            callback.onClickOnViewMoreReply(shortComment, position)
        }


    }


    public interface Callback {
        fun onClickOnReply(shortComment: Comment, parentPosition: Int,childPosition: Int,isChild:Boolean)
        fun onClickOnViewMoreReply(shortComment: Comment, position: Int)
        fun onCommentAddedSucessfully()
    }

    object ShortCommentDiffUtil : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }


    override fun getItemCount(): Int {
        return commentList.size
    }

    fun addToList(comments: List<Comment>) {

        val size = commentList.size
        commentList.addAll(comments)
        val sizeNew = commentList.size
        notifyItemRangeChanged(size, sizeNew)
    }

    fun submitList(comments: List<Comment>) {
        commentList.clear()
        commentList.addAll(comments)
        notifyDataSetChanged()
    }


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
                          )*/
                    it.results.replies = ArrayList<Comment>()
                    commentList.add(it.results)
                    notifyItemInserted(commentList.size - 1)
                    callback.onCommentAddedSucessfully()
                }


            }
        }
    }
    fun replyOverComment(
        data: String,
        commentId: String,
        parentPosition: Int,
        childPosition: Int,
        isReplyisChildComment: Boolean
    ) {
        Log.d("akjshdasdsdfsd",isReplyisChildComment.toString())
        Log.d("akjshdasdsdfsdparentINdex",isReplyisChildComment.toString())
        Log.d("akjshdasdsdfsdchildINdex",isReplyisChildComment.toString())
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
                        )*/


                    if (isReplyisChildComment){
                        it.results.replies = ArrayList<Comment>()
                        commentList[parentPosition].isExpanded=true
                        commentList[parentPosition].child_count++
                        commentList[parentPosition].replies.add(it.results)
                        notifyItemChanged(parentPosition)
                    }else{
                        it.results.replies = ArrayList<Comment>()
                        commentList[parentPosition].isExpanded=true
                        commentList[parentPosition].child_count++
                        commentList[parentPosition].replies.add(it.results)
                        notifyItemChanged(parentPosition)
                    }
                    callback.onCommentAddedSucessfully()
                }
            }
        }
    }

    fun viewReplies(comment: Comment, position: Int) {

        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funTimeId)
        hashmap.put("comment_id", comment._id)

        networkViewModel.getRepliesList(hashmap)
        networkViewModel.repliesLiveData.observe(lifecyclerOwner) { mainIt ->
            run {
                if (mainIt!!.status) {

                    mainIt.results.forEach {
                        /*      viewModel.addComment(
                                  Comment(
                                      it._id,
                                      Calendar.getInstance(Locale.ROOT).toSimpleDate(),
                                      "test",
                                      it.comment,
                                      parentId = it.parentId,
                                      first_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].first_name,
                                      last_name = PrefManager.getInstance(requireContext())!!.userDetail.results[0].last_name,
                                      child_count = 0,
                                      profile_img = it.profile_img
                                  )
                              )*/
                        it.replies = kotlin.collections.ArrayList<Comment>()


                    }
                    commentList[position].replies.addAll(mainIt.results)
                    notifyItemChanged(position)


                }
            }

        }
    }


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
                        commentList[position].like_count--
                    }else{
                        commentList[position].isLiked="Yes"
                        commentList[position].like_count++
                    }
                    notifyItemChanged(position)




                }
            }

        }
    }

}

