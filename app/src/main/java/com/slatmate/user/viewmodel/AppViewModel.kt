package com.slatmate.user.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.slatmate.user.base.App
import com.slatmate.user.model.CommonModelResponse
import com.slatmate.user.model.ModelFeed
import com.slatmate.user.model.ModelRegisterResponse
import com.slatmate.user.networking.ApiInterface
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

    var registerData: LiveData<ModelRegisterResponse?> = MutableLiveData<ModelRegisterResponse?>()
    fun registration(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelRegisterResponse?>()
        registerData = temp
        getResult(temp, apiInterface.setSignupDetails(map))
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