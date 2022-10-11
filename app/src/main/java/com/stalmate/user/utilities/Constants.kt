package com.stalmate.user.utilities

object Constants {
    const val url_category_list="auth_service/auth_api/category_list"
    const val url_language_list="auth_service/auth_api/language_list"
    const val URL_INTREST_LIST="auth_service/auth_api/category_list"
    const val URL_SIGN_UP="auth_service/auth_api/user_register"
    const val URL_LOGIN="auth_service/auth_api/user_login"
    const val URL_UPDATE_ABOUT="auth_service/auth_api/update_about"
    const val URL_OTP="auth_service/auth_api/update_password"
    const val URL_NUMBER_VERIFY_UPDATE="auth_service/auth_api/update_number"
    const val URL_BLOCKED_LIST="auth_service/friend_api/get_block_user_list"
    const val URL_CATEGORY_LIST="auth_service/friend_api/friend_category_list"
    const val URLFUNTIME_LIST="auth_service/funtime_api/get_funtime_list"
    const val URL_FUNTIME_MUSIC_LIST="auth_service/funtime_api/get_sound_api"
    const val URL_FUNTIME_LIKE_UNLIKE="auth_service/funtime_api/like_unlike_funtime"




    const val URL_OTP_REGISTRATION="auth_service/auth_api/otp_register_process"
    const val url_friend_list="auth_service/friend_api/get_friend_list"
    const val url_send_friend_request="auth_service/friend_api/send_friend_request"
    const val url_update_friend_request="auth_service/friend_api/update_friend_request"
    const val url_remove_user_from_suggestions="auth_service/friend_api/remove_suggestions"
    const val URL_PHOTO_ALBUM="auth_service/auth_api/get_albums"
    const val URL_PHOTO_ALBUM_NAME="auth_service/auth_api/add_update_albums"
    const val URL_PHOTO_ALBUM_PHOTO="auth_service/auth_api/get_albums_img"
    const val URL_PHOTO_INDEX="auth_service/auth_api/get_profile_image_list"
    const val URL_SEARCH_UNIVERCITY="auth_service/auth_api/university_list"
    const val URL_SEARCH_BRACNCHLIST="auth_service/auth_api/branch_list"
    const val URL_EDUCATION_ADD="auth_service/auth_api/add_update_usereducation"
    const val URL_PROFESSION_ADD="auth_service/auth_api/add_update_userwork"
    const val URL_UPDATE_FRIEND_CATEGORY="auth_service/friend_api/add_update_friend_category"


    const val url_send_follower_request="auth_service/friend_api/send_follower_request"
    const val GET_PROFILE_API="auth_service/auth_api/get_profile_data"
    const val GET_OTHER_USER_PROFILE_API="auth_service/auth_api/get_profile_user"
    const val UPDATE_PROFILE_API="auth_service/auth_api/profile_update"
    const val UPDATE_PROFILE_API_FILE="auth_service/auth_api/profile_update_file"
    const val UPLOADE_ALBUM_IMAGE_API_FILE="auth_service/auth_api/add_albums_img"
    const val BLOCK_API="/auth_service/friend_api/block_user"
    const val URL_GLOBAL_SEARCH="auth_service/friend_api/search_list"
    const val START_DATE = "01"


    const val TYPE_FRIEND_REQUEST = "request"
    const val TYPE_FRIEND_SUGGESTIONS = "suggestions"
    const val TYPE_MY_FRIENDS = "friends"
    const val TYPE_ALL_FOLLOWERS_FOLLOWING = "following_follower"
    const val TYPE_PROFILE_FRIENDS = "profile_friends"
    const val TYPE_USER_TYPE_FOLLOWERS = "follower"
    const val TYPE_USER_TYPE_FOLLOWINGS = "following"


    const val TYPE_FRIEND_SUGGESTIONS_SUGGESTED = "01"
    const val TYPE_FRIEND_SUGGESTIONS_FOLLOWERS = "02"
    const val TYPE_FRIEND_FOLLOWING = "following"
    const val TYPE_FRIEND_FOLLOWER = "follower"



    const val TYPE_USER_ACTION_ADD_FRIEND = "add_friend"
    const val TYPE_USER_ACTION_REMOVE_FRIEND = "add_friend"
    const val TYPE_USER_ACTION_CANCEL_FRIEND_REQUEST = "add_friend"
    const val TYPE_USER_ACTION_REMOVE_FROM_SUGGESTIONS = "remove from suggestion"
    const val TYPE_USER_ACTION_ACCEPT_FRIEND_REQUEST = "Accept"
    const val TYPE_USER_ACTION_DELETE_FRIEND_REQUEST = "delete"
    const val TYPE_USER_ACTION_FOLLOW = "follow"
    const val TYPE_USER_ACTION_UNFOLLOW = "unfollow"





    const val FRIEND_CONNECTION_STATUS_PENDING = "Pending"
    const val FRIEND_CONNECTION_STATUS_ACCEPT = "Accept"





    const val PARAMETER_EMAIL = "email"


    const val FILTER_NOTIFICATIONS = "com.stalmateuser"
    const val LOCATION_NOTIFICATION_BROADCAST = "com.stalmateuser"
    const val CHANNEL_ID = "com.stalmateuser"
    const val FILTER_NOTIFICATION_BROADCAST = "com.stalmateuser"
    const val CHANNEL_NAME = "Notification"
    const val CHANNEL_DESCRIPTION = "Example Partner Notifications"





    const val NOTIFICATION_TYPE_FRIEND_REQUEST_ACCEPTED = "friendRequestAccepted"
    const val NOTIFICATION_TYPE_NEW_FOLLOWER_REQUEST = "newFollowerRequest"
    const val NOTIFICATION_TYPE_NEW_FRIEND_REQUEST = "newFriendRequest"
    const val NOTIFICATION_TYPE_FOLLOWER_REQUESTED_ACCEPTED = "followRequestAccepted"



    const val ACCOUNT_TYPE: String = "com.stalmate.user"
    const val ACCOUNT_NAME: String = "SyncContacts"


    const val MESSAGE_LOC_API_EMPTY_RESULT = "Pin Location"

    const val ACTION_SYNC_COMPLETED: String = "ACTION_SYNC_COMPLETED"

}