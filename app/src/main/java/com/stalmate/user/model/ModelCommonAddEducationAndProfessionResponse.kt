package com.stalmate.user.model

data class ModelCommonAddEducationAndProfessionResponse(
    val message: String,
    val results: EducationProfession,
    val status: Boolean
)

data class EducationProfession(
    val Created_date: String,
    val Updated_date: String,
    val branch: String,
    val course: String,
    val sehool: String,
    val __v: Int,
    val _id: String,
    val company_name: String,
    val currently_working_here: String,
    val designation: String,
    val from: String,
    val is_delete: String,
    val status: String,
    val to: String,
    val user_id: String
)