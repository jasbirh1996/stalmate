package com.stalmate.user.networking

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
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        // Should be used only in Debug Mode.
        if (true) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(interceptor)
        }
        val gson = GsonBuilder()
            .setLenient()
            .create()
        // val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create() //now we can use our own variable apart from respones

        client = if (PrefManager.getInstance(App.getInstance())!!.keyIsLoggedIn) {
            (builder.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader(
                    "Authorization",
                    "Bearer ${PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token}"
                ).build()
                chain.proceed(request)
            }.build())
        } else {
            builder.build()
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