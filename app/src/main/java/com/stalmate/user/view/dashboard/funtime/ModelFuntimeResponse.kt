package com.stalmate.user.view.dashboard.funtime

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stalmate.user.model.ModelLoginResponse
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.Serializable

@Parcelize
data class ModelFuntimeResponse(
    var message: String = "", // List successfully
    var results: ArrayList<ResultFuntime> = arrayListOf(),
    var status: Boolean = false // true
) : Parcelable

@Parcelize
data class ResultFuntime(
    var Created_date: String = "", // 3 Min ago ..
    var app_logo: String = "", // http://newastrology.teknikoglobal.com/images/i/app_logo.png
    var artist_name: String = "",
    var comment_count: Int = 0, // 0
    var comment_status: String = "", // on
    var `file`: String = "", // https://stalematebucket.s3.me-south-1.amazonaws.com/file_1682598160028.jpg
    var file_type: String = "", // image/jpeg
    var first_name: String = "", // loop
    var hastag: String = "",
    var id: String = "", // 644a6910371bafdb0030b0dd
    var isFollowing: String = "", // No
    var isLiked: String = "", // No
    var isSave: String = "", // No
    var is_my: String = "", // YES
    var last_name: String = "", // two
    var like_count: Int = 0, // 0
    var location: String = "", // ,
    var profile_img: String = "", // https://webservice.kulluu.com/images/user_img/profile_img_1682071578425.jpg
    var share_count: Int = 0, // 0
    var sound_file: String = "",
    var sound_id: String = "", // none
    var sound_image: String = "",
    var sound_name: String = "",
    var tag_id: String = "",
    var tag_user: ArrayList<TaggedUser> = arrayListOf(),
    var text: String = "", // Hi
    var thum_icon: String = "",
    var url: String = "", // https://webservice.kulluu.com
    var user_id: String = "", // 643955be4d8ac204ccd950cd
    @SerializedName("isDataUpdated")
    var isDataUpdated: Boolean = false,
    var topcomment: ArrayList<TopComment>? = arrayListOf()
) : Parcelable {
    @Parcelize
    data class TopComment(
        var comment: String,
        var comment_image: String? = null,
        var new_comment_image: String? = null,
        var comment_id: String,
        var Created_date: String,
        var Updated_date: String,
        var is_delete: String,
        var _id: String,
        var funtime_id: String,
        var user_id: ModelLoginResponse.Results? = null,
        var __v: String
    ) : Parcelable
}

@Parcelize
data class TaggedUser(
    var _id: String,
    var first_name: String,
    var last_name: String,
    @SerializedName("profile_img_1")
    var profile_img_1: String = ""
) : Parcelable