package com.stalmate.user.model

data class ModelFriend(
    val message: String,
    val results: List<Friend>,
    val status: Boolean
)

data class Friend(
    val id: String,
    val image: String,
    val name: String
)