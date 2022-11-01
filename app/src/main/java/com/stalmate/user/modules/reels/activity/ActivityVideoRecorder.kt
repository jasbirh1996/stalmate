package com.stalmate.user.modules.reels.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.Display
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daasuu.imagetovideo.EncodeListener
import com.daasuu.imagetovideo.ImageToVideoConverter
import com.google.android.material.tabs.TabLayout
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
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
import com.stalmate.user.modules.reels.filters.*
import com.stalmate.user.modules.reels.utils.VideoFilter
import com.stalmate.user.modules.reels.utils.VideoUtil
import com.stalmate.user.modules.reels.workers.MergeAudioVideoWorker
import com.stalmate.user.modules.reels.workers.MergeVideosWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker
import com.stalmate.user.utilities.Common
import com.user.vaibhavmodules.reels.utils.SharedConstants
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class ActivityVideoRecorder : BaseActivity() {

    val EXTRA_AUDIO = "audio"
    private val TAG = "RecorderActivity"
    private var imageVideoDuration = 15
    private var mModel: RecorderActivityViewModel? = null
    private val mHandler = Handler()
    private var isImage = false
    private var mMediaPlayer: MediaPlayer? = null
    val PICK_FILE = 99
    var isDurationTabbarShowing=false
    var isspeedTabbarShowing=false
    private val mStopper = Runnable { stopRecording() }
    lateinit var binding: ActivityVideoRecorderBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoRecorderBinding.inflate(layoutInflater)
        mModel = ViewModelProvider(this)[RecorderActivityViewModel::class.java]
        setContentView(binding.root)
        if (intent.getStringExtra("type") != null) {
            isImage = true
        }

        isPermissionGranted(     arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ))

        setUpCameraView()

        setUPViews()
    }
/*
    fun setupView(){
        if (isImage){
            binding.stopIConView.drawable=ContextCompat.getDrawable(this,R.drawable.image)
        }
    }*/


    override fun onDestroy() {
        super.onDestroy()
        /* binding.cameraView.close()*/
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    fun setUpCameraView() {

        binding.cameraView.setLifecycleOwner(this);
        if (isImage) {
            binding.buttonSpeed.visibility=View.GONE
            binding.buttonDone.visibility=View.GONE
            binding.cameraView.setMode(Mode.PICTURE);
        } else {
            binding.buttonDone.visibility=View.VISIBLE
            binding.cameraView.setMode(Mode.VIDEO);
        }
        binding.cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        binding.cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        binding.cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.TAKE_PICTURE);
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {


                result.toFile(File(cacheDir, UUID.randomUUID().toString()), FileCallback {

                    runOnUiThread {

                        binding.selectedPhoto.visibility=View.VISIBLE
                        result.toBitmap {
                            Glide.with(this@ActivityVideoRecorder).load(it!!).into(binding.selectedPhoto)
                        }
                      //  createVideo(it!!.absolutePath, result.size)
                        mModel!!.video = File(it!!.absolutePath)
                    closeFinally(mModel!!.video!!)
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

                binding.segmentedProgressbar.pause()
                //    binding.segmentedProgressbar.addDivider()
                binding.buttonRecord.setSelected(false)
                if (mMediaPlayer != null) {
                    mMediaPlayer!!.pause()
                }
                mHandler.postDelayed({ processCurrentRecording() }, 500)
            }

            override fun onVideoRecordingStart() {

                binding.buttonRecord.setSelected(true)
                if (mMediaPlayer != null) {
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
                }
                binding.segmentedProgressbar.start()
            }
        })


    }

    override fun onResume() {
        super.onResume()
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }


    }


/*    fun onBtnSavePng(view: View?) {
        try {
            val fileName: String = getCurrentTimeString().toString() + ".jpg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
                values.put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                val directory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val file = File(directory, fileName)
                values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            contentResolver.openOutputStream(uri!!).use { output ->
                val bm: Bitmap = textureView.getBitmap()
                bm.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }
        } catch (e: Exception) {
            Log.d("onBtnSavePng", e.toString()) // java.io.IOException: Operation not permitted
        }
    }*/

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
                    val snapHelper = LinearSnapHelper()
                    snapHelper.attachToRecyclerView(  binding.rvFilters)


                    binding.rvFilters.setNestedScrollingEnabled(false)
                    binding.rvFilters.setHasFixedSize(true)
                    binding.rvFilters.adapter = adapter
                    binding.rvFilters.addItemDecoration(OffsetItemDecoration(this@ActivityVideoRecorder))
                   // binding.rvFilters.layoutManager=CenterLayoutManager(this@ActivityVideoRecorder, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvFilters.layoutManager=CenterZoomLayoutManager(this@ActivityVideoRecorder, LinearLayoutManager.HORIZONTAL, false)

                    binding.rvFilters.addOnScrollListener(object :RecyclerView.OnScrollListener(){

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


                    })

                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }






    private fun applyPreviewFilter(filter: VideoFilter) {

     //   binding.rvFilters.visibility=View.GONE


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
            binding.cameraView.takeVideoSnapshot(
                mModel!!.video!!, ((SharedConstants.MAX_DURATION - recorded).toInt())
            )
        }
    }

    private fun stopRecording() {
        binding.cameraView.stopVideo()
//        mHandler.removeCallbacks(mStopper)
    }

    private fun commitImage() {
        showLoader()
        val videos: MutableList<String> = ArrayList()
        for (segment in mModel!!.segments) {
            videos.add(segment.file!!.absolutePath)
        }
        val merged1 = File(cacheDir, UUID.randomUUID().toString())
        val data1: Data = Data.Builder()
            .putStringArray(MergeVideosWorker.KEY_VIDEOS, videos.toTypedArray())
            .putString(MergeVideosWorker.KEY_OUTPUT, merged1.absolutePath)
            .build()

        val request1: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MergeVideosWorker::class.java)
            .setInputData(data1)
            .build()

        val wm: WorkManager = WorkManager.getInstance(this)
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
            wm.getWorkInfoByIdLiveData(request1.getId())
                .observe(this) { info ->
                    val ended = (info.getState() === WorkInfo.State.CANCELLED
                            || info.getState() === WorkInfo.State.FAILED)
                    if (info.getState() === WorkInfo.State.SUCCEEDED) {
                        dismissLoader()
                        closeFinally(merged1)
                    } else if (ended) {

                    }
                }
        }
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
        binding.cameraView.destroy()
        val intent = Intent(this, ActivityVideoEditor::class.java)
        intent.putExtra(ActivityFilter.EXTRA_SONG, mModel!!.audio)
        intent.putExtra(ActivityFilter.EXTRA_VIDEO, file.absolutePath)
        intent.putExtra("isImage", isImage)
        startActivity(intent)
        finish()

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


 /*   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath: String? = ""
        if (requestCode == PICK_FILE && resultCode == RESULT_OK) {

            val uri: Uri? = data!!.getData()
            val wholeID = DocumentsContract.getDocumentId(uri)

            // Split at colon, use second item in the array
            val id = wholeID.split(":").toTypedArray()[1]

            val column = arrayOf(MediaStore.MediaColumns.DATA)

            // where id is equal to
            val sel = MediaStore.Audio.Media._ID + "=?"

            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )

            val columnIndex = cursor!!.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()

            Log.d("jcbjscb", "Chosen path = $filePath")

          //  mModel!!.audio = filePath
            //  mMediaPlayer = MediaPlayer.create(this, data.getData())

            *//*         var mp=MediaPlayer()
                                    mp.setDataSource(this,Uri.parse(filePath))
                                    mp.prepare()
                                    mp.start()*//*
        }
    }*/

    var resultCallbackOfSelectedMusicTrack: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result!!.resultCode == RESULT_OK) {
                val data: Intent = result.getData()!!
                val id = data.getIntExtra(EXTRA_SONG_ID, 0)
                val name = data.getStringExtra(EXTRA_SONG_NAME)
                val audio = data.getParcelableExtra<Uri>(EXTRA_SONG_FILE)

                Log.d("klajsdasd",audio!!.path.toString())
                binding.tvMusicName.text=name
                binding.layoutSelectedMusic.visibility=View.VISIBLE
                mModel!!.audio = audio
            }
        }

    lateinit var imageToVideo: ImageToVideoConverter
    private fun createVideo(imagePath: String, size: com.otaliastudios.cameraview.size.Size) {
        //int height = getInputData().getInt(ICON,0);
        // int width = getInputData().getInt(ICON,0);

        showLoader()
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        Log.d("asdasdasd",size.height.toString())
        Log.d("asdasdasd",size.width.toString())
        imageToVideo = ImageToVideoConverter(
            outputPath = outputPath,
            inputImagePath = imagePath,
            size = Size(528,1072),
            duration = TimeUnit.SECONDS.toMicros(imageVideoDuration.toLong()),
            listener = object : EncodeListener {
                override fun onProgress(progress: Float) {
                    Log.d("progress", "progress = $progress")
                    runOnUiThread {

                    }
                }

                override fun onCompleted() {
                    runOnUiThread {
                        Log.d("as;ldasd", outputPath)
                        mModel!!.video = File(outputPath)

                        val segment = RecordSegment()
                        segment.file = mModel!!.video
                        val duration: Long =
                            VideoUtil.getDuration(
                                this@ActivityVideoRecorder,
                                Uri.fromFile(mModel!!.video)
                            )
                        segment.duration = duration
                        Log.d("as;ldasd", duration.toString())
                        mModel!!.segments.add(segment)
                        dismissLoader()
                        commitImage()

                    }
                }

                override fun onFailed(exception: Exception) {

                }
            }
        )
        imageToVideo?.start()


        /*


           val outputPath = Common.getFilePath(this, Common.VIDEO)
           val size: ISize = SizeOfImage(imagePath)
           val query = ffmpegQueryExtension.imageToVideo(
               imagePath,
               outputPath,
               10,
               size.width(),
               size.height()
           )

           CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
               override fun process(logMessage: LogMessage) {

               }

               override fun success() {
                   Log.d("as;ldasd", outputPath)
                   mModel!!.video = File(outputPath)

                   val segment = RecordSegment()
                   segment.file = mModel!!.video
                   val duration: Long =
                       VideoUtil.getDuration(this@ActivityVideoRecorder, Uri.fromFile(mModel!!.video))
                   segment.duration = duration
                   Log.d("as;ldasd", duration.toString())
                   mModel!!.segments.add(segment)
                   commitImage()

               }

               override fun cancel() {

               }

               override fun failed() {

               }

           })*/
    }


    fun setupRecordDurationRecclerview() {
        binding.tabbarduration.animate().translationX(-1000f).setDuration(0).start()
        binding.tabbarduration.addTab( binding.tabbarduration.newTab().setText("15 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("30 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("60 Sec"))

        binding.tabbarduration.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        imageVideoDuration = 15
                        binding.ivTimer.setImageDrawable(ContextCompat.getDrawable(this@ActivityVideoRecorder,R.drawable.ic_crtpost_timer_one_active))
                        hideDurationBar(show = false)
                    }
                    1 -> {
                        imageVideoDuration = 30
                        binding.ivTimer.setImageDrawable(ContextCompat.getDrawable(this@ActivityVideoRecorder,R.drawable.ic_crtpost_timer_two_active))
                        hideDurationBar(show = false)
                    }
                    2 -> {
                        imageVideoDuration = 60
                        binding.ivTimer.setImageDrawable(ContextCompat.getDrawable(this@ActivityVideoRecorder,R.drawable.ic_crtpost_timer_three_active))
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





    fun hideDurationBar(show:Boolean){

        if (show){
            binding.tabbarduration.animate().translationX(0f).setDuration(500).start()
        }else{
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                binding.tabbarduration.animate().translationX(-1000f).setDuration(500).start()
            },500)
        }
    }


    fun setuptabSpeedRecclerview() {
        var speed = 1f
        binding.tabbarspeed.animate().translationX(-1000f).setDuration(0).start()
        binding.tabbarspeed.addTab( binding.tabbarspeed.newTab().setText("0.5x"))
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
                mModel!!.speed=speed
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                hideSpeedBar(show = false)
            }
        })
    }
    fun hideSpeedBar(show:Boolean){

        if (show){
            binding.tabbarspeed.animate().translationX(0f).setDuration(500).start()
        }else{
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                binding.tabbarspeed.animate().translationX(-1000f).setDuration(500).start()
            },500)
        }
    }




    fun setUPViews(){
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

        if (isImage){

        }


        binding.buttonDone.setOnClickListener { view: View? ->

            if (isImage) {

                if (mModel!!.segments.isEmpty()){
                   // Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
                }else{
                    commitImage()
                }
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

         /*   if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
            } else if (mModel!!.segments.isEmpty()) {
                Toast.makeText(this, R.string.recorder_error_no_clips, Toast.LENGTH_SHORT).show()
            } else {
                commitRecordings()
            }*/
        }
        binding.buttonMusic.setOnClickListener { view: View? ->


            if (isImage){
                if (binding.cameraView.isTakingPicture()) {
                    Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    /*val intent = Intent(
                        this@ActivityVideoRecorder,
                        ActivitySongPicker::class.java
                    )
                    startActivityForResult(intent, SharedConstants.REQUEST_CODE_PICK_SONG)*/
                   resultCallbackOfSelectedMusicTrack.launch(IntentHelper.getSongPickerActivity(this))
                }

            }else{
                if (binding.cameraView.isTakingVideo()) {
                    Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                        .show()
                } else {

                    resultCallbackOfSelectedMusicTrack.launch(IntentHelper.getSongPickerActivity(this))
                }
            }


        }
        binding.buttonCameraChanger.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.cameraView.toggleFacing()
            }
        }




        binding.buttonSpeed.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (isspeedTabbarShowing){
                    isspeedTabbarShowing=false
                    hideSpeedBar(show = false)
                }else{
                    isspeedTabbarShowing=true
                    hideSpeedBar(show = true)
                }
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

                if (binding.rvFilters.getVisibility() == View.VISIBLE){
                    binding.ivFilter.setImageDrawable(ContextCompat.getDrawable(this@ActivityVideoRecorder,R.drawable.ic_crtpost_magic_stick_active))

                }else{
                    binding.ivFilter.setImageDrawable(ContextCompat.getDrawable(this@ActivityVideoRecorder,R.drawable.ic_crtpost_magic_stick))

                }
            }
        }
        binding.buttonFlash.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.cameraView.setFlash(if (binding.cameraView.getFlash() === Flash.OFF) Flash.TORCH else Flash.OFF)
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


            if (isDurationTabbarShowing){
                isDurationTabbarShowing=false
                hideDurationBar(show = false)
            }else{
                isDurationTabbarShowing=true
                hideDurationBar(show = true)
            }

        }
        setupRecordDurationRecclerview()
        setuptabSpeedRecclerview()
    }





}



class CenterLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val centerSmoothScroller = CenterSmoothScroller(recyclerView.context)
        centerSmoothScroller.targetPosition = position
        startSmoothScroll(centerSmoothScroller)

    }

    private class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
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