package com.stalmate.user.view.dashboard.funtime

data class ModelMusicListResponse(
    val message: String,
    val results: List<ResultMusic>,
    val status: Boolean
)

data class ResultMusic(
    val Created_date: String,
    val artist_name: String,
    val id: String,
    val keywords: String,
    val playlist: String,
    val size: String,
    val sound_category: String,
    val sound_file: String,
    val sound_language: String,
    val sound_name: String
)