package com.stalmate.user.model

data class ModelLanguageResponse(
    val message: String,
    val results: ArrayList<Result>,
    val reponse: ArrayList<Result>,
    val status: Boolean
)

data class Result(
    val id: String,
    val name: String
)