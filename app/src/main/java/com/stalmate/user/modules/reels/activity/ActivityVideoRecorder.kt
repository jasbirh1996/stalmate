package com.stalmate.user.modules.reels.activity


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.arthenica.mobileffmpeg.FFmpeg
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.tabs.TabLayout
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Engine
import com.otaliastudios.cameraview.filter.Filters
import com.otaliastudios.cameraview.filters.BrightnessFilter
import com.otaliastudios.cameraview.filters.GammaFilter
import com.otaliastudios.cameraview.filters.SharpnessFilter
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityVideoRecorderBinding
import com.stalmate.user.modules.reels.adapter.FilterAdapterNew
import com.stalmate.user.modules.reels.adapter.GalleryItem
import com.stalmate.user.modules.reels.filters.*
import com.stalmate.user.modules.reels.filters.epf.EPlayerView
import com.stalmate.user.modules.reels.filters.epf.filter.*
import com.stalmate.user.modules.reels.photo_editing.RangeBSFragmnet
import com.stalmate.user.modules.reels.utils.VideoFilter
import com.stalmate.user.modules.reels.utils.VideoUtil
import com.stalmate.user.modules.reels.workers.VideoFilterWorker
import com.stalmate.user.utilities.Common
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.PathUtil
import com.stalmate.user.utilities.PriceFormatter
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ActivityVideoRecorder : BaseActivity(), FragmentGallery.GalleryPickerListener,
    FilterAdapterNew.OnFilterSelectListener, RangeBSFragmnet.RangeSelectedListener {

    private val TAG = "RecorderActivity"
    private var imageVideoDuration = 15
    private var mModel: RecorderActivityViewModel? = null
    private val mHandler = Handler()
    var songId = ""
    private var isImage = true
    private var isImageTakenByCamera = false
    private var isVideoTaken = false
    private var isMusicSelected = false
    private var isFilterApplied = false
    private var mMediaPlayer: MediaPlayer? = null
    val PICK_FILE = 99
    private val mStopper = Runnable { stopRecording() }
    lateinit var binding: ActivityVideoRecorderBinding
    override fun onClick(viewId: Int, view: View?) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemBars()
        super.onCreate(savedInstanceState)
        binding = ActivityVideoRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPlayer = ExoPlayer.Builder(this).build()
        getGalleryData()
        rangeFragment = RangeBSFragmnet(imageVideoDuration)
        rangeFragment.setEmojiListener(this)
        binding.cameraView.engine = Engine.CAMERA2
        mModel = ViewModelProvider(this)[RecorderActivityViewModel::class.java]
        isPermissionGranted(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
            )
        )

        mMediaPlayer = MediaPlayer()
        mMediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        // getGalleryData()

        binding.selectedPhoto.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        binding.cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        binding.cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        binding.cameraView.clearGesture(Gesture.LONG_TAP)
        binding.cameraView.useDeviceOrientation = false
        binding.cameraView.addCameraListener(object : CameraListener() {


            override fun onPictureTaken(result: PictureResult) {
                isImageTakenByCamera = true
                result.toFile(File(cacheDir, UUID.randomUUID().toString()), FileCallback {


                    mModel!!.video = it


                    result.toBitmap {
                        binding.selectedPhoto.visibility = View.VISIBLE
                        binding.selectedPhoto.setImage(it);

                        setupProgressBarWithDuration()
                        setUPCameraViewsOnCapture(false)

                        isFilterActive = false
                        updateColorButtons()
                    }

                })
                super.onPictureTaken(result)

            }

            override fun onZoomChanged(
                newValue: Float,
                bounds: FloatArray,
                fingers: Array<PointF?>?
            ) {
                // newValue: the new zoom value
                // bounds: this is always [0, 1]
                // fingers: if caused by touch gestures, these is the fingers position
            }

            override fun onCameraOpened(options: CameraOptions) {
                super.onCameraOpened(options)
                setUpFilterAdapter()
            }

            override fun onVideoRecordingEnd() {
                Log.d("rwehfilshdlf", "recordingEnd")
                setUPCameraViewsOnCapture(false)
                mMediaPlayer!!.pause()
                progressHandler.removeCallbacks(runnable)
                binding.buttonRecord.isSelected = false
                pauseProgress()
                mHandler.postDelayed({ processCurrentRecording() }, 500)
            }

            override fun onVideoRecordingStart() {
                isFilterActive = false
                updateColorButtons()
                mMediaPlayer!!.start()
                binding.buttonRecord.isSelected = true
                /*   if (mMediaPlayer != null) {
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                           var speed = 1f
                           if (mModel!!.speed == .5f) {
                               speed = 2f
                           } else if (mModel!!.speed == .75f) {
                               speed = 1.5f
                           } else if (mModel!!.speed == 1.5f) {
                               speed = .75f
                           } else if (mModel!!.speed == 2f) {
                               speed = .5f
                           }
                           val params = PlaybackParams()
                           params.speed = speed
                           mMediaPlayer!!.playbackParams = params
                       }
                       mMediaPlayer!!.start()
                   }*/
                progressHandler.removeCallbacks(runnable)
                progressHandler.post(runnable)
                Log.d("aplskdasd", "recordingStarted")
            }
        })
        if (intent.getStringExtra(EXTRA_SONG_ID) != null) {
            Log.d("ajkhdkasd", intent.getStringExtra(EXTRA_SONG_ID).toString())
            Log.d("ajkhdkasd", intent.getStringExtra(EXTRA_SONG_FILE).toString())
            songId = intent.getStringExtra(EXTRA_SONG_ID).toString()
            binding.tvMusicName.text = intent.getStringExtra(EXTRA_SONG_NAME).toString()
            binding.layoutSelectedMusic.visibility = View.VISIBLE
            mModel!!.audio = Uri.parse(intent.getStringExtra(EXTRA_SONG_FILE).toString())
            ImageLoaderHelperGlide.setGlideCorner(
                this,
                binding.ivMusicImage,
                intent.getStringExtra(EXTRA_SONG_COVER).toString(),
                R.drawable.user_placeholder
            )
        }
        setupFiltersRV()
        setUPViews()
        setUPCameraViewsOnCapture(true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BaseActivity.MULTIPLE_PERMISSIONS) {
            Log.d("alksjdlsadas", "aosjdopasd")

            var isPerpermissionForAllGranted = false
            if (grantResults.isNotEmpty() && permissions.size == grantResults.size) {
                var i = 0
                while (i < permissions.size) {
                    isPerpermissionForAllGranted =
                        grantResults[i] == PackageManager.PERMISSION_GRANTED
                    i++
                }

                Log.e("value", "Permission Granted, Now you can use local drive .")
            } else {
                isPerpermissionForAllGranted = true
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
            if (isPerpermissionForAllGranted) {
                runOnUiThread {

                    getGalleryData()

                    binding.cameraView.open()
                }
            }
        }
    }

    fun setUPCameraViewsOnCapture(isDefault: Boolean) {
        binding.tvCountDownValue.visibility = View.GONE
        if (isImage) {
            if (isDefault) {
                binding.buttonSpeed.visibility = View.VISIBLE
                binding.buttonDone.visibility = View.GONE
                binding.buttonMusic.visibility = View.VISIBLE
                binding.buttonCaptureCounter.visibility = View.VISIBLE
                binding.buttonColorFilterIcon.visibility = View.VISIBLE
                binding.segmentedProgressbar.visibility = View.GONE
                binding.buttonPickData.visibility = View.VISIBLE
                binding.selectedPhoto.visibility = View.GONE
                binding.cameraView.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.VISIBLE
                //  binding.buttonFlash.visibility = View.GONE
                runOnUiThread {
                    binding.cameraView.open()
                }
                binding.buttonDurationTimer.visibility = View.GONE

            } else {
                if (isFilterApplied) {
                    binding.buttonColorFilterIcon.visibility = View.GONE
                } else {
                    binding.buttonColorFilterIcon.visibility = View.VISIBLE
                }
                binding.buttonCaptureCounter.visibility = View.GONE
                binding.buttonDone.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.GONE
                binding.selectedPhoto.visibility = View.VISIBLE
                //  binding.buttonFlash.visibility = View.GONE
                binding.buttonSpeed.visibility = View.GONE
                binding.cameraView.visibility = View.GONE
                binding.rvFilters.visibility = View.GONE
                binding.cameraView.close()
                binding.cameraView.invalidate()
            }
            if (isMusicSelected) {
                binding.segmentedProgressbar.visibility = View.VISIBLE
            } else {
                binding.segmentedProgressbar.visibility = View.GONE
                binding.buttonMusic.visibility = View.VISIBLE
                binding.buttonDurationTimer.visibility = View.VISIBLE
            }
            binding.stopIConView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.circle_camerabackground
                )
            )
        } else {
            if (isDefault) {
                binding.buttonMusic.visibility = View.VISIBLE
                binding.buttonSpeed.visibility = View.VISIBLE
                //  binding.buttonFlash.visibility = View.VISIBLE
                binding.buttonColorFilterIcon.visibility = View.VISIBLE
                binding.cameraView.visibility = View.VISIBLE
                binding.buttonCaptureCounter.visibility = View.VISIBLE
                binding.segmentedProgressbar.visibility = View.GONE
                binding.buttonPickData.visibility = View.VISIBLE
                //  binding.buttonFlash.visibility = View.GONE
                binding.layoutMovieWrapper.visibility = View.GONE
                binding.buttonDurationTimer.visibility = View.GONE
                binding.layoutBottomControll.visibility = View.VISIBLE


            } else {
                if (isMusicSelected) {
                    binding.segmentedProgressbar.visibility = View.VISIBLE
                } else {
                    binding.segmentedProgressbar.visibility = View.VISIBLE
                    binding.buttonMusic.visibility = View.VISIBLE
                }
                if (isFilterApplied) {
                    binding.buttonColorFilterIcon.visibility = View.GONE
                } else {
                    binding.buttonColorFilterIcon.visibility = View.VISIBLE
                }
                runOnUiThread {
                    binding.cameraView.close()
                    binding.cameraView.visibility = View.GONE

                }
                binding.buttonCaptureCounter.visibility = View.GONE
                binding.buttonMusic.visibility = View.VISIBLE
                binding.buttonDone.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.VISIBLE
                binding.buttonPickData.visibility = View.GONE
                //  binding.buttonFlash.visibility = View.GONE
                binding.buttonDurationTimer.visibility = View.GONE
                binding.buttonSpeed.visibility = View.VISIBLE
                binding.rvFilters.visibility = View.GONE
                binding.layoutBottomControll.visibility = View.GONE

            }

            binding.stopIConView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.record
                )
            )


        }

    }


    override fun onStart() {
        Log.d("kasdasd1", "askldassdfsdfd")
        if (mMediaPlayer != null) {
            mMediaPlayer!!.start()
            resumeProgress()
            if (this::playerzview.isInitialized) {
                playerzview.pLayer.play()
            }
            if (!isImageTakenByCamera && isImage) {
                binding.cameraView.open()
            } else if (!isImage && !isVideoTaken) {
                Log.d("aklsjdlasd", "oaiudoiasd")
                binding.cameraView.open()
            } else {

            }
        }
        super.onStart()
    }


    override fun onPause() {
        if (mMediaPlayer != null) {
            pauseProgress()
            binding.cameraView.close()
            captureTimerHandler.removeCallbacks(runnableForTimer)
            if (this::playerzview.isInitialized) {
                playerzview.pLayer.pause()
            }

        }
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()

        if (mMediaPlayer != null) {

            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
            binding.cameraView.destroy()

        }
    }


    private val snapHelper = CenterSnapHelper()
    fun setUpFilterAdapter() {
    }

    lateinit var filterAdapter: FilterAdapterNew
    fun setupFiltersRV() {
        filterAdapter =
            FilterAdapterNew(
                this@ActivityVideoRecorder,
                binding.cameraView,
                true
            )
        filterAdapter.setListener(this)

        /*          val snapHelper = LinearSnapHelper()
                       snapHelper.attachToRecyclerView(  binding.rvFilters)
*/

        binding.rvFilters.isNestedScrollingEnabled = false
        binding.rvFilters.setHasFixedSize(true)
        binding.rvFilters.adapter = filterAdapter
        //      binding.rvFilters.addItemDecoration(OffsetItemDecoration(this@ActivityVideoRecorder))
        //  binding.rvFilters.layoutManager=CenterZoomLayoutManager(this@ActivityVideoRecorder, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilters.addItemDecoration(CenterDecoration(0))
        snapHelper.attachToRecyclerView(binding.rvFilters)


        binding.rvFilters.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView =
                        snapHelper.findSnapView(binding.rvFilters.layoutManager)
                    val pos =
                        (binding.rvFilters.layoutManager as LinearLayoutManager).getPosition(
                            centerView!!
                        )
                    filterAdapter.setCenterColor(pos)
                    when (pos) {
                        0 -> {

                            applyPreviewFilter(VideoFilter.NONE)
                        }
                        1 -> {
                            applyPreviewFilter(VideoFilter.BRIGHTNESS)
                        }
                        2 -> {
                            applyPreviewFilter(VideoFilter.EXPOSURE)
                        }
                        3 -> {
                            applyPreviewFilter(VideoFilter.GAMMA)
                        }
                        4 -> {
                            applyPreviewFilter(VideoFilter.GRAYSCALE)
                        }
                        5 -> {
                            applyPreviewFilter(VideoFilter.HAZE)
                        }
                        6 -> {
                            applyPreviewFilter(VideoFilter.INVERT)
                        }
                        7 -> {
                            applyPreviewFilter(VideoFilter.MONOCHROME)
                        }
                        8 -> {
                            applyPreviewFilter(VideoFilter.PIXELATED)
                        }
                        9 -> {
                            applyPreviewFilter(VideoFilter.POSTERIZE)
                        }
                        10 -> {
                            applyPreviewFilter(VideoFilter.SEPIA)
                        }
                        11 -> {
                            applyPreviewFilter(VideoFilter.SHARP)
                        }
                        12 -> {
                            applyPreviewFilter(VideoFilter.SOLARIZE)
                        }
                        13 -> {
                            applyPreviewFilter(VideoFilter.VIGNETTE)
                        }


                    }
                }

            }
        })


    }


    private fun applyPreviewFilter(filter: VideoFilter) {
        activeFilter = filter
        isFilterApplied = true
        if (isImage) {
            when (filter) {
                VideoFilter.BRIGHTNESS -> {
                    val glf = GPUImageBrightnessFilter()
                    glf.setBrightness(0.2f)
                    binding.selectedPhoto.setFilter(glf)
                }
                VideoFilter.EXPOSURE -> binding.selectedPhoto.setFilter(GPUImageExposureFilter())
                VideoFilter.GAMMA -> {
                    val glf = GPUImageGammaFilter()
                    glf.setGamma(2f)
                    binding.selectedPhoto.setFilter(glf)
                }
                VideoFilter.GRAYSCALE -> binding.selectedPhoto.setFilter(GPUImageGrayscaleFilter())
                VideoFilter.HAZE -> {
                    val glf = GPUImageHazeFilter()
                    glf.setSlope(-0.5f)
                    binding.selectedPhoto.setFilter(glf)
                }
                VideoFilter.INVERT -> binding.selectedPhoto.setFilter(GPUImageColorInvertFilter())
                VideoFilter.MONOCHROME -> binding.selectedPhoto.setFilter(GPUImageMonochromeFilter())
                VideoFilter.PIXELATED -> {
                    val glf = GPUImagePixelationFilter()
                    glf.setPixel(5f)
                    binding.selectedPhoto.setFilter(glf)
                }
                VideoFilter.POSTERIZE -> binding.selectedPhoto.setFilter(GPUImagePosterizeFilter())
                VideoFilter.SEPIA -> binding.selectedPhoto.setFilter(GPUImageSepiaToneFilter())
                VideoFilter.SHARP -> {
                    val glf = GPUImageSharpenFilter()
                    glf.setSharpness(1f)
                    binding.selectedPhoto.setFilter(glf)
                }
                VideoFilter.SOLARIZE -> binding.selectedPhoto.setFilter(GPUImageSolarizeFilter())
                VideoFilter.VIGNETTE -> binding.selectedPhoto.setFilter(GPUImageVignetteFilter())
                else -> {
                    Log.d("klasjdasd", "alskdjasd")
                    isFilterApplied = false
                    binding.selectedPhoto.setFilter(GPUImageFilter())
                }
            }
        }
        when (filter) {
            VideoFilter.BRIGHTNESS -> {
                val glf = Filters.BRIGHTNESS.newInstance() as BrightnessFilter
                glf.brightness = 1.2f
                binding.cameraView.setFilter(glf)
                playerzview.setGlFilter(GlBrightnessFilter())


            }
            VideoFilter.EXPOSURE -> {
                binding.cameraView.setFilter(ExposureFilter())
                playerzview.setGlFilter(GlExposureFilter())
            }
            VideoFilter.GAMMA -> {
                val glf = Filters.GAMMA.newInstance() as GammaFilter
                glf.gamma = 2f
                binding.cameraView.setFilter(glf)
                playerzview.setGlFilter(GlGammaFilter())
            }
            VideoFilter.GRAYSCALE -> {
                binding.cameraView.setFilter(Filters.GRAYSCALE.newInstance())
                playerzview.setGlFilter(GlGrayScaleFilter())
            }
            VideoFilter.HAZE -> {
                val glf = HazeFilter()
                glf.setSlope(-0.5f)
                binding.cameraView.setFilter(glf)
                playerzview.setGlFilter(GlHazeFilter())
            }

            VideoFilter.INVERT -> {
                binding.cameraView.setFilter(Filters.INVERT_COLORS.newInstance())
                playerzview.setGlFilter(GlInvertFilter())
            }
            VideoFilter.MONOCHROME -> {
                binding.cameraView.setFilter(MonochromeFilter())
                playerzview.setGlFilter(GlMonochromeFilter())
            }
            VideoFilter.PIXELATED -> {
                val glf = PixelatedFilter()
                glf.setPixel(5.0f)
                binding.cameraView.setFilter(glf)
                playerzview.setGlFilter(GlPixelationFilter())
            }
            VideoFilter.POSTERIZE -> {
                binding.cameraView.setFilter(Filters.POSTERIZE.newInstance())
                playerzview.setGlFilter(GlPosterizeFilter())
            }
            VideoFilter.SEPIA -> {
                binding.cameraView.setFilter(Filters.SEPIA.newInstance())
                playerzview.setGlFilter(GlSepiaFilter())
            }
            VideoFilter.SHARP -> {
                val glf = Filters.SHARPNESS.newInstance() as SharpnessFilter
                glf.sharpness = 0.25f
                binding.cameraView.setFilter(glf)
                playerzview.setGlFilter(GlSharpenFilter())
            }
            VideoFilter.SOLARIZE -> {
                binding.cameraView.setFilter(SolarizeFilter())
                playerzview.setGlFilter(GlSolarizeFilter())
            }
            VideoFilter.VIGNETTE -> {
                binding.cameraView.setFilter(Filters.VIGNETTE.newInstance())
                playerzview.setGlFilter(GlVignetteFilter())
            }
            else -> {
                binding.cameraView.setFilter(Filters.NONE.newInstance())
                playerzview.setGlFilter(GlFilter())
            }
        }


    }

    var recorded = 0.toLong()
    private fun startRecording() {
        recorded = mModel!!.recorded()
        if (recorded >= TimeUnit.SECONDS.toMillis(imageVideoDuration.toLong())) {
            Toast.makeText(
                this@ActivityVideoRecorder,
                R.string.recorder_error_maxed_out,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mModel!!.video = File(cacheDir, UUID.randomUUID().toString())
            binding.cameraView.takeVideoSnapshot(
                mModel!!.video!!,
                ((TimeUnit.SECONDS.toMillis(imageVideoDuration.toLong()) - recorded + 10).toInt())
            )

        }
    }

    private fun stopRecording() {
        binding.cameraView.stopVideo()
        //   mHandler.removeCallbacks(mStopper)
    }

    var activeFilter = VideoFilter.NONE
    private fun commitRecordings() {
        showLoader()
        val videos: MutableList<String> = ArrayList()
        for (segment in mModel!!.segments) {
            videos.add(segment.file!!.absolutePath)
        }

        if (mModel!!.speed != 1f) {
            val outputPath = Common.getFilePath(this, Common.VIDEO)
            showLoader()
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val asyncTask =
                FFmpegAsyncTask("-i ${mModel!!.segments[0].file!!.absolutePath} -vf setpts=PTS/${mModel!!.speed.toString()}  -crf 23 -preset ultrafast -vcodec libx264 -c:a aac  $outputPath",
                    object : FFmpegAsyncTask.OnTaskCompleted {
                        override fun onTaskCompleted(isSuccess: Boolean) {
                            Log.d("lkajsdadasd", "ioasoiduasd")
                            finishVideoWithFilter(File(outputPath))
                        }
                    })
            asyncTask.execute()

        } else {
            val outputPath = Common.getFilePath(this, Common.VIDEO)
            showLoader()
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val asyncTask =
                FFmpegAsyncTask("-f concat -safe 0 -i ${generateVideoListFile(videos)}   -crf 23 -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                    object : FFmpegAsyncTask.OnTaskCompleted {
                        override fun onTaskCompleted(isSuccess: Boolean) {
                            finishVideoWithFilter(File(outputPath))
                        }
                    })
            asyncTask.execute()
        }


    }


    private fun finishVideoWithFilter(file: File) {
        mPlayer!!.setPlayWhenReady(false)
        val wm = WorkManager.getInstance(this)
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val data = Data.Builder()
            .putString(VideoFilterWorker.KEY_FILTER, activeFilter.toString())
            .putString(VideoFilterWorker.KEY_INPUT, file.absolutePath)
            .putString(VideoFilterWorker.KEY_OUTPUT, outputPath)
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

                    dismissLoader()
                    closeFinally(File(outputPath))
                    if (!isImage) {
                        isVideoTaken = true
                        binding.buttonDone.visibility = View.VISIBLE
                    }
                } else if (ended) {

                }
            }
    }


    fun applyFilterUsingFFMPEG(filer: VideoFilter) {

        var filterString = ""

        when (filer) {
            VideoFilter.BRIGHTNESS -> {

            }
            VideoFilter.EXPOSURE -> {

            }
            VideoFilter.GAMMA -> {

            }
            VideoFilter.GRAYSCALE -> {

            }
            VideoFilter.HAZE -> {

            }

            VideoFilter.INVERT -> {

            }
            VideoFilter.MONOCHROME -> {

            }
            VideoFilter.PIXELATED -> {

            }
            VideoFilter.POSTERIZE -> {

            }
            VideoFilter.SEPIA -> {

            }
            VideoFilter.SHARP -> {

            }
            VideoFilter.SOLARIZE -> {

            }
            VideoFilter.VIGNETTE -> {
                filterString = "vignette"
            }
            else -> {

            }
        }


        val outputPath = Common.getFilePath(this, Common.VIDEO)
        showLoader()
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val asyncTask =
            FFmpegAsyncTask("-i ${mModel!!.segments[0].file!!.absolutePath} -vf $filterString -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        dismissLoader()
                        if (!isImage) {
                            isVideoTaken = true
                            binding.buttonDone.visibility = View.VISIBLE
                        }
                        closeFinally(File(outputPath))
                    }
                })
        asyncTask.execute()
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

    /**
     * Generate an ffmpeg file list
     * @param inputs Input files for ffmpeg
     * @return File path
     */

    private fun generateVideoListFile(inputs: MutableList<String>): String? {
        val list: File
        var writer: Writer? = null
        try {
            list = File.createTempFile("ffmpeg-list", ".txt")
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(list)))
            for (input in inputs) {
                writer.write("file '$input'\n")
                Log.d(TAG, "Writing to list file: file '$input'")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "/"
        } finally {
            try {
                writer?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        Log.d(TAG, "Wrote list file to " + list.absolutePath)
        return list.absolutePath
    }


    /**
     * Generate an ffmpeg file list
     * @param inputs Input files for ffmpeg
     * @return File path
     */
    private fun generateIMagesListFile(
        inputs: MutableList<String>,
        perIMageDuration: Int
    ): String? {
        val list: File
        var writer: Writer? = null
        try {
            list = File.createTempFile("ffmpeg-list", ".txt")
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(list)))
            for (input in inputs) {
                writer.write("file '$input'\nduration $perIMageDuration\n")
                Log.d(TAG, "Writing to list file: file '$input'")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "/"
        } finally {
            try {
                writer?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        Log.d(TAG, "Wrote list file to " + list.absolutePath)
        return list.absolutePath
    }


    private fun processCurrentRecording() {
        showLoader()
        val duration: Long = VideoUtil.getDuration(this, Uri.fromFile(mModel!!.video))






        applyVideoSpeed(mModel!!.video!!, mModel!!.speed, duration)
/*        mModel!!.speed = 1f
        mModel!!.video = null*/
    }

    private fun applyVideoSpeed(file: File, speed: Float, duration: Long) {
        applySpeed(file, speed, duration)
    }

    private fun closeFinally(file: File) {

        binding.cameraView.close()
        //  binding.cameraView.destroy()
        val intent = Intent(this, ActivityVideoEditor::class.java)

        if (mModel!!.audio != null) {
            intent.putExtra(ActivityFilter.EXTRA_SONG, File(mModel!!.audio!!.path!!).absolutePath)
        }

        intent.putExtra(EXTRA_SONG_DURATION, imageVideoDuration.toString())
        intent.putExtra(ActivityFilter.EXTRA_VIDEO, file.absolutePath)



        intent.putExtra("isImage", isImage)
        intent.putExtra(EXTRA_SONG_ID, songId)
        try {
            Log.d("asdasdasdxx", songId)

        } catch (e: java.lang.Exception) {
            Log.d("asdasdasdxx", "lasjda")
        }

        pauseProgress()
        startActivity(intent)

    }

    class RecorderActivityViewModel : ViewModel() {
        var audio: Uri? = null
        var segments: ArrayList<RecordSegment> = ArrayList()
        var song = 0
        var speed = 1f
        var video: File? = null
        fun recorded(): Long {
            var recorded: Long = 0
            for (segment in segments) {
                recorded += segment.duration
            }
            return recorded
        }
    }

    class RecordSegment {
        var file: File? = null
        var duration: Long = 0
    }


    private fun imageChooser() {
        val i = Intent()
        i.type = "image/* video/*"
        i.action = Intent.ACTION_GET_CONTENT
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launchActivityForImagePick.launch(i)
    }

    var imageCountname = 1
    var launchActivityForImagePick = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK
        ) {

            var data: Intent = result.data!!
            var pathList = ArrayList<String>()

            if (data.clipData != null) {

                val mClipData: ClipData? = data.clipData
                val mArrayUri = ArrayList<Uri>()

                var totalImages = mClipData!!.itemCount

                Log.d("kasdasd", totalImages.toString())
                var perImageDuration = (imageVideoDuration / totalImages).toInt()


                for (i in 0 until mClipData.itemCount) {
                    val item: ClipData.Item = mClipData.getItemAt(i)
                    val uri: Uri = item.uri
                    var path = PathUtil.getPath(this, uri)
                    pathList.add(path)
                    Log.d("lkajsda", uri.path.toString())
                    if (path != null) {
                        val cR = contentResolver
                        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
                        val type: String = mime.getExtensionFromMimeType(cR.getType(uri))!!
                        if (type == "mp4") {
                            Log.d(";lasda", "video")
                            val duration: Long =
                                VideoUtil.getDuration(this, Uri.fromFile(File(path)))
                            //video duration validation
                            if (duration >= 15 * 1000) {
                                Log.d("Greater", "Greater")
                                isImage = false
                                isVideoTaken = true

                                try {
                                    setUPCameraViewsOnCapture(false)
                                    mModel!!.video = File(path)
                                    val duration: Long =
                                        VideoUtil.getDuration(this, Uri.fromFile(mModel!!.video))
                                    Log.d("kjashjkdas", duration.toString())

                                    if (duration > imageVideoDuration * 1000) {
                                        /*            binding.segmentedProgressbar.progress = ((duration * 100) / imageVideoDuration).toInt()
                                                    prolength = ((duration * 100) / imageVideoDuration).toInt()*/
                                        pauseProgress()
                                        applySpeedWithDuration(mModel!!.video!!)

                                    } else {

                                        binding.segmentedProgressbar.progress =
                                            ((duration * 100) / imageVideoDuration).toInt()
                                        prolength = ((duration * 100) / imageVideoDuration).toInt()
                                        pauseProgress()
                                        processCurrentRecording()
                                    }


                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                break
                            } else {
                                Log.d("Less", "Less")
                                makeToast("Video should be grater than or equal to 15 seconds")
                            }
                        } else {

                        }

                    }

                }

                if (isImage) {
                    multipleImageToVideo(pathList, perImageDuration)
                } else {

                }


            } else {

                val cR = contentResolver
                val mime: MimeTypeMap = MimeTypeMap.getSingleton()
                val type: String = mime.getExtensionFromMimeType(cR.getType(data.data!!))!!

                Log.d(";lasda", type)
                if (type == "jpg") {
                    Log.d(";lasda", "imagee")
                    isImage = true
                    isImageTakenByCamera = true

                    val selectedImageUri = data.data
                    val selectedImageBitmap: Bitmap
                    startCrop(selectedImageUri!!)

                } else if (type == "mp4") {
                    Log.d(";lasda", "video")

                    val duration: Long = VideoUtil.getDuration(
                        this,
                        Uri.fromFile(File(getRealPathFromURIVideo(this, data!!.data!!)))
                    )
                    Log.d("dur", duration.toString())
                    if (duration >= 15 * 1000) {
                        Log.d("dura", "success")
                        isImage = false
                        isVideoTaken = true
                        // setUPCameraViewsOnCapture(false)
                        try {
                            setUPCameraViewsOnCapture(false)
                            Log.d("a;lskdasdhhhhhh", "data.data!!.path!!")
                            Log.d("a;lskdasdkjjjjjj", data.data!!.path!!)
                            mModel!!.video = File(getRealPathFromURIVideo(this, data!!.data!!))
                            Log.d("a;lskdasdkjjjjjj", mModel!!.video!!.absolutePath)
                            val duration: Long =
                                VideoUtil.getDuration(this, Uri.fromFile(mModel!!.video))
                            binding.segmentedProgressbar.progress =
                                ((duration * 100) / imageVideoDuration).toInt()
                            prolength = ((duration * 100) / imageVideoDuration).toInt()
                            pauseProgress()
                            processCurrentRecording()
                            Log.d(";alsjkdasd", "alskjdasd")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        makeToast("Video should be grater than or equal to 15 seconds")
                        Log.d("dura", "Fail")
                    }


                }

            }


        }
    }


    @Throws(IOException::class)
    fun copy(src: File?, dst: File?) {
        val `in`: InputStream = FileInputStream(src)
        try {
            val out: OutputStream = FileOutputStream(dst)
            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            } finally {
                out.close()
            }
        } finally {
            `in`.close()
        }
    }


    private fun videoChooser() {
        val i = Intent()
        i.type = "video/*"
        i.action = Intent.ACTION_GET_CONTENT
        launchActivityForVideoPick.launch(i)
    }

    var launchActivityForVideoPick = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode
            == RESULT_OK
        ) {
            var data: Intent = result.data!!
            // do your operation from here....
            if (data != null
                && data.data != null
            ) {
                val selectedVideo = data.data
                val selectedImageBitmap: Bitmap
                /*       try {
                           Log.d("a;lskdasd", "data.data!!.path!!")
                           Log.d("a;lskdasd", data.data!!.path!!)
                           mModel!!.video = File(data.data!!.path!!)
                           processCurrentRecording()
                           Log.d(";alsjkdasd", "alskjdasd")
                       } catch (e: IOException) {
                           e.printStackTrace()
                       }
       */
                /*        mModel!!.video = File(getRealPathFromURIVideo(this, data.data!!))
                        mModel!!.video=File(getVideoContentUri(this, data.data!!)!!)
                      */
                Handler(Looper.getMainLooper()).post {

                    runOnUiThread {
                        processCurrentRecording()
                    }
                }
                Log.d(";alsjkdasd", "alskjdasd")


            }
        }
    }

    fun playMusic(soundFile: String) {

        try {

            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(soundFile)
            mMediaPlayer!!.prepareAsync()
        } catch (e: IOException) {
            Log.d("asldkjasd", e.toString())
            e.printStackTrace()
        }
        mMediaPlayer!!.setOnPreparedListener { mp: MediaPlayer ->
            mMediaPlayer!!.start()
        }


    }


    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            cursor!!.getString(column_index)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "getRealPathFromURI Exception : $e")
            ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun getRealPathFromURIVideo(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Video.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor!!.moveToFirst()
            cursor!!.getString(column_index)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "getRealPathFromURI Exception : $e")
            ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }


    var resultCallbackOfSelectedMusicTrack: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result!!.resultCode == RESULT_OK) {
                val data: Intent = result.getData()!!
                songId = data.getStringExtra(EXTRA_SONG_ID).toString()
                val name = data.getStringExtra(EXTRA_SONG_NAME)
                val audio = data.getParcelableExtra<Uri>(EXTRA_SONG_FILE)
                isMusicSelected = true
                binding.buttonDurationTimer.visibility = View.GONE
                ImageLoaderHelperGlide.setGlideCorner(
                    this,
                    binding.ivMusicImage,
                    data.getStringExtra(EXTRA_SONG_COVER).toString(),
                    R.drawable.user_placeholder
                )
                Log.d("klajsdasd", audio!!.path.toString())
                binding.tvMusicName.text = name
                binding.layoutSelectedMusic.visibility = View.VISIBLE
                mModel!!.audio = audio
                playMusic(audio.path.toString())
                isMusicActive = true
                updateColorButtons()

                binding.segmentedProgressbar.progress = 0
                setupProgressBarWithDuration()
                resumeProgress()
            }
        }


    fun setupRecordDurationRecclerview() {
        binding.tabbarduration.animate().translationX(-1000f).setDuration(0).start()
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("15 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("30 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("60 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("90 Sec"))

        binding.tabbarduration.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.tvTimer.setTextColor(resources.getColor(R.color.colorYellow, null))
                when (tab!!.position) {
                    0 -> {
                        imageVideoDuration = 15
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder,
                                R.drawable.ic_crtpost_timer_one_active
                            )
                        )
                        hideDurationBar(show = false)
                        setupProgressBarWithDuration()


                    }
                    1 -> {
                        imageVideoDuration = 30
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder,
                                R.drawable.ic_crtpost_timer_two_active
                            )
                        )
                        hideDurationBar(show = false)
                        setupProgressBarWithDuration()

                    }
                    2 -> {
                        imageVideoDuration = 60
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder,
                                R.drawable.ic_crtpost_timer_three_active
                            )
                        )
                        hideDurationBar(show = false)

                    }
                    3 -> {
                        imageVideoDuration = 90
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder,
                                R.drawable.ic_crtpost_timer_three_active
                            )
                        )
                        hideDurationBar(show = false)

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                hideDurationBar(show = false)
            }
        })
    }


    fun hideDurationBar(show: Boolean) {

        if (show) {
            binding.tabbarduration.animate().translationX(0f).setDuration(500).start()
        } else {
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                binding.tabbarduration.animate().translationX(-1000f).setDuration(500).start()
            }, 500)
        }
    }


    fun setuptabSpeedRecclerview() {
        var speed = 1f
        binding.tabbarspeed.animate().translationX(-1000f).setDuration(0).start()
        binding.tabbarspeed.addTab(binding.tabbarspeed.newTab().setText("0.5x"))
        binding.tabbarspeed.addTab(binding.tabbarspeed.newTab().setText("1x"))
        binding.tabbarspeed.addTab(binding.tabbarspeed.newTab().setText("2x"))
        binding.tabbarspeed.addTab(binding.tabbarspeed.newTab().setText("3x"))
        binding.tabbarspeed.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        speed = 0.5f
                        hideSpeedBar(show = false)
                    }
                    1 -> {
                        speed = 1f
                        hideSpeedBar(show = false)
                    }
                    2 -> {
                        speed = 2f
                        hideSpeedBar(show = false)
                    }
                    3 -> {
                        speed = 3f
                        hideSpeedBar(show = false)
                    }
                }
                mModel!!.speed = speed
                if (isVideoTaken) {

                    Handler(Looper.getMainLooper()).post {
                        runOnUiThread {

                            setUpPlayer()
                        }
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
            binding.tabbarspeed.animate().translationX(0f).setDuration(500).start()
        } else {
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                binding.tabbarspeed.animate().translationX(-1000f).setDuration(500).start()
            }, 500)
        }
    }

    private lateinit var rangeFragment: RangeBSFragmnet
    fun setUPViews() {
        setupProgressBarWithDuration()
        binding.buttonRecord.setOnClickListener {
            if (countdownTimerDuration == 0) {
                if (isImage) {
                    if (!binding.cameraView.isTakingPicture) {
                        binding.cameraView.takePictureSnapshot()
                    }
                } else {
                    if (binding.cameraView.isTakingVideo) {
                        stopRecording()
                        binding.recordAnimationView.visibility = View.GONE
                        binding.stopIConView.visibility = View.VISIBLE

                    } else {
                        isImage = false
                        //  setUPCameraViewsOnCapture(false)
                        startRecording()
                        binding.recordAnimationView.visibility = View.VISIBLE

                        binding.stopIConView.visibility = View.GONE
                        uiWhenRecording()
                    }
                }
            } else {
                isImage = true
                runnableForTimer.run()
            }

        }

        binding.buttonRecord.setOnLongClickListener {
            if (countdownTimerDuration == 0) {
                isImage = false
                binding.segmentedProgressbar.progress = 0
                mMediaPlayer!!.reset()
                startRecording()
                binding.recordAnimationView.visibility = View.VISIBLE
                binding.stopIConView.visibility = View.GONE
                uiWhenRecording()
                binding.buttonRecord.setOnLongClickListener(null)
            } else {
                isImage = false
                runnableForTimer.run()
            }
            return@setOnLongClickListener true
        }


        binding.buttonCaptureCounter.setOnClickListener {
            binding.layoutBottomControll.visibility = View.GONE
            if (rangeFragment.isAdded) {
                return@setOnClickListener
            }
            rangeFragment!!.show(
                supportFragmentManager,
                rangeFragment!!.tag
            )
        }


        binding.buttonDone.setOnClickListener { view: View? ->
            if (isImage) {

                createDirectoryAndSaveFile(binding.selectedPhoto.capture())
                /*     binding.selectedPhoto.saveToPictures("yoo", System.currentTimeMillis().toString() + ".jpg",700,700,object :GPUImageView.OnPictureSavedListener{
                         override fun onPictureSaved(uri: Uri?) {

                         }
                     })*/
            } else {
                if (binding.cameraView.isTakingVideo) {
                    Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                        .show()
                } else if (mModel!!.segments.isEmpty()) {
                    Toast.makeText(this, R.string.recorder_error_no_clips, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    commitRecordings()
                }
            }
        }
        binding.buttonMusic.setOnClickListener { view: View? ->


            if (isImage) {
                if (binding.cameraView.isTakingPicture) {
                    Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    /*val intent = Intent(
                        this@ActivityVideoRecorder,
                        ActivitySongPicker::class.java
                    )
                    startActivityForResult(intent, SharedConstants.REQUEST_CODE_PICK_SONG)*/
                    resultCallbackOfSelectedMusicTrack.launch(
                        IntentHelper.getSongPickerActivity(
                            this
                        )
                    )
                }

            } else {
                if (binding.cameraView.isTakingVideo()) {
                    Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                        .show()
                } else {

                    resultCallbackOfSelectedMusicTrack.launch(
                        IntentHelper.getSongPickerActivity(
                            this
                        )
                    )
                }
            }


        }




        binding.buttonPickData.setOnClickListener {
            imageChooser()
        }

        binding.buttonCameraChanger.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.cameraView.toggleFacing()
                /*    if (binding.cameraView.facing == Facing.FRONT) {
                        binding.buttonFlash.visibility = View.GONE
                    } else {
                        binding.buttonFlash.visibility = View.VISIBLE
                    }*/
            }
        }




        binding.buttonSpeed.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (isSpeedActive) {
                    isSpeedActive = false
                    hideSpeedBar(show = false)
                } else {
                    isSpeedActive = true
                    hideSpeedBar(show = true)
                }
                updateColorButtons()
            }
        }
        binding.buttonColorFilterIcon.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.rvFilters.visibility =
                    if (binding.rvFilters.visibility == View.VISIBLE) View.GONE else View.VISIBLE

                isFilterActive = binding.rvFilters.visibility == View.VISIBLE


                if (isImage && isFilterActive) {
                    binding.layoutBottomControll.visibility = View.GONE
                    binding.rvFilters.visibility = View.VISIBLE


                } else if (isImage && !isFilterActive) {

                    if (isImageTakenByCamera) {
                        binding.rvFilters.visibility = View.GONE
                    } else {
                        binding.layoutBottomControll.visibility = View.VISIBLE
                        binding.rvFilters.visibility = View.GONE
                    }

                }



                updateColorButtons()
            }
        }
        /*    binding.buttonFlash.setOnClickListener { view: View? ->
                if (binding.cameraView.isTakingVideo) {
                    Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
                } else {
                    binding.cameraView.setFlash(if (binding.cameraView.getFlash() === Flash.OFF) Flash.TORCH else Flash.OFF)
                    isFlashActive = !isFlashActive
                    updateColorButtons()
                }
            }*/
        binding.speeds.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group: RadioGroup?, checked: Int ->
            var speed = 1f
            if (checked != R.id.speed05x) {
                binding.speed05x.setChecked(false)
            } else {
                speed = .5f
            }
            if (checked != R.id.speed075x) {
                binding.speed075x.setChecked(false)
            } else {
                speed = .75f
            }
            if (checked != R.id.speed1x) {
                binding.speed1x.setChecked(false)
            }
            if (checked != R.id.speed15x) {
                binding.speed15x.setChecked(false)
            } else {
                speed = 1.5f
            }
            if (checked != R.id.speed2x) {
                binding.speed2x.setChecked(false)
            } else {
                speed = 2f
            }
            mModel!!.speed = speed
        })
        binding.buttonDurationTimer.setOnClickListener {

            if (isCaptureDurationActive) {
                isCaptureDurationActive = false
                hideDurationBar(show = false)
                updateColorButtons()
            } else {
                isCaptureDurationActive = true
                hideDurationBar(show = true)
                updateColorButtons()
            }

        }
        binding.ivClose.setOnClickListener {
            onBackPressed()
        }

        setupRecordDurationRecclerview()
        setuptabSpeedRecclerview()
    }

    override fun onBackPressed() {


        var commonConfirmationDialog = CommonConfirmationDialog(
            this,
            "Save as Draft",
            "Drafts let you save your edits, so you can come back later.",
            "Yes",
            "Delete Video",
            object : CommonConfirmationDialog.Callback {
                override fun onDialogResult(isPermissionGranted: Boolean) {
                    isVideoTaken = false
                    isImageTakenByCamera = false
                    isFilterApplied = false
                    isMusicSelected = false
                    isCowntDownActive = false
                    isMusicActive = false
                    isFilterActive = false
                    isSpeedActive = false
                    isFlashActive = false
                    isCowntDownActive = false
                    isCaptureDurationActive = false
                    imageVideoDuration = 15
                    mModel!!.video = null
                    mModel!!.audio = null
                    mModel!!.speed = 1f
                    mModel!!.segments.clear()
                    binding.segmentedProgressbar.progress = 0
                    progressHandler.removeCallbacks(runnable)

                    // setUPCameraViewsOnCapture(true)
                    finish()


                }
            })


        if (isVideoTaken || isImageTakenByCamera) {
            commonConfirmationDialog.show()
        } else {
            super.onBackPressed()
        }


    }

    private fun createDirectoryAndSaveFile(imageToSave: Bitmap) {
        val file = File(cacheDir, UUID.randomUUID().toString())
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            mModel!!.video = File(file.absolutePath)
            closeFinally(mModel!!.video!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun uiWhenRecording() {
        binding.buttonMusic.visibility = View.GONE
        binding.buttonDurationTimer.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.buttonColorFilterIcon.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.layoutBottomControll.visibility = View.VISIBLE
        binding.buttonPickData.visibility = View.GONE
        binding.buttonDone.visibility = View.GONE
        binding.rvFilters.visibility = View.GONE
        binding.buttonCaptureCounter.visibility = View.GONE
        binding.segmentedProgressbar.visibility = View.VISIBLE
    }


    fun uiWhenCountdownActive() {
        binding.buttonMusic.visibility = View.GONE
        binding.buttonDurationTimer.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.buttonColorFilterIcon.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.layoutBottomControll.visibility = View.GONE
        binding.buttonPickData.visibility = View.GONE
        binding.buttonDone.visibility = View.GONE
        binding.rvFilters.visibility = View.GONE
        binding.buttonCaptureCounter.visibility = View.GONE
        binding.segmentedProgressbar.visibility = View.GONE
    }


    private val displayWidth: Int
        private get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    private val displayHeight: Int
        private get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    private fun multipleImageToVideo(pathList: ArrayList<String>, persond: Int) {
        showLoader()
        var height = 0
        var width = 0
        if (displayWidth % 2 == 0) {
            width = displayWidth
        } else {
            width = displayWidth - 1
        }

        if (displayHeight % 2 == 0) {
            height = displayHeight
        } else {
            height = displayHeight - 1
        }


        isVideoTaken = true
        isImage = false
        setUPCameraViewsOnCapture(false)
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val asyncTask =
            FFmpegAsyncTask("-f concat -safe 0 -i ${
                generateIMagesListFile(
                    pathList,
                    persond
                )
            } -c:v libx264 -r 15 -pix_fmt yuv420p -crf 23 -t ${pathList.size * persond} -vf scale=w=${width}:h=${height}:force_original_aspect_ratio=1,pad=${width}:${height}:(ow-iw)/2:(oh-ih)/2 -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        var recordSegment = RecordSegment()
                        recordSegment.duration = (pathList.size * persond).toLong()
                        recordSegment.file = File(outputPath)
                        mModel!!.segments.clear()
                        mModel!!.segments.add(recordSegment)
                        setUPCameraViewsOnCapture(false)
                        if (!isImage) {
                            isVideoTaken = true
                            binding.selectedPhoto.visibility = View.GONE
                            binding.cameraView.visibility = View.GONE
                            binding.layoutMovieWrapper.visibility = View.VISIBLE
                            resumeProgress()
                            setUpPlayer()
                        }


                        dismissLoader()

                    }
                })
        asyncTask.execute()


    }

    private lateinit var playerzview: EPlayerView
    private var mPlayer: ExoPlayer? = null
    private fun setUpPlayer() {
        playerzview = EPlayerView(this)
        mPlayer!!.repeatMode = ExoPlayer.REPEAT_MODE_OFF
        val factory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(mModel!!.segments[0].file))
        val source: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        mPlayer!!.setPlaybackSpeed(mModel!!.speed)
        mPlayer!!.prepare(source);
        mPlayer!!.playWhenReady = true;
        playerzview.visibility = View.VISIBLE
        applyPreviewFilter(activeFilter)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            runOnUiThread {
                playerzview.setSimpleExoPlayer(mPlayer)
                playerzview.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // add ePlayerView to WrapperView
                // add ePlayerView to WrapperView

                binding.layoutMovieWrapper.removeAllViews()
                binding.layoutMovieWrapper.addView(playerzview)
                playerzview.onResume()
            }

        }, 500)
        Log.d("aklsjdlasd", "fourth")
    }


    private fun applySpeed(file: File, speed: Float, duration: Long) {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        showLoader()
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val asyncTask =
            FFmpegAsyncTask("-i \"${file.absolutePath}\" -vf setpts=PTS/${mModel!!.speed.toString()}  -crf 23 -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        dismissLoader()
                        Log.d("lkasjldasd", "asdasdasd")
                        Log.d("kashdkhasd", outputPath)

                        val segment = RecordSegment()
                        segment.file = File(outputPath)
                        segment.duration = duration

                        mModel!!.segments.clear()
                        mModel!!.segments.add(segment)
                        file.delete()
                        dismissLoader()
                        if (!isImage) {
                            isVideoTaken = true
                            binding.buttonDone.visibility = View.VISIBLE
                            binding.layoutMovieWrapper.visibility = View.VISIBLE
                            setUpPlayer()
                        }

                        binding.segmentedProgressbar.progress = 0
                        Log.d(";laksdasd", duration.toString())
                        imageVideoDuration = ((duration / 1000).toInt())
                        Log.d(";laksdasd", imageVideoDuration.toString())
                        //setupProgressBarWithDuration()
                        setupProgressBarWithDuration()
                        resumeProgress()
                    }
                })
        asyncTask.execute()

    }

    fun formatSeconds(timeInSeconds: Int): String? {
        val secondsLeft = timeInSeconds % 3600 % 60
        val minutes = Math.floor((timeInSeconds % 3600 / 60).toDouble()).toInt()
        val hours = Math.floor((timeInSeconds / 3600).toDouble()).toInt()
        val HH = (if (hours < 10) "0" else "") + hours
        val MM = (if (minutes < 10) "0" else "") + minutes
        val SS = (if (secondsLeft < 10) "0" else "") + secondsLeft
        return "$HH:$MM:$SS"
    }

    private fun applySpeedWithDuration(file: File) {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        showLoader()
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val asyncTask =
            FFmpegAsyncTask("-i \"${file.absolutePath}\" -vf setpts=PTS/${mModel!!.speed.toString()}  -ss 00:00:00 -to ${
                formatSeconds(
                    imageVideoDuration.toInt()
                )
            } -crf 23 -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        dismissLoader()
                        Log.d("lkasjldasd", "asdasdasd")
                        Log.d("kashdkhasd", outputPath)
                        val duration: Long = VideoUtil.getDuration(
                            this@ActivityVideoRecorder,
                            Uri.fromFile(File(outputPath))
                        )
                        val segment = RecordSegment()
                        segment.file = File(outputPath)
                        segment.duration = duration
                        mModel!!.segments.clear()
                        mModel!!.segments.add(segment)
                        file.delete()
                        dismissLoader()
                        if (!isImage) {
                            isVideoTaken = true
                            binding.buttonDone.visibility = View.VISIBLE
                            binding.layoutMovieWrapper.visibility = View.VISIBLE
                            setUpPlayer()
                        }

                        binding.segmentedProgressbar.progress = 0
                        imageVideoDuration = ((duration / 1000).toInt())
                        Log.d(";laksdasd", imageVideoDuration.toString())
                        setupProgressBarWithDuration()
                        resumeProgress()
                    }
                })
        asyncTask.execute()

    }


    private fun hideSystemBars() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black);
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
    }

    var isMusicActive = false
    var isFilterActive = false
    var isSpeedActive = false
    var isFlashActive = false
    var isCowntDownActive = false
    var isCaptureDurationActive = false


    fun updateColorButtons() {


        if (isMusicActive) {
            binding.ivMusic.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_top_music_active
                )
            )
            binding.tvMusic.setTextColor(resources.getColor(R.color.colorYellow, null))
        } else {
            binding.tvMusic.setTextColor(resources.getColor(R.color.white, null))
            binding.ivMusic.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_top_music
                )
            )
        }

        if (isSpeedActive) {
            binding.ivSpeed.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_speed_active
                )
            )
            binding.tvSped.setTextColor(resources.getColor(R.color.colorYellow, null))
        } else {
            binding.tvSped.setTextColor(resources.getColor(R.color.white, null))
            binding.ivSpeed.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_speed
                )
            )
        }


        if (isFilterActive) {


            binding.ivFilter.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_magic_stick_active
                )
            )
            binding.tvFilter.setTextColor(resources.getColor(R.color.colorYellow, null))
        } else {

            binding.tvFilter.setTextColor(resources.getColor(R.color.white, null))
            binding.ivFilter.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_magic_stick
                )
            )
        }

        if (isFlashActive) {
            binding.buttonFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_speed_active
                )
            )
        } else {
            binding.buttonFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_live_flash
                )
            )
        }


    }


    private var count = 0
    private lateinit var thumbnails: Array<Bitmap?>
    private lateinit var thumbnailsselection: BooleanArray
    private lateinit var arrPath: Array<String?>
    private lateinit var typeMedia: IntArray
    fun getGalleryData() {
        // Get relevant columns for use later.
        // Get relevant columns for use later.
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
        )

        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)


        val queryUri = MediaStore.Files.getContentUri("external")

        val cursorLoader = CursorLoader(
            this,
            queryUri,
            projection,
            selection,
            null,  // Selection args (none).
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        )

        val cursor: Cursor = cursorLoader.loadInBackground()!!

        val image_column_index: Int = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        this.count = cursor.count
        this.thumbnails = arrayOfNulls<Bitmap>(this.count)
        this.arrPath = arrayOfNulls<String>(this.count)
        this.typeMedia = IntArray(this.count)
        this.thumbnailsselection = BooleanArray(this.count)
        for (i in 0 until this.count) {
            cursor.moveToPosition(i)
            val id: Int = cursor.getInt(image_column_index)
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
            bmOptions.inSampleSize = 4
            bmOptions.inPurgeable = true
            val type: Int = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val t: Int = cursor.getInt(type)
            try {
                if (t == 1) thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id.toLong(),
                    MediaStore.Images.Thumbnails.MINI_KIND, bmOptions


                ) else if (t == 3) thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                    contentResolver, id.toLong(),
                    MediaStore.Video.Thumbnails.MINI_KIND, bmOptions
                )
                arrPath[i] = cursor.getString(dataColumnIndex)
                typeMedia[i] = cursor.getInt(type)


                Glide.with(this).load(arrPath[0]!!).transform(CenterCrop(), RoundedCorners(5))
                    .into(binding.buttonPickData)


            } catch (e: Exception) {
            }

            break
        }

    }

    @SuppressLint("Range")
    fun getImageContentUri(context: Context, filePath: String): Uri? {

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            cursor.close()
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
        } else {
            null
        }
    }


    @SuppressLint("Range")
    fun getVideoContentUri(context: Context, filePath: String): Uri? {
        Log.d("alksjdasd", filePath)
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Video.Media._ID),
            MediaStore.Video.Media.DATA + "=? ", arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            cursor.close()
            Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id)
        } else {
            null
        }
    }


    override fun onItemPicked(item: GalleryItem) {

        var uri: Uri
        if (item.mediaType == 1) {
            uri = getImageContentUri(this, item.uri)!!
            isImageTakenByCamera = true
            isImage = true
            startCrop(uri)


        } else if (item.mediaType == 3 || item.mediaType == 2) {
            uri = getVideoContentUri(this, item.uri)!!
            Log.d(";lasda", "video")
            isImage = false
            setUPCameraViewsOnCapture(false)
            val selectedVideo = uri
            val selectedImageBitmap: Bitmap
            try {
                Log.d("a;lskdasdhhhhhh", "data.data!!.path!!")
                Log.d("a;lskdasdkjjjjjj", uri.path!!)
                mModel!!.video = File(uri.path!!)
                processCurrentRecording()
                Log.d(";alsjkdasd", "alskjdasd")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mModel!!.video = File(getRealPathFromURI(this, uri))
            Handler(Looper.getMainLooper()).post {

                runOnUiThread {
                    //   processCurrentRecording()
                }
            }
        }


    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(this) // optional usage

            val selectedImageUri = result.uriContent
            val selectedImageBitmap: Bitmap







            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    selectedImageUri
                )
                binding.buttonCaptureCounter.visibility = View.GONE
                binding.buttonSpeed.visibility = View.GONE
                binding.selectedPhoto.visibility = View.VISIBLE
                binding.selectedPhoto.gpuImage.deleteImage()
                binding.selectedPhoto.setImage(selectedImageBitmap);
                binding.cameraView.visibility = View.GONE
                //  createVideo(it!!.absolutePath, result.size)
                binding.buttonDone.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.GONE
                // closeFinally(mModel!!.video!!)
                Log.d(";alsjkdasd", "alskjdasd")
            } catch (e: IOException) {
                e.printStackTrace()
            }


        } else {
            // an error occurred
            val exception = result.error
        }
    }


    private fun startCrop(uri: Uri) {
        // start cropping activity for pre-acquired image saved on the device and customize settings
        cropImage.launch(
            options(uri = uri) {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            }
        )
    }


    var progressHandler = Handler()
    var captureTimerHandler = Handler()
    fun pauseProgress() {
        mMediaPlayer!!.pause()
        progressHandler.removeCallbacks(runnable)
    }

    fun resumeProgress() {
        progressHandler.removeCallbacks(runnable)
        if (isMusicSelected && isImage) {
            progressHandler.post(runnable)
        } else if (!isImage && isVideoTaken) {
            progressHandler.post(runnable)
        }
    }

    var prolength = 0 //

    //
    var runnable: Runnable = object : Runnable {
        override fun run() {

            prolength = binding.segmentedProgressbar.progress + 1
            binding.segmentedProgressbar.progress = prolength
            //100,1000runnable
            if (prolength < imageVideoDuration) {
                progressHandler.postDelayed(this, 1000)
                Log.d("akjsdasda", "akshdasd")
            } else {
                //    binding.segmentedProgressbar.setProgress(0)
                mMediaPlayer!!.pause()
                if (binding.cameraView.isTakingVideo) {
                    binding.cameraView.stopVideo()
                }
                //   progressHandler.post(this)
                Log.d("akjsdasda", "akshdasdfsdfsd")
            }
        }
    }


    var runnableForTimer: Runnable = object : Runnable {
        override fun run() {

            if (countdownTimerDuration == 0) {
                binding.tvCountDownValue.visibility = View.GONE
                captureTimerHandler.removeCallbacks(this)
                if (isImage) {
                    binding.buttonRecord.performClick()
                } else {
                    binding.buttonRecord.performLongClick()
                }

            } else {
                countdownTimerDuration--
                runOnUiThread {
                    binding.tvCountDownValue.visibility = View.VISIBLE
                    binding.tvCountDownValue.text = countdownTimerDuration.toString()
                }
                captureTimerHandler.postDelayed(this, 1000)
            }
        }
    }

    fun setupProgressBarWithDuration() {
        if (isImage) {
            if (isMusicSelected) {
                binding.segmentedProgressbar.visibility = View.VISIBLE
            } else {
                binding.segmentedProgressbar.visibility = View.GONE
            }
        } else {
            binding.segmentedProgressbar.visibility = View.VISIBLE
        }
        binding.segmentedProgressbar.max = imageVideoDuration
    }

    override fun onSelectFilter(
        filter: VideoFilter?,
        position: Int,
        isAllShowing: Boolean, isLongClick: Boolean
    ) {
        applyPreviewFilter(
            filter!!
        )
        /*  filterAdapter.showAllFilters(isAllShowing)
          binding.rvFilters.getLayoutManager()!!.scrollToPosition(position);*/

        if (isLongClick) {
            isImage = false
            if (!isVideoTaken) {
                uiWhenRecording()
                binding.segmentedProgressbar.progress = 0
                mMediaPlayer!!.reset()
                startRecording()
                binding.recordAnimationView.visibility = View.VISIBLE
                binding.stopIConView.visibility = View.GONE
                binding.buttonRecord.setOnLongClickListener(null)
            }
        } else {
            if (isImageTakenByCamera) {
                binding.buttonRecord.performClick()//it is image
            }

        }


    }


    fun setUPRangeBar() {

    }

    var countdownTimerDuration = 0
    override fun onCaptureAfternSeconds(type: String, seconds: Int) {

        isImage = type != "video"

        uiWhenCountdownActive()
        countdownTimerDuration = seconds
        captureTimerHandler.removeCallbacks(runnableForTimer)
        captureTimerHandler.post(runnableForTimer)
    }

    override fun onRangeDialogDismiss() {
        binding.layoutBottomControll.visibility = View.VISIBLE
    }

}


class CenterZoomLayoutManager : LinearLayoutManager {
    private val mShrinkAmount = 0.15f
    private val mShrinkDistance = 0.9f

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val orientation = orientation
        return if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            val midpoint = height / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint = (getDecoratedBottom(child!!) + getDecoratedTop(
                    child
                )) / 2f
                val d = Math.min(d1, Math.abs(midpoint - childMidpoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }
            scrolled
        } else {
            0
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val orientation = orientation
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            val midpoint = width / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint = (getDecoratedRight(child!!) + getDecoratedLeft(
                    child
                )) / 2f
                val d = Math.min(d1, Math.abs(midpoint - childMidpoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }
            scrolled
        } else {
            0
        }
    }


}


/**
 * A LinearSnapHelper that ignores item decorations to determine a view's center
 */
class CenterSnapHelper : LinearSnapHelper() {

    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private var scrolled = false
    private var recyclerView: RecyclerView? = null
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE && scrolled) {
                if (recyclerView.layoutManager != null) {
                    val view = findSnapView(recyclerView.layoutManager)
                    if (view != null) {
                        val out = calculateDistanceToFinalSnap(recyclerView.layoutManager!!, view)
                        if (out != null) {
                            recyclerView.smoothScrollBy(out[0], out[1])
                        }
                    }
                }
                scrolled = false
            } else {
                scrolled = true
            }
        }
    }

    fun scrollTo(position: Int, smooth: Boolean) {
        if (recyclerView?.layoutManager != null) {
            val viewHolder = recyclerView!!.findViewHolderForAdapterPosition(position)
            if (viewHolder != null) {
                val distances = calculateDistanceToFinalSnap(
                    recyclerView!!.layoutManager!!,
                    viewHolder.itemView
                )
                if (smooth) {
                    recyclerView!!.smoothScrollBy(distances!![0], distances[1])
                } else {
                    recyclerView!!.scrollBy(distances!![0], distances[1])
                }
            } else {
                if (smooth) {
                    recyclerView!!.smoothScrollToPosition(position)
                } else {
                    recyclerView!!.scrollToPosition(position)
                }
            }
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager == null) {
            return null
        }
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        recyclerView?.addOnScrollListener(scrollListener)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(layoutManager, targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        val center: Int = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        var absClosest = Integer.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childCenter = if (helper == horizontalHelper) {
                (child!!.x + child.width / 2).toInt()
            } else {
                (child!!.y + child.height / 2).toInt()
            }
            val absDistance = Math.abs(childCenter - center)

            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        return closestChild
    }

    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val childCenter = if (helper == horizontalHelper) {
            (targetView.x + targetView.width / 2).toInt()
        } else {
            (targetView.y + targetView.height / 2).toInt()
        }
        val containerCenter = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        return childCenter - containerCenter
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (verticalHelper == null || verticalHelper!!.layoutManager !== layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper!!
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (horizontalHelper == null || horizontalHelper!!.layoutManager !== layoutManager) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper!!
    }
}


class CenterDecoration(@Px private val spacing: Int) : RecyclerView.ItemDecoration() {

    private var firstViewWidth = -1
    private var lastViewWidth = -1

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val lm = parent.layoutManager as LinearLayoutManager
        if (adapterPosition == 0) {
            // Invalidate decorations when this view width has changed
            if (view.width != firstViewWidth) {
                view.doOnPreDraw { parent.invalidateItemDecorations() }
            }
            firstViewWidth = view.width
            outRect.left = parent.width / 2 - view.width / 2
            // If we have more items, use the spacing provided
            if (lm.itemCount > 1) {
                outRect.right = spacing / 2
            } else {
                // Otherwise, make sure this to fill the whole width with the decoration
                outRect.right = outRect.left
            }
        } else if (adapterPosition == lm.itemCount - 1) {
            // Invalidate decorations when this view width has changed
            if (view.width != lastViewWidth) {
                view.doOnPreDraw { parent.invalidateItemDecorations() }
            }
            lastViewWidth = view.width
            outRect.right = parent.width / 2 - view.width / 2
            outRect.left = spacing / 2
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing / 2
        }
    }
}
