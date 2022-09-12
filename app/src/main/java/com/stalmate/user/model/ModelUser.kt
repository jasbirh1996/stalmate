

package com.stalmate.user.model

import java.io.Serializable

data class ModelUser(
    val message: String,
    val results: User,
    val status: Boolean
)

data class User(

    val img:String,
    var isFriend:Int,
    var isFollowed:Int,


    var hasFriendRequest:Int,
        var request_status:String,
    var friendRequestsent:Int,

    var isFriendRemovedFromSuggestion:Int,
    var isBlocked:Int,
    val friends_count:Int,
    val about: String,
    val albums: List<Any>,
    val city: String,

    val company: String,
    val cover_img: List<CoverImg>,
    val cover_img1: String,
    val dob: String,
    var follower_count:Int,
    var following_count:Int,
    val email: String,
    val first_name: String,
    val gender: String,
    val id: String,
    val img_url: String,
    val last_name: String,
    val number: String,
    val profile_data: List<ProfileData>,
    val profile_img: List<ProfileImg>,
    val profile_img1: String,
    val schoolandcollege: String,
    val schoolandcollegename: String,
    val url: String
):Serializable

data class CoverImg(
    val _id: String,
    val img: String
):Serializable

data class ProfileData(
    val education: ArrayList<Education>,
    val home_town: String,
    val location: String,
    val marital_status: String,
    val profession: ArrayList<Profession>
):Serializable

data class ProfileImg(
    val _id: String,
    val img: String
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
):Serializable

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
):Serializable