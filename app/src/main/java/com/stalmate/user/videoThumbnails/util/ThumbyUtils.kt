package com.stalmate.user.videoThumbnails.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build

object ThumbyUtils {

    fun getBitmapAtFrame(
        context: Context,
        uri: Uri,
        frameTime: Long,
        width: Int,
        height: Int
    ): Bitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.Q)) {
            mediaMetadataRetriever.setDataSource(context, uri)
            //mediaMetadataRetriever.setDataSource(uri.path)
        } else {
            mediaMetadataRetriever.setDataSource(context, uri)
        }
        val bitmap = mediaMetadataRetriever.getFrameAtTime(
            frameTime,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )
        return try {
            bitmap?.let { Bitmap.createBitmap(it) }//Bitmap.createScaledBitmap(bitmap, width, height, false)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}