package com.stalmate.user.model

data class ModelBlockedUser(
    val message: String,
    val results: List<User>,
    val status: Boolean
)
