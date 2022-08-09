package com.stalmate.user.model

data class ModelLanguage(
    val `data`: List<Language>,
    val message: String,
    val result: Boolean
)

data class Language(
    val commentCount: String,
    val created_at: String,
    val description: String,
    val id: String,
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