package com.stalmate.user.view.photoalbum.imageshowindex

import com.stalmate.user.model.Photo

data class ModelPhotoIndexDataResponse(
    val message: String,
    val results: ArrayList<Photo>,
    val status: Boolean
)

