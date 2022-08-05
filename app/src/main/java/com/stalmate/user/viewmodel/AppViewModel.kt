package com.stalmate.user.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stalmate.user.base.App
import com.stalmate.user.model.*
import com.stalmate.user.networking.ApiInterface

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




    var friendLiveData: LiveData<ModelFriend?> = MutableLiveData<ModelFriend?>()
    fun getFriendList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFriend?>()
        friendLiveData = temp
        getResult(temp, apiInterface.getFriendList())
    }


/*
    var categoryLiveData: LiveData<ModelCategory?> = MutableLiveData<ModelCategory?>()
    fun getCategoryList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelCategory?>()
        categoryLiveData = temp
        getResult(temp, apiInterface.getCategorList())
    }


    var languageLiveData: LiveData<ModelLanguage?> = MutableLiveData<ModelLanguage?>()
    fun getLanguageList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLanguage?>()
        languageLiveData = temp
        getResult(temp, apiInterface.getLanguageList())
    }

*/





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