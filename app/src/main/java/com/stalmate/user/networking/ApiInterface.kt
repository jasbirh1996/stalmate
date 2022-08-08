package com.stalmate.user.networking

import android.content.Context
import com.stalmate.user.model.*
import com.stalmate.user.utilities.Constants

import com.slatmate.user.model.*

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("signup")
    fun setSignupDetails(@Body map: String, map1: HashMap<String, String>): Call<ModelFeed>

    @GET(Constants.url_category_list)
    fun getCategorList(): Call<ModelCategory>

    @GET(Constants.url_language_list)
    fun getLanguageList(): Call<ModelLanguage>


    @GET(Constants.url_language_list)
    fun getFeedList(): Call<ModelFeed>
    @GET(Constants.url_language_list)
    fun getFriendList(): Call<ModelFriend>


    @POST("auth_service/auth_api/user_register")
    fun setSignupDetails(@Body map: HashMap<String, String>): Call<ModelRegisterResponse>


    @PATCH("auth_service/auth_api/update_password")
    fun setOtpVerify(@Body map: HashMap<String, String>): Call<CommonModelResponse>


/*
    @HTTP(method = "DELETE", path = "delete_post" ,hasBody = true)
    fun deletePost(@Header("accessToken") header: String ,@Body map: HashMap<String, String>) : Call<CommonResponse>

*/

    companion object Factory {
        @Volatile
        private var instance: ApiInterface? = null
        fun init(context: Context): ApiInterface {
            return (instance ?: synchronized(this) {
                instance ?: RestClient.inst.mRestService
            })!!
        }
    }
}