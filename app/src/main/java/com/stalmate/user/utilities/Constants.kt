package com.stalmate.user.utilities

object Constants {
    const val url_category_list = "/auth_api/category_list"
    const val url_language_list = "/auth_api/language_list"
    const val URL_INTREST_LIST = "/auth_api/category_list"
    const val URL_SIGN_UP = "/auth_api/user_register"
    const val URL_EMAIL_CHECK = "/auth_api/email_number_check"


    const val URL_LOGIN = "/auth_api/user_login"
    const val URL_UPDATE_ABOUT = "/auth_api/update_about"
    const val URL_OTP = "/auth_api/update_password"
    const val URL_OTP_REGISTRATION = "/auth_api/otp_register_process"
    const val URL_NUMBER_VERIFY_UPDATE = "/auth_api/update_number"
    const val URL_CATEGORY_LIST = "/friend_api/friend_category_list"
    const val URLFUNTIME_LIST = "/funtime_api/get_funtime_list"
    const val URL_CREATE_ROOM = "/friend_api/group_create"


    const val URLMY_FUNTIME_LIST = "/funtime_api/get_my_funtime_list"


    const val URLFUNTIME_DETAIL_LIST = "/funtime_api/get_funtime_detail"
    const val URL_FUNTIME_MUSIC_LIST = "/funtime_api/get_sound_api"
    const val URL_FUNTIME_LIKE_UNLIKE = "/funtime_api/like_unlike_funtime"
    const val URL_SAVE_UNSAVE_FUNTIME = "/funtime_api/savefuntime"


    const val URL_GET_SAVED_FUNTIME_MUSIC = "/funtime_api/get_save_sound_api"
    const val URL_GET_SAVED_FUNTIME_REELS = "/funtime_api/get_savefuntime_list"
    const val URL_FUNTIME_LIST_BY_AUDIO = "/funtime_api/get_song_funtime_list"
    const val URL_SAVE_UNSAVE_MUISIC = "/funtime_api/savesound"

    const val url_friend_list = "/friend_api/get_friend_list"
    const val URL_FUNTIME_UPDATE = "/funtime_api/funtime_update"


    const val url_send_friend_request = "/friend_api/send_friend_request"
    const val url_update_friend_request = "/friend_api/update_friend_request"
    const val url_remove_user_from_suggestions = "/friend_api/remove_suggestions"
    const val URL_SHARE_FUNTIME_WITH_FRIEND = "/funtime_api/funtime_share"
    const val URL_PHOTO_ALBUM = "/auth_api/get_albums"
    const val URL_PHOTO_ALBUM_NAME = "/auth_api/add_update_albums"
    const val URL_PHOTO_ALBUM_PHOTO = "/auth_api/get_albums_img"
    const val URL_PHOTO_INDEX = "/auth_api/get_profile_image_list"

    const val URL_SEARCH_UNIVERCITY = "/auth_api/university_list"
    const val URL_SEARCH_BRACNCHLIST = "/auth_api/branch_list"
    const val URL_GLOBAL_SEARCH = "/friend_api/stalmate_search"//"/friend_api/search_list"

    const val URL_EDUCATION_ADD = "/auth_api/add_update_usereducation"
    const val URL_PROFESSION_ADD = "/auth_api/add_update_userwork"
    const val URL_UPDATE_FRIEND_CATEGORY = "/friend_api/add_update_friend_category"


    const val URL_COMMENT_LIST = "/funtime_api/get_funtime_comment"
    const val URL_REPLY_LIST = "/funtime_api/get_funtime_comment"
    const val URL_ADD_COMMENT = "/funtime_api/comment_funtime"
    const val URL_LIKE_COMMENT = "/funtime_api/like_unlike_funtime_comment"


    const val url_send_follower_request = "/friend_api/send_follower_request"
    const val GET_PROFILE_API = "/auth_api/get_profile_data"
    const val GET_OTHER_USER_PROFILE_API = "/auth_api/get_profile_user"
    const val UPDATE_PROFILE_API = "/auth_api/profile_update"
    const val UPDATE_PROFILE_API_FILE = "/auth_api/profile_update_file"
    const val UPLOADE_ALBUM_IMAGE_API_FILE = "/auth_api/add_albums_img"
    const val URL_REPORT_FUNTIME = "/funtime_api/funtime_report"
    const val START_DATE = "01"
    const val ADD_REEL = "/funtime_api/add_funtime"

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
    const val PRIVACY_TYPE_PUBLIC: String = "Public"
    const val PRIVACY_TYPE_PRIVATE: String = "Private"
    const val PRIVACY_TYPE_MY_FOLLOWER: String = "My Followers"
    const val PRIVACY_TYPE_SPECIFIC: String = "Specific Friends"


    const val SETTING_TYPE_ACCOUNT: String = "Account Setting"
    const val SETTING_TYPE_CHAT: String = "Chat Setting"
    const val SETTING_TYPE_APP: String = "App Setting"
    const val SETTING_TYPE_NOTIFICATION: String = "Notification Setting"
    const val SETTING_TYPE_ABOUT_US: String = "About Us"
    const val SETTING_TYPE_LEGAL: String = "Legal"


    const val SETTING_ACCOUNT_PRIVACY: String = "Privacy"
    const val SETTING_CHANGE_NUMBER: String = "Change Number"
    const val SETTING_CHANGE_PASSWORD: String = "Change Password"
    const val SETTING_BLOCKED_CONTACTS: String = "Blocked Contacts"
    const val SETTING_DELETEACCOUNT: String = "Delete my account"

    const val BLOCK_API = "/friend_api/block_user"
    const val URL_BLOCKED_LIST = "/friend_api/get_block_user_list"

    //M8
    //const val changeNumberApi = "setting_api/change_number"
    const val changePasswordApi = "setting_api/change_password"

    const val blockUserList = "setting_api/block_user_get"
    const val BLOCK_API_M8 = "setting_api/block_user_stalmate"

    const val accountSettings = "setting_api/myaccount_setting_get"
    const val accountSettingsUpdate = "setting_api/myaccount_setting_update"
    const val deleteMyAccount = "setting_api/myaccount_delete"
    const val contactUs = "setting_api/contact_us"
    const val SendOtp = "auth_api/send_otp"
    const val VerifyOTP = "auth_api/verify_otp"
    const val UpdatePrivacy = "setting_api/privacy"
    const val GetPrivacy = "setting_api/privacy_get"
    const val UpdateLanguageAndCountry = "setting_api/general_setting"

    const val ReportProblem = "/setting_api/file_report"
    const val getFileReport = "setting_api/get_file_report"

    //Feedbacks
    const val saveAsDraft = "funtime_api/add_draft"
    const val commentDisable = "funtime_api/add_comment_disable"
}