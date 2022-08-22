package com.stalmate.user.base

import android.R
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.simform.videooperations.Common
import com.stalmate.user.base.callbacks.BaseCallBacks
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.untilitys
import com.stalmate.user.viewmodel.AppViewModel
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener,
    BaseCallBacks {

    val networkViewModel: AppViewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]

    }


    var isGPSAvailable = false
    var isInternetAvailable = false
    private var progressDialog: com.stalmate.user.view.dialogs.ProgressDialog? = null
    private var mProgressDialog: ProgressDialog? = null
    private val isInBAckground = false
    private var context: Context? = null
    abstract fun onClick(viewId: Int, view: View?)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = com.stalmate.user.view.dialogs.ProgressDialog(this)
        context = this
        try {
          debugPrint("tokenn", PrefManager.getInstance(App.getInstance())!!.userDetailLogin.results[0].token)
        }catch (e:Exception){

        }


    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        /*if (getSaveString("LANG").isNotEmpty()) {
            untilitys.setAppLocale(this, Common.getString("LANG"))
        }*/
    }

    fun makeToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun makeLongToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    override fun onClick(v: View) {
        when (v.id) {
            else -> onClick(v.id, v)
        }
    }

    override
    fun showLoader() {
        Log.d("asdsadsd", "asdsadsd")
        try {
            if (!isFinishing && !progressDialog!!.isShowing) progressDialog!!.showDialog(com.stalmate.user.view.dialogs.ProgressDialog.DIALOG_CENTERED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override
    fun dismissLoader() {
        if (!isFinishing && progressDialog!!.isShowing) progressDialog!!.dismiss()
    }

    protected fun showLoading(message: String) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setMessage(message)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()
    }

    protected fun hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    protected fun showSnackbar(message: String) {
        val view = findViewById<View>(R.id.content)
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override
    fun onFragmentDetach(fragmentTag: String?) {
    }

    fun requestPermission(permission: String): Boolean {
        val isGranted =
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission), READ_WRITE_STORAGE
            )
        }
        return isGranted
    }

    fun isPermissionGranted(permissions: Array<String>): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_WRITE_STORAGE -> isPermissionGranted(
                grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0]
            )
        }
    }

    fun isPermissionGranted(isGranted: Boolean, permission: String?) {}

    companion object {
        const val READ_WRITE_STORAGE = 52
        const val MULTIPLE_PERMISSIONS = 10
        private val sessionExpireDialogList: List<AlertDialog> = ArrayList()
        private val errorDialogList: List<AlertDialog> = ArrayList()
    }


    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun debugPrint(key:String,data:String){
        Log.d(key,data)
    }
}