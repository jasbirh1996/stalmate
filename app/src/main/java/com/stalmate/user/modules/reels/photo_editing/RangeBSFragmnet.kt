package com.stalmate.user.modules.reels.photo_editing


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.databinding.FragmentBottomsheetRangeBinding
import com.stalmate.user.modules.reels.audioVideoTrimmer.ui.seekbar.widgets.CrystalRangeSeekbar
import com.stalmate.user.modules.reels.audioVideoTrimmer.ui.seekbar.widgets.CrystalRangeSeekbarForTrim


class RangeBSFragmnet(var videoDuration: Int) : BottomSheetDialogFragment() {
    private var rangeSelectedListener: RangeSelectedListener? = null
    lateinit var binding: FragmentBottomsheetRangeBinding

    interface RangeSelectedListener {
        fun onCaptureAfternSeconds(seconds: Int)
        fun onRangeDialogDismiss()


    }

    override fun onCancel(dialog: DialogInterface) {
        rangeSelectedListener!!.onRangeDialogDismiss()
        super.onCancel(dialog)

    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                rangeSelectedListener!!.onRangeDialogDismiss()
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottomsheet_range, null)
        binding = DataBindingUtil.bind<FragmentBottomsheetRangeBinding>(contentView)!!
        dialog.getWindow()!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        seekbar = contentView.findViewById(R.id.range_seek_bar)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        totalDuration = (videoDuration + 10).toLong()
        fixedGap = videoDuration.toLong()
        setUpSeekBar()
        binding.buttonTrimDone.setOnClickListener {
            rangeSelectedListener!!.onCaptureAfternSeconds(lastMinValue.toInt())
            rangeSelectedListener!!.onRangeDialogDismiss()
            dismiss()
        }
    }


    fun setEmojiListener(emojiListener: RangeSelectedListener?) {
        rangeSelectedListener = emojiListener
    }

    private var lastMinValue: Long = 0
    private var lastMaxValue: Long = 0
    private var trimType = 1
    private var fixedGap: Long = 0
    private var minGap: kotlin.Long = 0
    private var minFromGap: kotlin.Long = 0
    private var maxToGap: kotlin.Long = 0
    private var hidePlayerSeek = false
    private lateinit var seekbar: CrystalRangeSeekbarForTrim
    private var totalDuration: Long = 0
    private fun setUpSeekBar() {
        seekbar.visibility = View.VISIBLE
        /*       txtStartDuration.setVisibility(View.VISIBLE)
               txtEndDuration.setVisibility(View.VISIBLE)*/
        seekbar.setMaxValue(totalDuration.toFloat()).apply()
        seekbar.setMaxStartValue(totalDuration.toFloat()).apply()

        lastMaxValue = if (trimType == 1) {
            Log.d("lakjsdasd", "aposkdasd")
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
            Log.d("lakjsdasd", "aposkdasasdd")
            seekbar.setGap(2F).apply()
            totalDuration
        }
        seekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->


        }
        seekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            val minVal = minValue as Long
            val maxVal = maxValue as Long
            if (lastMinValue != minVal) {


            }
            lastMinValue = minVal
            lastMaxValue = maxVal


            seekbar.setLeftThumbBitmap(createDrawableFromView(lastMinValue,R.drawable.switch_button_thumb))
            seekbar.setRightThumbBitmap(createDrawableFromView(lastMaxValue,R.drawable.switch_button_thumb))




            Log.d("akjsdasdoo", lastMinValue.toString())
            Log.d("akjsdasdoo", lastMaxValue.toString())

/*            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal))
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal))*/
        }
    }


    fun createDrawableFromView(progress: Long, drawable:Int): Bitmap? {
        val inflatedFrame: View = (App.getInstance()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.view_custom_thumb,
            null
        )
  /*      val ivStar: ImageView = inflatedFrame.findViewById(R.id.ivIcon) as ImageView

        ivStar.setImageDrawable(
            ContextCompat.getDrawable(
                App.getInstance(),
                drawable
            )
        )*/
        val tvValue: TextView = inflatedFrame.findViewById(R.id.tvValue) as TextView
        tvValue.setText(progress.toString()+"s")
        val frameLayout: FrameLayout = inflatedFrame.findViewById(R.id.screen) as FrameLayout


        frameLayout.setDrawingCacheEnabled(true)
        frameLayout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        frameLayout.layout(0, 0, frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight())
        frameLayout.buildDrawingCache(true)
        return frameLayout.getDrawingCache()
    }

}
