package com.slatmate.user.model

data class CommonModelResponse(
    val message: String? = null,
    val status: Boolean? = null,
    val results: String = ""
)

data class FunTimeAddResponse(
    val message: String? = "", // Save successfully
    val results: Results? = null,
    val status: Boolean? = false // true
) {
    data class Results(
        val Created_date: String? = "", // 2023-04-27 10:42:27
        val Created_date_1: String? = "", // Thu Apr 27 2023 10:42:27 GMT+0000 (Coordinated Universal Time)
        val Updated_date: String? = "",
        val __v: Int? = 0, // 0
        val _id: String? = "", // 644a5193371bafdb00309134
        val comment_status: String? = "",
        val cover_image: String? = "",
        val deviceId: String? = "", // 12345
        val deviceToken: String? = "", // 54321
        val `file`: String? = "", // https://stalematebucket.s3.me-south-1.amazonaws.com/file_1682592142782.mp4
        val file_type: String? = "", // null
        val hastag: String? = "",
        val is_delete: String? = "", // 0
        val is_draft: Boolean? = false, // false
        val location: String? = "", // ,
        val privacy: String? = "", // Public
        val privacy_data: String? = "", // none
        val sound_id: String? = "", // none
        val status: String? = "", // Active
        val tag_id: String? = "",
        val text: String? = "", // Hi
        val thum_icon: String? = "",
        val user_id: String? = "" // 643955be4d8ac204ccd950cd
    )
}