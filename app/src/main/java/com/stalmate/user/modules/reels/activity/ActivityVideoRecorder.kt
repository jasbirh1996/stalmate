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
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.*
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
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityVideoRecorderBinding
import com.stalmate.user.modules.reels.adapter.GalleryItem
import com.stalmate.user.modules.reels.filters.*
import com.stalmate.user.modules.reels.filters.epf.EPlayerView
import com.stalmate.user.modules.reels.filters.epf.filter.*
import com.stalmate.user.modules.reels.photo_editing.Counter
import com.stalmate.user.modules.reels.utils.VideoFilter
import com.stalmate.user.modules.reels.utils.VideoUtil
import com.stalmate.user.utilities.Common
import com.stalmate.user.utilities.PathUtil
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import jp.co.cyberagent.android.gpuimage.filter.*
import ly.img.android.pesdk.PhotoEditorSettingsList
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.ui.activity.PhotoEditorActivityResultContract
import ly.img.android.pesdk.ui.activity.VideoEditorActivityResultContract
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit


class ActivityVideoRecorder : BaseActivity(), FragmentGallery.GalleryPickerListener {

    private val TAG = "RecorderActivity"
    private var imageVideoDuration = 15
    private var mModel: RecorderActivityViewModel? = null
    private val mHandler = Handler()
    var songId = ""
    private var isImage = true
    private var isImageTakenByCamera = false
    private var isVideoTaken = false
    private var isFilterApplied = false
    private var mMediaPlayer: MediaPlayer? = null
    val PICK_FILE = 99
    private val mStopper = Runnable { stopRecording() }
    lateinit var binding: ActivityVideoRecorderBinding
    override fun onClick(viewId: Int, view: View?) {

    }

    fun isPermissionGranted(): Boolean {
        return isPermissionGranted(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
            )
        )
    }

    var countdownTimerDuration = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemBars()
        super.onCreate(savedInstanceState)
        binding = ActivityVideoRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!isPermissionGranted()) isPermissionGranted()
        mModel = ViewModelProvider(this)[RecorderActivityViewModel::class.java]
        binding.cameraView.engine = Engine.CAMERA2
        //Fetch Image
        getGalleryData()
        //Media Player
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()
        )
        //Set Counter
        counter = Counter(onCaptureAfterNthSeconds = { type: String, duration: Int ->
            isImage = (type != "video")
            uiWhenCountdownActive()
            countdownTimerDuration = duration
            captureTimerHandler.removeCallbacks(runnableForTimer)
            captureTimerHandler.post(runnableForTimer)
        }, onRangeDialogDismiss = {
            binding.layoutBottomControll.visibility = View.VISIBLE
        })


//        binding.selectedPhoto.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        binding.cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        binding.cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        binding.cameraView.clearGesture(Gesture.LONG_TAP)
        binding.cameraView.useDeviceOrientation = false
        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                isImageTakenByCamera = true
                result.toFile(File(cacheDir, UUID.randomUUID().toString()), FileCallback {
                    mModel?.video = it
                    result.toBitmap {
//                        binding.selectedPhoto.visibility = View.VISIBLE
//                        binding.selectedPhoto.setImage(it)
                        setupProgressBarWithDuration()
                        setUPCameraViewsOnCapture(false)
                        updateColorButtons()
                    }
                })
                super.onPictureTaken(result)
            }

            override fun onZoomChanged(
                newValue: Float, bounds: FloatArray, fingers: Array<PointF?>?
            ) {
                // newValue: the new zoom value
                // bounds: this is always [0, 1]
                // fingers: if caused by touch gestures, these is the fingers position
            }

            override fun onCameraOpened(options: CameraOptions) {
                super.onCameraOpened(options)
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
        setUPViews()
        setUPCameraViewsOnCapture(true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
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
                binding.segmentedProgressbar.visibility = View.GONE

                binding.buttonSpeed.visibility = View.VISIBLE
                binding.buttonDurationTimer.visibility = View.VISIBLE
                binding.buttonCaptureCounter.visibility = View.VISIBLE
                binding.buttonFlash1.visibility = View.GONE
                binding.buttonReverse.visibility = View.GONE

                binding.buttonPickData.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.VISIBLE
                binding.buttonDone.visibility = View.GONE

//                binding.selectedPhoto.visibility = View.GONE
                binding.cameraView.visibility = View.VISIBLE

                runOnUiThread { binding.cameraView.open() }
            } else {
                binding.segmentedProgressbar.visibility = View.GONE

                binding.buttonSpeed.visibility = View.GONE
                binding.buttonDurationTimer.visibility = View.GONE
                binding.buttonCaptureCounter.visibility = View.GONE
                binding.buttonFlash1.visibility = View.GONE
                binding.buttonReverse.visibility = View.GONE

                binding.buttonPickData.visibility = View.GONE
                binding.layoutBottomControll.visibility = View.INVISIBLE
                binding.buttonDone.visibility = View.VISIBLE

//                binding.selectedPhoto.visibility = View.VISIBLE
                binding.cameraView.visibility = View.GONE

                binding.cameraView.close()
                binding.cameraView.invalidate()
            }
            binding.stopIConView.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.circle_camerabackground
                )
            )
        } else {
            if (isDefault) {
                binding.segmentedProgressbar.visibility = View.GONE

                binding.buttonSpeed.visibility = View.VISIBLE
                binding.buttonDurationTimer.visibility = View.VISIBLE
                binding.buttonCaptureCounter.visibility = View.VISIBLE
                binding.buttonFlash1.visibility = View.VISIBLE
                binding.buttonReverse.visibility = View.VISIBLE

                binding.buttonPickData.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.VISIBLE
                binding.buttonDone.visibility = View.GONE

//                binding.selectedPhoto.visibility = View.GONE
                binding.cameraView.visibility = View.VISIBLE

                binding.layoutMovieWrapper.visibility = View.GONE
            } else {
                //binding.segmentedProgressbar.visibility = View.GONE

                binding.buttonSpeed.visibility = View.VISIBLE
                binding.buttonDurationTimer.visibility = View.GONE
                binding.buttonCaptureCounter.visibility = View.GONE
                binding.buttonFlash1.visibility = View.GONE
                binding.buttonReverse.visibility = View.VISIBLE

                binding.buttonPickData.visibility = View.GONE
                binding.layoutBottomControll.visibility = View.INVISIBLE
                binding.buttonDone.visibility = View.VISIBLE

                runOnUiThread {
                    binding.cameraView.close()
                    binding.cameraView.visibility = View.GONE
                }
            }
            binding.stopIConView.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.record
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

    var recorded = 0.toLong()
    private fun startRecording() {
        binding.buttonRecord.setOnClickListener(null)
        binding.buttonRecord.setOnLongClickListener(null)
        recorded = mModel!!.recorded()
        if (recorded >= TimeUnit.SECONDS.toMillis(imageVideoDuration.toLong())) {
            Toast.makeText(
                this@ActivityVideoRecorder, R.string.recorder_error_maxed_out, Toast.LENGTH_SHORT
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
        for (segment in mModel?.segments!!) {
            segment.file?.absolutePath?.let { videos.add(it) }
        }

        if (mModel?.speed != 1f) {
            val outputPath = Common.getFilePath(this, Common.VIDEO)
            showLoader()
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val asyncTask =
                FFmpegAsyncTask("-i ${mModel?.segments?.get(0)?.file?.absolutePath} " +
                        "-vf setpts=PTS/${mModel?.speed.toString()}  -crf 23 -preset ultrafast " +
                        "-vcodec libx264 -c:a aac  $outputPath",
                    object : FFmpegAsyncTask.OnTaskCompleted {
                        override fun onTaskCompleted(isSuccess: Boolean) {
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
                FFmpegAsyncTask(
                    "-f concat -safe 0 -i ${generateVideoListFile(videos)}  " +
                            " -crf 23 -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                    object : FFmpegAsyncTask.OnTaskCompleted {
                        override fun onTaskCompleted(isSuccess: Boolean) {
                            finishVideoWithFilter(File(outputPath))
                        }
                    })
            asyncTask.execute()
        }
    }


    private fun finishVideoWithFilter(file: File) {
        if (!isImage) {
            isVideoTaken = true
            binding.buttonDone.visibility = View.VISIBLE
        }
        mPlayer?.playWhenReady = false
        dismissLoader()
        closeFinally(file)
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
        inputs: MutableList<String>, perIMageDuration: Int
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
        val duration: Long = VideoUtil.getDuration(this, Uri.fromFile(mModel?.video))
        applyVideoSpeed(mModel?.video!!, mModel?.speed!!, duration)
    }

    private fun applyVideoSpeed(file: File, speed: Float, duration: Long) {
        applySpeed(file, speed, duration)
    }

    private fun closeFinally(file: File) {
        binding.cameraView.close()
        //  binding.cameraView.destroy()
        /*val intent = Intent(this, ActivityVideoEditor::class.java)
        mModel?.audio?.path?.let {
            intent.putExtra(
                ActivityFilter.EXTRA_SONG, File(it).absolutePath
            )
        }
        intent.putExtra(EXTRA_SONG_DURATION, imageVideoDuration.toString())
        intent.putExtra(ActivityFilter.EXTRA_VIDEO, file.absolutePath)
        intent.putExtra("isImage", isImage)
        intent.putExtra(EXTRA_SONG_ID, songId)
        pauseProgress()
        startActivity(intent)*/

        //Start Video & Image SDKs from here
        if (isImage) {
            // In this example, we do not need access to the Uri(s) after the editor is closed
            // so we pass false in the constructor
            val settingsList = PhotoEditorSettingsList(false)
                .configure<LoadSettings> {
                    // Set the source as the Uri of the image to be loaded
                    it.source = file.absolutePath.toUri()
                }
            photoEditorResult.launch(settingsList)
            // Release the SettingsList once done
            settingsList.release()
        } else {
            // In this example, we do not need access to the Uri(s) after the editor is closed
            // so we pass false in the constructor
            val settingsList = VideoEditorSettingsList(false)
                .configure<LoadSettings> {
                    // Set the source as the Uri of the video to be loaded
                    it.source = file.absolutePath.toUri()
                }
            videoEditorResult.launch(settingsList)
            // Release the SettingsList once done
            settingsList.release()
        }
    }

    private val photoEditorResult = registerForActivityResult(PhotoEditorActivityResultContract()) {
        when (it.resultStatus) {
            EditorSDKResult.Status.CANCELED -> showToast("Editor cancelled")
            EditorSDKResult.Status.EXPORT_DONE -> {
                startActivity(
                    IntentHelper.getCreateFuntimePostScreen(this)!!
                        .putExtra(ActivityFilter.EXTRA_VIDEO, it.resultUri)
                        .putExtra(EXTRA_SONG_ID, songId)
                )
                showToast("Result saved at ${it.resultUri}")
            }
            else -> {
            }
        }
    }

    private val videoEditorResult = registerForActivityResult(VideoEditorActivityResultContract()) {
        when (it.resultStatus) {
            EditorSDKResult.Status.CANCELED -> showToast("Editor cancelled")
            EditorSDKResult.Status.EXPORT_DONE -> {
                startActivity(
                    IntentHelper.getCreateFuntimePostScreen(this)!!
                        .putExtra(ActivityFilter.EXTRA_VIDEO, it.resultUri)
                        .putExtra(EXTRA_SONG_ID, songId)
                )
                showToast("Result saved at ${it.resultUri}")
            }
            else -> {
            }
        }
    }

    /**
     * Command for reversing segmented videos
     */
    /*private fun reverseVideoCommand() {
        val moviesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES
        )
        val srcDir = File(moviesDir, ".VideoSplit")
        val files = srcDir.listFiles()
        val filePrefix = "reverse_video"
        val fileExtn = ".mp4"
        val destDir = File(moviesDir, ".VideoPartsReverse")
        if (destDir.exists()) videoeditor.bhuvnesh.com.ffmpegvideoeditor.activity.MainActivity.deleteDir(
            destDir
        )
        destDir.mkdir()
        for (i in files.indices) {
            val dest = File(destDir, filePrefix + i + fileExtn)
            val command = arrayOf(
                "-i",
                files[i].absolutePath,
                "-vf",
                "reverse",
                "-af",
                "areverse",
                dest.absolutePath
            )
            if (i == files.size - 1) lastReverseCommand = command
            execFFmpegBinary(command)
        }
    }*/

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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

    var launchActivityForImagePick = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {

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
                            if (duration < 15 * 1000) {
                                Log.d("Less", "Less")
                                makeToast("Video should be grater than or equal to 15 seconds")

                            } else if (duration > 90 * 1000) {
                                makeToast("Video should be less than or equal to 90 seconds")
                            } else {
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
                        this, Uri.fromFile(File(getRealPathFromURIVideo(this, data!!.data!!)))
                    )
                    Log.d("dur", duration.toString())
                    if (duration < 15 * 1000) {
                        makeToast("Video should be grater than or equal to 15 seconds")
                        Log.d("dura", "Fail")
                    } else if (duration > 90 * 1000) {
                        makeToast("Video should be less than or equal to 90 seconds")
                        Log.d("duraMore", "Fail")
                    } else {
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
        if (result.resultCode == RESULT_OK) {
            var data: Intent = result.data!!
            // do your operation from here....
            if (data != null && data.data != null) {
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

    private fun setupRecordDurationRecclerview() {
        binding.tabbarduration.animate().translationX(-1000f).setDuration(0).start()
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("15 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("30 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("60 Sec"))
        binding.tabbarduration.addTab(binding.tabbarduration.newTab().setText("90 Sec"))

        binding.tabbarduration.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.tvTimer.setTextColor(resources.getColor(R.color.colorYellow, null))
                when (tab?.position) {
                    0 -> {
                        imageVideoDuration = 15
                        binding.segmentedProgressbar.max = imageVideoDuration
                        hideDurationBar(show = false)
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder, R.drawable.ic_crtpost_timer_one_active
                            )
                        )
                    }
                    1 -> {
                        imageVideoDuration = 30
                        binding.segmentedProgressbar.max = imageVideoDuration
                        hideDurationBar(show = false)
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder, R.drawable.ic_crtpost_timer_two_active
                            )
                        )
                    }
                    2 -> {
                        imageVideoDuration = 60
                        binding.segmentedProgressbar.max = imageVideoDuration
                        hideDurationBar(show = false)
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder, R.drawable.ic_crtpost_timer_three_active
                            )
                        )
                    }
                    3 -> {
                        imageVideoDuration = 90
                        binding.segmentedProgressbar.max = imageVideoDuration
                        hideDurationBar(show = false)
                        binding.ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ActivityVideoRecorder, R.drawable.ic_crtpost_timer_four_active
                            )
                        )
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

    private fun setuptabSpeedRecclerview() {
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
                mModel?.speed = speed
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

    private lateinit var counter: Counter
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
            if (counter.isAdded) {
                return@setOnClickListener
            }
            counter.show(
                supportFragmentManager, counter.tag
            )
        }


        binding.buttonDone.setOnClickListener { view: View? ->
            if (isImage) {
//                createDirectoryAndSaveFile(binding.selectedPhoto.capture())
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

        binding.buttonPickData.setOnClickListener {
            /*if (galleryFragment == null) {
                galleryFragment = FragmentGallery(list = thumbnails, typeMedia = typeMedia, arrPath = arrPath)
            }
            galleryFragment?.show(supportFragmentManager, galleryFragment?.tag)*/
            imageChooser()
        }

        binding.buttonCameraChanger.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
            } else {
                binding.cameraView.toggleFacing()
                if (binding.cameraView.facing == Facing.FRONT) {
                    binding.buttonFlash1.visibility = View.GONE
                } else {
                    binding.buttonFlash1.visibility = View.VISIBLE
                }
            }
        }




        binding.buttonSpeed.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
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
        binding.buttonFlash.setOnClickListener { view: View? ->
            if (binding.cameraView.isTakingVideo) {
                Toast.makeText(this, R.string.recorder_error_in_progress, Toast.LENGTH_SHORT).show()
            } else {
                binding.cameraView.flash =
                    if (binding.cameraView.flash === Flash.OFF) Flash.TORCH else Flash.OFF
                isFlashActive = !isFlashActive
                updateColorButtons()
            }
        }
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
        binding.buttonReverse.setOnClickListener {
            if (isReverseActive) {
                isReverseActive = false
                updateColorButtons()
            } else {
                isReverseActive = true
                updateColorButtons()
//                reverseVideoCommand()
            }
        }
        binding.ivClose.setOnClickListener {
            onBackPressed()
        }

        setupRecordDurationRecclerview()
        setuptabSpeedRecclerview()
    }

    override fun onBackPressed() {
        var commonConfirmationDialog = CommonConfirmationDialog(this,
            "Save as Draft",
            "Drafts let you save your edits, so you can come back later.",
            "Yes",
            "Delete Video",
            object : CommonConfirmationDialog.Callback {
                override fun onDialogResult(isPermissionGranted: Boolean) {
                    isVideoTaken = false
                    isImageTakenByCamera = false
                    isFilterApplied = false
                    isReverseActive = false
                    isCowntDownActive = false
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
//        val file = File(cacheDir, "IMG_${System.currentTimeMillis()}" + ".jpg")
        val file = Common.getFile(this, Common.IMAGE)
        try {
            val out = FileOutputStream(file)
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            mModel?.video = file
            closeFinally(mModel?.video!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun uiWhenRecording() {
        binding.buttonDurationTimer.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.layoutBottomControll.visibility = View.VISIBLE
        binding.buttonPickData.visibility = View.GONE
        binding.buttonDone.visibility = View.GONE
        binding.buttonCaptureCounter.visibility = View.GONE
        binding.segmentedProgressbar.visibility = View.VISIBLE
    }


    fun uiWhenCountdownActive() {
        binding.buttonDurationTimer.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.buttonSpeed.visibility = View.GONE
        binding.layoutBottomControll.visibility = View.INVISIBLE
        binding.buttonPickData.visibility = View.GONE
        binding.buttonDone.visibility = View.GONE
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
        val asyncTask = FFmpegAsyncTask("-f concat -safe 0 -i ${
            generateIMagesListFile(
                pathList, persond
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
//                        binding.selectedPhoto.visibility = View.GONE
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
        mPlayer = ExoPlayer.Builder(this).build()
        playerzview = EPlayerView(this)
        mPlayer!!.repeatMode = ExoPlayer.REPEAT_MODE_OFF
        val factory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(mModel!!.segments[0].file))
        val source: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        mPlayer!!.setPlaybackSpeed(mModel?.speed ?: 1f)
        mPlayer!!.prepare(source);
        mPlayer!!.playWhenReady = true;
        playerzview.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            runOnUiThread {
                playerzview.setSimpleExoPlayer(mPlayer)
                playerzview.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
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
            FFmpegAsyncTask("-i \"${file.absolutePath}\" -vf setpts=PTS/${mModel?.speed.toString()}  -ss 00:00:00 -to ${
                formatSeconds(
                    imageVideoDuration.toInt()
                )
            } -crf 23 -preset ultrafast -vcodec libx264 -c:a aac $outputPath",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        dismissLoader()
                        val duration: Long = VideoUtil.getDuration(
                            this@ActivityVideoRecorder, Uri.fromFile(File(outputPath))
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

    var isSpeedActive = false
    var isFlashActive = false
    var isCowntDownActive = false
    var isCaptureDurationActive = false
    var isReverseActive = false


    fun updateColorButtons() {
        if (isSpeedActive) {
            binding.ivSpeed.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_crtpost_speed_active
                )
            )
            binding.tvSped.setTextColor(resources.getColor(R.color.colorYellow, null))
        } else {
            binding.tvSped.setTextColor(resources.getColor(R.color.white, null))
            binding.ivSpeed.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_crtpost_speed
                )
            )
        }
        if (isFlashActive) {
            binding.buttonFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_crtpost_speed_active
                )
            )
        } else {
            binding.buttonFlash.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_live_flash
                )
            )
        }
        if (isReverseActive) {
            binding.ivreverse.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.round_play_circle_24_active
                )
            )
        } else {
            binding.ivreverse.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.round_play_circle_24
                )
            )
        }
    }


    private var galleryFragment: FragmentGallery? = null
    private var count = 0
    private lateinit var thumbnails: Array<Bitmap?>
    private lateinit var thumbnailsselection: BooleanArray
    private lateinit var arrPath: Array<String?>
    private lateinit var typeMedia: IntArray
    private fun getGalleryData() {
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
        val selection =
            (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        val queryUri = MediaStore.Files.getContentUri("external")
        val cursorLoader = CursorLoader(
            this, queryUri, projection, selection, null,  // Selection args (none).
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
                    contentResolver, id.toLong(), MediaStore.Images.Thumbnails.MINI_KIND, bmOptions
                ) else if (t == 3) thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                    contentResolver, id.toLong(), MediaStore.Video.Thumbnails.MINI_KIND, bmOptions
                )
                arrPath[i] = cursor.getString(dataColumnIndex)
                typeMedia[i] = cursor.getInt(type)
                Glide.with(this).load(arrPath[0]!!).transform(CenterCrop(), RoundedCorners(5)).into(binding.buttonPickData)
            } catch (e: Exception) {
            }

            break
        }
    }

    @SuppressLint("Range")
    fun getImageContentUri(context: Context, filePath: String): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(filePath),
            null
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
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID),
            MediaStore.Video.Media.DATA + "=? ",
            arrayOf(filePath),
            null
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
        val uri: Uri
        if (item.mediaType == 1) {
            uri = getImageContentUri(this, item.uri)!!
            isImageTakenByCamera = true
            isImage = true
            startCrop(uri)
        } else if (item.mediaType == 3 || item.mediaType == 2) {
            uri = getVideoContentUri(this, item.uri)!!
            isImage = false
            setUPCameraViewsOnCapture(false)
            val selectedVideo = uri
            val selectedImageBitmap: Bitmap
            try {
                mModel!!.video = File(uri.path!!)
                processCurrentRecording()
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
                    this.contentResolver, selectedImageUri
                )
                binding.buttonCaptureCounter.visibility = View.GONE
                binding.buttonSpeed.visibility = View.GONE
//                binding.selectedPhoto.visibility = View.VISIBLE
//                binding.selectedPhoto.gpuImage.deleteImage()
//                binding.selectedPhoto.setImage(selectedImageBitmap);
                binding.cameraView.visibility = View.GONE
                //  createVideo(it!!.absolutePath, result.size)
                binding.buttonDone.visibility = View.VISIBLE
                binding.layoutBottomControll.visibility = View.INVISIBLE
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
        cropImage.launch(options(uri = uri) {
            setGuidelines(CropImageView.Guidelines.ON)
            setOutputCompressFormat(Bitmap.CompressFormat.PNG)
        })
    }


    var progressHandler = Handler()
    var captureTimerHandler = Handler()
    fun pauseProgress() {
        mMediaPlayer!!.pause()
        progressHandler.removeCallbacks(runnable)
    }

    fun resumeProgress() {
        progressHandler.removeCallbacks(runnable)
        if (isImage) {
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


    private var runnableForTimer: Runnable = object : Runnable {
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
        binding.segmentedProgressbar.visibility = View.VISIBLE
        binding.segmentedProgressbar.max = imageVideoDuration
    }
}
