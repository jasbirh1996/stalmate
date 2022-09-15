package com.stalmate.user.model

data class ModelGlobalSearch(
    val message: String,
    val status: Boolean,
    val user_list: List<User>
)

