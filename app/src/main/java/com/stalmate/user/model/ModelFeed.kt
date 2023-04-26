package com.stalmate.user.model

data class ModelFeed(
    val message: String,
    val results: List<Feed>,
    val reponse: List<Feed>,
    val status: Boolean
)

data class Feed(
    val id: String,
    val image: String,
    val name: String
)