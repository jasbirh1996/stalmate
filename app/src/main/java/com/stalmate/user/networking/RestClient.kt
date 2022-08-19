package com.stalmate.user.networking

import android.util.Log
import com.google.gson.GsonBuilder
import com.stalmate.user.base.App
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.UrlFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

class RestClient private constructor() {
    var client: OkHttpClient? = null
    var retrofit: Retrofit? = null
     var mRestService: ApiInterface? = null

    /**
     * Here is the setup call for http service for this app. setup is done once in MyApplication class.
     * Step 1 : Logging is added using HttpLoggingInterceptor. Logging is removed from retrofit2 so
     * it has to be added as a part of OkHttp interceptor.
     * Step 2 : Build an OkHttpClient with logging interceptor.
     * Step 3 : Retrofit is build with baseUrl, okhttp client, Gson Converter factory for easy JSON to POJO conversion.
     */
    fun setup() {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
        // Should be used only in Debug Mode.
        if (true) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(if (true) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE) //// TODO: 21-07-2016
            builder.addInterceptor(interceptor)
        }
        val gson = GsonBuilder()
            .setLenient()
            .create()


        if (PrefManager.getInstance(App.getInstance())!!.keyIsLoggedIn){
            client =  (builder.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${PrefManager.getInstance(App.getInstance())!!.userDetailLogin.results[0].token}").build()
                chain.proceed(request)
            }.build())

        }else{
            client =  builder.build()
        }



        retrofit = Retrofit.Builder()
            .baseUrl(UrlFactory.baseUrl)
            .client(client!!) //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        mRestService = retrofit!!.create(ApiInterface::class.java)
    }

    companion object {
        val inst = RestClient()
    }
}