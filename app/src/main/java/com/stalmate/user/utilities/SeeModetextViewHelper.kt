package com.stalmate.user.utilities

import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView

object SeeModetextViewHelper {


    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release()
            }
        }
        return bitmap
    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText())
        }
        val vto: ViewTreeObserver = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override
            fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs: ViewTreeObserver = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0)
                    text = tv.getText()
                        .subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1)
                    text = tv.getText()
                        .subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1)
                    text = tv.getText().subSequence(0, lineEndIndex).toString() + " " + expandText
                }
                tv.setText(text)
                tv.setMovementMethod(LinkMovementMethod.getInstance())
                tv.setText(
                    addClickablePartTextViewResizable(
                        SpannableString(tv.getText().toString()), tv, lineEndIndex, expandText,
                        viewMore
                    ), TextView.BufferType.SPANNABLE
                )
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder? {
        val str: String = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : ClickableSpan() {

                override fun onClick(widget: View) {
                    tv.setLayoutParams(tv.getLayoutParams())
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "less", false)
                    } else {
                        makeTextViewResizable(tv, 3, "more", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }
}

class MySpannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = false

    /**
     * Constructor
     */
    init {
        this.isUnderline = isUnderline
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.setUnderlineText(isUnderline)
        ds.setColor(Color.parseColor("#343434"))
    }

    override fun onClick(widget: View) {}
}