package com.stalmate.user.model

data class ModelProfileResponse(
    val message: String,
    val results: Results_Data,
    val status: Boolean
)

data class Results_Data(
    val about: String,
    val albums: List<Any>,
    val city: String,
    val company: String,
    val cover_img: List<Any>,
    val dob: String,
    val email: String,
    val first_name: String,
    val gender: String,
    val home_town: String,
    val id: String,
    val img_url: String,
    val last_name: String,
    val marital_status: String,
    val number: String,
    val profile_img: List<Any>,
    val schoolandcollege: String,
    val schoolandcollegename: String,
    val url: String
)