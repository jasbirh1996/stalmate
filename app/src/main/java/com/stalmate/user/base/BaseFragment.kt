package com.stalmate.user.base

import android.R
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.stalmate.user.base.callbacks.BaseCallBacks
import com.stalmate.user.view.dashboard.funtime.FragmentFuntimeTag
import com.stalmate.user.viewmodel.AppViewModel
import java.util.*


open class BaseFragment : Fragment(), BaseCallBacks {
    private var callBacks: BaseCallBacks? = null

    val networkViewModel: AppViewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]

    }
    val taggedPeopleViewModel: FragmentFuntimeTag.TagPeopleViewModel by lazy {
        ViewModelProvider(this)[FragmentFuntimeTag.TagPeopleViewModel::class.java]
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
    protected fun showSnackbar(message: String) {
        val snack = Snackbar.make(
            activity!!.findViewById(R.id.content),
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
     fun hideKeyboard(view:View){
        // since our app extends AppCompatActivity, it has access to context
        val imm=requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // we have to tell hide the keyboard from what. inorder to do is we have to pass window token
        // all of our views,like message, name, button have access to same window token. since u have button
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        // if you are using binding object
        // imm.hideSoftInputFromWindow(binding.button.windowToken,0)

    }


}