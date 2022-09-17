package com.stalmate.user.view.dashboard.Friend.categorymodel

data class ModelCategoryResponse(
    val message: String,
    val results: List<CategoryResponse>,
    val status: Boolean
)

data class CategoryResponse(
    val id: String,
    val name: String,
    val type: String
)