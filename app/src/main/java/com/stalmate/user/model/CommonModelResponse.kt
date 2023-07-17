package com.slatmate.user.model
import android.os.Parcelable

import kotlinx.parcelize.Parcelize


data class CommonModelResponse(
    val message: String? = null,
    val status: Boolean? = null,
    val results: String = ""
)

data class FunTimeAddResponse(
    val message: String? = "", // Save successfully
    val results: Results? = null,
    val status: Boolean? = false // true
) {
    data class Results(
        val Created_date: String? = "", // 2023-04-27 10:42:27
        val Created_date_1: String? = "", // Thu Apr 27 2023 10:42:27 GMT+0000 (Coordinated Universal Time)
        val Updated_date: String? = "",
        val __v: Int? = 0, // 0
        val _id: String? = "", // 644a5193371bafdb00309134
        val comment_status: String? = "",
        val cover_image: String? = "",
        val deviceId: String? = "", // 12345
        val deviceToken: String? = "", // 54321
        val `file`: String? = "", // https://stalematebucket.s3.me-south-1.amazonaws.com/file_1682592142782.mp4
        val file_type: String? = "", // null
        val hastag: String? = "",
        val is_delete: String? = "", // 0
        val is_draft: Boolean? = false, // false
        val location: String? = "", // ,
        val privacy: String? = "", // Public
        val privacy_data: String? = "", // none
        val sound_id: String? = "", // none
        val status: String? = "", // Active
        val tag_id: String? = "",
        val text: String? = "", // Hi
        val thum_icon: String? = "",
        val user_id: String? = "" // 643955be4d8ac204ccd950cd
    )
}
@Parcelize
data class ProfileImagesUpdated(
    val message: String? = "", // Image Updated successfully !
    val results: Results? = null,
    val status: Boolean? = false // false
) : Parcelable {
    @Parcelize
    data class Results(
        val Created_date: String? = "", // 2023-07-15 9:37:6
        val Updated_date: String? = "",
        val __v: Int? = 0, // 0
        val _id: String? = "", // 64b268c21fb3be7293ef2000
        val about: String? = "",
        val access_token: String? = "", // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjRiMjY4YzIxZmIzYmU3MjkzZWYyMDAwIiwiaXNfdG9rZW5fdmFsaWRlIjoxLCJpYXQiOjE2ODk0MTM4MjZ9.3-1D4WVtMWPpCKbHhICN3Tgv3T10IV-OMVTdB128Y2k
        val branch_id: String? = "",
        val category_id: String? = "",
        val city: String? = "",
        val country: String? = "",
        val countrycode: String? = "",
        val cover_img_1: String? = "",
        val deviceId: String? = "",
        val deviceToken: String? = "",
        val dob: String? = "", // 1-January-1960
        val email: String? = "", // demoiii@gmail.com
        val first_name: String? = "", // gy
        val gender: String? = "", // Male
        val home_town: String? = "",
        val is_account_recover: Boolean? = false, // false
        val is_block: Int? = 0, // 0
        val is_delete: String? = "", // 0
        val language: String? = "",
        val last_name: String? = "", // gh
        val last_update_of_username: String? = "", // 1689246320973
        val marital_status: String? = "",
        val number: String? = "",
        val otp: String? = "",
        val otp_send_time: String? = "",
        val password: String? = "", // $2b$10$u9PBf3WlODF9.h0rZ.cYLOgrlLp65pe3bZjsEY6icX1Td7ryXIYa6
        val profile_img_1: String? = "", // https://stalematebucket.s3.me-south-1.amazonaws.com/1689414019118/temp_file_20230715_151019.jpg
        val schoolandcollege: String? = "",
        val state: String? = "",
        val status: String? = "", // Active
        val stepper: Int? = 0, // 0
        val university_id: String? = "",
        val url: String? = "",
        val user_name: String? = "" // Loopuser
    ) : Parcelable
}