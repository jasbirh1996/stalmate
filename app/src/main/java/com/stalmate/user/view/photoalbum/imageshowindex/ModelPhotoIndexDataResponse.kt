package com.stalmate.user.view.photoalbum.imageshowindex

data class ModelPhotoIndexDataResponse(
    val message: String,
    val results: ArrayList<PhotoIndexResult>,
    val status: Boolean
)

data class PhotoIndexResult(
    val Created_date: String,
    val id: String,
    val img: String
)