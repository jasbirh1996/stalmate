package com.stalmate.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ReportsListingResponse(
    val message: String? = "", // success !!
    val reponse: ArrayList<Reponse?>? = arrayListOf()
) : Parcelable {
    @Parcelize
    data class Reponse(
        val _id: String? = "", // 643fcf9d0726c51beb129cfe
        val admin_response: String? = "", // []
        val detailed_reason: String? = "", // I just don't like it.
        val report_category: String? = "", // I just don't like it.
        val report_image: String? = "", // https://stalematebucket.s3.me-south-1.amazonaws.com/report_image_1681903516448.jpg
        val report_reason: String? = "", // I just don't like it.
        val ticket_id: String? = "", // 643fcf9d0726c51beb129cfe
        val user_id: String? = "" // 643955be4d8ac204ccd950cd
    ) : Parcelable
}