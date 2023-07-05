package com.stalmate.user.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelLoginResponse(
    val message: String = "", // Login successfully
    val results: Results? = Results(),
    val status: Boolean = false // true
) : Parcelable {
    @Parcelize
    data class Results(
        val Created_date: String? = "", // 2023-04-14 13:31:42
        val Updated_date: String? = "", // 2023-04-21 10:6:39
        val __v: Int? = 0, // 0
        val _id: String? = "", // 643955be4d8ac204ccd950cd
        val about: String? = "",
        val access_token: String? = "", // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjQzOTU1YmU0ZDhhYzIwNGNjZDk1MGNkIiwiaXNfdG9rZW5fdmFsaWRlIjoxLCJpYXQiOjE2ODI1ODgyMjN9.PltI-zEeNl-yAq6E6SYA4ciqtiUb-WN4yedIesV7fS0
        val branch_id: String? = "",
        val category_id: String? = "",
        val city: String? = "",
        val company: String? = "",
        val country: String? = "IN",
        val countrycode: String? = "",
        val cover_img_1: String? = "", // https://webservice.kulluu.com/images/user_img/cover_img_1682071598504.jpg
        val deviceId: String? = "",
        val deviceToken: String? = "", // euyKCF3vQIO47qOW-6Ex15:APA91bGkgjbTkrK0uiMPZeKSqEdjgdwExtlGIXnoKLgpR_c8etaW2uX_IgBTfYRrvWp2dZaQmOnpghYNwB4c3eTpt0gmgV2EmMs1Z_sDspBOGtSQPmlZ751dYQX1nFvkuxDfVRPZ-lax
        val dob: String? = "", // 11-January-1969
        val email: String? = "", // loop2@gmail.com
        val first_name: String? = "", // loop
        val gender: String? = "", // Male
        val home_town: String? = "",
        val is_account_recover: Boolean? = false, // true
        val is_delete: String? = "", // 0
        val language: String? = "English",
        val last_name: String? = "", // two
        val marital_status: String? = "",
        val number: String? = "", // 4234234230
        val otp: String? = "", // 3010
        val otp_send_time: String? = "", // 1682329504355
        val password: String? = "", // $2b$10$ljjazZ6N7J0rEKvRryd4iu1lpo8POwt56QaHmfUd0qaj.NvjClUpS
        var profile_img_1: String? = "", // https://webservice.kulluu.com/images/user_img/profile_img_1682071578425.jpg
        val schoolandcollege: String? = "",
        val show_to_about: String? = "",
        val show_to_group: String? = "",
        val show_to_profile: String? = "",
        val show_to_profile_photo: String? = "",
        val show_to_story: String? = "",
        val state: String? = "",
        val status: String? = "", // Active
        val university_id: String? = "",
        val url: String? = "",
        val user_name: String? = "", // null
        val who_can_like_my_post: String? = "",
        val who_can_post_comment: String? = "",
        val who_can_see_email: String? = "",
        val who_can_see_my_future_post: String? = "",
        val who_can_see_number: String? = "",
        val who_can_see_people_page: String? = "",
        val who_can_send_me_message: String? = "",
        val who_can_send_you_friendrequest: String? = "",
        val last_update_of_username: String? = "",
        val is_block: String? = "0",
        val stepper: Int? = 0
    ) : Parcelable
}