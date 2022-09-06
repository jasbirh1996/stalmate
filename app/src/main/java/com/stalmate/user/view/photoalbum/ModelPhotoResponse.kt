package com.stalmate.user.view.photoalbum

import com.stalmate.user.model.ResultResponse

data class ModelPhotoResponse(
    val message: String,
    val results: List<ResultImage>,
    val status: Boolean
)

class ResultImage(
    val Created_date: String,
    val files: String,
    val id: String,
    val url: String
)