package com.stalmate.user.model


data class ModelSuccess(
    val status: Boolean,
    val message: String?,
    val results: Comment,
)
data class ModelRoom(
    val status: Boolean,
    val results: Comment,
    val Room_id:String
)
