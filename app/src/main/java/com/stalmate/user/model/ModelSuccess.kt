package com.stalmate.user.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class ModelSuccess(
    val status: Boolean,
    val message: String?,
    val results: Comment,
)

data class ModelRoom(
    val status: Boolean,
    val results: Comment,
    val Room_id: String
)

@Parcelize
data class UserNameValidatedResponse(
    val message: String? = "", // success !!
    val reponse: Reponse? = null
) : Parcelable {
    @Parcelize
    data class Reponse(
        val name_status: Boolean? // true
    ) : Parcelable
}

@Parcelize
data class UserNameChangeResponse(
    val message: String? = "", // success !!
) : Parcelable