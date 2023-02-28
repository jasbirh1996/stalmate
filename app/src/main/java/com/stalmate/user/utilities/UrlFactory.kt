package com.stalmate.user.utilities

import com.stalmate.user.base.App
import java.util.*


object UrlFactory {
    private const val URL_DEV = "http://157.241.38.111:3000/"/*"https://api-gateway.suzero.co/"*/ // Development
    private const val URL_PROD = "http://157.241.38.111:3000/"/*"https://api-gateway.suzero.co/"*/ // Live
    private const val API_VERSION = "api/v0/"
    const val isModeDevelopment = true

    //return isModeDevelopment() ? URL_DEV : URL_PROD;
    val baseUrl: String
        get() = if (isModeDevelopment) {
            URL_DEV
        } else {
            URL_PROD
        }

    //return isModeDevelopment() ? URL_DEV : URL_PROD;

    val baseUrlWithApiVersioning: String
        get() = baseUrl + API_VERSION
    private const val kHeaderDevice = "deviceType"
    private const val kHeaderAccessToken = "SessionToken"
    private const val vHeaderDevice = "android"
    private const val kTimezone = "Timezone"
    private const val KDeviceToken = "deviceToken"
    fun generateUrl(urlSuffix: String): String {
        return baseUrl + urlSuffix
    }

    fun generateUrlWithVersion(urlSuffix: String): String {
        return baseUrl + API_VERSION + urlSuffix
    }

    /*       defaultHeaders.put("GuestToken", GuestPrefrenceManager.getInstance(App.getInstance()).getGuestToken());
        defaultHeaders.put("currentVersion", BuildConfig.VERSION_NAME);*/
    val defaultHeaders: HashMap<String, String>
        get() {

            val defaultHeaders = HashMap<String, String>()
            defaultHeaders["Content-Type"] = "application/json"
            defaultHeaders["Accept"] = "application/json"
            defaultHeaders[kHeaderDevice] = vHeaderDevice
            defaultHeaders[kTimezone] = timezone
            defaultHeaders["language"] = "english"
            defaultHeaders["AppKey"] = "Poru13feqwopto2qMpt43"

            /*       defaultHeaders.put("GuestToken", GuestPrefrenceManager.getInstance(App.getInstance()).getGuestToken());
            defaultHeaders.put("currentVersion", BuildConfig.VERSION_NAME);*/return defaultHeaders
        }
    val guestHeaders: HashMap<String, String>
        get() {
            val defaultHeaders = HashMap<String, String>()
            defaultHeaders["Content-Type"] = "application/json"
            defaultHeaders[kHeaderDevice] = vHeaderDevice
            defaultHeaders[kTimezone] = timezone
            defaultHeaders["language"] = "english"
            defaultHeaders["AppKey"] = "Poru13feqwopto2qMpt43"
            return defaultHeaders
        }

    //  appHeaders.put(kHeaderAccessToken, App.getInstance().getAccessToken());
    val appHeaders: HashMap<String, String>
        get() {
            defaultHeaders.put(kHeaderAccessToken, PrefManager.getInstance(App.getInstance())!!.userDetail.results[0].token)
            return  defaultHeaders
        }


    /*public static String getLanguage() {
        if (new LanguagePref(App.getInstance()).getLanguage()!=null && new LanguagePref(App.getInstance()).getLanguage().equalsIgnoreCase("ar")) {
            return new LanguagePref(App.getInstance()).getLanguage();
        } else {
            return "en";
        }
    }*/
    private val timezone: String
        private get() {
            val tz = TimeZone.getDefault()
            return tz.id + ""
        }
    /*    public static String getDeviceToken(){
              return  FirebaseInstanceId.getInstance().getToken();
          }*/
}