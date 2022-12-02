package com.stalmate.user.modules.reels.photo_editing



import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentBottomsheetRangeBinding
import com.stalmate.user.modules.reels.audioVideoTrimmer.ui.seekbar.widgets.CrystalRangeSeekbar
import com.stalmate.user.modules.reels.audioVideoTrimmer.ui.seekbar.widgets.CrystalSeekbar
import com.stalmate.user.modules.reels.audioVideoTrimmer.utils.CustomProgressView


class RangeBSFragmnet : BottomSheetDialogFragment() {
    private var rangeSelectedListener: RangeSelectedListener? = null
    lateinit var binding:FragmentBottomsheetRangeBinding

    interface RangeSelectedListener {
        fun onRangeSelected(seconds: String?)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }



    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottomsheet_range, null)

        binding=DataBindingUtil.bind<FragmentBottomsheetRangeBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        seekbar = contentView.findViewById(R.id.range_seek_bar)
        seekbarController = contentView.findViewById(R.id.seekbar_controller)


        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))






        setUpSeekBar()





/*        binding.rangeBar.addOnChangeListener { slider, value, fromUser -> {
     run {
         if (mMin != slider.setMinSeparationValue()) {
             Toast.makeText(this@MainActivity, "Min Value changed", Toast.LENGTH_SHORT)
                 .show()
         }
         if (mMax != maxValue) {
             Toast.makeText(this@MainActivity, "Max Value changed", Toast.LENGTH_SHORT)
                 .show()
         }

     }*/

        }



    fun setEmojiListener(emojiListener: RangeSelectedListener?) {
        rangeSelectedListener = emojiListener
    }

    private var lastMinValue: Long = 0

    private var lastMaxValue: Long = 0

    private var menuDone: MenuItem? = null


    private var isValidVideo = true
    private var isVideoEnded: kotlin.Boolean = false

    private var seekHandler: Handler? = null
    private var trimType = 0
    private var fixedGap: Long = 0
    private var minGap: kotlin.Long = 0
    private var minFromGap: kotlin.Long = 0
    private var maxToGap: kotlin.Long = 0
    private var hidePlayerSeek =
        false
    private var seekbarController: CrystalSeekbar? = null
    private lateinit var seekbar: CrystalRangeSeekbar
    private var totalDuration: Long = 0
    private fun setUpSeekBar() {
        seekbar.visibility = View.VISIBLE
        /*       txtStartDuration.setVisibility(View.VISIBLE)
               txtEndDuration.setVisibility(View.VISIBLE)*/
        seekbarController!!.setMaxValue(totalDuration.toFloat()).apply()
        seekbar.setMaxValue(totalDuration.toFloat()).apply()
        seekbar.setMaxStartValue(totalDuration.toFloat()).apply()
        lastMaxValue = if (trimType == 1) {
            seekbar.setFixGap(fixedGap.toFloat()).apply()
            totalDuration
        } else if (trimType == 2) {
            seekbar.setMaxStartValue(minGap.toFloat())
            seekbar.setGap(minGap.toFloat()).apply()
            totalDuration
        } else if (trimType == 3) {
            seekbar.setMaxStartValue(maxToGap.toFloat())
            seekbar.setGap(minFromGap.toFloat()).apply()
            maxToGap
        } else {
            seekbar.setGap(2F).apply()
            totalDuration
        }
        if (hidePlayerSeek) seekbarController!!.visibility = View.GONE
        seekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            if (!hidePlayerSeek) seekbarController!!.visibility = View.VISIBLE
        }
        seekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            val minVal = minValue as Long
            val maxVal = maxValue as Long
            if (lastMinValue != minVal) {
                if (!hidePlayerSeek) seekbarController!!.visibility = View.INVISIBLE
            }
            lastMinValue = minVal
            lastMaxValue = maxVal

            Log.d("akjsdasdoo", lastMinValue.toString())
            Log.d("akjsdasdoo", lastMaxValue.toString())

/*            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal))
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal))*/
        }
        seekbarController!!.setOnSeekbarFinalValueListener { value ->
            val value1 = value as Long
            if (value1 < lastMaxValue && value1 > lastMinValue) {
           /*     seekTo(value1)*/
                return@setOnSeekbarFinalValueListener
            }
            if (value1 > lastMaxValue) seekbarController!!.setMinStartValue(
                lastMaxValue.toInt().toFloat()
            ).apply() else if (value1 < lastMinValue) {
                seekbarController!!.setMinStartValue(lastMinValue.toInt().toFloat()).apply()

            }
        }
    }



}
