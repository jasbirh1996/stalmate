package com.stalmate.user.utilities

object Constants {
    const val url_category_list="auth_service/auth_api/category_list"
    const val url_language_list="auth_service/auth_api/language_list"
    const val URL_SIGN_UP="auth_service/auth_api/user_register"
    const val URL_LOGIN="auth_service/auth_api/user_login"
    const val URL_OTP="auth_service/auth_api/update_password"
    const val URL_OTP_REGISTRATION="auth_service/auth_api/otp_register_process"
    const val url_friend_list="auth_service/friend_api/get_friend_list"
    const val url_send_friend_request="auth_service/friend_api/send_friend_request"
    const val url_update_friend_request="auth_service/friend_api/update_friend_request"
    const val url_send_follower_request="auth_service/friend_api/send_follower_request"
    const val GET_PROFILE_API="auth_service/auth_api/get_profile_data"
    const val GET_OTHER_USER_PROFILE_API="auth_service/auth_api/get_profile_user"
    const val UPDATE_PROFILE_API="auth_service/auth_api/profile_update"
    const val BLOCK_API="/auth_service/friend_api/block_user"

    const val START_DATE = "01"


    const val TYPE_FRIEND_REQUEST = "friend_requests"
    const val TYPE_FRIEND_SUGGESTIONS = "suggestions"
    const val TYPE_MY_FRIENDS = "friends"
    const val TYPE_ALL_FOLLOWERS_FOLLOWING = "following_follower"



    const val TYPE_FRIEND_SUGGESTIONS_SUGGESTED = "01"
    const val TYPE_FRIEND_SUGGESTIONS_FOLLOWERS = "02"
    const val TYPE_FRIEND_FOLLOWING = "following"
    const val TYPE_FRIEND_FOLLOWER = "follower"
}