package com.stalmate.user.base

import android.app.Application
import android.content.Context
import android.location.Geocoder
import com.google.gson.Gson
import com.stalmate.user.networking.RestClient


class App :Application(){
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


          RestClient.inst.setup()
            /*         gson = Gson()
                   geocoder = Geocoder(this)
                 FirebaseApp.initializeApp(applicationInstance!!)
                       // Initialize the SDK before executing any other operations,
                       FacebookSdk.sdkInitialize(getApplicationContext());
                       AppEventsLogger.activateApp(this);
                   RestClient.getInst().setup()
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
                       })*/
    }



    @JvmName("getFirebaseToken1")
    fun getFirebaseToken(): String? {
        return firebaseToken
    }


        override
    protected fun attachBaseContext(base: Context?) {
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
}