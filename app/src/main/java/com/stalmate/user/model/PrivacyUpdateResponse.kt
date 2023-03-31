package com.stalmate.user.model

data class PrivacyUpdateResponse(
    var message: String? = "", // success !!
    var reponse: Reponse? = Reponse()
) {
    data class Reponse(
        var _id: String? = "", // 642453b2365d5c0bde4a8d2a
        var about: Int = 0, // 1
        var allow_others_to_find_me: Boolean = false, // true
        var block_contact: ArrayList<BlockedUsers>? = arrayListOf(),
        var groups: Int = 0, // 1
        var last_seen: Int = 0, // 1
        var prfile_photo: Int = 0, // 1
        var profile: Int = 0, // 1
        var read_receipts: Boolean = false, // true
        var story: Int = 0, // 1
        var user_id: String? = "", // 6422398ad7070c51811ff0f9
        var who_can_like_my_post: Int = 0, // 1
        var who_can_post_comment: Int = 0, // 1
        var who_can_see_email_address: Int = 0, // 1
        var who_can_see_my_future_post: Int = 0, // 1
        var who_can_see_people_page_list: Int = 0, // 1
        var who_can_see_phone_number: Int = 0, // 1
        var who_can_send_me_message: Int = 0, // 1
        var who_can_send_you_friend_request: Int = 0 // 1
    ) {
        data class BlockedUsers(
            var _id: String = "",
            var first_name: String = "",
            var last_name: String = "",
            var profile_img_1: String = ""
        )
    }
}