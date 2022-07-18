package com.slatmate.user.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.slatmate.user.model.ModelSuccess
import com.slatmate.user.networking.ApiInterface

import retrofit2.Call
import retrofit2.Response

open class AppViewModel : ViewModel() {

    var apiInterface = ApiInterface.init(App.getInstance())
    var orderListResponse: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun getOrderHistory(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        orderListResponse = temp
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