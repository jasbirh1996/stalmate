package com.stalmate.user.base

import android.R
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.stalmate.user.base.callbacks.BaseCallBacks


class BaseFragment : Fragment(), BaseCallBacks {
    private var callBacks: BaseCallBacks? = null
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
}