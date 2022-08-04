package com.slatmate.user.networking

import android.content.Context
import com.slatmate.user.model.ModelCategory
import com.slatmate.user.model.ModelFeed
import com.slatmate.user.model.ModelLanguage
import com.slatmate.user.model.ModelSuccess
import com.slatmate.user.utilities.Constants


import okhttp3.MultipartBody
import okhttp3.RequestBody
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