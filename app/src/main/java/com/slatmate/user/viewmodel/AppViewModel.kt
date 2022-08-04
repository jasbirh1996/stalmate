package com.slatmate.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.slatmate.user.base.App
import com.slatmate.user.model.ModelFeed
import com.slatmate.user.model.ModelSuccess
import com.slatmate.user.networking.ApiInterface

import retrofit2.Call
import retrofit2.Response

open class AppViewModel : ViewModel() {

    var apiInterface = ApiInterface.init(App.getInstance())




    var feedLiveData: LiveData<ModelFeed?> = MutableLiveData<ModelFeed?>()
    fun getFeedList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFeed?>()
        feedLiveData = temp
        getResult(temp, apiInterface.setSignupDetails(token, map))
    }







    fun <T : Any> getResult(data: MutableLiveData<T?>, call: Call<T>) {
        call.enqueue(object : retrofit2.Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                data.value = response.body()
            }
            override fun onFailure(call: Call<T?>, t: Throwable) {
                data.value = null
            }
        })
    }


}