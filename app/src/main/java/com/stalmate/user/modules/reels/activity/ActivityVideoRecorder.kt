package com.stalmate.user.modules.reels.activity


import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.tabs.TabLayout
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Engine
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
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
import com.stalmate.user.modules.reels.adapter.FilterAdapter
import com.stalmate.user.modules.reels.adapter.GalleryItem
import com.stalmate.user.modules.reels.filters.*
import com.stalmate.user.modules.reels.utils.VideoFilter
import com.stalmate.user.modules.reels.utils.VideoUtil
import com.stalmate.user.modules.reels.workers.MergeAudioVideoWorker
import com.stalmate.user.modules.reels.workers.MergeVideosWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import com.user.vaibhavmodules.reels.utils.SharedConstants
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ActivityVideoRecorder : BaseActivity(), FragmentGallery.GalleryPickerListener {

    private val TAG = "RecorderActivity"
    private var imageVideoDuration = 15
    private var mModel: RecorderActivityViewModel? = null
    private val mHandler = Handler()
    var songId = ""
    private var isImage = true
    private var isImageTakenByCamera = false
    private var mMediaPlayer: MediaPlayer? = null
    val PICK_FILE = 99
    private val mStopper = Runnable { stopRecording() }
    lateinit var binding: ActivityVideoRecorderBinding
    override fun onClick(viewId: Int, view: View?) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityVideoRecorderBinding.inflate(layoutInflater)
        binding.cameraView.engine = Engine.CAMERA2
        mModel = ViewModelProvider(this)[RecorderActivityViewModel::class.java]
        setContentView(binding.root)
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


                    runOnUiThread {
                        binding.selectedPhoto.visibility = View.VISIBLE
                        result.toBitmap {
                            runOnUiThread {
                                binding.selectedPhoto.setImage(it);

                                binding.cameraView.visibility = View.GONE
                            }
                        }


                        //  createVideo(it!!.absolutePath, result.size)
                        //  binding.rvFilters.visibility=View.VISIBLE
                        binding.buttonDone.visibility = View.VISIBLE
                        binding.layoutBottomControll.visibility = View.GONE
                        // closeFinally(mModel!!.video!!)
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
                binding.buttonDone.visibility = View.VISIBLE
                pauseProgress()
                //    binding.segmentedProgressbar.addDivider()
                binding.buttonRecord.setSelected(false)
                /*       if (mMediaPlayer != null) {
                           mMediaPlayer!!.pause()
                       }*/
                mHandler.postDelayed({ processCurrentRecording() }, 500)
            }

            override fun onVideoRecordingStart() {

                binding.buttonRecord.setSelected(true)
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
                resumeProgress()
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




        isPermissionGranted(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )
        setUPViews()
        setUpCameraView()


    }


    override fun onDestroy() {
        super.onDestroy()

        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
            binding.cameraView.destroy()
            progressHandler.removeCallbacks(runnable)
        }
    }


    override fun onStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
        }
        super.onStop()
    }


    fun setUpCameraView() {


        runOnUiThread {
            if (isImage) {
                binding.buttonSpeed.visibility = View.GONE
                binding.buttonDone.visibility = View.GONE
                binding.cameraView.setMode(Mode.PICTURE);
                binding.segmentedProgressbar.visibility = View.GONE
                binding.stopIConView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.circle_camerabackground
                    )
                )
            } else {
                binding.buttonSpeed.visibility = View.VISIBLE
                binding.buttonDone.visibility = View.VISIBLE
                binding.cameraView.setMode(Mode.VIDEO);
                binding.segmentedProgressbar.visibility = View.VISIBLE

                binding.stopIConView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.record
                    )
                )
            }
            binding.cameraView.close()
            binding.cameraView.invalidate()
            binding.cameraView.open()

        }
    }

    override fun onPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
            binding.cameraView.close()
        }
        super.onPause()
    }

    override fun onStart() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.start()
            if (!isImageTakenByCamera) {
                binding.cameraView.open()
            }
            if (!isImage) {
                Log.d("aklsjdlasd", "oaiudoiasd")
                runOnUiThread {
                    binding.cameraView.open()
                }
            }
        }
        super.onStart()
    }


    fun setUpFilterAdapter() {
        //  binding.cameraView.
        var bitmap: Bitmap? = null
        val into = Glide.with(this).asBitmap().load(R.drawable.placeholder_filter)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    @Nullable transition: Transition<in Bitmap?>?
                ) {
                    bitmap = resource
                    //  binding.buttonColorFilterIcon.setImageBitmap(resource)


                    val adapter =
                        FilterAdapter(this@ActivityVideoRecorder, bitmap, binding.cameraView, true)
                    adapter.setListener { filter: VideoFilter? ->
                        this@ActivityVideoRecorder.applyPreviewFilter(
                            filter!!
                        )
                    }
                    /*            val snapHelper = LinearSnapHelper()
                                  snapHelper.attachToRecyclerView(  binding.rvFilters)
              */

                    binding.rvFilters.setNestedScrollingEnabled(false)
                    binding.rvFilters.setHasFixedSize(true)
                    binding.rvFilters.adapter = adapter
                    //  binding.rvFilters.addItemDecoration(OffsetItemDecoration(this@ActivityVideoRecorder))
                    // binding.rvFilters.layoutManager=CenterLayoutManager(this@ActivityVideoRecorder, LinearLayoutManager.HORIZONTAL, false)
                    //  binding.rvFilters.layoutManager=CenterZoomLayoutManager(this@ActivityVideoRecorder, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvFilters.layoutManager = LinearLayoutManager(
                        this@ActivityVideoRecorder,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    /*           val layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true)
                               layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
                               binding.rvFilters.layoutManager = layoutManager
                               binding.rvFilters.addOnScrollListener(CenterScrollListener())
                               binding.rvFilters.setNestedScrollingEnabled(false)
                               binding.rvFilters.setHasFixedSize(true)
                               binding.rvFilters.adapter = adapter
           */
                    /*        binding.rvFilters.addOnScrollListener(object :RecyclerView.OnScrollListener(){

                                override fun onScrollStateChanged(
                                    recyclerView: RecyclerView,
                                    newState: Int
                                ) {
                                    super.onScrollStateChanged(recyclerView, newState)
                                  if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                        val centerView = snapHelper.findSnapView(binding.rvFilters.layoutManager)
                                        val pos = (binding.rvFilters.layoutManager as CenterZoomLayoutManager).getPosition(centerView!!)

                                        when(pos){
                                            0->{
                                                applyPreviewFilter(VideoFilter.NONE)
                                            }
                                            1->{
                                                applyPreviewFilter(VideoFilter.BRIGHTNESS)
                                            }
                                            2->{
                                                applyPreviewFilter(VideoFilter.EXPOSURE)
                                            }
                                            3->{
                                                applyPreviewFilter(VideoFilter.GAMMA)
                                            }
                                            4->{
                                                applyPreviewFilter(VideoFilter.GRAYSCALE)
                                            }
                                            5->{
                                                applyPreviewFilter(VideoFilter.HAZE)
                                            }
                                            6->{
                                                applyPreviewFilter(VideoFilter.INVERT)
                                            }
                                            7->{
                                                applyPreviewFilter(VideoFilter.MONOCHROME)
                                            }
                                            8->{
                                                applyPreviewFilter(VideoFilter.PIXELATED)
                                            }
                                            9->{
                                                applyPreviewFilter(VideoFilter.POSTERIZE)
                                            }
                                            10->{
                                                applyPreviewFilter(VideoFilter.SEPIA)
                                            }
                                            11->{
                                                applyPreviewFilter(VideoFilter.SHARP)
                                            }
                                            12->{
                                                applyPreviewFilter(VideoFilter.SOLARIZE)
                                            }
                                            13->{
                                                applyPreviewFilter(VideoFilter.VIGNETTE)
                                            }


                                        }




                                    }

                                }


                            })*/

                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }


    private fun applyPreviewFilter(filter: VideoFilter) {

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
                else -> binding.selectedPhoto.setFilter(GPUImageFilter())
            }
        } else {
            binding.rvFilters.visibility = View.GONE

        }
        when (filter) {
            VideoFilter.BRIGHTNESS -> {
                val glf = Filters.BRIGHTNESS.newInstance() as BrightnessFilter
                glf.brightness = 1.2f
                binding.cameraView.setFilter(glf)
            }
            VideoFilter.EXPOSURE -> binding.cameraView.setFilter(ExposureFilter())
            VideoFilter.GAMMA -> {
                val glf = Filters.GAMMA.newInstance() as GammaFilter
                glf.gamma = 2f
                binding.cameraView.setFilter(glf)
            }
            VideoFilter.GRAYSCALE -> binding.cameraView.setFilter(Filters.GRAYSCALE.newInstance())
            VideoFilter.HAZE -> {
                val glf = HazeFilter()
                glf.setSlope(-0.5f)
                binding.cameraView.setFilter(glf)
            }

            VideoFilter.INVERT -> binding.cameraView.setFilter(Filters.INVERT_COLORS.newInstance())
            VideoFilter.MONOCHROME -> binding.cameraView.setFilter(MonochromeFilter())
            VideoFilter.PIXELATED -> {
                val glf = PixelatedFilter()
                glf.setPixel(5.0f)
                binding.cameraView.setFilter(glf)
            }
            VideoFilter.POSTERIZE -> binding.cameraView.setFilter(Filters.POSTERIZE.newInstance())
            VideoFilter.SEPIA -> binding.cameraView.setFilter(Filters.SEPIA.newInstance())
            VideoFilter.SHARP -> {
                val glf = Filters.SHARPNESS.newInstance() as SharpnessFilter
                glf.sharpness = 0.25f
                binding.cameraView.setFilter(glf)
            }
            VideoFilter.SOLARIZE -> binding.cameraView.setFilter(SolarizeFilter())
            VideoFilter.VIGNETTE -> binding.cameraView.setFilter(Filters.VIGNETTE.newInstance())
            else -> binding.cameraView.setFilter(Filters.NONE.newInstance())
        }


    }

    var recorded = 0.toLong()
    private fun startRecording() {
        recorded = mModel!!.recorded()
        if (recorded >= SharedConstants.MAX_DURATION) {
            Toast.makeText(
                this@ActivityVideoRecorder,
                R.string.recorder_error_maxed_out,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mModel!!.video = File(cacheDir, UUID.randomUUID().toString())
            binding.buttonPickData.visibility = View.GONE
            binding.buttonDone.visibility = View.GONE
            binding.cameraView.takeVideoSnapshot(
                mModel!!.video!!, ((SharedConstants.MAX_DURATION - recorded).toInt())
            )
        }
    }

    private fun stopRecording() {
        binding.cameraView.stopVideo()
        //   mHandler.removeCallbacks(mStopper)
    }


    private fun commitRecordings() {
        showLoader()
        val videos: MutableList<String> = ArrayList()
        for (segment in mModel!!.segments) {
            videos.add(segment.file!!.absolutePath)
        }
        // File merged1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        val merged1 = File(cacheDir, UUID.randomUUID().toString())
        val data1 = Data.Builder()
            .putStringArray(MergeVideosWorker.KEY_VIDEOS, videos.toTypedArray())
            .putString(MergeVideosWorker.KEY_OUTPUT, merged1.absolutePath)
            .build()
        val request1 = OneTimeWorkRequest.Builder(MergeVideosWorker::class.java)
            .setInputData(data1)
            .build()
        val wm = WorkManager.getInstance(this)
        if (mModel!!.audio != null) {
            Log.d("path====>>>", mModel!!.audio!!.path!!)
            val merged2 = File(cacheDir, UUID.randomUUID().toString())
            val audioFile: File = File(mModel!!.audio!!.path!!)
            val data2 =
                Data.Builder() // .putString(MergeAudioVideoWorker.KEY_AUDIO,mModel.audio.getPath())
                    .putString(MergeAudioVideoWorker.KEY_AUDIO, audioFile.absolutePath)
                    .putString(MergeAudioVideoWorker.KEY_VIDEO, merged1.absolutePath)
                    .putString(MergeAudioVideoWorker.KEY_OUTPUT, merged2.absolutePath)
                    .build()
            Log.d("===>>", audioFile.absolutePath)
            Log.d("===>>", merged1.absolutePath)
            val request2 =
                OneTimeWorkRequest.Builder(MergeAudioVideoWorker::class.java).setInputData(data2)
                    .build()
            wm.beginWith(request1).then(request2).enqueue()
            wm.getWorkInfoByIdLiveData(request2.id)
                .observe(this) { info: WorkInfo ->
                    Log.d("states====>", info.state.toString())
                    val ended = (info.state == WorkInfo.State.CANCELLED
                            || info.state == WorkInfo.State.FAILED)
                    if (info.state == WorkInfo.State.SUCCEEDED) {
                        dismissLoader()
                        closeFinally(merged2)
                    } else if (ended) {
                        dismissLoader()
                    }
                }
        } else {
            wm.enqueue(request1)
            wm.getWorkInfoByIdLiveData(request1.id)
                .observe(this) { info: WorkInfo ->
                    val ended = (info.state == WorkInfo.State.CANCELLED
                            || info.state == WorkInfo.State.FAILED)
                    if (info.state == WorkInfo.State.SUCCEEDED) {
                        dismissLoader()
                        closeFinally(merged1)
                    } else if (ended) {
                        dismissLoader()
                    }
                }
        }
    }

    private fun processCurrentRecording() {
        showLoader()
        val duration: Long = VideoUtil.getDuration(this, Uri.fromFile(mModel!!.video))
        /*    if (mModel!!.speed != 1f) {
            applyVideoSpeed(mModel!!.video!!, mModel!!.speed, duration)
        } else {
            val segment = RecordSegment()
            segment.file = mModel!!.video
            segment.duration = duration
            mModel!!.segments.add(segment)
        }*/
        applyVideoSpeed(mModel!!.video!!, mModel!!.speed, duration)
        mModel!!.speed = 1f
        mModel!!.video = null
    }

    private fun applyVideoSpeed(file: File, speed: Float, duration: Long) {

        val output = File(cacheDir, UUID.randomUUID().toString())
        val data = Data.Builder()
            .putString(VideoSpeedWorker.KEY_INPUT, file.absolutePath)
            .putString(VideoSpeedWorker.KEY_OUTPUT, output.absolutePath)
            .putFloat(VideoSpeedWorker.KEY_SPEED, speed)
            .build()

        val request = OneTimeWorkRequest.Builder(VideoSpeedWorker::class.java)
            .setInputData(data)
            .build()
        val wm = WorkManager.getInstance(this)
        wm.enqueue(request)
        wm.getWorkInfoByIdLiveData(request.id)
            .observe(this) { info: WorkInfo ->
                val ended =
                    (info.state == WorkInfo.State.CANCELLED || info.state == WorkInfo.State.FAILED)

                if (info.state == WorkInfo.State.SUCCEEDED) {
                    val segment = RecordSegment()
                    segment.file = output
                    segment.duration = duration
                    mModel!!.segments.add(segment)
                    Log.d("akljsdas", "ioajsd")
                    file.delete()
                    dismissLoader()


                } else if (ended) {

                    file.delete()
                    dismissLoader()
                }
            }
    }

    private fun closeFinally(file: File) {

        binding.cameraView.close()
        //  binding.cameraView.destroy()
        val intent = Intent(this, ActivityVideoEditor::class.java)

        if (mModel!!.audio != null) {
            intent.putExtra(ActivityFilter.EXTRA_SONG, File(mModel!!.audio!!.path!!).absolutePath)
        }


        intent.putExtra(ActivityFilter.EXTRA_VIDEO, file.absolutePath)
        intent.putExtra("isImage", isImage)
        intent.putExtra(EXTRA_SONG_ID, songId)
        try {
            Log.d("asdasdasdxx", songId)

        } catch (e: java.lang.Exception) {
            Log.d("asdasdasdxx", "lasjda")
        }



        startActivity(intent)
        // finish()

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
        launchActivityForImagePick.launch(i)
    }

    var launchActivityForImagePick = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.getResultCode()
            == RESULT_OK
        ) {
            var data: Intent = result.data!!
            // do your operation from here....
            if (data != null
                && data.data != null
            ) {
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
                    /*       try {
                               selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                   this.contentResolver,
                                   selectedImageUri
                               )
                               Handler(Looper.getMainLooper()).post {
                                   binding.selectedPhoto.visibility = View.VISIBLE
                                   binding.selectedPhoto.gpuImage.deleteImage()
                                   runOnUiThread {
                                       binding.selectedPhoto.setImage(selectedImageBitmap);
                                       binding.cameraView.visibility = View.GONE
                                       //  createVideo(it!!.absolutePath, result.size)
                                       binding.buttonDone.visibility = View.VISIBLE
                                       binding.layoutBottomControll.visibility = View.GONE
                                       // closeFinally(mModel!!.video!!)
                                   }
                               }
                               Log.d(";alsjkdasd", "alskjdasd")
                           } catch (e: IOException) {
                               e.printStackTrace()
                           }*/
                } else if (type == "mp4") {
                    Log.d(";lasda", "video")
                    isImage = false
                    setUpCameraView()
                    val selectedVideo = data.data
                    val selectedImageBitmap: Bitmap
                    try {
                        Log.d("a;lskdasd", "data.data!!.path!!")
                        Log.d("a;lskdasd", data.data!!.path!!)
                        mModel!!.video = File(data.data!!.path!!)
                        Handler(Looper.getMainLooper()).post {

                            runOnUiThread {
                                processCurrentRecording()
                            }
                        }
                        Log.d(";alsjkdasd", "alskjdasd")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    mModel!!.video = File(getRealPathFromURI(this, data.data!!))
                    Handler(Looper.getMainLooper()).post {

                        runOnUiThread {
                            //   processCurrentRecording()
                        }
                    }
                }


            }
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
        if (result.getResultCode()
            == RESULT_OK
        ) {
            var data: Intent = result.data!!
            // do your operation from here....
            if (data != null
                && data.data != null
            ) {
                val selectedVideo = data.data
                val selectedImageBitmap: Bitmap
                try {
                    Log.d("a;lskdasd", "data.data!!.path!!")
                    Log.d("a;lskdasd", data.data!!.path!!)
                    mModel!!.video = File(data.data!!.path!!)
                    Handler(Looper.getMainLooper()).post {

                        runOnUiThread {
                            processCurrentRecording()
                        }
                    }
                    Log.d(";alsjkdasd", "alskjdasd")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                mModel!!.video = File(getRealPathFromURI(this, data.data!!))
                Handler(Looper.getMainLooper()).post {

                    runOnUiThread {
                        //   processCurrentRecording()
                    }
                }
                Log.d(";alsjkdasd", "alskjdasd")


            }
        }
    }

    fun playMusic(soundFile: String) {

        try {
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


    var resultCallbackOfSelectedMusicTrack: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result!!.resultCode == RESULT_OK) {
                val data: Intent = result.getData()!!
                songId = data.getStringExtra(EXTRA_SONG_ID).toString()
                val name = data.getStringExtra(EXTRA_SONG_NAME)
                val audio = data.getParcelableExtra<Uri>(EXTRA_SONG_FILE)
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
                playMusic(audio!!.path.toString())
                isMusicActive = true
                updateColorButtons()


            }
        }


    fun setupRecordDurationRecclerview() {
        binding.tabbarduration.animate().translationX(-1000f).setDuration(0).start()
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("15 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("30 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("60 Sec"))

        binding.tabbarduration.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
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


    fun setUPViews() {
        binding.buttonRecord.setOnClickListener {
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
                    startRecording()
                    binding.recordAnimationView.visibility = View.VISIBLE
                    binding.stopIConView.visibility = View.GONE
                }
            }
        }

        binding.buttonRecord.setOnLongClickListener {

            isImage = false
            setUpCameraView()
            startRecording()
            binding.recordAnimationView.visibility = View.VISIBLE
            binding.stopIConView.visibility = View.GONE
            binding.buttonRecord.setOnLongClickListener(null)
            return@setOnLongClickListener true
        }




        binding.buttonDone.setOnClickListener { view: View? ->
            if (isImage) {

                createDirectoryAndSaveFile(binding.selectedPhoto.capture())
                /*     binding.selectedPhoto.saveToPictures("yoo", System.currentTimeMillis().toString() + ".jpg",700,700,object :GPUImageView.OnPictureSavedListener{
                         override fun onPictureSaved(uri: Uri?) {

                         }
                     })*/
            } else {
                if (binding.cameraView.isTakingVideo()) {
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
                if (binding.cameraView.isTakingPicture()) {
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
            /* if (isImage) {
                    imageChooser()
                } else {
                    videoChooser()
                }*/
            imageChooser()

            /*        var mEmojiBSFragment = FragmentGallery(thumbnails, typeMedia, arrPath)
                    mEmojiBSFragment.setEmojiListener(this)
                    if (mEmojiBSFragment.isAdded) {

                    }
                    mEmojiBSFragment!!.show(
                        supportFragmentManager,
                        mEmojiBSFragment!!.tag
                    )
        */

        }

        binding.buttonCameraChanger.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.cameraView.toggleFacing()
                if (binding.cameraView.facing == Facing.FRONT) {
                    binding.buttonFlash.visibility = View.GONE
                } else {
                    binding.buttonFlash.visibility = View.VISIBLE
                }
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
                binding.rvFilters.setVisibility(
                    if (binding.rvFilters.getVisibility() == View.VISIBLE) View.GONE else View.VISIBLE
                )

                if (binding.rvFilters.getVisibility() == View.VISIBLE) {
                    isFilterActive = true
                    binding.ivFilter.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ActivityVideoRecorder,
                            R.drawable.ic_crtpost_magic_stick_active
                        )
                    )

                } else {
                    isFilterActive = false
                    binding.ivFilter.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ActivityVideoRecorder,
                            R.drawable.ic_crtpost_magic_stick
                        )
                    )

                }
                updateColorButtons()
            }
        }
        binding.buttonFlash.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.cameraView.setFlash(if (binding.cameraView.getFlash() === Flash.OFF) Flash.TORCH else Flash.OFF)

                isFlashActive = !isFlashActive
                updateColorButtons()

            }
        }
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


            if (isCowntDownActive) {
                isCowntDownActive = false
                hideDurationBar(show = false)
                updateColorButtons()
            } else {
                isCowntDownActive = true
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
                    finish()
                }
            })
        if (isImage){
            if (isImageTakenByCamera) {
                commonConfirmationDialog.show()
            }else{
                super.onBackPressed()
            }

        }else{
            if (mModel!!.segments!!.size > 0) {
                commonConfirmationDialog.show()
            }else{
                super.onBackPressed()
            }
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


    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }




/*    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val decorView = window.decorView
        if (hasFocus) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }*/

    var isMusicActive = false
    var isFilterActive = false
    var isSpeedActive = false
    var isFlashActive = false
    var isCowntDownActive = false


    fun updateColorButtons() {
        if (isMusicActive) {
            binding.ivMusic.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_top_music_active
                )
            )
        } else {
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
        } else {
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
        } else {
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

// Return only video and image metadata.

// Return only video and image metadata.
        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

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
                    getContentResolver(), id.toLong(),
                    MediaStore.Images.Thumbnails.MINI_KIND, bmOptions
                ) else if (t == 3) thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                    getContentResolver(), id.toLong(),
                    MediaStore.Video.Thumbnails.MINI_KIND, bmOptions
                )
                arrPath[i] = cursor.getString(dataColumnIndex)
                typeMedia[i] = cursor.getInt(type)

            } catch (e: Exception) {
            }

        }

    }

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
            setUpCameraView()
            val selectedVideo = uri
            val selectedImageBitmap: Bitmap
            try {
                Log.d("a;lskdasdhhhhhh", "data.data!!.path!!")
                Log.d("a;lskdasdkjjjjjj", uri.path!!)
                mModel!!.video = File(uri.path!!)
                Handler(Looper.getMainLooper()).post {

                    runOnUiThread {
                        processCurrentRecording()
                    }
                }
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
                Handler(Looper.getMainLooper()).post {
                    binding.selectedPhoto.visibility = View.VISIBLE
                    binding.selectedPhoto.gpuImage.deleteImage()
                    runOnUiThread {
                        binding.selectedPhoto.setImage(selectedImageBitmap);
                        binding.cameraView.visibility = View.GONE
                        //  createVideo(it!!.absolutePath, result.size)
                        binding.buttonDone.visibility = View.VISIBLE
                        binding.layoutBottomControll.visibility = View.GONE
                        // closeFinally(mModel!!.video!!)
                    }
                }
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
    var progressStatus = 0

    fun pauseProgress() {
        progressHandler!!.removeCallbacks(runnable)
    }

    fun resumeProgress() {
        progressHandler!!.post(runnable)
    }

    var prolength = 0 //

    //
    var runnable: Runnable = object : Runnable {
        override fun run() {

            prolength = binding.segmentedProgressbar.getProgress() + 1
            binding.segmentedProgressbar.setProgress(prolength)
            //100,1000runnable
            if (prolength < 100) {
                progressHandler.postDelayed(this, 1000)
            } else {
                binding.segmentedProgressbar.setProgress(0)
                progressHandler.post(this)
            }
        }
    }


}


class CenterLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val centerSmoothScroller = CenterSmoothScroller(recyclerView.context)
        centerSmoothScroller.targetPosition = position
        startSmoothScroll(centerSmoothScroller)

    }

    private class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }
}

class OffsetItemDecoration(private val ctx: Context) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = (screenWidth / 2.toFloat()).toInt() - view.layoutParams.width / 2
        val lp = view.layoutParams as MarginLayoutParams
        if (parent.getChildAdapterPosition(view) == 0) {
            (view.layoutParams as MarginLayoutParams).leftMargin = 0
            setupOutRect(outRect, offset, true)
        } else if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            (view.layoutParams as MarginLayoutParams).rightMargin = 0
            setupOutRect(outRect, offset, false)
        }
    }

    private fun setupOutRect(rect: Rect, offset: Int, start: Boolean) {
        if (start) {
            rect.left = offset
        } else {
            rect.right = offset
        }
    }

    private val screenWidth: Int
        get() {
            val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display: Display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
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