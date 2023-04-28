package com.stalmate.user.videoThumbnails.listener

interface SeekListener {
    fun onVideoSeeked(percentage: Double)
}