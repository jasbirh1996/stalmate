package com.stalmate.user.view.photoalbum.imageshowindex

import com.stalmate.user.model.Albums

data class ModelPhotoIndexDataResponse(
    val message: String,
    val results: ArrayList<Albums>,
    val status: Boolean
)

