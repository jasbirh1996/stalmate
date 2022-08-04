package com.slatmate.user.model
data class ModelCategory(
    val `data`: List<Category>,
    val message: String,
    val result: Boolean
)

data class Category(
    val commentCount: String,
    val created_at: String,
    val description: String,
    val id: String,
    val feedimages: List<FeedImage>,
    val feedvideos: List<FeedVideo>,
    val is_deleted: String,
    var already_follow: String,
    val status: String,
    var userProfile:String,
    val title: String,
    val updated_at: String,
    val user_id: String,
    var like:Int,
    var likeCount: String,
    var own_post: String,
    var user_name:String
)