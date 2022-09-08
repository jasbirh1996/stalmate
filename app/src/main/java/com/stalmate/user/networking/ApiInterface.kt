package com.stalmate.user.networking

import android.content.Context
import com.stalmate.user.model.*
import com.stalmate.user.utilities.Constants

import com.slatmate.user.model.*
import com.stalmate.user.view.photoalbum.ModelAlbumCreateResponse
import com.stalmate.user.view.photoalbum.ModelPhotoResponse
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



    @POST(Constants.URL_PHOTO_ALBUM_PHOTO)
    fun getPhotoList(@Body map: HashMap<String, String>): Call<ModelPhotoResponse>


    @GET(Constants.url_language_list)
    fun getFeedList(): Call<ModelFeed>

    @POST(Constants.url_friend_list)
    fun getFriendList(@Body map: HashMap<String, String>): Call<ModelFriend>


    @POST(Constants.URL_SIGN_UP)
    fun setSignupDetails(@Body map: HashMap<String, String>): Call<ModelLoginResponse>


    @PATCH(Constants.URL_OTP)
    fun setOtpVerify(@Body map: HashMap<String, String>): Call<CommonModelResponse>


    @POST(Constants.URL_LOGIN)
    fun setLoginDetails(@Body map: HashMap<String, String>): Call<ModelLoginResponse>

    @POST(Constants.URL_PHOTO_ALBUM_NAME)
    fun setCreateAlbumDetails(@Body map: HashMap<String, String>): Call<ModelAlbumCreateResponse>


    @POST(Constants.url_send_friend_request)
    fun sendFriendRequest(@Body map: HashMap<String, String>): Call<ModelSuccess>


    @PUT(Constants.url_update_friend_request)
    fun updateFriendRequest(@Body map: HashMap<String, String>): Call<CommonModelResponse>


    @POST(Constants.url_remove_user_from_suggestions)
    fun removeUserFromSuggestions(@Body map: HashMap<String, String>): Call<CommonModelResponse>





    @POST(Constants.url_send_follower_request)
    fun requestBeFollower(@Body map: HashMap<String, String>): Call<ModelSuccess>



    @GET(Constants.GET_PROFILE_API)
    fun setProfileDetails(): Call<ModelUser>

    @POST(Constants.BLOCK_API)
    fun setBlock(@Body map: HashMap<String, String>): Call<CommonModelResponse>


    @GET(Constants.GET_OTHER_USER_PROFILE_API)
    fun getOtherUserProfileDetails(@Query("id_user") id_user :String): Call<ModelUser>

    @GET(Constants.URL_OTP_REGISTRATION)
    fun setOtpVerifyRegistration(@Query("email") email :String, @Query("otp") otp :String): Call<CommonModelResponse>

    @GET(Constants.URL_SEARCH_UNIVERCITY)
    fun setSearch(@Query("search") search :String): Call<ModelSearch>

    @GET(Constants.URL_SEARCH_BRACNCHLIST)
    fun setSearchBranch(@Query("search") search :String): Call<ModelSearch>

    @Multipart
    @PATCH(Constants.UPDATE_PROFILE_API)
    fun updateUserProfile(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("about") about: RequestBody,
        @Part("number") number: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("marital_status") maritalStatus: RequestBody,
        @Part("url") url: RequestBody,
       /* @Part("company") company: RequestBody,*/
        @Part("gender") gender: RequestBody,
        @Part("city") city: RequestBody,
        @Part("home_town") home_town: RequestBody,
    ): Call<CommonModelResponse>

    @Multipart
    @PATCH(Constants.UPDATE_PROFILE_API)
    fun updateUserProfileImage(
        @Part cover_img: MultipartBody.Part
    ): Call<CommonModelResponse>

    @GET(Constants.URL_PHOTO_ALBUM)
    fun getAlbumList(): Call<ModelAlbumsResponse>


/*
    @HTTP(method = "DELETE", path = "delete_post" ,hasBody = true)
    fun deletePost(@Header("accessToken") header: String ,@Body map: HashMap<String, String>) : Call<CommonResponse>

*/


}