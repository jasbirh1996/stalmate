package com.stalmate.user.modules.reels.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.stalmate.user.R
import com.stalmate.user.databinding.ActivityFilterBinding
import com.stalmate.user.modules.reels.utils.VideoFilter
import com.stalmate.user.modules.reels.workers.VideoFilterWorker
import java.io.File
import java.util.*

class ActivityFilter : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    private var mModel: ActivityFilterViewModel? = null
    private var mPlayer: ExoPlayer? = null
    private var mSong = 0
    private var mVideo: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mModel = ViewModelProvider(this)[ActivityFilterViewModel::class.java]
        mSong = intent.getIntExtra(EXTRA_SONG, 0)
        mVideo = intent.getStringExtra(EXTRA_VIDEO)

        mPlayer = ExoPlayer.Builder(this).build()
        mPlayer!!.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL)
        val factory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(File(mVideo!!)))
        val source: ProgressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        binding.playerView.player=mPlayer;

        // we are preparing our exoplayer
        // with media source.
        mPlayer!!.prepare(source);

        // we are setting our exoplayer
        // when it is ready.
        mPlayer!!.setPlayWhenReady(true);
        mPlayer!!.play()


    binding.button.setOnClickListener {
        val intent = Intent(this, ActivityVideoEditor::class.java)
        intent.putExtra(EXTRA_VIDEO, mVideo)
        startActivity(intent)
       // finish()
    }


/*       val player: GPUPlayerView = findViewById(R.id.player)
        player.setSimpleExoPlayer(mPlayer)
        val frame: Bitmap = VideoUtil.getFrameAtTime(mVideo, TimeUnit.SECONDS.toMicros(3))
        val square: Bitmap = BitmapUtil.getSquareThumbnail(frame, 250)
        frame.recycle()
        val rounded: Bitmap = BitmapUtil.addRoundCorners(square, 25)
        square.recycle()
        val adapter = FilterAdapter(this, rounded)
        adapter.setListener { filter: VideoFilter -> applyFilter(filter) }
        val filters = findViewById<RecyclerView>(R.id.filters)
        filters.adapter = adapter*/
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer!!.stop(true)
        mPlayer!!.setPlayWhenReady(false)
        mPlayer!!.release()
        mPlayer = null
    }

/*
    fun applyFilter(filter: VideoFilter) {
        Log.v(TAG, "User wants to apply " + filter.name().toString() + " filter.")
        val player: GPUPlayerView = findViewById(R.id.player)
        when (filter.also { mModel!!.filter = it }) {
            BRIGHTNESS -> {
                val glf = GlBrightnessFilter()
                glf.setBrightness(0.2f)
                player.setGlFilter(glf)
            }
            EXPOSURE -> player.setGlFilter(GlExposureFilter())
            GAMMA -> {
                val glf = GlGammaFilter()
                glf.setGamma(2f)
                player.setGlFilter(glf)
            }
            GRAYSCALE -> player.setGlFilter(GlGrayScaleFilter())
            HAZE -> {
                val glf = GlHazeFilter()
                glf.setSlope(-0.5f)
                player.setGlFilter(glf)
            }
            INVERT -> player.setGlFilter(GlInvertFilter())
            MONOCHROME -> player.setGlFilter(GlMonochromeFilter())
            PIXELATED -> {
                val glf = GlPixelationFilter()
                glf.setPixel(5)
                player.setGlFilter(glf)
            }
            POSTERIZE -> player.setGlFilter(GlPosterizeFilter())
            SEPIA -> player.setGlFilter(GlSepiaFilter())
            SHARP -> {
                val glf = GlSharpenFilter()
                glf.setSharpness(1f)
                player.setGlFilter(glf)
            }
            SOLARIZE -> player.setGlFilter(GlSolarizeFilter())
            VIGNETTE -> player.setGlFilter(GlVignetteFilter())
            else -> player.setGlFilter(GlFilter())
        }
    }
*/

    private fun closeFinally(clip: File) {
/*        Log.v(TAG, "Filter was successfully applied to $clip")
        val intent = Intent(this, UploadActivity::class.java)
        intent.putExtra(UploadActivity.EXTRA_SONG, mSong)
        intent.putExtra(UploadActivity.EXTRA_VIDEO, clip.absolutePath)
        startActivity(intent)
        finish()*/
    }

    private fun commitSelection() {
        mPlayer!!.setPlayWhenReady(false)

        val wm = WorkManager.getInstance(this)
        val filtered = File(cacheDir, UUID.randomUUID().toString())
        val data = Data.Builder()
            .putString(VideoFilterWorker.KEY_FILTER, mModel!!.filter.name)
            .putString(VideoFilterWorker.KEY_INPUT, mVideo)
            .putString(VideoFilterWorker.KEY_OUTPUT, filtered.absolutePath)
            .build()
        val request = OneTimeWorkRequest.Builder(VideoFilterWorker::class.java)
            .setInputData(data)
            .build()
        wm.enqueue(request)
        wm.getWorkInfoByIdLiveData(request.id)
            .observe(this) { info: WorkInfo ->
                val ended = (info.state == WorkInfo.State.CANCELLED
                        || info.state == WorkInfo.State.FAILED)
                if (info.state == WorkInfo.State.SUCCEEDED) {

                    closeFinally(filtered)
                } else if (ended) {

                }
            }
    }

    class ActivityFilterViewModel : ViewModel() {
        var filter: VideoFilter = VideoFilter.NONE
    }

    companion object {
        const val EXTRA_SONG = "song"
        const val EXTRA_VIDEO = "video"
        const val TAG = "ActivityFilter"
    }
}