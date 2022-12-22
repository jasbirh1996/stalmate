package com.stalmate.user.model

import java.io.Serializable

data class ModelUser(
    val message: String,
    val results: User,
    val status: Boolean
)

data class User(
    val img: String? = null,
    var isFriend: Int=0,
    var isFollowed: Int=0,
    var hasFriendRequest: Int=0,
    var request_status: String? = null,
    var friendRequestsent: Int=0,
    var isFriendRemovedFromSuggestion: Int? = null,
    var isBlocked: Int? = null,
    val friends_count: Int? = null,
    val about: String? = null,
    val city: String? = null,
    val company: String? = null,
    val cover_img: List<Photo> = ArrayList<Photo>(),
    val cover_img1: String? = null,
    val dob: String? = null,
    var follower_count: Int=0,
    var following_count: Int=0,
    val email: String? = null,
    val first_name: String,
    val last_name: String,
    val mutual_friend: String? = null,
    val id: String,
    val img_url: String? = null,
    val gender:String? = null,

    val number: String? = null,
    val profile_data: ArrayList<ProfileData> = ArrayList<ProfileData>(),
    val profile_img: List<Photo> = ArrayList<Photo>(),
    val profile_img1: String,
    val schoolandcollege: String? = null,
    val schoolandcollegename: String? = null,
    val photos: ArrayList<AlbumImage> = ArrayList<AlbumImage>(),
    val albums: ArrayList<Albums> = ArrayList<Albums>(),
    val url: String? = null,
    val mes:String? = null,
    var isSelected:Boolean=false,
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

):Serializable


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
