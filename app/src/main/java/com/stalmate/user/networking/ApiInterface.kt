package com.stalmate.user.networking

import android.content.Context
import android.database.Observable
import com.slatmate.user.model.*
import com.stalmate.user.model.*
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.dashboard.Friend.categorymodel.AddCategoryModel
import com.stalmate.user.view.dashboard.Friend.categorymodel.ModelCategoryResponse
import com.stalmate.user.view.dashboard.funtime.ModelFuntimeLikeResponse
import com.stalmate.user.view.dashboard.funtime.ModelFuntimeResponse
import com.stalmate.user.view.dashboard.funtime.ModelMusicListResponse
import com.stalmate.user.view.photoalbum.ModelAlbumCreateResponse
import com.stalmate.user.view.photoalbum.ModelPhotoResponse
import com.stalmate.user.view.photoalbum.imageshowindex.ModelPhotoIndexDataResponse
import com.stalmate.user.view.singlesearch.ModelSearch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    companion object Factory {
        @Volatile
        private var instance: ApiInterface? = null
        fun init(context: Context): ApiInterface {
            return (instance ?: synchronized(this) {
                instance ?: RestClient.inst.mRestService
            })!!
        }
    }

    @POST("signup")
    fun setSignupDetails(@Body map: String, map1: HashMap<String, String>): Call<ModelFeed>

    @GET(Constants.url_category_list)
    fun getCategorList(): Call<ModelCategory>

    @GET(Constants.url_language_list)
    fun getLanguageList(): Call<ModelLanguageResponse>

    @POST(Constants.URLFUNTIME_LIST)
    fun getFuntimeList(@Body map: HashMap<String, String>): Call<ModelFuntimeResponse>

    @POST(Constants.URL_CREATE_ROOM)
    fun createroomId(@Body map: HashMap<String, String>): Call<ModelRoom>


    @POST(Constants.URLMY_FUNTIME_LIST)
    fun getMyFuntimeList(@Body map: HashMap<String, String>): Call<ModelFuntimeResponse>


    @POST(Constants.URLFUNTIME_DETAIL_LIST)
    fun getFuntimeDetailListList(@Body map: HashMap<String, String>): Call<ModelFuntimeResponse>


    @PATCH(Constants.URL_FUNTIME_LIKE_UNLIKE)
    fun getFuntimeLikeUnlike(@Body map: HashMap<String, String>): Call<ModelFuntimeLikeResponse>


    @PATCH(Constants.URL_SAVE_UNSAVE_FUNTIME)
    fun saveUnsaveFuntime(@Body map: HashMap<String, String>): Call<CommonModelResponse>


    @POST(Constants.URL_GET_SAVED_FUNTIME_MUSIC)
    fun getSavedFuntimMusic(@Body map: HashMap<String, String>): Call<ModelMusicListResponse>


    @POST(Constants.URL_GET_SAVED_FUNTIME_REELS)
    fun getSavedFuntimReels(@Body map: HashMap<String, String>): Call<ModelFuntimeResponse>

    @PATCH(Constants.URL_SAVE_UNSAVE_MUISIC)
    fun saveUnsaveMusic(@Body map: HashMap<String, String>): Call<CommonModelResponse>


    @POST(Constants.URL_FUNTIME_MUSIC_LIST)
    fun getFuntimeMusicList(@Body map: HashMap<String, String>): Call<ModelMusicListResponse>

    @GET(Constants.URL_CATEGORY_LIST)
    fun getCategoryList(): Call<ModelCategoryResponse>

    @POST(Constants.URL_FUNTIME_LIST_BY_AUDIO)
    fun get_song_funtime_list(@Body map: HashMap<String, String>): Call<ModelFuntimeResponse>

    @POST(Constants.URL_PHOTO_ALBUM_PHOTO)
    fun getPhotoList(@Body map: HashMap<String, String>): Call<ModelPhotoResponse>

    @POST(Constants.URL_PHOTO_INDEX)
    fun getPhotoIndexList(@Body map: HashMap<String, String>): Call<ModelPhotoIndexDataResponse>

    @GET(Constants.url_language_list)
    fun getFeedList(): Call<ModelFeed>

    @POST(Constants.url_friend_list)
    fun getFriendList(@Body map: HashMap<String, String>): Call<ModelFriend>

    @POST(Constants.URL_FUNTIME_UPDATE)
    fun funtimeUpdate(@Body map: HashMap<String, String>): Call<ModelSuccess>

    @POST(Constants.URL_SIGN_UP)
    fun setSignupDetails(@Body map: HashMap<String, String>): Call<ModelLoginResponse>

    @POST(Constants.URL_EMAIL_CHECK)
    fun checkIfOldEmail(@Body map: HashMap<String, String>): Call<ModelSuccess>

    @PATCH(Constants.URL_OTP)
    fun setOtpVerify(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @POST(Constants.URL_BLOCKED_LIST)
    fun getBlockedList(@Body map: HashMap<String, String>): Call<ModelBlockedUser>

    @POST(Constants.URL_LOGIN)
    fun setLoginDetails(@Body map: HashMap<String, String>): Call<ModelLoginResponse>

    @POST(Constants.URL_NUMBER_VERIFY_UPDATE)
    fun setOtpNumberVerify(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @PATCH(Constants.URL_UPDATE_ABOUT)
    fun setUpdateAbout(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @POST(Constants.URL_EDUCATION_ADD)
    fun setEducationAddDetails(@Body map: HashMap<String, String>): Call<ModelCommonAddEducationAndProfessionResponse>

    @POST(Constants.URL_UPDATE_FRIEND_CATEGORY)
    fun setUpdateFriendCategoryDetails(@Body map: HashMap<String, String>): Call<AddCategoryModel>

    @POST(Constants.URL_PROFESSION_ADD)
    fun setProfessionAddDetails(@Body map: HashMap<String, String>): Call<ModelCommonAddEducationAndProfessionResponse>

    @POST(Constants.URL_PHOTO_ALBUM_NAME)
    fun setCreateAlbumDetails(@Body map: HashMap<String, String>): Call<ModelAlbumCreateResponse>

    @POST(Constants.url_send_friend_request)
    fun sendFriendRequest(@Body map: HashMap<String, String>): Call<ModelSuccess>

    @PUT(Constants.url_update_friend_request)
    fun updateFriendRequest(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @POST(Constants.url_remove_user_from_suggestions)
    fun removeUserFromSuggestions(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @POST(Constants.URL_SHARE_FUNTIME_WITH_FRIEND)
    fun shareWithFriend(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @POST(Constants.url_send_follower_request)
    fun requestBeFollower(@Body map: HashMap<String, String>): Call<ModelSuccess>

    @GET(Constants.GET_PROFILE_API)
    fun setProfileDetails(): Call<ModelUser>

//    @POST(Constants.BLOCK_API)
//    fun setBlock(@Body map: HashMap<String, String>): Call<CommonModelResponse>

    @FormUrlEncoded
    @POST(Constants.BLOCK_API_M8)
    fun setBlock(
        @Header("access_token") access_token: String,
        @Field("_id") _id: String
    ): Call<CommonModelResponse>

    @GET(Constants.GET_OTHER_USER_PROFILE_API)
    fun getOtherUserProfileDetails(@Query("id_user") id_user: String): Call<ModelUser>

    @GET(Constants.URL_OTP_REGISTRATION)
    fun setOtpVerifyRegistration(
        @Query("email") email: String, @Query("otp") otp: String
    ): Call<CommonModelResponse>

    @GET(Constants.URL_SEARCH_UNIVERCITY)
    fun setSearch(@Query("search") search: String): Call<ModelSearch>

    @GET(Constants.URL_SEARCH_BRACNCHLIST)
    fun setSearchBranch(@Query("search") search: String): Call<ModelSearch>


    @POST(Constants.URL_GLOBAL_SEARCH)
    fun getGlobalSearch(@Body map: HashMap<String, String>): Call<ModelGlobalSearch>


    @POST(Constants.URL_COMMENT_LIST)
    fun getCommentList(@Body map: HashMap<String, String>): Call<ModelGetComment>


    @POST(Constants.URL_REPLY_LIST)
    fun getReplyList(@Body map: HashMap<String, String>): Call<ModelGetComment>

    @PATCH(Constants.URL_ADD_COMMENT)
    fun addComment(@Body map: HashMap<String, String>): Call<ModelComment>

    @PATCH(Constants.URL_LIKE_COMMENT)
    fun likeComment(@Body map: HashMap<String, String>): Call<ModelSuccess>


    @Multipart
    @POST(Constants.UPDATE_PROFILE_API)
    fun updateUserProfile(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("about") about: RequestBody,
        @Part("countrycode") countrycode: RequestBody,
        @Part("number") number: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("marital_status") maritalStatus: RequestBody,
        @Part("url") url: RequestBody,
        @Part("company") company: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("city") city: RequestBody,
        @Part("home_town") home_town: RequestBody
    ): Call<CommonModelResponse>

    @Multipart
    @POST(Constants.UPDATE_PROFILE_API_FILE)
    fun updateUserProfileImage(@Part cover_img: MultipartBody.Part): Call<CommonModelResponse>


    @Multipart
    @POST(Constants.UPLOADE_ALBUM_IMAGE_API_FILE)
    fun addAlbumImage(
        @Part cover_img: MultipartBody.Part,
        @Part("album_id") firstName: RequestBody,
    ): Call<CommonModelResponse>


    @Multipart
    @POST(Constants.URL_REPORT_FUNTIME)
    fun reportFuntime(
        @Part file: MultipartBody.Part? = null,
        @Part("funtime_id") funtimeId: RequestBody,
        @Part("category") category: RequestBody,
        @Part("report_reason") report_reason: RequestBody,
        @Part("detailed_reason") detailed_reason: RequestBody,
    ): Call<CommonModelResponse>


    @GET(Constants.URL_PHOTO_ALBUM)
    fun getAlbumList(): Call<ModelAlbumsResponse>


    @Multipart
    @POST(Constants.ADD_REEL)
    fun postReel(
        @Part file: MultipartBody.Part?,
        @Part cover_image: MultipartBody.Part?,
        @Part("file_type") file_type: RequestBody,
        @Part("text") text: RequestBody,
        @Part("tag_id") tag_id: RequestBody,
        @Part("sound_id") sound_id: RequestBody,
        @Part("location") location: RequestBody,
        @Part("privacy") privacy: RequestBody,
        @Part("privacy_data") privacy_data: RequestBody,
        @Part("deviceId") deviceId: RequestBody,
        @Part("deviceToken") deviceToken: RequestBody
    ): Call<CommonModelResponse>

    //M8
//    @FormUrlEncoded
//    @POST(Constants.changeNumberApi)
//    fun changeNumberApi(
//        @Header("access_token") access_token: StrictMath,
//        @Field("number_old") number_old: String,
//        @Field("number_new") number_new: String,
//        @Field("notify_contact") notify_contact: Boolean,
//        @Field("number_c_code") number_c_code: String
//    )

    @FormUrlEncoded
    @POST(Constants.changePasswordApi)
    fun changePasswordApi(
        @Header("access_token") access_token: String,
        @Field("password_old") password_old: String,
        @Field("password_new") password_new: String,
        @Field("password_confirm") password_confirm: String
    ): Call<ChangePasswordResponse>

//    @GET(Constants.blockUserList)
//    fun blockUserList(@Header("access_token") access_token: String): Observable<BlockUserListResponse>

    @GET(Constants.accountSettings)
    fun accountSettingsGet(@Header("access_token") access_token: String): Call<AccountSettingGetAndPut>

    @PUT(Constants.accountSettingsUpdate)
    fun accountSettingsUpdate(
        @Header("access_token") access_token: String,
        @Body requestBody: AccountSettingGetAndPut.Reponse
    ): Call<AccountSettingGetAndPut>

    @GET(Constants.GetPrivacy)
    fun privacyGet(@Header("access_token") access_token: String): Call<PrivacyUpdateResponse>

    @FormUrlEncoded
    @POST(Constants.UpdatePrivacy)
    fun privacyUpdate(
        @Header("access_token") access_token: String,
        @Field("allow_others_to_find_me") allow_others_to_find_me: Boolean,
        @Field("profile") profile: Int,
        @Field("last_seen") last_seen: Int,
        @Field("prfile_photo") prfile_photo: Int,
        @Field("about") about: Int,
        @Field("read_receipts") read_receipts: Boolean,
        @Field("story") story: Int,
        @Field("groups") groups: Int,
        @Field("block_contact") block_contact: String,
        @Field("who_can_like_my_post") who_can_like_my_post: Int,
        @Field("who_can_post_comment") who_can_post_comment: Int,
        @Field("who_can_send_me_message") who_can_send_me_message: Int,
        @Field("who_can_see_my_future_post") who_can_see_my_future_post: Int,
        @Field("who_can_see_people_page_list") who_can_see_people_page_list: Int,
        @Field("who_can_send_you_friend_request") who_can_send_you_friend_request: Int,
        @Field("who_can_see_email_address") who_can_see_email_address: Int,
        @Field("who_can_see_phone_number") who_can_see_phone_number: Int
    ): Call<PrivacyUpdateResponse>

    @FormUrlEncoded
    @POST(Constants.deleteMyAccount)
    fun deleteMyAccount(
        @Header("access_token") access_token: String,
        @Field("number") number: String,
        @Field("email") email: String,
        @Field("number_c_code") number_c_code: String,
        @Field("otp") otp: String,
        @Field("notify_contact") notify_contact: Boolean
    ): Call<ChangePasswordResponse>

    @POST(Constants.SendOtp)
    fun sendOtp(@Header("access_token") access_token: String): Call<OtpReceiveResponse>

    @FormUrlEncoded
    @POST(Constants.contactUs)
    fun contactUs(
        @Header("access_token") access_token: String,
        @Field("category") category: String,
        @Field("topic") topic: String,
        @Field("message") message: String,
    )

    @FormUrlEncoded
    @POST(Constants.commentDisable)
    fun commentDisable(
        @Header("access_token") access_token: String, @Field("funtime_id") funtime_id: String
    )

    @FormUrlEncoded
    @POST(Constants.UpdateLanguageAndCountry)
    fun updateLanguageAndCountry(
        @Header("access_token") access_token: String,
        @Field("country") country: String,
        @Field("language") language: String
    ): Call<CommonModelResponse>

    @Multipart
    @POST(Constants.saveAsDraft)
    fun saveAsDraft(
        @Part file: MultipartBody.Part?,
        @Part cover_image: MultipartBody.Part?,
        @Part("file_type") file_type: RequestBody,
        @Part("text") text: RequestBody,
        @Part("tag_id") tag_id: RequestBody,
        @Part("sound_id") sound_id: RequestBody,
        @Part("location") location: RequestBody,
        @Part("privacy") privacy: RequestBody,
        @Part("privacy_data") privacy_data: RequestBody,
        @Part("deviceId") deviceId: RequestBody,
        @Part("deviceToken") deviceToken: RequestBody
    ): Call<CommonModelResponse>
}