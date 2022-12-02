package com.stalmate.user.model

data class ModelComment(
    val status: Boolean,
    val results: Comment,
)


data class ModelGetComment(
    val status: Boolean,
    val results: List<Comment>,
)

data class Comment(
    val _id: String,
    val date: String,
    val author: String,
    val comment: String,
    var level: Int = 1,
    var replies: MutableList<Comment> = mutableListOf(),
    val parentId: String? = null,
    val first_name: String,
    val last_name: String,
    var child_count: Int,
    var profile_img:String,
    var isShowingReplies:Boolean=false
)


