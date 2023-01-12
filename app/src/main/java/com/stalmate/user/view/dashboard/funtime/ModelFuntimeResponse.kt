package com.stalmate.user.view.dashboard.funtime

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ModelFuntimeResponse(
    val message: String,
    val results: ArrayList<ResultFuntime>,
    val status: Boolean
)

@Parcelize
data class ResultFuntime(
    val Created_date: String,
    var comment_count: Int,
    val file: String,
    val file_type: String,
    val first_name: String,
    val hastag: String,
    val id: String,
    val last_name: String,
    var like_count: Int,
    val location: String,
    val profile_img: String,
    var share_count: Int,
    val sound_file: String,
    val sound_name: String,
    val sound_image: String? = null,
    val artist_name: String,
    val tag_id: String,
    var isLiked: String? = null,
    val text: String,
    val sound_id: String?,
    val is_my: String?,
    val url: String,
    val user_id: String? = null,
    var isSave: String? = null,
    var isFollowing: String? = null,
    @SerializedName("isDataUpdated")
    var isDataUpdated: Boolean? = false,
    var tag_user: ArrayList<TaggedUser>
) : Parcelable


@Parcelize
data class TaggedUser(
    val _id: String,
    val first_name: String,
    val last_name: String,
    @SerializedName("profile_img_1")
    val profile_img_1: String?=""
) : Parcelable