package com.stalmate.user.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.stalmate.user.R
import com.stalmate.user.view.authentication.ActivityAuthentication
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

object ErrorUtil {
    val TAG = ErrorUtil::class.simpleName


    fun handlerGeneralError(context: Context?, view: View?, throwable: Throwable) {
        Log.e("Error", "Error: ${throwable.message}")
        throwable.printStackTrace()

        if (context == null) return


        when (throwable) {
            is HttpException -> {
                try {
                    when (throwable.code()) {
                        401 -> {
                            Log.d(TAG, "Inside 401 error code")
                            displayError(view, throwable)
                            forceLogout(context)    //...when logout option...
                        }
                        400 -> {
                            displayError(view, throwable)
                            //logout(context)    //...when logout option...
                        }
                        500 -> {
                            displayError(view, throwable)
                            //forceLogout(context)    //...when logout option...
                        }
                        else -> {
                            displayError(view, throwable)
                        }
                    }
                } catch (exception: Exception) {
                    view?.let { showScanBar(view, "Something went wrong.") }
                    exception.printStackTrace()
                }
            }
            is ConnectException -> {
                view?.let { showScanBar(view, "Check connection please.") }
            }
            is SocketTimeoutException -> {
                view?.let { showScanBar(view, "Check connection please.") }
            }
            else -> {
                view?.let { showScanBar(view, "Something went wrong.") }
            }
        }
    }

//   when force logout ko open karne ke liye ye dono line open karna hai only......
//    fun logout(context: Context) {
//        forceLogout(context)

    ////ye nahi
    // preventing from unnessary error if occur however didn't find any
    /*  try {

         RetrofitUtil.createBaseApiService().logout(
                  access_token = SharedPreferenceUtil.getInstance(context).access_token
          ).subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(
                          {},
                          { forceLogout(context) },
                          { forceLogout(context) }
                  )
      } catch (e: Exception) {
      }*/
}

/**
 * Perform logout for both the success and error case (force logout)
 */

fun forceLogout(context: Context) {
    Toast.makeText(context, context.getString(R.string.session_expired), Toast.LENGTH_SHORT).show()
    //SharedPreferenceUtil.getInstance(context).deletePreferences()
    val intent = Intent(context, ActivityAuthentication::class.java)
    (context as Activity).startActivity(intent)
    (context as Activity).finishAffinity()
}

fun displayError(view: View?, exception: HttpException) {
    Log.i("Error", "displayError()")
    try {
        val gson = Gson()
        val errorBody = gson.fromJson(
            exception.response()?.errorBody()?.string(),
            ErrorBean::class.java
        )
        Log.e("ErrorMessage", gson.toJson(errorBody))
        view?.let { showScanBar(view, errorBody.message) }
    } catch (e: Exception) {
        view?.let { showScanBar(view, "Something went wrong.") }
    }
}


fun showScanBar(view: View?, exception: String) {
    view?.let { Snackbar.make(it, exception, Snackbar.LENGTH_SHORT).show() }
}

data class ErrorBean(
    var message: String
)