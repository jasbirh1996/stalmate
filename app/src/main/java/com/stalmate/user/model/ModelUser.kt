

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
    var follower:Int,
    var following:Int,
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
    val education: List<Any>,
    val home_town: String,
    val location: String,
    val marital_status: String,
    val profession: List<Any>
):Serializable

data class ProfileImg(
    val _id: String,
    val img: String
):Serializable