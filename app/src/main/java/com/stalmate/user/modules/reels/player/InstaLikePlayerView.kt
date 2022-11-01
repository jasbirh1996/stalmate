package com.stalmate.user.modules.reels.player

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AdViewProvider
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Assertions
import com.stalmate.user.R

class InstaLikePlayerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? =  /* attrs= */null,
    defStyleAttr: Int =  /* defStyleAttr= */0
) : FrameLayout(
    context!!, attrs, defStyleAttr
), AdViewProvider {
    var videoSurfaceView: View?
    private var player: ExoPlayer? = null
    private var textureViewRotation = 0
    private var isTouching = false

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    fun getPlayer(): Player? {
        return player
    }

    /**
     * Set the [Player] to use.
     *
     *
     * To transition a [Player] from targeting one view to another, it's recommended to use
     * [.switchTargetView] rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view *before*
     * calling `setPlayer(null)` to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The [Player] to use, or `null` to detach the current player. Only
     * players which are accessed on the main thread are supported (`player.getApplicationLooper() == Looper.getMainLooper()`).
     */
    fun setPlayer(player: ExoPlayer?) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper())
        Assertions.checkArgument(
            player == null || player.applicationLooper == Looper.getMainLooper()
        )
        if (this.player === player) {
            return
        }
        val oldPlayer = this.player
        if (oldPlayer != null) {
            val oldVideoComponent = oldPlayer.videoComponent
            if (oldVideoComponent != null) {
                oldVideoComponent.clearVideoSurfaceView(videoSurfaceView as SurfaceView?)
            }
        }
        this.player = player
        if (player != null) {
            val newVideoComponent = player.videoComponent
            if (newVideoComponent != null) {
                newVideoComponent.setVideoSurfaceView(videoSurfaceView as SurfaceView?)
            }
        } else {
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        // Work around https://github.com/google/ExoPlayer/issues/3160.
        videoSurfaceView?.setVisibility(visibility)

    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (player != null && player!!.isPlayingAd) {
            return super.dispatchKeyEvent(event)
        }
        val isDpadKey = isDpadKey(event.keyCode)
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (player == null) {
            false
        } else when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                true
            }
            MotionEvent.ACTION_UP -> {
                if (isTouching) {
                    isTouching = false
                    performClick()
                    return true
                }
                false
            }
            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    override fun onTrackballEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun getAdViewGroup(): ViewGroup? {
        return null
    }

/*    override fun getAdOverlayInfos(): MutableList<AdOverlayInfo> {
        return super.getAdOverlayInfos()
    }

    override fun getAdOverlayViews(): Array<View?> {
        return arrayOfNulls(0)
    }*/

    @SuppressLint("InlinedApi")
    private fun isDpadKey(keyCode: Int): Boolean {
        return keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_CENTER
    }

    init {
        if (isInEditMode) {
            videoSurfaceView = null

        } else {
            val playerLayoutId = R.layout.exo_simple_player_view
            LayoutInflater.from(context).inflate(playerLayoutId, this)
            descendantFocusability = FOCUS_AFTER_DESCENDANTS

            // Content frame.
            videoSurfaceView = findViewById(R.id.surface_view)
            init()
        }
    }

    private var lastPos: Long? = 0
    private var videoUri: Uri? = null;






    fun init() {
        reset()

        /*Setup player + Adding Cache Directory*/

        val loadControl: LoadControl = DefaultLoadControl.Builder()
            .setAllocator( DefaultAllocator(true, 16))
        .setBufferDurationsMs(VideoPlayerConfig.MIN_BUFFER_DURATION,
            VideoPlayerConfig.MAX_BUFFER_DURATION,
            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER)
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val trackSelector = DefaultTrackSelector(context)

        val simpleExoPlayer = ExoPlayer.Builder(context).setTrackSelector(trackSelector).setLoadControl(loadControl)
            .build()
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ONE;
        simpleExoPlayer.addListener(object : Player.Listener {


            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d("askdasd",playbackState.toString())
                if (playbackState == Player.STATE_READY) {

                  //  simpleExoPlayer.seekTo(lastPos!!)
                    alpha = 1f

                }

            }



        })

        simpleExoPlayer.playWhenReady = false
        setPlayer(simpleExoPlayer);

    }





/*
    *//**
     * This will resuse the player and will play new URI we have provided
     *//*
    fun startPlaying() {


        val mediaSource = ProgressiveMediaSource.Factory(
            CacheDataSource.Factory()
                .setCache(Constants.simpleCache!!)
                .setUpstreamDataSourceFactory(
                    DefaultHttpDataSource.Factory()
                        .setUserAgent("ExoplayerDemo")
                )
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        ).createMediaSource(MediaItem.fromUri(videoUri!!))


        (player as ExoPlayer).prepare(mediaSource)



        player?.seekTo(lastPos!!)
        player?.playWhenReady = true


    }*/


    /**
     * This will resuse the player and will play new URI we have provided
     */
    fun startPlaying() {
/*
        // Build data source factory with cache enabled, if data is available in cache it will return immediately, otherwise it will open a new connection to get the data.
        val cacheDataSourceFactory = CacheDataSource.Factory()
        cacheDataSourceFactory.setCache(Constants.simpleCache!!)
        val mediaSource =
            ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri!!))
        (player as ExoPlayer).prepare(mediaSource)

      //  player?.seekTo(lastPos!!)
        player?.playWhenReady = true

*/



        try {

    /*        val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(
                    context, context.getString(R.string.app_name)
                )
            Log.d("alskdasd","aosdasd")
            val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUri!!))
            //  exoplayer!!.setThrowsWhenUsingWrongThread(false)
         //   player!!.addMediaSource(videoSource)
            Log.d("alskdasd","aosdasd")

*/




            val mediaSource = ProgressiveMediaSource.Factory(
                CacheDataSource.Factory()
                    .setCache(Constants.simpleCache!!)
                    .setUpstreamDataSourceFactory(
                        DefaultHttpDataSource.Factory()
                            .setUserAgent("ExoplayerDemo")
                    )
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            ).createMediaSource(MediaItem.fromUri(videoUri!!))










            player!!.prepare(mediaSource)
            /*   val audioAttributes = AudioAttributes.Builder()
                       .setUsage(C.USAGE_MEDIA)
                       .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                       .build()
               exoplayer!!.setAudioAttributes(audioAttributes, true)*/
        } catch (e: Exception) {
            Log.d("Constants.tag", "Exception audio focus : $e")
        }

        if (player != null) {
            Log.d("jkasdas","aklsdlaskjdlas")
            player?.seekTo(lastPos!!)
            player?.playWhenReady = true
          //  player!!.play()
        }




    }




    /**
     * This will stop the player, but stopping the player shows blackscreen
     * so to cover that we set alpha to 0 of player
     * and lastFrame of player using imageView over player to make it look like paused player
     *
     * (If we will not stop the player, only pause , then it can cause memory issue due to overload of player
     * and paused player can not be payed with new URL, after stopping the player we can reuse that with new URL
     *
     */
    fun removePlayer() {
        getPlayer()?.setPlayWhenReady(false)
        lastPos = getPlayer()?.currentPosition
        reset()
        getPlayer()?.stop(true)

    }

    fun reset() {
        // This will prevent surface view to show black screen,
        // and we will make it visible when it will be loaded
        alpha = 0f
    }

    fun setVideoUri(uri: Uri?) {
        this.videoUri = uri;
    }
}

object VideoPlayerConfig {
    //Minimum Video you want to buffer while Playing
    const val MIN_BUFFER_DURATION = 2000

    //Max Video you want to buffer during PlayBack
    const val MAX_BUFFER_DURATION = 5000

    //Min Video you want to buffer before start Playing it
    const val MIN_PLAYBACK_START_BUFFER = 1500

    //Min video You want to buffer when user resumes video
    const val MIN_PLAYBACK_RESUME_BUFFER = 2000
}