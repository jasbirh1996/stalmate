package com.stalmate.user.modules.reels.activity.createFun

import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.modules.reels.filters.epf.EPlayerView
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.ui.activity.VideoEditorActivityResultContract

class SpeedReverseActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null
    lateinit var segmented_progressbar: LinearProgressIndicator

    private val videoUri: Uri
        get() = intent.getStringExtra("videoUri").toString().toUri()
    private val mimeType: String
        get() = intent.getStringExtra("mimeType").toString()
    private val imageVideoDuration: Int
        get() = intent.getIntExtra("imageVideoDuration", -0)

    lateinit var tabbarspeed: TabLayout
    private var isSpeedActive: Boolean = false
    private var isReverseActive: Boolean = false
    private var speed = 1f
    private lateinit var playerzview: EPlayerView
    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var ivPlay: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_speed_reverse)
        initViews()
    }

    private fun initViews() {
        segmented_progressbar = findViewById<LinearProgressIndicator>(R.id.segmented_progressbar)
        val buttonSpeed = findViewById<ConstraintLayout>(R.id.buttonSpeed)
        setuptabSpeedRecclerview()
        buttonSpeed.setOnClickListener { view: View? ->
            if (isSpeedActive) {
                isSpeedActive = false
                hideSpeedBar(show = false)
            } else {
                isSpeedActive = true
                hideSpeedBar(show = true)
            }
            updateColorButtons()
        }

        val buttonReverse = findViewById<ConstraintLayout>(R.id.buttonReverse)
        buttonReverse.setOnClickListener {
            if (isReverseActive) {
                isReverseActive = false
                updateColorButtons()
            } else {
                isReverseActive = true
                updateColorButtons()
                //reverseVideoCommand()
            }
        }

        val ivClose = findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener { onBackPressed() }

        val buttonDone = findViewById<TextView>(R.id.buttonDone)
        buttonDone.setOnClickListener {
            /*val outputPath = Common.getFilePath(this, Common.VIDEO)
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val asyncTask = FFmpegAsyncTask("-i $videoUri " +
                    "-vf setpts=PTS/${speed}  -crf 23 -preset ultrafast " +
                    "-vcodec libx264 -c:a aac  $outputPath",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        // In this example, we do not need access to the Uri(s) after the editor is closed
                        // so we pass false in the constructor
                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    }
                })
            asyncTask.execute()*/
            val settingsList = VideoEditorSettingsList(false)
                .configure<LoadSettings> {
                    // Set the source as the Uri of the video to be loaded
                    it.source = videoUri
                }
            videoEditorResult.launch(settingsList)
            // Release the SettingsList once done
            settingsList.release()
            mPlayer?.playWhenReady = false
            mPlayer?.pause()
            mPlayer?.stop()
            mPlayer = null
        }
        ivPlay = findViewById<ImageView>(R.id.ivPlay)
        ivPlay.setOnClickListener {
            setUpPlayer()
        }
        setupProgressBarWithDuration()
        setUpPlayer()
    }

    private class FFmpegAsyncTask(var command: String, var callback: OnTaskCompleted) :
        AsyncTask<Void?, Void?, Void?>() {
        protected override fun onPreExecute() {
            super.onPreExecute()

        }

        protected override fun doInBackground(vararg nc: Void?): Void? {
            FFmpeg.execute(command);
            return null
        }

        protected override fun onPostExecute(v: Void?) {
            callback.onTaskCompleted(true)
            super.onPostExecute(v)
        }


        public interface OnTaskCompleted {
            fun onTaskCompleted(isSuccess: Boolean);
        }
    }

    private fun setuptabSpeedRecclerview() {
        tabbarspeed = findViewById(R.id.tabbarspeed)
        tabbarspeed.animate().translationX(-1000f).setDuration(0).start()
        tabbarspeed.addTab(tabbarspeed.newTab().setText("0.5x"))
        tabbarspeed.addTab(tabbarspeed.newTab().setText("1x"))
        tabbarspeed.addTab(tabbarspeed.newTab().setText("2x"))
        tabbarspeed.addTab(tabbarspeed.newTab().setText("3x"))
        tabbarspeed.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        speed = 0.5f
                        createTimer((imageVideoDuration + (imageVideoDuration / 2)))
                        segmented_progressbar.max = (imageVideoDuration + (imageVideoDuration / 2))
                        hideSpeedBar(show = false)
                    }
                    1 -> {
                        speed = 1f
                        setupProgressBarWithDuration()
                        hideSpeedBar(show = false)
                    }
                    2 -> {
                        speed = 2f
                        createTimer((imageVideoDuration - (imageVideoDuration / 2)))
                        segmented_progressbar.max = (imageVideoDuration - (imageVideoDuration / 2))
                        hideSpeedBar(show = false)
                    }
                    3 -> {
                        speed = 3f
                        createTimer((imageVideoDuration - (imageVideoDuration / 3)))
                        segmented_progressbar.max = (imageVideoDuration - (imageVideoDuration / 3))
                        hideSpeedBar(show = false)
                    }
                }
                Handler(Looper.getMainLooper()).post {
                    runOnUiThread {
                        setUpPlayer()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                hideSpeedBar(show = false)
            }
        })
    }

    fun hideSpeedBar(show: Boolean) {
        if (show) {
            tabbarspeed.animate().translationX(0f).setDuration(500).start()
        } else {
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                tabbarspeed.animate().translationX(-1000f).setDuration(500).start()
            }, 500)
        }
    }

    private fun updateColorButtons() {
        val ivSpeed: ImageView = findViewById(R.id.ivSpeed)
        val tvSped: TextView = findViewById(R.id.tvSped)
        if (isSpeedActive) {
            ivSpeed.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_crtpost_speed_active
                )
            )
            tvSped.setTextColor(resources.getColor(R.color.colorYellow, null))
        } else {
            tvSped.setTextColor(resources.getColor(R.color.white, null))
            ivSpeed.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_crtpost_speed
                )
            )
        }
        val ivreverse: ImageView = findViewById(R.id.ivreverse)
        val tvreverse: TextView = findViewById(R.id.tvreverse)
        if (isReverseActive) {
            ivreverse.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.round_play_circle_24_active
                )
            )
            tvreverse.setTextColor(resources.getColor(R.color.colorYellow, null))
        } else {
            tvreverse.setTextColor(resources.getColor(R.color.white, null))
            ivreverse.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.round_play_circle_24
                )
            )
        }
    }

    private val videoEditorResult = registerForActivityResult(VideoEditorActivityResultContract()) {
        when (it.resultStatus) {
            EditorSDKResult.Status.CANCELED -> {}
            EditorSDKResult.Status.EXPORT_DONE -> {
                startActivity(
                    IntentHelper.getCreateFuntimePostScreen(this)!!
                        .putExtra(ActivityFilter.EXTRA_VIDEO, it.resultUri.toString())
                        .putExtra("mimeType", mimeType)
                        .putExtra("isImage", false)
                )
            }
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mPlayer?.playWhenReady = false
        mPlayer?.pause()
        mPlayer?.stop()
        mPlayer = null
    }

    private fun setUpPlayer() {
        countDownTimer?.cancel()
        countDownTimer?.start()
        mPlayer = SimpleExoPlayer.Builder(this).build()
        playerzview = EPlayerView(this)
        mPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
        val factory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)//Uri.fromFile())
        val source: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        mPlayer?.setPlaybackSpeed(speed)
        mPlayer?.prepare(source);
        mPlayer?.playWhenReady = true;
        playerzview.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            runOnUiThread {
                playerzview.setSimpleExoPlayer(mPlayer)
                playerzview.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                // add ePlayerView to WrapperView
                // add ePlayerView to WrapperView
                val layoutMovieWrapper = findViewById<FrameLayout>(R.id.layout_movie_wrapper)
                layoutMovieWrapper.removeAllViews()
                layoutMovieWrapper.addView(playerzview)
                playerzview.onResume()
            }
        }, 500)
        Log.d("aklsjdlasd", "fourth")
    }

    private fun createTimer(duration: Int) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((duration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ivPlay.visibility = View.GONE
                segmented_progressbar.visibility = View.VISIBLE
                segmented_progressbar.progress = (duration - (millisUntilFinished / 1000)).toInt()
            }

            override fun onFinish() {
                ivPlay.visibility = View.VISIBLE
                segmented_progressbar.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupProgressBarWithDuration() {
        createTimer(imageVideoDuration)
        segmented_progressbar.max = imageVideoDuration
    }
}