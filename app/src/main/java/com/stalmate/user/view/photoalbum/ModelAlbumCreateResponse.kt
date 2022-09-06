package com.stalmate.user.view.photoalbum

data class ModelAlbumCreateResponse(
    val message: String,
    val results: Results,
    val status: Boolean
)

data class Results(
    val Created_date: String,
    val Updated_date: String,
    val __v: Int,
    val _id: String,
    val is_delete: String,
    val name: String,
    val status: String,
    val user_id: String
)