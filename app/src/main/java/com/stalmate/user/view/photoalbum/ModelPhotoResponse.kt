package com.stalmate.user.view.photoalbum

import com.stalmate.user.model.Photo
import com.stalmate.user.model.ResultResponse

data class ModelPhotoResponse(
    val message: String,
    val results: List<Photo>,
    val status: Boolean
)

