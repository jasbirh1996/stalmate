package com.stalmate.user.model


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class HashTagsListResponse(
    val message: String? = "", // List successfully
    val results: ArrayList<Result?>? = arrayListOf(),
    val status: Boolean? = false // true
) : Parcelable {
    @Parcelize
    data class Result(
        val id: String? = "", // 646cab4840dee1de171a0188
        val name: String? = "", // #Navanindra
        val use_count: Int? = 0 // 0
    ) : Parcelable
}