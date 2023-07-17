package com.stalmate.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ModelGlobalSearch(
    val message: String? = "", // success !!
    val reponse: ArrayList<Reponse?>? = arrayListOf()
) : Parcelable {
    @Parcelize
    data class Reponse(
        val Created_date: String? = "", // 2023-04-14 13:30:4
        val city: String? = "",
        val first_name: String? = "", // loop
        val id: String? = "", // 6439555c4d8ac204ccd94b68
        val img: String? = "",
        val isFollowed: Int? = 0, // 0
        var isFriend: Int? = 0, // 0
        val last_name: String? = "", // one
        val mutual_friend: Int? = 0, // 0
        val profile_data: ArrayList<ProfileData?>? = arrayListOf(),
        var request_status: String? = "",
        val url: String? = "" // https://webservice.kulluu.com
    ) : Parcelable {

        fun profileData() = if (!profile_data.isNullOrEmpty())
            profile_data.get(0)
        else
            null
        @Parcelize
        data class ProfileData(
            val education: ArrayList<Educations?>? = arrayListOf(),
            val home_town: String? = "",
            val location: String? = "",
            val marital_status: String? = "",
            val privacy_status: PrivacyStatus? = PrivacyStatus(),
            val profession: ArrayList<Professions?>? = arrayListOf()
        ) : Parcelable {
            @Parcelize
            data class Professions(
                val Created_date: String,
                val Updated_date: String,
                val __v: Int,
                val _id: String,
                var company_name: String,
                var currently_working_here: String,
                var designation: String,
                var from: String,
                val is_delete: String,
                val status: String,
                var to: String,
                val user_id: String
            ) : Parcelable

            @Parcelize
            data class Educations(
                val Created_date: String,
                val Updated_date: String,
                val __v: Int,
                val _id: String,
                var branch: String,
                var course: String,
                val is_delete: String,
                var sehool: String,
                val status: String,
                val user_id: String
            ) : Parcelable

            @Parcelize
            data class PrivacyStatus(
                val _id: String? = "", // 6439557c4d8ac204ccd94e22
                val about: Int? = 0, // 2
                val allow_others_to_find_me: Boolean? = false, // true
                val block_contact: ArrayList<@RawValue Any?>? = arrayListOf(),
                val groups: Int? = 0, // 1
                val last_seen: Int? = 0, // 3
                val prfile_photo: Int? = 0, // 3
                val profile: Int? = 0, // 2
                val read_receipts: Boolean? = false, // true
                val story: Int? = 0, // 1
                val user_id: String? = "", // 6439555c4d8ac204ccd94b68
                val who_can_like_my_post: Int? = 0, // 1
                val who_can_post_comment: Int? = 0, // 1
                val who_can_see_email_address: Int? = 0, // 1
                val who_can_see_my_future_post: Int? = 0, // 1
                val who_can_see_people_page_list: Int? = 0, // 1
                val who_can_see_phone_number: Int? = 0, // 1
                val who_can_send_me_message: Int? = 0, // 1
                val who_can_send_you_friend_request: Int? = 0 // 1
            ) : Parcelable
        }
    }
}/*(
    val message: String,
    val status: Boolean,
    val user_list: ArrayList<User>
)*/

