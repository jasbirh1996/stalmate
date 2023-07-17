package com.stalmate.user.view.photoalbum

import com.stalmate.user.model.Albums

data class ModelPhotoResponse(
    val message: String,
    val results: List<Albums>,
    val status: Boolean,
    val position: Int
)

