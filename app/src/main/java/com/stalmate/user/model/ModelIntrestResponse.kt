package com.stalmate.user.model

data class ModelIntrestResponse(
    val message: String,
    val results: List<ResultIntrest>,
    val status: Boolean
)


data class ResultIntrest(
    val id: String,
    val image: String,
    val name: String
)