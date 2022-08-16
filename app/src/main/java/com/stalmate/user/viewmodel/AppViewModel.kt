package com.stalmate.user.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.stalmate.user.base.App
import com.stalmate.user.model.*
import com.stalmate.user.networking.ApiInterface



import com.slatmate.user.model.CommonModelResponse

import com.slatmate.user.model.ModelRegisterResponse


import retrofit2.Call
import retrofit2.Response

open class AppViewModel : ViewModel() {

    var apiInterface = ApiInterface.init(App.getInstance())


    var feedLiveData: LiveData<ModelFeed?> = MutableLiveData<ModelFeed?>()
    fun getFeedList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFeed?>()
        feedLiveData = temp
        getResult(temp, apiInterface.getFeedList())
    }


    var languageLiveData: LiveData<ModelLanguageResponse?> = MutableLiveData<ModelLanguageResponse?>()
    fun languageLiveData(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLanguageResponse?>()
        languageLiveData = temp
        getResult(temp, apiInterface.getLanguageList())
    }



    var friendLiveData: LiveData<ModelFriend?> = MutableLiveData<ModelFriend?>()
    fun getFriendList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFriend?>()
        friendLiveData = temp
        getResult(temp, apiInterface.getFriendList())
    }


    var updateFriendRequestLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun updateFriendRequest(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        updateFriendRequestLiveData = temp
        getResult(temp, apiInterface.updateFriendRequest(map))
    }



    var categoryLiveData: LiveData<ModelCategory?> = MutableLiveData<ModelCategory?>()
    fun getCategoryList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelCategory?>()
        categoryLiveData = temp
        getResult(temp, apiInterface.getCategorList())
    }

    var registerData: LiveData<ModelRegisterResponse?> = MutableLiveData<ModelRegisterResponse?>()
    fun registration(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelRegisterResponse?>()
        registerData = temp
        getResult(temp, apiInterface.setSignupDetails(map))

    }


    var loginData: LiveData<ModelLoginResponse?> = MutableLiveData<ModelLoginResponse?>()
    fun login(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLoginResponse?>()
        loginData = temp
        getResult(temp, apiInterface.setLoginDetails(map))
    }


    var otpVerifyData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun otpVerify(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        otpVerifyData = temp
        getResult(temp, apiInterface.setOtpVerify(map))
    }










    fun <T : Any> getResult(data: MutableLiveData<T?>, call: Call<T>) {
        call.enqueue(object : retrofit2.Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                Log.d("asdasdas","spfoksdf")
                data.value = response.body()

            }
            override fun onFailure(call: Call<T?>, t: Throwable) {
                data.value = null
            }
        })
    }


}