package com.stalmate.user.modules.reels.player

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.stalmate.user.base.App

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

/**
 *
 * This class provides way to precache the video from remoteUrl in to Cache Directory
 * So Player will not reload videos from server if they are already loaded in cache
 */
class VideoPreLoadingService : IntentService(VideoPreLoadingService::class.java.simpleName) {
    private lateinit var mContext: Context
    private var simpleCache: SimpleCache? = null
    private var cachingJob: Job? = null
    private var videosList: ArrayList<String>? = null

    override fun onHandleIntent(intent: Intent?) {
        mContext = applicationContext

        if (intent != null) {
            val extras = intent.extras
            videosList = extras?.getStringArrayList(Constants.VIDEO_LIST)

            if (!videosList.isNullOrEmpty()) {
                preCacheVideo(videosList)
            }
        }
    }

    private fun preCacheVideo(videosList: ArrayList<String>?) {
        var videoUrl: String? = null
        if (!videosList.isNullOrEmpty()) {
            videoUrl = videosList[0]
            videosList.removeAt(0)
        } else {
            stopSelf()
        }
        if (!videoUrl.isNullOrBlank()) {
            val videoUri = Uri.parse(videoUrl)
            val dataSpec = DataSpec(videoUri)


            val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                val downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
                // Do Something
            }


            var mHttpDataSourceFactory: HttpDataSource.Factory

            mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)



          var   mCacheDataSource = CacheDataSource.Factory()

                .setCache(Constants.simpleCache!!)
                .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                .createDataSource()












            cachingJob = GlobalScope.async(Dispatchers.IO) {
                cacheVideo(dataSpec, progressListener,mCacheDataSource)
                preCacheVideo(videosList)
            }
            cachingJob?.start();
        }
    }



    private fun cacheVideo(
        mDataSpec: DataSpec,
        mProgressListener: CacheWriter.ProgressListener,
        mCacheDataSource: CacheDataSource
    ) {
        runCatching {
            CacheWriter(
                mCacheDataSource,
                mDataSpec,
                null,
                mProgressListener,
            ).cache()
        }.onFailure {
            it.printStackTrace()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        cachingJob?.cancel()

    }
}

