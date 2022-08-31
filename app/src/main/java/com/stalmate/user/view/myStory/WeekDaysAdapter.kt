package com.stalmate.user.view.myStory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.model.WeekDaysModel

class WeekDaysAdapter(private val mList: List<WeekDaysModel>) : RecyclerView.Adapter<WeekDaysAdapter.ViewHolder>() {



    class ViewHolder(ItemView : View): RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.daysText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_days_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.name
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}