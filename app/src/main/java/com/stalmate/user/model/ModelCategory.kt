package com.stalmate.user.model
data class ModelCategory(
    val message: String,
    val results: List<Category>,
    val status: Boolean
)

data class Category(
    val id: String,
    val image: String,
    val name: String
)