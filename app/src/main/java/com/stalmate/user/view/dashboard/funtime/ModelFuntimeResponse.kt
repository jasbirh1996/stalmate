package com.stalmate.user.view.dashboard.funtime

data class ModelFuntimeResponse(
    val message: String,
    val results: List<ResultFuntime>,
    val status: Boolean
)

data class ResultFuntime(
    val Created_date: String,
    val `file`: String,
    val file_type: String,
    val first_name: String,
    val hastag: String,
    val id: String,
    val last_name: String,
    val location: String,
    val tag_id: String,
    val text: String,
    val url: String
)