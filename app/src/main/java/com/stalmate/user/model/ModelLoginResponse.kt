package com.stalmate.user.model

data class ModelLoginResponse(
    val message: String,
    val results: List<Results>,
    val status: Boolean
)

data class Results(
    val email: String,
    val first_name: String,
    val gender: String,
    val id: String,
    val last_name: String,
    val token: String
)