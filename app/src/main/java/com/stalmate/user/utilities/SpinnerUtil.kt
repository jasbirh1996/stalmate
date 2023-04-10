package com.stalmate.user.utilities

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.stalmate.user.R


object SpinnerUtil {
    //set the data to the spinner
    fun Spinner.setSpinner(
        listFromServer: ArrayList<String>? = null,
        listFromResources: Int,
        setSelection: Int,
        onItemSelectedListener: (position: Int) -> Unit
    ) {
        var list = arrayListOf<String>()
        list =
            listFromServer ?: ArrayList(resources.getStringArray(listFromResources).toMutableList())
        this.dropDownVerticalOffset = 100
        this.setPopupBackgroundResource(R.drawable.bg_spinner_popup)
        // Creating adapter for spinner
        val spinnerArrayAdapter: ArrayAdapter<String?> =
            object : ArrayAdapter<String?>(
                this.context,
                R.layout.spinner_item,
                list as List<String?>
            ) {
                // Disable the first item from Spinner
                // First item will be use for hint
                /*override fun isEnabled(position: Int): Boolean {
                return position != 0
            }*/

                /*override fun getDropDownView(
                position: Int, convertView: View?,
                parent: ViewGroup
            ): View {
                var view =
                    super.getDropDownView(position, convertView, parent)
                val tv = view as CheckedTextView
                if (position == 0) {
                    // Set the hint text color gray
                    //tv.setTextColor(context.resources.getColor(R.color.colorAccent))
                    tv.isClickable = false
                    view = tv
                }*//* else {
                        //tv.setTextColor(context.resources.getColor(R.color.black))
                        tv.visibility = View.VISIBLE
                        view = tv
                    }*//*
                    return view
                }*/
            }
        // Drop down layout style - list view with radio button
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Creating adapter for spinner
        //val dataAdapter = ArrayAdapter(context, R.layout.spinner_item, array)
        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        this.adapter = spinnerArrayAdapter

        //onItemSelectedListener
        val listener =
            SpinnerInteractionListener(onItemSelectedByUser = { parent: AdapterView<*>?, view: View, pos: Int, id: Long ->
                onItemSelectedListener(pos)
            })
        this.setOnTouchListener(listener)
        this.onItemSelectedListener = listener

        //Set item in spinner according to the position
        /*for (i in 0 until array.size) {
            if (array[i].equals(value, ignoreCase = true)) {
                spinner.setSelection(i)
                break
            } else
                spinner.setSelection(array.size - 1)
        }*/
        this.setSelection(setSelection)
    }
}

open class SpinnerInteractionListener(val onItemSelectedByUser: (parent: AdapterView<*>?, view: View, pos: Int, id: Long) -> Unit) :
    AdapterView.OnItemSelectedListener, OnTouchListener {
    var userSelect = false
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        //v.performClick()
        userSelect = true
        return false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
        if (userSelect) {
            userSelect = false
            // Your selection handling code here
            onItemSelectedByUser(parent, view, pos, id)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}