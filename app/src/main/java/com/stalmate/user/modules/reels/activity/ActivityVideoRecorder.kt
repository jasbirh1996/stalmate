package com.stalmate.user.modules.reels.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.filter.Filters
import com.otaliastudios.cameraview.filters.BrightnessFilter
import com.otaliastudios.cameraview.filters.GammaFilter
import com.otaliastudios.cameraview.filters.SharpnessFilter
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
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
import com.user.vaibhavmodules.reels.utils.SharedConstants

import java.io.File
import java.util.*


class ActivityVideoRecorder : BaseActivity() {


    val EXTRA_AUDIO = "audio"
    private val TAG = "RecorderActivity"

    private var mModel: RecorderActivityViewModel? = null
    private val mHandler = Handler()
    private var mMediaPlayer: MediaPlayer? = null
    val PICK_FILE = 99

    private val mStopper = Runnable { stopRecording() }
    lateinit var binding: ActivityVideoRecorderBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoRecorderBinding.inflate(layoutInflater)
        mModel = ViewModelProvider(this)[RecorderActivityViewModel::class.java]

        setContentView(binding.root)
        binding.cameraView.open()
        setUpCameraView()


        binding.buttonRecord.setOnClickListener {
            if (binding.cameraView.isTakingVideo){
                stopRecording()
                binding.recordAnimationView.visibility=View.GONE
                binding.stopIConView.visibility=View.VISIBLE
            }else{
                startRecording()
                binding.recordAnimationView.visibility=View.VISIBLE
                binding.stopIConView.visibility=View.GONE
            }
        }

        binding.buttonDone.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
            } else if (mModel!!.segments.isEmpty()) {
                Toast.makeText(this, R.string.recorder_error_no_clips, Toast.LENGTH_SHORT).show()
            } else {
                commitRecordings()
            }
        }
        binding.buttonMusic.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            }else{
                val intent = Intent(
                    this@ActivityVideoRecorder,
                    ActivitySongPicker::class.java
                )
                startActivityForResult(intent, SharedConstants.REQUEST_CODE_PICK_SONG)





            }
        }

        binding.buttonCameraChanger.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo()) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            }else{
                binding.cameraView.toggleFacing()
            }
        }

        binding.buttonSpeed.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.speeds.setVisibility(
                    if (    binding.speeds.getVisibility() == View.VISIBLE) View.GONE else View.VISIBLE
                )
            }
        }


        binding.buttonColorFilterIcon.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.rvFilters.setVisibility(
                    if (    binding.rvFilters.getVisibility() == View.VISIBLE) View.GONE else View.VISIBLE
                )
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
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.cameraView.close()
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying()) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    fun setUpCameraView() {

        binding.cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        binding.cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        binding.cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.TAKE_PICTURE);
        // Long tap to shoot!

        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bitmap: Bitmap? ->

                }
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
                             mMediaPlayer!!.playbackParams=params
                         }
                         mMediaPlayer!!.start()
                     }
                binding.segmentedProgressbar.start()
            }
        })
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
                    val adapter = FilterAdapter(this@ActivityVideoRecorder, bitmap, binding.cameraView, true)
                    adapter.setListener { filter: VideoFilter? ->
                        this@ActivityVideoRecorder.applyPreviewFilter(
                            filter!!
                        )
                    }
                    binding.rvFilters.setNestedScrollingEnabled(false)
                    binding.rvFilters.setHasFixedSize(true)
                    binding.rvFilters.adapter = adapter
                    binding.rvFilters.visibility = View.VISIBLE
                }
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }

    private fun applyPreviewFilter(filter: VideoFilter) {
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


    private fun startRecording() {
        val recorded = mModel!!.recorded()
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

    private fun commitRecordings() {
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

            val merged2 = File(cacheDir, UUID.randomUUID().toString())

            val data2: Data = Data.Builder()
                .putString(MergeAudioVideoWorker.KEY_AUDIO, mModel!!.audio!!.path)
                .putString(MergeAudioVideoWorker.KEY_VIDEO, merged1.absolutePath)
                .putString(MergeAudioVideoWorker.KEY_OUTPUT, merged2.absolutePath)
                .build()

            val request2: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MergeAudioVideoWorker::class.java)
                .setInputData(data2)
                .build()

            Log.d("jjjkkjkjkj", data2.toString())

            wm.beginWith(request1).then(request2).enqueue()
            wm.getWorkInfoByIdLiveData(request2.getId())
                .observe(this) { info ->
                    val ended = (info.state === WorkInfo.State.CANCELLED
                            || info.state === WorkInfo.State.FAILED)
                    if (info.state === WorkInfo.State.SUCCEEDED) {
                        closeFinally(merged2)
                    } else if (ended) {
                    }
                 }
        } else {
            wm.enqueue(request1)
            wm.getWorkInfoByIdLiveData(request1.getId())
                .observe(this) { info ->
                    val ended = (info.state === WorkInfo.State.CANCELLED
                            || info.state === WorkInfo.State.FAILED)
                    if (info.state === WorkInfo.State.SUCCEEDED) {
                        closeFinally(merged1)
                    } else if (ended) {

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

                val ended = (info.state == WorkInfo.State.CANCELLED || info.state == WorkInfo.State.FAILED)

                if (info.state == WorkInfo.State.SUCCEEDED) {

                    val segment = RecordSegment()
                    segment.file = output
                    segment.duration = duration
                    mModel!!.segments.add(segment)
                    file.delete()
                    dismissLoader()
                } else if (ended) {

                    file.delete()
                    dismissLoader()
                }
            }
    }
    private fun closeFinally(video: File) {

        dismissLoader()
        val intent = Intent(this, ActivityFilter::class.java)
        intent.putExtra(ActivityFilter.EXTRA_SONG, mModel!!.song)
        intent.putExtra(ActivityFilter.EXTRA_VIDEO, video.absolutePath)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var filePath: String? = ""
        if (requestCode == PICK_FILE && resultCode == RESULT_OK) {

            if (android.R.attr.data != null) {
                val uri: Uri? = data!!.getData()
                /* uri?.let { getContentResolver().openInputStream(it).toString() }
                     ?.let { Log.d("cbjkabcjk", it) }*/

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
                /*setImageFromIntent(filePath)*/
                Log.d("jcbjscb", "Chosen path = $filePath")

                mModel!!.audio = uri
                mMediaPlayer = MediaPlayer.create(this, data.getData())

                var mp=MediaPlayer()
                    mp.setDataSource(this,Uri.parse(filePath))
                    mp.prepare()
                    mp.start()
            }
        }
    }



    /*@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.v(
            TAG,
            "Received request: $requestCode, result: $resultCode."
        )
 *//*       if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selection: List<String>? =
                data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH)
            if (selection != null && !selection.isEmpty()) {
                val first = selection[0]
                Log.v(
                    ltd.starthub.muly.activities.RecorderActivity.TAG,
                    "User chose video file: $first"
                )
                val intent = Intent(this, TrimmerActivity::class.java)
                intent.putExtra(TrimmerActivity.EXTRA_VIDEO, first)
                startActivity(intent)
                finish()
            }
        } else *//*
        if (requestCode == SharedConstants.REQUEST_CODE_PICK_SONG && resultCode == RESULT_OK && data != null) {
            val id = data.getIntExtra(EXTRA_SONG_ID, 0)
            val name = data.getStringExtra(EXTRA_SONG_NAME)
            val audio = data.getParcelableExtra<Uri>(EXTRA_SONG_FILE)
            if (!TextUtils.isEmpty(name) && audio != null) {
                Log.v(
                    TAG,
                    "User chose audio file: $audio"
                )
             *//*   val sound = findViewById<TextView>(R.id.sound)
                sound.text = name*//*
                mModel!!.audio = audio
                mModel!!.song = id
                mMediaPlayer = MediaPlayer.create(this, audio)
                mMediaPlayer!!.setOnCompletionListener(OnCompletionListener { mp: MediaPlayer? ->
                    mMediaPlayer = null
                })
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    */





}