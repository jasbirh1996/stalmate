package com.stalmate.user.model

data class BlockUserListResponse(
    val message: String,
    val reponse: ArrayList<Reponse?>? = arrayListOf()
) {
    data class Reponse(
        val Created_date: String,
        val Updated_date: String,
        val __v: Int,
        val _id: String,
        val id_user: String,
        val is_delete: String,
        val status: String,
        val user_id: String
    )
}