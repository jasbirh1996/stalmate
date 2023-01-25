package com.stalmate.user.base

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.net.http.HttpResponseCache
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.stalmate.user.modules.reels.player.Constants.Companion.simpleCache
import com.stalmate.user.networking.RestClient
import okhttp3.CacheControl
import java.io.File

class App :Application(){

    var simpleAppCache: SimpleCache? = null


    companion object {
        private var appContext: Context? = null
        private var gson: Gson? = null
        private var geocoder: Geocoder? = null
        private var applicationInstance: App? = null
        fun getInstance(): App {
            return applicationInstance!!
        }
    }
    private val TAG = "appp"
    var firebaseToken: String? = null

    override
    fun onCreate() {
        super.onCreate()
        applicationInstance = this
        appContext = this

        setUpForPreCaching()













        RestClient.inst.setup()
        gson = Gson()
        geocoder = Geocoder(this)
        FirebaseApp.initializeApp(applicationInstance!!)
        // Initialize the SDK before executing any other operations,
        /*      FacebookSdk.sdkInitialize(getApplicationContext());
              AppEventsLogger.activateApp(this);
   */
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                firebaseToken = token
                // Log and toast
                Log.d(TAG, token!!)
            })
    }



    @JvmName("getFirebaseToken1")
    fun getFirebaseToken(): String? {
        return firebaseToken
    }


    public override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //  MultiDex.install(this);
    }

    fun getGson(): Gson? {
        return gson
    }

    fun getGeoCoder(): Geocoder? {
        return geocoder
    }

/*    public String getAccessToken() {
        try {
            if (PrefManager.getInstance(appContext).getUserDetail() != null) {
                return PrefManager.getInstance(appContext).getUserDetail().getUser().getSession_token();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    fun setupApis(){
        RestClient.inst.setup()
    }

    // check how much memory is available for cache video
    fun freeMemory() {
        try {
            val dir = cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }


    // delete the cache if it is full
    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }


    fun setUpForPreCaching() {

        val exoPlayerCacheSize = 50 * 1024 * 1024.toLong()// Set the size of cache for video
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: DatabaseProvider? = null


        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        }

        if (exoDatabaseProvider == null) {
            exoDatabaseProvider = StandaloneDatabaseProvider(this)
        }

        if (simpleCache == null) {
            val cache: File = File(cacheDir, "Video_Cache")
            if (!cache.exists()) {
                cache.mkdirs()
            }
          Handler(Looper.myLooper()!!).post {
              simpleCache = SimpleCache(cache, leastRecentlyUsedCacheEvictor, exoDatabaseProvider!!)
              simpleAppCache= simpleCache
          }
        }else{
            if (simpleCache!!.cacheSpace >= 400207768) {
                freeMemory()
            }
        }

    }


}