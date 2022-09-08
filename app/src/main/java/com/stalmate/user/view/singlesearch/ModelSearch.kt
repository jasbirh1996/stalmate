package com.stalmate.user.view.singlesearch

data class ModelSearch(
    val message: String,
    val results: List<ResultSearch>,
    val status: Boolean
)

data class ResultSearch(
    val id: String,
    val name: String
)