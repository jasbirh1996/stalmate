package com.stalmate.user.model

data class ModelEdutionAddResponse(
    val message: String,
    val results: ResultsEdutionResponse,
    val status: Boolean
)

data class ResultsEdutionResponse(
    val Created_date: String,
    val Updated_date: String,
    val __v: Int,
    val _id: String,
    val branch: String,
    val course: String,
    val is_delete: String,
    val sehool: String,
    val status: String,
    val user_id: String
)