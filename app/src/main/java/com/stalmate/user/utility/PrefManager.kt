package com.stalmate.user.utilities;

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.slatmate.user.model.ModelRegisterResponse
import com.stalmate.user.model.ModelLoginResponse


class PrefManager(private val context: Context) {
    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    var PRIVATE_MODE = 0



    var keyIsLoggedIn: Boolean
        get() = getBooleanValue(KEY_IS_LOGGED_IN)
        set(value) {
            setBooleanValue(KEY_IS_LOGGED_IN, value)
        }
    var keyRememberMe: Boolean
        get() = getBooleanValue(KEY_REMEMBER_ME)
        set(value) {
            setBooleanValue(KEY_REMEMBER_ME, value)
        }

    fun setKeyIsOldUser(value: Boolean) {
        setBooleanValue(KEY_IS_OLD_USER, value)
    }

    val keyIsOldusere: Boolean
        get() = getBooleanValue(KEY_IS_OLD_USER)

    var userDetail: ModelRegisterResponse
        get() {
            val gson = Gson()
            val json = preferences.getString(KEY_USER_DETAILS, "")
            return gson.fromJson(json, ModelRegisterResponse::class.java)
        }
        set(user) {
            val gson = Gson()
            val json: String = gson.toJson(user)
            editor.putString(KEY_USER_DETAILS, json)
            editor.commit()
        }


    var userDetailLogin: ModelLoginResponse
        get() {
            val gson = Gson()
            val json = preferences.getString(KEY_USER_DETAILS, "")
            return gson.fromJson(json, ModelLoginResponse::class.java)
        }
        set(user) {
            val gson = Gson()
            val json: String = gson.toJson(user)
            editor.putString(KEY_USER_DETAILS, json)
            editor.commit()
        }

    /*

    public void setCurrentWeddingDetail(ModelWeddingList.DataBean modelWeddingList) {
        Gson gson = new Gson();
        String json = gson.toJson(modelWeddingList);
        editor.putString(KEY_CURRENT_WEDDING_DETAILS, json);
        editor.commit();
    }
    public ModelWeddingList.DataBean getCurrentWeddingDetail() {
        Gson gson = new Gson();
        String json = preferences.getString(KEY_CURRENT_WEDDING_DETAILS, "");
        return gson.fromJson(json, ModelWeddingList.DataBean.class);
    }

*/
    fun setUserLogin(isSignedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isSignedIn)
        editor.commit()
    }

    fun getBooleanValue(key: String?): Boolean {
        return preferences.getBoolean(key, false)
    }

    fun setBooleanValue(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBooleanValueOTP(key: String?): Boolean {
        return preferences.getBoolean(key, false)
    }

    fun setBooleanValueOTP(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }


    fun getStringValue(key: String?): String? {
        return preferences.getString(key, "")
    }

    fun setStringValue(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun setLongitudeValue(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getLongitudeValue(key: String?): String? {
        return preferences.getString(key, null)
    }

    fun clearPrefs() {
        editor.clear()
        editor.commit()
    }

    fun removeFromPreference(key: String?) {
        editor.remove(key)
        editor.commit()
    }

    companion object {
        private var instance: PrefManager? = null
        private const val PREF_NAME = "WedGuru"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_IS_LOGGED_IN = "is_Login"
        private const val KEY_IS_OLD_USER = "is_First_Time"
        private const val KEY_USER_DETAILS = "user"
        fun getInstance(context: Context): PrefManager? {
            if (instance == null) {
                instance = PrefManager(context)
            }
            return instance
        }
    }

    init {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences.edit()
    }
}