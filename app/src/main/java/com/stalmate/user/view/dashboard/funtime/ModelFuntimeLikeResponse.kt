package com.stalmate.user.view.dashboard.funtime

data class ModelFuntimeLikeResponse(
    val like_count: Int,
    val message: String,
    val results: Any,
    val status: Boolean
)