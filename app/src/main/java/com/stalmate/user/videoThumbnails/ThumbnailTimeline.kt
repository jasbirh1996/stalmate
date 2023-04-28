package com.stalmate.user.videoThumbnails

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.stalmate.user.R
import com.stalmate.user.databinding.ViewTimelineBinding
import com.stalmate.user.videoThumbnails.listener.SeekListener
import kotlin.math.roundToInt

class ThumbnailTimeline @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs) {

    var binding: ViewTimelineBinding? = null
    private var frameDimension: Int = 0
    var currentProgress = 0.0
    var currentSeekPosition = 0f
    var seekListener: SeekListener? = null
    var uri: Uri? = null
        set(value) {
            field = value
            field?.let { uri ->
                loadThumbnails(uri)
                invalidate()
                binding?.let {
                    it.viewSeekBar.setDataSource(context, uri, 4)
                    it.viewSeekBar.seekTo(currentSeekPosition.toInt())
                }
            }
        }

    init {
        binding = ViewTimelineBinding.inflate(LayoutInflater.from(getContext()), this, true)
        binding?.root
        frameDimension = context.resources.getDimensionPixelOffset(R.dimen.frames_video_height)
        isFocusable = true
        isFocusableInTouchMode = true
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) elevation = 8f

        val margin = DisplayMetricsUtil.convertDpToPixel(16f, context).toInt()
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(margin, 0, margin, 0)
        layoutParams = params
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> handleTouchEvent(event)
        }
        return true
    }

    private fun handleTouchEvent(event: MotionEvent) {
        val seekViewWidth = context.resources.getDimensionPixelSize(R.dimen.frames_video_height)
        currentSeekPosition = (event.x.roundToInt() - (seekViewWidth / 2)).toFloat()
        binding?.let {
            val availableWidth = it.containerThumbnails.width -
                    (layoutParams as ConstraintLayout.LayoutParams).marginEnd -
                    (layoutParams as ConstraintLayout.LayoutParams).marginStart
            if (currentSeekPosition + seekViewWidth > it.containerThumbnails.right) {
                currentSeekPosition = (it.containerThumbnails.right - seekViewWidth).toFloat()
            } else if (currentSeekPosition < it.containerThumbnails.left) {
                currentSeekPosition = paddingStart.toFloat()
            }

            currentProgress = (currentSeekPosition.toDouble() / availableWidth.toDouble()) * 100
            it.containerSeekBar.translationX = currentSeekPosition
            it.viewSeekBar.seekTo(((currentProgress * it.viewSeekBar.getDuration()) / 100).toInt())
        }
        seekListener?.onVideoSeeked(currentProgress)
    }

    private fun loadThumbnails(uri: Uri) {
        val metaDataSource = MediaMetadataRetriever()
        metaDataSource.setDataSource(context, uri)

        val videoLength = ((metaDataSource.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()?:0) * 1000).toLong()

        val thumbnailCount = 7

        val interval = videoLength / thumbnailCount

        for (i in 0 until thumbnailCount - 1) {
            val frameTime = i * interval
            var bitmap =
                metaDataSource.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            try {
                val targetWidth: Int
                val targetHeight: Int
                if ((bitmap?.height ?: 0) > (bitmap?.width ?: 0)) {
                    targetHeight = frameDimension
                    val percentage = frameDimension.toFloat() / (bitmap?.height ?: 0)
                    targetWidth = ((bitmap?.width ?: 0) * percentage).toInt()
                } else {
                    targetWidth = frameDimension
                    val percentage = frameDimension.toFloat() / (bitmap?.width ?: 0)
                    targetHeight = ((bitmap?.height ?: 0) * percentage).toInt()
                }
                bitmap =
                    bitmap?.let { Bitmap.createScaledBitmap(it, targetWidth, targetHeight, false) }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding?.containerThumbnails?.addView(ThumbnailView(context).apply {
                setImageBitmap(
                    bitmap
                )
            })
        }
        metaDataSource.release()
    }
}