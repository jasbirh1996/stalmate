package com.stalmate.user.model

import java.io.Serializable

data class ModelFriend(
    val message: String,
    val results: List<User>,
    val status: Boolean
)

data class ModelFriend1(
    val message: String,
    val results: List<User1>,
    val status: Boolean
)



