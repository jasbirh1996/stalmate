package com.stalmate.user.model

data class OtpReceiveResponse(
    val message: String? = "", // success !!
    val reponse: Reponse? = Reponse()
) {
    data class Reponse(
        val Created_date: String? = "", // 2023-03-28 0:49:14
        val Updated_date: String? = "", // 2023-03-29 22:21:56
        val __v: Int? = 0, // 0
        val _id: String? = "", // 6422398ad7070c51811ff0f9
        val about: String? = "",
        val access_token: String? = "", // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjQyMjM5OGFkNzA3MGM1MTgxMWZmMGY5IiwiaXNfdG9rZW5fdmFsaWRlIjoxLCJpYXQiOjE2ODA1NzM4NTZ9.IvlwpXe5FPkhf3nKn4riQytNHbay-oKyc4FJUpOa1Hc
        val branch_id: String? = "",
        val category_id: String? = "",
        val city: String? = "",
        val company: String? = "",
        val country: String? = "",
        val countrycode: String? = "",
        val cover_img_1: String? = "", // https://api-gateway.suzero.co/auth_service/images/user_img/cover_img_1680128516309.jpg
        val deviceId: String? = "",
        val deviceToken: String? = "", // ckBWhL3MTralFAh6SV84qu:APA91bEfap0iJi5PdcFjV86BufYZvxIJVGOjj5qOGfIdxmjoIqVOLILKw1t3gwreFqfyNpcUto8_dvXMQXepZ9fN40zGX0HP4pJzsMH6tjCD6u8jlm-9_PEpyY_rCLq5i2kB1EHYu5WL
        val dob: String? = "", // 1995-September-1
        val email: String? = "", // amankumar.007@gmail.com
        val first_name: String? = "", // Aman
        val gender: String? = "", // Male
        val home_town: String? = "",
        val is_delete: String? = "", // 0
        val language: String? = "",
        val last_name: String? = "", // Kumar
        val marital_status: String? = "", // Single
        val number: String? = "", // 8791344333
        val otp: String? = "", // 2504
        val otp_send_time: String? = "", // 1680579956260
        val password: String? = "", // $2b$10$nOp0lE5X1xr67Bu6XQHvuel/fin7bzh3clvIemtcozApnS2zpkoEu
        val profile_img_1: String? = "", // https://api-gateway.suzero.co/auth_service/images/user_img/profile_img_1680128016665.jpg
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
        val who_can_like_my_post: String? = "",
        val who_can_post_comment: String? = "",
        val who_can_see_email: String? = "",
        val who_can_see_my_future_post: String? = "",
        val who_can_see_number: String? = "",
        val who_can_see_people_page: String? = "",
        val who_can_send_me_message: String? = "",
        val who_can_send_you_friendrequest: String? = ""
    )
}