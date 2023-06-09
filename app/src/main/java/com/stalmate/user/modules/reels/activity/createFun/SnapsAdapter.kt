package com.stalmate.user.modules.reels.activity.createFun

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.databinding.ItemSnapBinding

class SnapsAdapter(
    val timeList: ArrayList<CreateFunActivity.SnapsData>,
    val callback: OnSnapListener
) : RecyclerView.Adapter<SnapsAdapter.SnapViewHolder>() {

    private val holders = ArrayList<SnapViewHolder>()
    private var oldHolder: SnapViewHolder? = null

    override fun onViewAttachedToWindow(holder: SnapViewHolder) {
        super.onViewAttachedToWindow(holder)
        holders.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: SnapViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val pos = holders.indexOf(holder)
        if (pos > -1) {
            holders.removeAt(pos)
        }
    }

    override fun getItemCount(): Int {
        return timeList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_snap, parent, false)
        return SnapViewHolder(DataBindingUtil.bind<ItemSnapBinding>(view)!!)
    }

    override fun onBindViewHolder(holder: SnapViewHolder, position: Int) {
        holder.bind(timeList[position], position)
    }

    inner class SnapViewHolder(var binding: ItemSnapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            feed: CreateFunActivity.SnapsData,
            position: Int
        ) {
            if (position < 2 || position > timeList.size - 3) {
                binding.itemSnapshot.visibility = View.INVISIBLE
            } else {
                binding.itemSnapshot.visibility = View.VISIBLE
                Glide.with(binding.itemSnapshot.context)
                    .load(feed.image)
                    .placeholder(R.drawable.white_circle)
                    .error(R.drawable.white_circle)
                    .into(binding.recordButton)
            }
        }
    }

    fun setCurrentFocus(rx: Int, ry: Int, context: Context) {
        for (holder in holders) {
            if (isViewContains(holder.itemView, rx, ry)) {
                if (oldHolder != null) {
                    //Change unselected item here
                    /*val dimensionInDp = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        R.dimen._70sdp.toFloat(),
                        context.resources.displayMetrics
                    )
                    oldHolder?.binding?.recordButton?.layoutParams?.height = dimensionInDp.toInt()
                    oldHolder?.binding?.recordButton?.layoutParams?.width = dimensionInDp.toInt()
                    oldHolder?.binding?.recordButton?.requestLayout()*/
                }
                //Change selected item here
                /*val dimensionInDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    R.dimen._60sdp.toFloat(),
                    (holder.itemView.context as CreateFunActivity).resources.displayMetrics
                )
                holder.binding.recordButton.layoutParams.height = dimensionInDp.toInt()
                holder.binding.recordButton.layoutParams.width = dimensionInDp.toInt()
                holder.binding.recordButton.requestLayout()*/

                oldHolder = holder
                if (holder.binding.itemSnapshot.visibility == View.VISIBLE) {
                    callback.onItemSelected(timeList[holder.layoutPosition])
                    holder.binding.itemSnapshot.setOnClickListener {
                        callback.setOnClickListener()
                    }
                    holder.binding.itemSnapshot.setOnLongClickListener {
                        callback.setOnLongClickListener()
                        true
                    }
                } else {
                    callback.scrollBound()
                }
                break
            }
        }
    }

    private fun isViewContains(view: View, rx: Int, ry: Int): Boolean {
        val l = IntArray(2)
        view.getLocationOnScreen(l)
        val x = l[0]
        val y = l[1]
        val w = view.width
        val h = view.height
        return !(rx < x || rx > x + w || ry < y || ry > y + h)
    }

    interface OnSnapListener {
        fun onItemSelected(data: CreateFunActivity.SnapsData)
        fun scrollBound()
        fun setOnClickListener()
        fun setOnLongClickListener()
    }
}