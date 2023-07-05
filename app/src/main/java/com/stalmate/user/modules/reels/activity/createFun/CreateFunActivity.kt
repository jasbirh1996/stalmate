package com.stalmate.user.modules.reels.activity.createFun

import ai.deepar.ar.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.common.util.concurrent.ListenableFuture
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.makeramen.roundedimageview.RoundedImageView
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.modules.reels.activity.FragmentGallery
import com.stalmate.user.modules.reels.photo_editing.Counter
import com.stalmate.user.modules.reels.utils.RealPathUtil
import ly.img.android.pesdk.PhotoEditorSettingsList
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.ui.activity.PhotoEditorActivityResultContract
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ExecutionException


class CreateFunActivity : AppCompatActivity(), SurfaceHolder.Callback, AREventListener {
    private var galleryFragment: FragmentGallery? = null
    private var count = 0
    private lateinit var thumbnails: Array<Bitmap?>
    private lateinit var thumbnailsselection: BooleanArray
    private lateinit var arrPath: Array<String?>
    private lateinit var typeMedia: IntArray
    private var mimeType: String = ""
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
                Glide.with(this).load(arrPath[0]).transform(CenterCrop(), RoundedCorners(5))
                    .into(findViewById<RoundedImageView>(R.id.buttonPickData))
            } catch (e: Exception) {
            }
            break
        }
    }

    // Default camera lens value, change to CameraSelector.LENS_FACING_BACK to initialize with back camera
    private val defaultLensFacing = CameraSelector.LENS_FACING_FRONT
    private var surfaceProvider: ARSurfaceProvider? = null
    private var lensFacing = defaultLensFacing
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var buffers: Array<ByteBuffer?> = arrayOf()
    private var currentBuffer = 0
    private var buffersInitialized = false
    private var deepAR: DeepAR? = null

    private var snapAdapter: SnapsAdapter? = null

    /*
               get interface orientation from
               https://stackoverflow.com/questions/10380989/how-do-i-get-the-current-orientation-activityinfo-screen-orientation-of-an-a/10383164
            */
    private val screenOrientation: Int
        private get() {
            val rotation = windowManager.defaultDisplay.rotation
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            width = dm.widthPixels
            height = dm.heightPixels
            val orientation: Int
            // if the device's natural orientation is portrait:
            orientation = if ((rotation == Surface.ROTATION_0
                        || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height
            ) {
                when (rotation) {
                    Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            } else {
                when (rotation) {
                    Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
            return orientation
        }

    data class SnapsData(
        val name: String = "",
        val image: Int = 0
    )

    var masks: ArrayList<SnapsData> = arrayListOf()
    var effects: ArrayList<SnapsData> = arrayListOf()
    var filters: ArrayList<SnapsData> = arrayListOf()
    private var activeFilterType = 0
    private var width = 0
    private var height = 0
    private var videoFileName: File? = null
    private var countDownTimer: CountDownTimer? = null
    private var isTakingVideo: Boolean = false
    var isImage: Boolean = true

    var isCountDownActive = false
    var isCaptureDurationActive = false

    private var imageVideoDuration: Int = 15
    lateinit var tabbarduration: TabLayout

    private lateinit var counter: Counter
    var countdownTimerDuration = 0
    var captureTimerHandler = Handler()
    private var runnableForTimer: Runnable = object : Runnable {
        override fun run() {
            if (countdownTimerDuration == 0) {
                isCountDownActive = false
                isCounterSelected()
                findViewById<TextView>(R.id.tvCountDownValue).visibility = View.GONE

                findViewById<RecyclerView>(R.id.rvRecordButton).visibility = View.VISIBLE
                findViewById<View>(R.id.focusStart).visibility = View.VISIBLE
                findViewById<RoundedImageView>(R.id.buttonPickData).visibility = View.VISIBLE

                captureTimerHandler.removeCallbacks(this)
                if (isImage) {
                    isImage = true
                    isTakingVideo = false
                    deepAR?.takeScreenshot()
                } else {
                    val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
                    videoFileName = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Stalmate"),
                        "video_$now.mp4"
                    )
                    isImage = false
                    isTakingVideo = true
                    deepAR?.startVideoRecording(videoFileName.toString(), width / 2, height / 2)
                    countDownTimer?.start()
                }
            } else {
                countdownTimerDuration--
                runOnUiThread {
                    isCountDownActive = true
                    isCounterSelected()

                    findViewById<RecyclerView>(R.id.rvRecordButton).visibility = View.VISIBLE
                    findViewById<View>(R.id.focusStart).visibility = View.VISIBLE
                    findViewById<RoundedImageView>(R.id.buttonPickData).visibility = View.VISIBLE

                    findViewById<TextView>(R.id.tvCountDownValue).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvCountDownValue).text =
                        countdownTimerDuration.toString()
                }
                captureTimerHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_create_fun_activity)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return  // no permission
                }
            }
            initialize()
        }
    }

    private fun initialize() {
        initializeDeepAR()
        initalizeViews()
        initializeFilters()
        getGalleryData()
        restoreDeepArState()
    }

    private fun initializeFilters() {
        masks.clear()
        masks.add(SnapsData(name = "none", image = R.drawable.white_circle))
        masks.add(SnapsData(name = "none", image = R.drawable.white_circle))
        masks.add(SnapsData(name = "none", image = R.drawable.white_circle))
        masks.add(SnapsData(name = "aviators", image = R.drawable.aviators_mask))
        masks.add(SnapsData(name = "bigmouth", image = R.drawable.bigmouth_mask))
        masks.add(SnapsData(name = "dalmatian", image = R.drawable.dalmatian_mask))
        masks.add(SnapsData(name = "flowers", image = R.drawable.flowers_mask))
        masks.add(SnapsData(name = "koala", image = R.drawable.koala_mask))
        masks.add(SnapsData(name = "lion", image = R.drawable.lion_mask))
        masks.add(SnapsData(name = "smallface", image = R.drawable.smallface_mask))
        masks.add(SnapsData(name = "teddycigar", image = R.drawable.teddycigar_mask))
        masks.add(SnapsData(name = "kanye", image = R.drawable.kanye_mask))
        masks.add(SnapsData(name = "tripleface", image = R.drawable.tripleface_mask))
        masks.add(SnapsData(name = "sleepingmask", image = R.drawable.sleepingmask_mask))
        masks.add(SnapsData(name = "fatify", image = R.drawable.fatify_mask))
        masks.add(SnapsData(name = "obama", image = R.drawable.obama_mask))
        masks.add(SnapsData(name = "mudmask", image = R.drawable.mudmask_mask))
        masks.add(SnapsData(name = "pug", image = R.drawable.pug_mask))
        masks.add(SnapsData(name = "slash", image = R.drawable.slash_mask))
        masks.add(SnapsData(name = "twistedface", image = R.drawable.twistedface_mask))
        masks.add(SnapsData(name = "grumpycat", image = R.drawable.grumpycat_mask))
        masks.add(SnapsData(name = "none", image = R.drawable.white_circle))
        masks.add(SnapsData(name = "none", image = R.drawable.white_circle))

        effects.clear()
        effects.add(SnapsData(name = "none", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "none", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "none", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "viking_helmet.deepar", image = R.drawable.viking_helmet))
        effects.add(SnapsData(name = "MakeupLook.deepar", image = R.drawable.makeup_look))
        effects.add(SnapsData(name = "Split_View_Look.deepar", image = R.drawable.spilit_view))
        effects.add(
            SnapsData(
                name = "Emotions_Exaggerator.deepar",
                image = R.drawable.emotions_exaggerated
            )
        )
        effects.add(SnapsData(name = "Emotion_Meter.deepar", image = R.drawable.emotion_meter))
        effects.add(SnapsData(name = "Stallone.deepar", image = R.drawable.etallone_effect))
        effects.add(SnapsData(name = "flower_face.deepar", image = R.drawable.flower_face))
        effects.add(
            SnapsData(
                name = "galaxy_background.deepar",
                image = R.drawable.galaxy_background
            )
        )
        effects.add(SnapsData(name = "Humanoid.deepar", image = R.drawable.humanoid))
        effects.add(
            SnapsData(
                name = "Neon_Devil_Horns.deepar",
                image = R.drawable.neon_devil_horns
            )
        )
        effects.add(SnapsData(name = "Ping_Pong.deepar", image = R.drawable.ping_pong))
        effects.add(SnapsData(name = "Pixel_Hearts.deepar", image = R.drawable.pixel_hearts))
        effects.add(SnapsData(name = "Snail.deepar", image = R.drawable.snail_img))
        effects.add(SnapsData(name = "Hope.deepar", image = R.drawable.hope_img))
        effects.add(
            SnapsData(
                name = "Vendetta_Mask.deepar",
                image = R.drawable.vendetta_mask_effect
            )
        )
        effects.add(SnapsData(name = "Fire_Effect.deepar", image = R.drawable.fair_effect))
        effects.add(SnapsData(name = "burning_effect.deepar", image = R.drawable.burning_effect))
        effects.add(SnapsData(name = "Elephant_Trunk.deepar", image = R.drawable.elephant_trunk))
        effects.add(SnapsData(name = "fire", image = R.drawable.fire_effect))
        effects.add(SnapsData(name = "rain", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "heart", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "blizzard", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "none", image = R.drawable.white_circle))
        effects.add(SnapsData(name = "none", image = R.drawable.white_circle))

        filters.clear()
        filters.add(SnapsData(name = "none", image = R.drawable.white_circle))
        filters.add(SnapsData(name = "none", image = R.drawable.white_circle))
        filters.add(SnapsData(name = "none", image = R.drawable.white_circle))
        filters.add(
            SnapsData(
                name = "filmcolorperfection",
                image = R.drawable.filmcolorperfection_filter
            )
        )
        filters.add(SnapsData(name = "tv80", image = R.drawable.tv_eighty_filter))
        filters.add(SnapsData(name = "drawingmanga", image = R.drawable.drawingmanga_filter))
        filters.add(SnapsData(name = "sepia", image = R.drawable.sepia_filter))
        filters.add(SnapsData(name = "bleachbypass", image = R.drawable.bleachbypass_filter))
        filters.add(SnapsData(name = "none", image = R.drawable.white_circle))
        filters.add(SnapsData(name = "none", image = R.drawable.white_circle))

        findViewById<RecyclerView>(R.id.rvRecordButton).layoutManager = LinearLayoutPagerManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false,
            5
        )

        if (snapAdapter != null) {
            snapAdapter = null
        }

        snapAdapter =
            SnapsAdapter(
                timeList = when (activeFilterType) {
                    0 -> masks
                    1 -> effects
                    2 -> filters
                    else -> arrayListOf()
                },
                callback = object : SnapsAdapter.OnSnapListener {
                    override fun onItemSelected(data: SnapsData) {
                        val type = when (activeFilterType) {
                            0 -> "mask"
                            1 -> "effect"
                            2 -> "filter"
                            else -> ""
                        }
                        if (data.name == "none") {
                            findViewById<TextView>(R.id.tvEffectName).visibility = View.INVISIBLE
                        } else {
                            findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.tvEffectName).text =
                                (data.name[0]?.toUpperCase().toString() + data.name?.substring(
                                    1,
                                    (data.name?.lastIndex ?: 0) + 1
                                )?.replace(".deepar", "")).replace("_", " ")
                        }
                        deepAR?.switchEffect(type, getFilterPath(data.name))
                    }

                    override fun scrollBound() {
                        findViewById<RecyclerView>(R.id.rvRecordButton).smoothScrollBy(-100, 0)
                    }

                    override fun setOnClickListener() {
                        //Capture
                        isImage = true
                        isTakingVideo = false
                        deepAR?.takeScreenshot()
                    }

                    override fun setOnLongClickListener() {
                        //Record
                        val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
                        videoFileName = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Stalmate"),
                            "video_$now.mp4"
                        )
                        isImage = false
                        isTakingVideo = true
                        deepAR?.startVideoRecording(videoFileName.toString(), width / 2, height / 2)
                        countDownTimer?.start()
                    }
                }
            )
        findViewById<RecyclerView>(R.id.rvRecordButton)?.apply {
            adapter = snapAdapter
            setHasFixedSize(true)
        }
        try {
            PagerSnapHelper().attachToRecyclerView(findViewById<RecyclerView>(R.id.rvRecordButton))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        findViewById<RecyclerView>(R.id.rvRecordButton).setOnScrollChangeListener(object :
            RecyclerView.OnScrollListener(), View.OnScrollChangeListener {
            override fun onScrollChange(
                v: View?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                val l = IntArray(2)
                findViewById<View>(R.id.centerFocusedView).getLocationOnScreen(l)
                val x = l[0]
                val y = l[1]
                snapAdapter?.setCurrentFocus(
                    x,
                    y,
                    this@CreateFunActivity
                )
            }
        })
    }

    private fun createTimer(duration: Int) {
        findViewById<LinearProgressIndicator>(R.id.segmented_progressbar).max = imageVideoDuration
        countDownTimer?.cancel()
        countDownTimer = null
        countDownTimer = object : CountDownTimer((duration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                findViewById<LinearProgressIndicator>(R.id.segmented_progressbar).visibility =
                    View.VISIBLE
                findViewById<LinearProgressIndicator>(R.id.segmented_progressbar).progress =
                    (duration - (millisUntilFinished / 1000)).toInt()
            }

            override fun onFinish() {
                isTakingVideo = false
                findViewById<LinearProgressIndicator>(R.id.segmented_progressbar).visibility =
                    View.GONE
                findViewById<RecyclerView>(R.id.rvRecordButton).visibility = View.VISIBLE
                deepAR?.stopVideoRecording()
            }
        }
    }

    private fun initalizeViews() {
        //Set ArView
        val arView = findViewById<SurfaceView>(R.id.surface)
        arView.holder.addCallback(this)
        // Surface might already be initialized, so we force the call to onSurfaceChanged
        arView.visibility = View.GONE
        arView.visibility = View.VISIBLE

        tabbarduration = findViewById<TabLayout>(R.id.tabbarduration)
        findViewById<ImageView>(R.id.ivClose).setOnClickListener { onBackPressed() }

        //Set From Gallery
        val buttonPickData = findViewById<RoundedImageView>(R.id.buttonPickData)
        buttonPickData.setOnClickListener {
            imageChooser()
        }

        //Switch Camera
        val switchCamera = findViewById<ImageView>(R.id.switchCamera)
        switchCamera.setOnClickListener {
            lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
            //unbind immediately to avoid mirrored frame.
            var cameraProvider: ProcessCameraProvider? = null
            try {
                cameraProvider = cameraProviderFuture?.get()
                cameraProvider?.unbindAll()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            setupCamera()
        }

        //Choose ARs
        val radioMasks = findViewById<RadioButton>(R.id.masks)
        val radioEffects = findViewById<RadioButton>(R.id.effects)
        val radioFilters = findViewById<RadioButton>(R.id.filters)
        radioMasks.setOnClickListener {
            radioEffects.isChecked = false
            radioFilters.isChecked = false
            activeFilterType = 0

            initializeFilters()

            /*snapAdapter?.timeList?.clear()
            snapAdapter?.timeList?.addAll(masks)
            snapAdapter?.notifyDataSetChanged()*/

            /*if (masks?.get(currentMask) == "none") {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.GONE
            } else {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEffectName).text =
                    (masks?.get(currentMask)?.get(0)?.toUpperCase()
                        .toString() + masks?.get(currentMask)?.substring(
                        1,
                        (masks?.get(currentMask)?.lastIndex ?: 0) + 1
                    )?.replace(".deepar", "")).replace("_", " ")
            }
            deepAR?.switchEffect(
                "mask",
                getFilterPath(masks?.get(currentMask).toString())
            )*/
        }
        radioEffects.setOnClickListener {
            radioMasks.isChecked = false
            radioFilters.isChecked = false
            activeFilterType = 1

            initializeFilters()

            /*snapAdapter?.timeList?.clear()
            snapAdapter?.timeList?.addAll(effects)
            snapAdapter?.notifyDataSetChanged()*/

            /*if (effects?.get(currentEffect) == "none") {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.GONE
            } else {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEffectName).text =
                    (effects?.get(currentEffect)?.get(0)?.toUpperCase()
                        .toString() + effects?.get(currentEffect)?.substring(
                        1,
                        (effects?.get(currentEffect)?.lastIndex ?: 0) + 1
                    )?.replace(".deepar", "")).replace("_", " ")
            }
            deepAR?.switchEffect(
                "effect",
                getFilterPath(effects?.get(currentEffect).toString())
            )*/
        }
        radioFilters.setOnClickListener {
            radioEffects.isChecked = false
            radioMasks.isChecked = false
            activeFilterType = 2

            initializeFilters()

            /*snapAdapter?.timeList?.clear()
            snapAdapter?.timeList?.addAll(filters)
            snapAdapter?.notifyDataSetChanged()*/

            /*if (filters?.get(currentFilter) == "none") {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.GONE
            } else {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEffectName).text =
                    (filters?.get(currentFilter)?.get(0)?.toUpperCase()
                        .toString() + filters?.get(currentFilter)?.substring(
                        1,
                        (filters?.get(currentFilter)?.lastIndex ?: 0) + 1
                    )?.replace(".deepar", "")).replace("_", " ")
            }
            deepAR?.switchEffect(
                "filter",
                getFilterPath(filters?.get(currentFilter).toString())
            )*/
        }

        //Change Video DUration
        val ivTimer = findViewById<ImageView>(R.id.ivTimer)
        ivTimer.setOnClickListener {
            if (isCaptureDurationActive) {
                isCaptureDurationActive = false
                hideDurationBar(show = false)
            } else {
                isCaptureDurationActive = true
                hideDurationBar(show = true)
            }
        }

        //Set Counter
        counter = Counter(onCaptureAfterNthSeconds = { type: String, duration: Int ->
            isImage = (type != "video")
            isCountDownActive = true
            isCounterSelected()
            countdownTimerDuration = duration
            captureTimerHandler.removeCallbacks(runnableForTimer)
            captureTimerHandler.post(runnableForTimer)
        }, onRangeDialogDismiss = {
            isCountDownActive = false
            isCounterSelected()
            findViewById<RecyclerView>(R.id.rvRecordButton).visibility = View.VISIBLE
            findViewById<View>(R.id.focusStart).visibility = View.VISIBLE
            findViewById<RoundedImageView>(R.id.buttonPickData).visibility = View.VISIBLE
        })

        val buttonCaptureCounter = findViewById<ConstraintLayout>(R.id.buttonCaptureCounter)
        buttonCaptureCounter.setOnClickListener {
            if (counter.isAdded) {
                return@setOnClickListener
            }
            isCountDownActive = true
            isCounterSelected()
            findViewById<RecyclerView>(R.id.rvRecordButton).visibility = View.INVISIBLE
            findViewById<View>(R.id.focusStart).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.tvEffectName).visibility = View.INVISIBLE
            findViewById<RoundedImageView>(R.id.buttonPickData).visibility = View.INVISIBLE
            counter.show(supportFragmentManager, counter.tag)
        }

        //Set Progress max and video durations tabs
        setupProgressBarWithDuration()
        setupRecordDurationRecclerview()
    }

    override fun onResume() {
        super.onResume()
        val readImagePermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        val readVideoPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_VIDEO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        val readAudioPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_AUDIO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

        if (
            ContextCompat.checkSelfPermission(
                this,
                readImagePermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                readVideoPermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                readAudioPermission
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permission not granted
            if (Build.VERSION.SDK_INT <= 29) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        readImagePermission,
                        readAudioPermission,
                        readVideoPermission,
                    ),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        readImagePermission,
                        readAudioPermission,
                        readVideoPermission
                    ),
                    1
                )
            }
        } else {
            // Permission has already been granted
            initialize()
        }
    }

    private fun restoreDeepArState() {
        /*try {
            if (masks?.get(currentMask) == "none") {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.GONE
            } else {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEffectName).text =
                    (masks?.get(currentMask)?.get(0)?.toUpperCase()
                        .toString() + masks?.get(currentMask)?.substring(
                        1,
                        (masks?.get(currentMask)?.lastIndex ?: 0) + 1
                    )?.replace(".deepar", "")).replace("_", " ")
            }
            deepAR?.switchEffect(
                "mask",
                getFilterPath(masks?.get(currentMask).toString())
            )
            //----------------------------------------------------------------
            if (effects?.get(currentEffect) == "none") {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.GONE
            } else {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEffectName).text =
                    (effects?.get(currentEffect)?.get(0)?.toUpperCase()
                        .toString() + effects?.get(currentEffect)?.substring(
                        1,
                        (effects?.get(currentEffect)?.lastIndex ?: 0) + 1
                    )?.replace(".deepar", "")).replace("_", " ")
            }
            deepAR?.switchEffect(
                "effect",
                getFilterPath(effects?.get(currentEffect).toString())
            )
            //-----------------------------------------------------------------
            if (filters?.get(currentFilter) == "none") {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.GONE
            } else {
                findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvEffectName).text =
                    (filters?.get(currentFilter)?.get(0)?.toUpperCase()
                        .toString() + filters?.get(currentFilter)?.substring(
                        1,
                        (filters?.get(currentFilter)?.lastIndex ?: 0) + 1
                    )?.replace(".deepar", "")).replace("_", " ")
            }
            deepAR?.switchEffect(
                "filter",
                getFilterPath(filters?.get(currentFilter).toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    private fun imageChooser() {
        val intent = Intent(this, FilePickerActivity::class.java)
        intent.putExtra(
            FilePickerActivity.CONFIGS, Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowVideos(true)
                .enableImageCapture(true)
                .enableVideoCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .setIgnoreHiddenFile(true)
                .setLandscapeSpanCount(8)
                .setPortraitSpanCount(4)
                .build()
        )

        val pickerIntent = Intent(Intent.ACTION_PICK)
        pickerIntent.type = "video/*, image/*"
        pickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        pickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        launchActivityForImagePick.launch(pickerIntent)
    }

    private var launchActivityForImagePick = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        //result.data?.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)?.let {
        result.data?.data?.let {
            if (!it.toString().isNullOrEmpty()) {
                val extension: String = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(this.contentResolver.getType(it)).toString()
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()

                if (mimeType.split("/")[0].toString() == "image") {
                    videoFileName = File(RealPathUtil.getRealPath(this, it))
                    if (videoFileName?.exists() == true)
                        videoFileName?.toUri()?.let { it1 -> startPhotoEditorImgLy(it1) }
                }
                if (mimeType.split("/")[0].toString() == "video") {
                    videoFileName = File(RealPathUtil.getRealPath(this, it))

                    val retriever = MediaMetadataRetriever()
                    //use one of overloaded setDataSource() functions to set your data source
                    retriever.setDataSource(this, Uri.fromFile(videoFileName))
                    val time =
                        (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toLongOrNull() ?: 0L)
                    retriever.release()

                    if (time > 900000L) {
                        showToast("Video should be less than or equal to 15 minutes")
                    } else {
                        if (videoFileName?.exists() == true) {
                            imageVideoDuration = (time / 1000L).toInt()
                            setupProgressBarWithDuration()
                            startVideoEditor(videoFileName?.absolutePath.toString())
                        }
                    }
                }
            }
        }
        /*if (result.resultCode == RESULT_OK) {
            val data: Intent = result.data!!
            val pathList = ArrayList<String>()
            if (data.clipData != null) {
                val mClipData: ClipData? = data.clipData
                val mArrayUri = ArrayList<Uri>()
                val totalImages = mClipData!!.itemCount
                Log.d("kasdasd", totalImages.toString())
                val perImageDuration = (imageVideoDuration / totalImages).toInt()
                for (i in 0 until mClipData.itemCount) {
                    val item: ClipData.Item = mClipData.getItemAt(i)
                    val uri: Uri = item.uri
                    val path = PathUtil.getPath(this, uri)
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
                                showToast("Video should be grater than or equal to 15 seconds")
                            } else if (duration > 90 * 1000) {
                                showToast("Video should be less than or equal to 90 seconds")
                            } else {
                                Log.d("Greater", "Greater")
                                isImage = false
                                //isVideoTaken = true
                                try {
                                    videoFileName = File(path)
                                    val duration: Long =
                                        VideoUtil.getDuration(this, Uri.fromFile(videoFileName))
                                    Log.d("kjashjkdas", duration.toString())
                                    if (duration > imageVideoDuration * 1000) {
                                        *//*            binding.segmentedProgressbar.progress = ((duration * 100) / imageVideoDuration).toInt()
                                                    prolength = ((duration * 100) / imageVideoDuration).toInt()*//*
                                        *//*pauseProgress()
                                        applySpeedWithDuration(mModel!!.video!!)*//*
                                    } else {
                                        segmented_progressbar.progress =
                                            ((duration * 100) / imageVideoDuration).toInt()
//                                        prolength = ((duration * 100) / imageVideoDuration).toInt()
//                                        pauseProgress()
//                                        processCurrentRecording()
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
                    //multipleImageToVideo(pathList, perImageDuration)
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
                    //isImageTakenByCamera = true
                    val selectedImageUri = data.data
                    selectedImageUri?.let {
                        RealPathUtil.getRealPath(this, it)?.let {
                            startPhotoEditorImgLy(File(it).toUri())
                        }
                    }
                } else if (type == "mp4") {
                    Log.d(";lasda", "video")

                    val duration: Long = VideoUtil.getDuration(
                        this, Uri.fromFile(File(getRealPathFromURIVideo(this, data!!.data!!)))
                    )
                    Log.d("dur", duration.toString())
                    if (duration < 15 * 1000) {
                        showToast("Video should be grater than or equal to 15 seconds")
                        Log.d("dura", "Fail")
                    } else if (duration > 90 * 1000) {
                        showToast("Video should be less than or equal to 90 seconds")
                        Log.d("duraMore", "Fail")
                    } else {
                        Log.d("dura", "success")
                        isImage = false
                        //isVideoTaken = true
                        try {
                            //setUPCameraViewsOnCapture(false)
                            Log.d("a;lskdasdhhhhhh", "data.data!!.path!!")
                            Log.d("a;lskdasdkjjjjjj", data.data!!.path!!)
                            videoFileName = File(getRealPathFromURIVideo(this, data!!.data!!))
                            videoFileName?.absolutePath?.let { Log.d("a;lskdasdkjjjjjj", it) }
                            val duration: Long =
                                VideoUtil.getDuration(this, Uri.fromFile(videoFileName))
                            segmented_progressbar.progress =
                                ((duration * 100) / imageVideoDuration).toInt()
//                            prolength = ((duration * 100) / imageVideoDuration).toInt()
//                            pauseProgress()
//                            processCurrentRecording()
                            videoFileName?.absolutePath?.toString()?.let { startVideoEditor(it) }
                            Log.d(";alsjkdasd", "alskjdasd")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }*/
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
            Log.e("TAG", "getRealPathFromURI Exception : $e")
            ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
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

    fun isCounterSelected() {
        val ivCounterx = findViewById<ImageView>(R.id.ivCounterx)
        val tvCounter = findViewById<TextView>(R.id.tvCounter)
        if (isCountDownActive) {
            tvCounter.setTextColor(resources.getColor(R.color.colorYellow, null))
            ivCounterx.setImageDrawable(
                ContextCompat.getDrawable(
                    this@CreateFunActivity, R.drawable.ic_crtpost_countdown_active
                )
            )
        } else {
            tvCounter.setTextColor(resources.getColor(R.color.white, null))
            ivCounterx.setImageDrawable(
                ContextCompat.getDrawable(
                    this@CreateFunActivity, R.drawable.ic_crtpost_countdown
                )
            )
        }
    }

    private fun setupRecordDurationRecclerview() {
        tabbarduration.animate().translationX(-1000f).setDuration(0).start()
        if (tabbarduration.tabCount <= 0) {
            tabbarduration.addTab(tabbarduration.newTab().setText("15 Sec"))
            tabbarduration.addTab(tabbarduration.newTab().setText("30 Sec"))
            tabbarduration.addTab(tabbarduration.newTab().setText("60 Sec"))
            tabbarduration.addTab(tabbarduration.newTab().setText("90 Sec"))
        }
        val tvTimer = findViewById<TextView>(R.id.tvTimer)
        val ivTimer = findViewById<ImageView>(R.id.ivTimer)

        imageVideoDuration = 15
        setupProgressBarWithDuration()
        hideDurationBar(show = false)
        ivTimer.setImageDrawable(
            ContextCompat.getDrawable(
                this@CreateFunActivity, R.drawable.ic_crtpost_timer_one_active
            )
        )

        tabbarduration.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tvTimer.setTextColor(resources.getColor(R.color.colorYellow, null))
                when (tab?.position) {
                    0 -> {
                        imageVideoDuration = 15
                        setupProgressBarWithDuration()
                        hideDurationBar(show = false)
                        ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@CreateFunActivity, R.drawable.ic_crtpost_timer_one_active
                            )
                        )
                    }
                    1 -> {
                        imageVideoDuration = 30
                        setupProgressBarWithDuration()
                        hideDurationBar(show = false)
                        ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@CreateFunActivity, R.drawable.ic_crtpost_timer_two_active
                            )
                        )
                    }
                    2 -> {
                        imageVideoDuration = 60
                        setupProgressBarWithDuration()
                        hideDurationBar(show = false)
                        ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@CreateFunActivity, R.drawable.ic_crtpost_timer_three_active
                            )
                        )
                    }
                    3 -> {
                        imageVideoDuration = 90
                        setupProgressBarWithDuration()
                        hideDurationBar(show = false)
                        ivTimer.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@CreateFunActivity, R.drawable.ic_crtpost_timer_four_active
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
            tabbarduration.animate().translationX(0f).setDuration(500)
                .start()
        } else {
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                tabbarduration.animate().translationX(-1000f)
                    .setDuration(500).start()
            }, 500)
        }
    }

    private fun setupProgressBarWithDuration() {
        createTimer(imageVideoDuration)
    }

    //DeepAr Code Below

    private fun initializeDeepAR() {
        deepAR = DeepAR(this)
        deepAR?.setLicenseKey("a8934255341d56543840fc370805931902372ed78dc6def033a9f6c38ea8ec102617d6ed4da7fc17")
        deepAR?.initialize(this, this)
        setupCamera()
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture?.addListener({
            try {
                val cameraProvider = cameraProviderFuture?.get()
                cameraProvider?.let { bindImageAnalysis(it) }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindImageAnalysis(cameraProvider: ProcessCameraProvider) {
        val cameraResolutionPreset = CameraResolutionPreset.P1920x1080
        val width: Int
        val height: Int
        val orientation = screenOrientation
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            width = cameraResolutionPreset.width
            height = cameraResolutionPreset.height
        } else {
            width = cameraResolutionPreset.height
            height = cameraResolutionPreset.width
        }
        val cameraResolution = Size(width, height)
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        if (useExternalCameraTexture) {
            val preview = Preview.Builder()
                .setTargetResolution(cameraResolution)
                .build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, preview)
            if (surfaceProvider == null) {
                deepAR?.let {
                    if (surfaceProvider == null)
                        surfaceProvider = ARSurfaceProvider(this, it)
                }
            }
            preview.setSurfaceProvider(surfaceProvider)
            surfaceProvider?.setMirror((lensFacing == CameraSelector.LENS_FACING_FRONT))
        } else {
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(cameraResolution)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageAnalyzer)
            buffersInitialized = false
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, imageAnalysis)
        }
    }

    private fun initializeBuffers(size: Int) {
        buffers = arrayOfNulls(NUMBER_OF_BUFFERS)
        for (i in 0 until NUMBER_OF_BUFFERS) {
            buffers[i] = ByteBuffer.allocateDirect(size)
            buffers[i]?.order(ByteOrder.nativeOrder())
            buffers[i]?.position(0)
        }
    }

    private val imageAnalyzer = ImageAnalysis.Analyzer { image ->
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        if (!buffersInitialized) {
            buffersInitialized = true
            initializeBuffers(ySize + uSize + vSize)
        }
        val byteData = ByteArray(ySize + uSize + vSize)
        val width = image.width
        val yStride = image.planes[0].rowStride
        val uStride = image.planes[1].rowStride
        val vStride = image.planes[2].rowStride
        var outputOffset = 0
        if (width == yStride) {
            yBuffer[byteData, outputOffset, ySize]
            outputOffset += ySize
        } else {
            var inputOffset = 0
            while (inputOffset < ySize) {
                yBuffer.position(inputOffset)
                yBuffer[byteData, outputOffset, Math.min(yBuffer.remaining(), width)]
                outputOffset += width
                inputOffset += yStride
            }
        }
        //U and V are swapped
        if (width == vStride) {
            vBuffer[byteData, outputOffset, vSize]
            outputOffset += vSize
        } else {
            var inputOffset = 0
            while (inputOffset < vSize) {
                vBuffer.position(inputOffset)
                vBuffer[byteData, outputOffset, Math.min(vBuffer.remaining(), width)]
                outputOffset += width
                inputOffset += vStride
            }
        }
        if (width == uStride) {
            uBuffer[byteData, outputOffset, uSize]
            outputOffset += uSize
        } else {
            var inputOffset = 0
            while (inputOffset < uSize) {
                uBuffer.position(inputOffset)
                uBuffer[byteData, outputOffset, Math.min(uBuffer.remaining(), width)]
                outputOffset += width
                inputOffset += uStride
            }
        }
        buffers[currentBuffer]!!.put(byteData)
        buffers[currentBuffer]!!.position(0)
        if (deepAR != null) {
            deepAR?.receiveFrame(
                buffers[currentBuffer],
                image.width, image.height,
                image.imageInfo.rotationDegrees,
                lensFacing == CameraSelector.LENS_FACING_FRONT,
                DeepARImageFormat.YUV_420_888,
                image.planes[1].pixelStride
            )
        }
        currentBuffer = (currentBuffer + 1) % NUMBER_OF_BUFFERS
        image.close()
    }

    private fun getFilterPath(filterName: String): String? {
        return if (filterName == "none") {
            null
        } else "file:///android_asset/$filterName"
    }

    override fun onStop() {
        var cameraProvider: ProcessCameraProvider? = null
        try {
            cameraProvider = cameraProviderFuture?.get()
            cameraProvider?.unbindAll()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (surfaceProvider != null) {
            surfaceProvider?.stop()
            surfaceProvider = null
        }
        deepAR?.release()
        deepAR = null
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (surfaceProvider != null) {
            surfaceProvider!!.stop()
        }
        if (deepAR == null) {
            return
        }
        deepAR?.setAREventListener(null)
        deepAR?.release()
        deepAR = null
    }

    //Surface Callbacks
    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // If we are using on screen rendering we have to set surface view where DeepAR will render
        if (deepAR != null) {
            deepAR?.setRenderSurface(holder.surface, width, height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (deepAR != null) {
            deepAR?.setRenderSurface(null, 0, 0)
        }
    }

    //Callbacks DeepAr
    override fun screenshotTaken(bitmap: Bitmap) {
        val now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date())
        try {
            val file =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Stalmate")
            if (!file.exists())
                file.mkdir()
            val imageFile = File(file, "image_$now.jpg")
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            MediaScannerConnection.scanFile(
                this@CreateFunActivity,
                arrayOf(imageFile.toString()),
                null,
                null
            )
            /*Toast.makeText(
                this@CreateFunActivity,
                "Screenshot " + imageFile.name + " saved.",
                Toast.LENGTH_SHORT
            ).show()*/

            /*val extension: String =
                MimeTypeMap.getSingleton().getExtensionFromMimeType(this@CreateFunActivity.contentResolver.getType(Uri.fromFile(imageFile))).toString()
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()*/
            mimeType = "image/jpeg"
            startPhotoEditorImgLy(imageFile.toUri())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun startPhotoEditorImgLy(imageFile: Uri) {
        //Start img.ly
        // In this example, we do not need access to the Uri(s) after the editor is closed
        // so we pass false in the constructor
        val settingsList = PhotoEditorSettingsList(false)
            .configure<LoadSettings> {
                // Set the source as the Uri of the image to be loaded
                it.source = imageFile
            }
        Handler(Looper.getMainLooper()).postDelayed({
            photoEditorResult.launch(settingsList)
            // Release the SettingsList once done
            settingsList.release()
        }, 1000)
    }

    private fun startVideoEditor(videoUri: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SpeedReverseActivity::class.java).apply {
                putExtra("videoUri", videoUri)
                putExtra("mimeType", mimeType)
                putExtra("imageVideoDuration", imageVideoDuration)
            })
        }, 1000)
    }

    override fun videoRecordingStarted() {
        //Toast.makeText(applicationContext, "Recording started.", Toast.LENGTH_LONG).show()
    }

    override fun videoRecordingFinished() {
        /*Toast.makeText(
            applicationContext,
            "Recording " + videoFileName?.name + " saved.",
            Toast.LENGTH_LONG
        ).show()*/
        //Send to change speed and to do reverse
        videoFileName?.let {
            /*val extension: String = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                this.contentResolver.getType(
                    Uri.fromFile(it)
                )
            ).toString()
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()*/
            mimeType = "video/mp4"
            startVideoEditor(it.absolutePath.toString())
        }
    }

    override fun videoRecordingFailed() {
        //Toast.makeText(applicationContext, "Error while recording.", Toast.LENGTH_LONG).show()
    }

    override fun videoRecordingPrepared() {
        //Toast.makeText(applicationContext, "Recording prepared.", Toast.LENGTH_LONG).show()
    }

    override fun shutdownFinished() {}
    override fun initialized() {
        // Restore effect state after deepar release
        val type = when (activeFilterType) {
            0 -> "mask"
            1 -> "effect"
            2 -> "filter"
            else -> ""
        }
        val typeList = when (activeFilterType) {
            0 -> masks
            1 -> effects
            2 -> filters
            else -> masks
        }
        if (typeList[0].name == "none") {
            findViewById<TextView>(R.id.tvEffectName).visibility = View.INVISIBLE
        } else {
            findViewById<TextView>(R.id.tvEffectName).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvEffectName).text =
                (typeList[0].name?.toUpperCase().toString() + typeList[0].name?.substring(
                    1,
                    (typeList[0].name?.lastIndex ?: 0) + 1
                )?.replace(".deepar", "")).replace("_", " ")
        }
        deepAR?.switchEffect(type, getFilterPath(typeList[0].name))
        deepAR?.switchEffect("effect", getFilterPath(typeList[0].name))
    }

    override fun faceVisibilityChanged(b: Boolean) {}
    override fun imageVisibilityChanged(s: String, b: Boolean) {}
    override fun frameAvailable(image: Image) {}
    override fun error(arErrorType: ARErrorType, s: String) {}
    override fun effectSwitched(s: String) {}

    //Callbacks img.Ly
    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private val photoEditorResult = registerForActivityResult(PhotoEditorActivityResultContract()) {
        when (it.resultStatus) {
            EditorSDKResult.Status.CANCELED -> {}
            EditorSDKResult.Status.EXPORT_DONE -> {
                startActivity(
                    IntentHelper.getCreateFuntimePostScreen(this)!!
                        .putExtra(ActivityFilter.EXTRA_VIDEO, it.resultUri.toString())
                        .putExtra("mimeType", mimeType)
                        .putExtra("isImage", true)
                )
            }
            else -> {
            }
        }
    }

    companion object {
        private const val NUMBER_OF_BUFFERS = 2
        private const val useExternalCameraTexture = true
    }
}