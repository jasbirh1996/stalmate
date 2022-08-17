package com.stalmate.user.model

import java.io.Serializable

data class ModelFriend(
    val message: String,
    val results: List<Friend>,
    val status: Boolean
)

data class Friend(
    val id: String
):Serializable