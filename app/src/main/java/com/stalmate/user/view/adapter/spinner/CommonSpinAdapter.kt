package com.wedguruphotographer.adapter

import android.R
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import com.stalmate.user.model.ModelCustumSpinner

import java.util.ArrayList

class CustumSpinAdapter(// Your sent context
    private val context: Context, private val textViewResourceId: Int,
    var list: ArrayList<ModelCustumSpinner>, hideTop: Boolean
) : BaseAdapter() {
    private val hideTop: Boolean
    override fun getCount(): Int {
        Log.d("asdasdasd",list.size.toString())
        return list.size
    }

    override fun getItem(position: Int): ModelCustumSpinner {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun isEnabled(position: Int): Boolean {
        // Disable the first item from Spinner
        // First item will be used for hint
        return position != 0
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        val view = LayoutInflater.from(context)
            .inflate(R.layout.simple_spinner_dropdown_item, parent, false)
        val label: TextView
        label = view.findViewById(R.id.text1)
        label.setTextColor(Color.GRAY)
        label.setText(list.get(position).name);


        if (hideTop) {
            if (position == 0) {
                view.layoutParams = AbsListView.LayoutParams(-1, 1)
                view.visibility = View.GONE
            }
        }


        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        // label.setText(list.get(position).getName());
        // And finally return your dynamic (or custom) view for each spinner item
        return label
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {

        val label = super.getDropDownView(position, convertView, parent) as TextView
        if(position == 0) {
            label.setTextColor(Color.GRAY)
        } else {
            label.setTextColor(Color.BLACK)
        }
       label.setText(list[position].name)
        return label
    }

    // Your custom values for the spinner (Staff)
    init {
        this.hideTop = hideTop
    }
}