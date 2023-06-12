package com.stalmate.user.model

import java.io.Serializable

data class ModelUser(
    val message: String,
    val results: User,
    val status: Boolean
)

data class User(
    var isFriend: Int = 0,
    var isFollowed: Int = 0,
    var hasFriendRequest: Int = 0,
    var friendRequestsent: Int = 0,
    var isBlocked: String? = "0",
    val friends_count: Int? = null,
    val about: String? = null,
    val city: String? = null,
    val company: String? = null,
    val cover_img: String? = null,
    val cover_img1: String? = null,
    val dob: String? = null,
    var follower_count: Int = 0,
    var following_count: Int = 0,
    val email: String? = null,
    val first_name: String = "",
    val last_name: String = "",
    val user_name: String = "",
    val mutual_friend: String? = null,
    val id: String,
    val img_url: String? = null,
    val gender: String? = null,
    val number: String? = null,
    val profile_data: ArrayList<ProfileData> = ArrayList<ProfileData>(),
    val profile_img: String = "",
    val profile_img1: String = "",
    val schoolandcollege: String? = null,
    val schoolandcollegename: String? = null,
    val albums: ArrayList<Albums> = ArrayList<Albums>(),
    val url: String? = null,


    //Not coming for new users
    val mes: String? = null,
    var isSelected: Boolean = false,
    val photos: ArrayList<AlbumImage> = ArrayList<AlbumImage>(),
    val img: String? = null,
    var request_status: String? = null,
    var isFriendRemovedFromSuggestion: Int? = null,


    //From M8 work
    val connected_since: String? = "",
    val privacy_setting: PrivacySetting? = null,
    val block_contact: ArrayList<Any?>? = arrayListOf()
) : Serializable

data class Photo(
    val _id: String,
    val id: String,
    val img: String,
    val files: String,
    val url: String
) : Serializable


data class ProfileData(
    val education: ArrayList<Education>,
    val home_town: String,
    val location: String,
    val marital_status: String,
    val profession: ArrayList<Profession>,
) : Serializable


data class Education(
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
) : Serializable

data class Profession(
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
) : Serializable


data class AlbumImage(
    val album_id: String,
    val user_id: String,
    val Created_date: String,
    val Updated_date: String,
    val is_delete: String,
    val _id: String,
    val files: String,
    val __v: String
) : Serializable


data class Albums(
    val id: String,
    val name: String,
    val img: String
) : Serializable

data class PrivacySetting(
    val _id: String? = "", // 643955d84d8ac204ccd95141
    val about: Int? = 0, // 2
    val allow_others_to_find_me: Boolean? = false, // true
    val groups: Int? = 0, // 1
    val last_seen: Int? = 0, // 1
    val prfile_photo: Int? = 0, // 1
    val profile: Int? = 0, // 2
    val read_receipts: Boolean? = false, // true
    val story: Int? = 0, // 1
    val user_id: String? = "", // 643955be4d8ac204ccd950cd
    val who_can_like_my_post: Int? = 0, // 1
    val who_can_post_comment: Int? = 0, // 1
    val who_can_see_email_address: Int? = 0, // 1
    val who_can_see_my_future_post: Int? = 0, // 1
    val who_can_see_people_page_list: Int? = 0, // 1
    val who_can_see_phone_number: Int? = 0, // 1
    val who_can_send_me_message: Int? = 0, // 1
    val who_can_send_you_friend_request: Int? = 0 // 1
)