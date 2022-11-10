package com.stalmate.user.modules.reels.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.daasuu.imagetovideo.EncodeListener
import com.daasuu.imagetovideo.ImageToVideoConverter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityPreviewVideoBinding
import com.stalmate.user.modules.reels.activity.ActivityFilter.Companion.EXTRA_SONG
import com.stalmate.user.modules.reels.activity.ActivityFilter.Companion.EXTRA_VIDEO
import com.stalmate.user.modules.reels.audioVideoTrimmer.ui.seekbar.widgets.CrystalRangeSeekbar
import com.stalmate.user.modules.reels.audioVideoTrimmer.ui.seekbar.widgets.CrystalSeekbar
import com.stalmate.user.modules.reels.audioVideoTrimmer.utils.*
import com.stalmate.user.modules.reels.photo_editing.EmojiBSFragment
import com.stalmate.user.modules.reels.photo_editing.PropertiesBSFragment
import com.stalmate.user.modules.reels.photo_editing.StickerBSFragment
import com.stalmate.user.modules.reels.photo_editing.TextEditorDialogFragment
import com.stalmate.user.modules.reels.utils.ColorSeekBar
import com.stalmate.user.modules.reels.workers.MergeAudioVideoWorker
import com.stalmate.user.modules.reels.workers.MergeVideosWorker
import com.stalmate.user.modules.reels.workers.VideoTrimmerWorker
import com.stalmate.user.modules.reels.workers.WatermarkWorker
import com.stalmate.user.utilities.Common
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.Utils.getScaledDimension
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ActivityVideoEditor() : BaseActivity(), OnPhotoEditorListener,
    PropertiesBSFragment.Properties, View.OnClickListener, StickerBSFragment.StickerListener,
    EmojiBSFragment.EmojiListener {
    lateinit var binding: ActivityPreviewVideoBinding
    lateinit var mPhotoEditor: PhotoEditor
    private val globalVideoUrl = ""
    lateinit var propertiesBSFragment: PropertiesBSFragment
    lateinit var mStickerBSFragment: StickerBSFragment
    private var mediaPlayer: ExoPlayer? = null
    private var videoPath = ""
    private var audioPath = ""
    private var imagePath = ""
    private var songId = ""
    private lateinit var exeCmd: ArrayList<String>
    val PICK_FILE = 99
    var id = 0
    private lateinit var newCommand: Array<String?>
    private lateinit var progressDialog: ProgressDialog
    override fun onClick(viewId: Int, view: View?) {

    }

    var isIMage = false

    private var originalDisplayWidth = 0
    private var originalDisplayHeight = 0
    private var newCanvasWidth = 0
    private var newCanvasHeight = 0
    private lateinit var mEmojiBSFragment: EmojiBSFragment
    private var DRAW_CANVASW = 0
    private var DRAW_CANVASH = 0


    var resultCallbackOfSelectedMusicTrack: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result!!.resultCode == RESULT_OK) {
                val data: Intent = result.getData()!!
                songId = data.getStringExtra(EXTRA_SONG_ID).toString()
                val name = data.getStringExtra(EXTRA_SONG_NAME)
                val audio = data.getParcelableExtra<Uri>(EXTRA_SONG_FILE)
                Log.d("klajsdasd", songId)
                Log.d("klajsdasd", audio!!.path.toString())
                /*        binding.tvMusicName.text=name
                        binding.layoutSelectedMusic.visibility=View.VISIBLE*/

                audioPath = File(audio.path!!).absolutePath
                setupDataOverExoplayer()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityPreviewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializePlayer()








        isIMage = intent.getBooleanExtra("isImage", false)

        initViews()
        videoPath = intent.getStringExtra(EXTRA_VIDEO).toString()
        audioPath = intent.getStringExtra(EXTRA_SONG).toString()
        try {
            Log.d("askldjlasd", audioPath)
        } catch (e: java.lang.Exception) {

        }
        songId = intent.getStringExtra(EXTRA_SONG_ID).toString()
        if (!isIMage) {


            Handler(Looper.myLooper()!!).post {
                runOnUiThread {
                    var drawable: Drawable

                    var view = View(this)
                    view.setLayoutParams(
                        ViewGroup.LayoutParams(
                            displayWidth,
                            displayHeight
                        )
                    )

                    drawable = BitmapDrawable(resources, loadBitmapFromView(view))
                    drawable.setAlpha(1)
                    binding.ivImage.source.setImageDrawable(drawable)


                    mPhotoEditor = PhotoEditor.Builder(this, binding.ivImage)
                        .setPinchTextScalable(true)
                        .setClipSourceImage(true)
                        .build()
                }
            }


            binding.imgTrim.visibility = View.VISIBLE
            val retriever = MediaMetadataRetriever()
            Log.d("pathhhh", videoPath)
            Log.d("pathhhh", "videoPath")
            retriever.setDataSource(videoPath)
            val metaRotation =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            val rotation = metaRotation?.toInt() ?: 0
            if (rotation == 90 || rotation == 270) {
                DRAW_CANVASH =
                    Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                DRAW_CANVASW =
                    Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            } else {
                DRAW_CANVASW =
                    Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                DRAW_CANVASH =
                    Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            }


        } else {
            Handler(Looper.myLooper()!!).post {
                runOnUiThread {
                    binding.videoSurface.visibility = View.GONE
                }
            }
            getDropboxIMGSize(Uri.parse(videoPath!!))
        }


        //  initializePlayer()

        Handler(Looper.getMainLooper()).post {
            setCanvasAspectRatio()
/*            binding.ivImage.getLayoutParams().width = newCanvasWidth
            binding.ivImage.getLayoutParams().height = newCanvasHeight*/
            /*     binding.videoSurface.getLayoutParams().width = newCanvasWidth
                 binding.videoSurface.getLayoutParams().height = newCanvasHeight
     */

            binding.ivImage.getLayoutParams().width = displayWidth
            binding.ivImage.getLayoutParams().height = displayHeight
            binding.videoSurface.getLayoutParams().width = displayWidth
            binding.videoSurface.getLayoutParams().height = displayHeight


            Log.d("asldkjshlad", newCanvasWidth.toString())
            Log.d("asldkjshlad", newCanvasHeight.toString())
        }
    }


    fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(
            v.layoutParams.width,
            v.layoutParams.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }


    private fun getDropboxIMGSize(uri: Uri) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(File(uri.path).absolutePath, options)
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        DRAW_CANVASW =
            imageWidth
        DRAW_CANVASH =
            imageHeight

    }


    private fun initViews() {


        seekbar = findViewById(R.id.range_seek_bar)
        seekbarController = findViewById(R.id.seekbar_controller)
        seekHandler = Handler()

        progressDialog = ProgressDialog(this)
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment.setStickerListener(this)
        propertiesBSFragment = PropertiesBSFragment()
        propertiesBSFragment.setPropertiesChangeListener(this)
        mEmojiBSFragment = EmojiBSFragment()
        mPhotoEditor = PhotoEditor.Builder(this, binding.ivImage)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            .setDeleteView(binding.imgDelete) //.setDefaultTextTypeface(mTextRobotoTf)

            // .setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk
        mPhotoEditor.setOnPhotoEditorListener(this)
        mEmojiBSFragment?.setEmojiListener(this)
        binding.imgClose.setOnClickListener(this)
        binding.imgDone.setOnClickListener(this)
        binding.imgDraw.setOnClickListener(this)
        binding.imgText.setOnClickListener(this)
        binding.imgUndo.setOnClickListener(this)
        binding.imgSticker.setOnClickListener(this)
        binding.ivEmoji.setOnClickListener(this)
        binding.buttonTrimDone.setOnClickListener {
            submitForTrim()
        }
        binding.ivMusic.setOnClickListener(this)

        binding.imgTrim.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgClose -> {

                onBackPressed()


            }
            R.id.imgDone -> saveImage()
            R.id.imgDraw -> {


                if (mPhotoEditor.brushDrawableMode == true) {
                    binding.colorSeekBar.visibility = View.GONE
                    mPhotoEditor.setBrushDrawingMode(false)
                    binding.imgDraw.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_crtpost_top_sketch
                        )
                    )
                } else {

                    binding.colorSeekBar.visibility = View.VISIBLE
                    mPhotoEditor.setBrushDrawingMode(true)


                    binding.imgDraw.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_crtpost_top_sketch_active
                        )
                    )
                }


                //setDrawingMode()
                binding.colorSeekBar.setOnColorChangeListener(object :
                    ColorSeekBar.OnColorChangeListener {
                    override fun onColorChangeListener(color: Int) {
                        mPhotoEditor.brushColor = color
                    }

                })

            }

            R.id.imgText -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object :
                    TextEditorDialogFragment.TextEditorListener {
                    override fun onDone(inputText: String?, colorCode: Int) {
                        val styleBuilder = TextStyleBuilder()
                        styleBuilder.withTextColor(colorCode)
                        mPhotoEditor.addText(inputText, styleBuilder)
                        /*    binding.txtCurrentTool.setText(R.string.label_text)*/
                    }
                })
            }


            R.id.imgUndo -> {
                Log.d("canvas>>", mPhotoEditor.undo().toString() + "")
                mPhotoEditor.undo()
            }

            R.id.imgSticker -> {

                if (mStickerBSFragment.isAdded) {
                    return
                }
                mStickerBSFragment.show(
                    supportFragmentManager,
                    mStickerBSFragment.tag
                )

            }

            R.id.ivEmoji -> {
                if (mEmojiBSFragment.isAdded) {
                    return
                }
                mEmojiBSFragment!!.show(
                    supportFragmentManager,
                    mEmojiBSFragment!!.tag
                )

            }

            R.id.ivMusic -> {
                resultCallbackOfSelectedMusicTrack.launch(IntentHelper.getSongPickerActivity(this))
            }

            R.id.imgTrim -> {
                if (binding.layoutTrim.visibility == View.VISIBLE) {
                    binding.layoutTrim.visibility = View.GONE
                } else {
                    binding.layoutTrim.visibility = View.VISIBLE
                }
            }

        }
    }


    private fun setCanvasAspectRatio() {
        originalDisplayHeight = displayHeight
        originalDisplayWidth = displayWidth
        val displayDiamenion: DimensionData = getScaledDimension(
            DimensionData(
                DRAW_CANVASW,
                DRAW_CANVASH
            ),
            DimensionData(originalDisplayWidth, originalDisplayHeight)
        )



        newCanvasWidth = displayDiamenion.width
        newCanvasHeight = displayDiamenion.height

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
        commonConfirmationDialog.show()
    }


    private fun setDrawingMode() {
        if (mPhotoEditor.brushDrawableMode == true) {
            mPhotoEditor.setBrushDrawingMode(false)
            binding.imgDraw.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_top_sketch
                )
            )
        } else {
            mPhotoEditor.setBrushDrawingMode(true)


            binding.imgDraw.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_crtpost_top_sketch_active
                )
            )

            propertiesBSFragment.show(getSupportFragmentManager(), propertiesBSFragment.getTag())
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveImage() {
        showLoader()
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                .toString() + File.separator + ""
                    + System.currentTimeMillis() + ".png"
        )


        try {
            file.createNewFile()
            val saveSettings: SaveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(false)
                .build()
            mPhotoEditor.saveAsFile(
                file.absolutePath,
                saveSettings,
                object : PhotoEditor.OnSaveListener {
                    override
                    fun onSuccess(@NonNull imagePath: String) {
                        //  dismissLoader()
                        this@ActivityVideoEditor.imagePath = imagePath
                        Log.d("imagePath>>", imagePath)
                        Log.d("imagePath2>>", Uri.fromFile(File(imagePath)).toString())
                        binding.ivImage.source.setImageURI(Uri.fromFile(File(imagePath)))
                        /*        Toast.makeText(
                                    this@ActivityVideoEditor,
                                    "Saved successfully...",
                                    Toast.LENGTH_SHORT
                                ).show()*/

                        if (isIMage) {
                            convertImageToVideo(imagePath)
                        } else {
                            applayWaterMark(File(videoPath))
                        }


                        //  saveVideoToInternalStorage()
                    }

                    override fun onFailure(exception: Exception) {
                        dismissLoader()
                        Toast.makeText(
                            this@ActivityVideoEditor,
                            "Saving Failed...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                })
        } catch (e: IOException) {
            dismissLoader()
            e.printStackTrace()
        }
    }

    private fun applayWaterMark(beforeWatermarkAddedFile: File) {

        try {
            //    addWaterMarkProcess()
            addToWatermark(beforeWatermarkAddedFile)
        } catch (e: Exception) {
            Log.d("lkajsdlasd", e!!.toString())
            e.printStackTrace()
        }
    }

    override
    fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor.setBrushDrawingMode(false)
        binding.imgDraw.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))

        mPhotoEditor.addImage(bitmap)
        binding.txtCurrentTool.setText(R.string.label_sticker)
    }


    private val displayWidth: Int
        private get() {
            val displayMetrics = DisplayMetrics()
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    private val displayHeight: Int
        private get() {
            val displayMetrics = DisplayMetrics()
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    override
    fun onColorChanged(colorCode: Int) {


        mPhotoEditor.brushColor = colorCode
        binding.txtCurrentTool.setText(R.string.label_brush)
    }

    override
    fun onOpacityChanged(opacity: Int) {
        binding.txtCurrentTool.setText(R.string.label_brush)

    }

    override
    fun onBrushSizeChanged(brushSize: Int) {
    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        val textEditorDialogFragment =
            TextEditorDialogFragment.show(this, text.toString(), colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditorDialogFragment.TextEditorListener {
            override fun onDone(inputText: String?, colorCode: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                if (rootView != null) {
                    mPhotoEditor.editText(rootView, inputText, styleBuilder)
                }

                /*      binding.txtCurrentTool.setText(R.string.label_text)*/
            }
        })
    }


    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

    }

    override fun onStartViewChangeListener(viewType: ViewType?) {

    }

    override fun onStopViewChangeListener(viewType: ViewType?) {

    }

    override fun onTouchSourceImage(event: MotionEvent?) {

    }


    override fun onEmojiClick(emojiUnicode: String?) {
        mPhotoEditor.addEmoji(emojiUnicode)
        // binding.txtCurrentTool.setText(R.string.label_emoji)
    }


    fun addToWatermark(beforeWatermarkAddedFile: File) {
        val wm = WorkManager.getInstance(this)
        val output = File(cacheDir, UUID.randomUUID().toString())
        val data: Data = Data.Builder()
            .putString(WatermarkWorker.KEY_INPUT, beforeWatermarkAddedFile.absolutePath)
            .putString(WatermarkWorker.ICON, imagePath)
            .putString(WatermarkWorker.KEY_OUTPUT, output.absolutePath)
            .build()
        val request = OneTimeWorkRequest.Builder(WatermarkWorker::class.java)
            .setInputData(data)
            .build()
        wm.enqueue(request)
        wm.getWorkInfoByIdLiveData(request.id)
            .observe(this) { info: WorkInfo ->
                val ended = (info.state == WorkInfo.State.CANCELLED
                        || info.state == WorkInfo.State.FAILED)
                if (info.state == WorkInfo.State.SUCCEEDED) {
                    dismissLoader()
                    saveVideoToInternalStorage(output.path)
                } else if (ended) {
                }
            }

    }


    private fun saveVideoToInternalStorage(path: String) {
        try {
            val currentFile: File = File(path)
            val newfile = File(Common.getFilePath(this, Common.VIDEO))
            if (currentFile.exists()) {
                val inputStream: InputStream = FileInputStream(currentFile)
                val outputStream: OutputStream = FileOutputStream(newfile)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }
                outputStream.flush()
                inputStream.close()
                outputStream.close()


                /*       Toast.makeText(applicationContext, "Video has just saved!!", Toast.LENGTH_LONG)
                           .show()
                       */
                try {
                    Log.d("asdasdasd", "songIdvv")
                    Log.d("asdasdasd", songId)
                } catch (e: java.lang.Exception) {
                    Log.d("asdasdasd", "songId")
                }
                startActivity(
                    IntentHelper.getCreateFuntimePostScreen(this)!!
                        .putExtra(EXTRA_VIDEO, newfile.absolutePath).putExtra(EXTRA_SONG_ID, songId)
                )
            } else {
                Toast.makeText(
                    applicationContext,
                    "Video has failed for saving!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun releasePlayer() {
        mediaPlayer!!.release()
    }

    public override fun onStart() {
        super.onStart()
        setupDataOverExoplayer()
    }


    fun initializePlayer() {
        mediaPlayer = ExoPlayer.Builder(this@ActivityVideoEditor).build()
        binding.videoSurface.player = mediaPlayer
        binding.videoSurface.hideController()
        binding.videoSurface.useController = false
        mediaPlayer!!.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        mediaPlayer!!.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                /*   imagePlayPause.visibility = if (playWhenReady) View.GONE else View.VISIBLE*/
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_ENDED -> {
                        /*      imagePlayPause.visibility = View.VISIBLE*/
                        isVideoEnded = true
                    }
                    Player.STATE_READY -> {
                        isVideoEnded = false
                        /*         imagePlayPause.visibility = View.GONE*/
                        startProgress() //use this when start audio trimmer

                    }
                    else -> {}
                }
            }
        })

    }

    override fun onPause() {
        mediaPlayer!!.pause()
        super.onPause()

    }

    fun setupDataOverExoplayer() {

        var videoSource: MediaSource? = null
        var audioSource: MediaSource? = null
        var mergedSource: MediaSource? = null
        if (!isIMage) {
            videoSource =
                ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(
                    MediaItem.fromUri(Uri.parse(videoPath))
                )
            if (!ValidationHelper.isNull(audioPath)) {
                audioSource =
                    ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(
                        MediaItem.fromUri(Uri.parse(audioPath))
                    )
                mergedSource = MergingMediaSource(videoSource!!, audioSource);
                mediaPlayer!!.setMediaSource(mergedSource)

            } else {


                mediaPlayer!!.setMediaSource(videoSource)

            }
        } else {


            if (!ValidationHelper.isNull(audioPath)) {
                audioSource =
                    ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(
                        MediaItem.fromUri(Uri.parse(audioPath))
                    )
                mediaPlayer!!.setMediaSource(audioSource)
            }

            Handler(Looper.myLooper()!!).post {
                runOnUiThread {
                    Glide.with(this).load(Drawable.createFromPath(videoPath)).centerCrop()
                        .into(binding.ivImage.source)
                    binding.ivImage.source.scaleType = ImageView.ScaleType.FIT_XY
                }
            }


        }


        mediaPlayer!!.playWhenReady = true
        mediaPlayer!!.prepare()
        setDataInView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) mediaPlayer!!.release()
        deleteFile("temp_file")
        stopRepeatingTask()
    }


    lateinit var imageToVideo: ImageToVideoConverter
    private fun convertImageToVideo(imagePath: String) {
        //int height = getInputData().getInt(ICON,0);
        // int width = getInputData().getInt(ICON,0);

        val outputPath = Common.getFilePath(this, Common.VIDEO)
        imageToVideo = ImageToVideoConverter(
            outputPath = outputPath,
            inputImagePath = imagePath,
            size = Size(528, 1072),
            duration = TimeUnit.SECONDS.toMicros(10),
            listener = object : EncodeListener {
                override fun onProgress(progress: Float) {
                    Log.d("progress", "progress = $progress")
                    runOnUiThread {

                    }
                }

                override fun onCompleted() {
                    runOnUiThread {
                        mergeAudioVideo(outputPath)
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

    private fun mergeAudioVideo(filePath: String) {
        val videos: MutableList<String> = ArrayList()
        videos.add(filePath)
        val merged1 = File(cacheDir, UUID.randomUUID().toString())
        val data1: Data = Data.Builder()
            .putStringArray(MergeVideosWorker.KEY_VIDEOS, videos.toTypedArray())
            .putString(MergeVideosWorker.KEY_OUTPUT, merged1.absolutePath)
            .build()

        val request1: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MergeVideosWorker::class.java)
            .setInputData(data1)
            .build()

        val wm: WorkManager = WorkManager.getInstance(this)
        if (!ValidationHelper.isNull(audioPath)) {
            val merged2 = File(cacheDir, UUID.randomUUID().toString())
            val audioFile: File = File(audioPath)
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
                        applayWaterMark(merged2)
                    } else if (ended) {
                    }
                }
        } else {
            wm.enqueue(request1)
            wm.getWorkInfoByIdLiveData(request1.getId())
                .observe(this) { info ->
                    val ended = (info.getState() === WorkInfo.State.CANCELLED
                            || info.getState() === WorkInfo.State.FAILED)
                    if (info.getState() === WorkInfo.State.SUCCEEDED) {
                        applayWaterMark(merged1)
                    } else if (ended) {

                    }
                }
        }
    }


    private fun getCroppedTrack(track: Track, startTimeMs: Int, endTimeMs: Int): CroppedTrack? {
        var currentSample: Long = 0
        var currentTime = 0.0
        var startSample: Long = -1
        var endSample: Long = -1
        val startTime = (startTimeMs / 1000).toDouble()
        val endTime = (endTimeMs / 1000).toDouble()
        for (i in 0 until track.sampleDurations.size) {
            if (currentTime <= startTime) {
                // current sample is still before the new starttime
                startSample = currentSample
            }
            endSample = if (currentTime <= endTime) {
                // current sample is after the new start time and still before the new endtime
                currentSample
            } else {
                // current sample is after the end of the cropped video
                break
            }
            currentTime += track.sampleDurations.get(i).toDouble() / track.trackMetaData
                .timescale.toDouble()
            currentSample++
        }
        return CroppedTrack(track, startSample, endSample)
    }


/*    private lateinit var imagePlayPause: ImageView*/

    private var imageViews = ArrayList<ImageView>()

    private var totalDuration: Long = 0

    private lateinit var dialog: Dialog


/*    private lateinit var txtStartDuration: TextView
    private  lateinit var txtEndDuration:TextView*/

    private lateinit var seekbar: CrystalRangeSeekbar

    private var lastMinValue: Long = 0

    private var lastMaxValue: Long = 0

    private var menuDone: MenuItem? = null

    private var seekbarController: CrystalSeekbar? = null

    private var isValidVideo = true
    private var isVideoEnded: kotlin.Boolean = false

    private var seekHandler: Handler? = null

    private lateinit var bundle: Bundle


    private var currentDuration: Long = 0
    private var lastClickedTime: kotlin.Long = 0
    var updateSeekbar: Runnable = object : Runnable {
        override fun run() {
            try {
                Log.d("aksdasda", "ioasjdsa")
                currentDuration = mediaPlayer!!.getCurrentPosition() / 1000
                if (!mediaPlayer!!.getPlayWhenReady()) return
                if (currentDuration <= lastMaxValue) seekbarController!!.setMinStartValue(
                    currentDuration.toInt().toFloat()
                ).apply() else mediaPlayer!!.setPlayWhenReady(false)
            } finally {
                seekHandler!!.postDelayed(this, 1000)
            }
        }
    }
    private var trimType = 0
    private var fixedGap: Long = 0
    private var minGap: kotlin.Long = 0
    private var minFromGap: kotlin.Long = 0
    private var maxToGap: kotlin.Long = 0
    private var hidePlayerSeek =
        false
    private var progressView: CustomProgressView? = null


    private fun setUpSeekBar() {
        seekbar.visibility = View.VISIBLE
        /*       txtStartDuration.setVisibility(View.VISIBLE)
               txtEndDuration.setVisibility(View.VISIBLE)*/
        seekbarController!!.setMaxValue(totalDuration.toFloat()).apply()
        seekbar.setMaxValue(totalDuration.toFloat()).apply()
        seekbar.setMaxStartValue(totalDuration.toFloat()).apply()
        lastMaxValue = if (trimType == 1) {
            seekbar.setFixGap(fixedGap.toFloat()).apply()
            totalDuration
        } else if (trimType == 2) {
            seekbar.setMaxStartValue(minGap.toFloat())
            seekbar.setGap(minGap.toFloat()).apply()
            totalDuration
        } else if (trimType == 3) {
            seekbar.setMaxStartValue(maxToGap.toFloat())
            seekbar.setGap(minFromGap.toFloat()).apply()
            maxToGap
        } else {
            seekbar.setGap(2F).apply()
            totalDuration
        }
        if (hidePlayerSeek) seekbarController!!.visibility = View.GONE
        seekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            if (!hidePlayerSeek) seekbarController!!.visibility = View.VISIBLE
        }
        seekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            val minVal = minValue as Long
            val maxVal = maxValue as Long
            if (lastMinValue != minVal) {
                seekTo(minValue)
                if (!hidePlayerSeek) seekbarController!!.visibility = View.INVISIBLE
            }
            lastMinValue = minVal
            lastMaxValue = maxVal

            Log.d("akjsdasdoo", lastMinValue.toString())
            Log.d("akjsdasdoo", lastMaxValue.toString())

/*            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal))
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal))*/
            if (trimType == 3) setDoneColor(minVal, maxVal)
        }
        seekbarController!!.setOnSeekbarFinalValueListener { value ->
            val value1 = value as Long
            if (value1 < lastMaxValue && value1 > lastMinValue) {
                seekTo(value1)
                return@setOnSeekbarFinalValueListener
            }
            if (value1 > lastMaxValue) seekbarController!!.setMinStartValue(
                lastMaxValue.toInt().toFloat()
            ).apply() else if (value1 < lastMinValue) {
                seekbarController!!.setMinStartValue(lastMinValue.toInt().toFloat()).apply()
                if (mediaPlayer!!.playWhenReady) seekTo(lastMinValue)
            }
        }
    }


    private fun seekTo(sec: Long) {
        if (mediaPlayer != null) mediaPlayer!!.seekTo(sec * 1000)
    }

    private fun setDoneColor(minVal: Long, maxVal: Long) {
        try {
            if (menuDone == null) return
            //changed value is less than maxDuration
            if (maxVal - minVal <= maxToGap) {
                menuDone!!.icon!!.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.white),
                        PorterDuff.Mode.SRC_IN
                    )
                isValidVideo = true
            } else {
                menuDone!!.icon!!.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.black),
                        PorterDuff.Mode.SRC_IN
                    )
                isValidVideo = false
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun setDataInView() {
        try {
            val fileUriRunnable = Runnable {
                runOnUiThread {
                    totalDuration = TrimmerUtils.getDuration(this, Uri.parse(videoPath))

                    // imagePlayPause.setOnClickListener { v: View? -> onVideoClicked() }
                    /*                 Objects.requireNonNull<View>(playerView.getVideoSurfaceView())
                                         .setOnClickListener { v: View? -> onVideoClicked() }*/

                    loadThumbnails()
                    setUpSeekBar()
                }
            }
            Executors.newSingleThreadExecutor().execute(fileUriRunnable)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /*
     *  loading thumbnails
     * */
    private fun loadThumbnails() {
        try {
            val diff = totalDuration / 8
            var sec = 1
            for (img in imageViews) {
                val interval = diff * sec * 1000000
                val options = RequestOptions().frame(interval)
                Glide.with(this)
                    .load(bundle.getString(TrimVideo.TRIM_VIDEO_URI))
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(img)
                if (sec < totalDuration) sec++
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun startProgress() {
        updateSeekbar.run()
    }


    fun stopRepeatingTask() {
        seekHandler!!.removeCallbacks(updateSeekbar)
    }

    private fun submitForTrim() {
        val wm = WorkManager.getInstance(this)
        val trimmed = File(cacheDir, UUID.randomUUID().toString())
        val data = Data.Builder()
            .putString(VideoTrimmerWorker.KEY_INPUT, videoPath)
            .putString(VideoTrimmerWorker.KEY_OUTPUT, trimmed.absolutePath)
            .putLong(VideoTrimmerWorker.KEY_START, lastMinValue * 1000)
            .putLong(VideoTrimmerWorker.KEY_END, lastMaxValue * 1000)
            .build()
        val request = OneTimeWorkRequest.Builder(VideoTrimmerWorker::class.java)
            .setInputData(data)
            .build()
        wm.enqueue(request)
        wm.getWorkInfoByIdLiveData(request.id)
            .observe(this) { info: WorkInfo ->
                val ended = (info.state == WorkInfo.State.CANCELLED
                        || info.state == WorkInfo.State.FAILED)
                if (info.state == WorkInfo.State.SUCCEEDED) {
                    videoPath = trimmed.absolutePath
                    binding.layoutTrim.visibility = View.GONE
                    setupDataOverExoplayer()
                } else if (ended) {

                }
            }
    }

}