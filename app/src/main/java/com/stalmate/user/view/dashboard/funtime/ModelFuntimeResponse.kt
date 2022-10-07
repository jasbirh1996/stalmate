package com.stalmate.user.view.dashboard.funtime

data class ModelFuntimeResponse(
    val message: String,
    val results: List<ResultFuntime>,
    val status: Boolean
)

data class ResultFuntime(
    val Created_date: String,
    val artist_name: String,
    val comment_count: Int,
    val `file`: String,
    val file_type: String,
    val first_name: String,
    val hastag: String,
    val id: String,
    val last_name: String,
    val like_count: Int,
    val location: String,
    val profile_img: String,
    val share_count: Int,
    val sound_file: String,
    val sound_name: String,
    val tag_id: String,
    val text: String,
    val url: String
)