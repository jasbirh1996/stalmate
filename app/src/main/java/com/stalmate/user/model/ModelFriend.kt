package com.stalmate.user.model

import java.io.Serializable

data class ModelFriend(
    val message: String,
    val results: List<Friend>,
    val status: Boolean
)

data class Friend(
    val first_name: String,
    val id: String,
    val img: String,
    val last_name: String,
    val request_status: String,
    val url: String
):Serializable