package com.stalmate.user.model

data class ModelComment(
    val message: String,
    val results: List<Feed>,
    val status: Boolean
)

data class Comment(
    val id: String,
    val image: String,
    val name: String
)