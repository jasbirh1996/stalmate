package com.stalmate.user.base

import android.R
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.stalmate.user.base.callbacks.BaseCallBacks
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.viewmodel.AppViewModel
import java.util.*


open class BaseFragment : Fragment(), BaseCallBacks {

    private var callBacks: BaseCallBacks? = null

    val networkViewModel: AppViewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }

    val prefManager by lazy {
        PrefManager.getInstance(App.getInstance())?.userDetail?.results
    }
    //PrefManager.getInstance(App.getInstance())?.userDetail?.results?.get(0)?.access_token ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        Log.d("fjha", "ppp")
        super.onAttach(context)
        if (context is BaseCallBacks) {
            Log.d("fjha", "pppuuu")
            callBacks = context as BaseCallBacks
        }
    }

    override fun onDetach() {
        super.onDetach()
        callBacks = null
    }

    override fun showLoader() {
        callBacks?.showLoader()
    }

    override fun dismissLoader() {
        callBacks?.dismissLoader()
    }

    override fun onFragmentDetach(fragmentTag: String?) {
        callBacks?.onFragmentDetach(fragmentTag)
    }

    fun makeToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /*    protected void showSnackbar(@NonNull String message) {
        View view = getActivity().findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }*/


    fun isPermissionGranted(permissions: Array<String>, context: Context): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(context, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                listPermissionsNeeded.toTypedArray(),
                BaseActivity.MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    protected fun showSnackbar(message: String) {
        val snack = Snackbar.make(
            requireActivity().findViewById(R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.show()
    }


    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}