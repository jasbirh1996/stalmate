package com.stalmate.user.modules.reels.activity

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.arthenica.mobileffmpeg.FFmpeg
import com.c2m.storyviewer.utils.showToast
import com.stalmate.user.BuildConfig
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityFullViewReelsBinding
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.modules.reels.adapter.ReelFullViewAdapter
import com.stalmate.user.modules.reels.player.VideoAutoPlayHelper
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.view.dashboard.funtime.viewmodel.ReelListViewModel
import java.io.*

class ActivityFullViewReels : BaseActivity(), ReelFullViewAdapter.Callback {
    lateinit var reelFullViewAdapter: ReelFullViewAdapter
    lateinit var binding: ActivityFullViewReelsBinding;
    var isFirstApiHit = true
    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount: kotlin.Int = 0
    var totalItemCount: kotlin.Int = 0
    var videoAutoPlayHelper: VideoAutoPlayHelper? = null

    override fun onClick(viewId: Int, view: View?) {

    }

    lateinit var reelListViewModel: ReelListViewModel
    var masked = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //setSystemUIVisibility(true)
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view_reels)!!

        reelListViewModel = ViewModelProvider(this).get(ReelListViewModel::class.java)
        downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        customDownloadDialog =
            CustomDownloadDialog(this, "0%", object : CustomDownloadDialog.DownloadListener {
                override fun eventListener() {
                    downloadManager?.remove(downloadId)
                }
            })

        reelFullViewAdapter = ReelFullViewAdapter(this, this, prefManager?._id.toString())
        /*Helper class to provide AutoPlay feature inside cell*/
        if (videoAutoPlayHelper == null) {
            binding.recyclerView.apply {
                adapter = reelFullViewAdapter
                (adapter as ReelFullViewAdapter).stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding.recyclerView)
            videoAutoPlayHelper = VideoAutoPlayHelper(recyclerView = binding.recyclerView)
            videoAutoPlayHelper?.startObserving()
        }

        isSelfVideos = intent.getBooleanExtra("showMyVideos", false)
        /*Helper class to provide show/hide toolBar*/
        //  attachScrollControlListener(binding.customToolBar, binding.recyclerView)
        if (intent.hasExtra("data")) {
            val list = ArrayList<ResultFuntime>()
            val data = intent.getParcelableExtra<ResultFuntime>("data") as ResultFuntime
            list.add(data)
            reelFullViewAdapter.setList(list)
        }
        callApi()

        binding.ivBack.setOnClickListener {
            onPause()
            finish()
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).getChildCount()
                    totalItemCount =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).getItemCount()
                    pastVisiblesItems =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            Log.v("...", "Last Item Wow !")
                            // Do pagination.. i.e. fetch new data

                            if (!isApiRuning) {
                                page_count++
                                callApi()
                            }
                            loading = true
                        }
                    }
                }
            }
        })
    }

    override fun onPause() {
        try {
            if ((videoAutoPlayHelper != null)) {
                binding.recyclerView.postDelayed({
                    val viewholder =
                        binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
                    if (viewholder != null) {
                        val viewMainHolder = (viewholder as VideoReelFullViewHolder)
                        if (viewMainHolder.isVideo && viewMainHolder.customPlayerView.getPlayer()?.isPlaying == true)
                            viewMainHolder.customPlayerView.removePlayer()
                    }
                }, 500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onPause()
    }

    override fun onResume() {
        try {
            if ((videoAutoPlayHelper != null)) {
                binding.recyclerView.postDelayed({
                    val viewholder =
                        binding.recyclerView.findViewHolderForAdapterPosition(videoAutoPlayHelper!!.currentPlayingVideoItemPos);
                    if (viewholder != null) {
                        val viewMainHolder = (viewholder as VideoReelFullViewHolder)
                        if (viewMainHolder.isVideo && viewMainHolder.customPlayerView.getPlayer()?.isPlaying == false)
                            viewMainHolder.customPlayerView.startPlaying()
                    }
                }, 500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onResume()
    }

    var isSelfVideos = false
    var page_count = 1
    var isApiRuning = false
    var handler: Handler? = null
    private fun callApi() {
        isApiRuning = true
        val index = 0
        val hashmap = HashMap<String, String>()
        hashmap.put("page", page_count.toString())
        if (isSelfVideos) {
            if (!reelFullViewAdapter.reelList.isNullOrEmpty()) {
                hashmap.put("id_user", reelFullViewAdapter.reelList[0].user_id.toString())
            } else {
                hashmap.put("id_user", "")
            }
        } else {
            hashmap.put("id_user", "")
        }
        if (!reelFullViewAdapter.reelList.isNullOrEmpty()) {
            hashmap.put("fun_id", reelFullViewAdapter.reelList[0].id.toString())
        } else {
            hashmap.put("fun_id", "")
        }
        hashmap.put("limit", "5")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveData.observe(this) {
            isApiRuning = false
            //  binding.shimmerLayout.visibility =  View.GONE
            Log.d("========", "empty")
            if (!it?.results.isNullOrEmpty()) {
                Log.d("========", "full")
                if (isFirstApiHit) {
                    val list = it?.results
                    list?.forEach {
                        it.isDataUpdated = false
                    }
                    list?.removeAt(0)
                    list?.let { it1 -> reelFullViewAdapter.addToList(it1) }
                } else {
                    val list = it?.results
                    list?.forEach {
                        it.isDataUpdated = false
                    }
                    list?.let { it1 -> reelFullViewAdapter.addToList(it1) }
                }
                isFirstApiHit = false
            }
        }
    }

    private fun likeApiHit(funtime: ResultFuntime) {
        val hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtime.id.toString())
        networkViewModel.funtimeLiveLikeUnlikeData(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeLiveLikeUnlikeData.observe(this) {
            it.let {
                if (it!!.status) {
                    reelFullViewAdapter.likeReelById(funtime.id.toString())
                }
            }
        }
    }

    override fun finish() {
        val returnIntent = Intent()
        val updatedList = ArrayList<ResultFuntime>()
        reelFullViewAdapter.reelList.forEach {
            if (it.isDataUpdated != null) {
                if (it.isDataUpdated!!) {
                    Log.d("alsjdasdddsddmm", it.isDataUpdated.toString())
                    updatedList.add(it)
                }
            }
        }
        returnIntent.putParcelableArrayListExtra("data", updatedList);
        returnIntent.putParcelableArrayListExtra("blockList", blockLisuUser);
        setResult(
            Activity.RESULT_OK,
            returnIntent
        )
        super.finish()
    }

    private fun hitBlockApi(funtime: ResultFuntime) {

        showLoader()
        /*val hashMap = HashMap<String, String>()
        hashMap["id_user"] = funtime.user_id!!
        networkViewModel.block(hashMap)*/
        networkViewModel.block(
            access_token = prefManager?.access_token.toString(),
            _id = funtime.user_id.toString()
        )
        networkViewModel.blockData.observe(this, Observer {
            dismissLoader()
            it.let {
                val position = reelFullViewAdapter.reelList.indexOfFirst { it.id == funtime.id }
                binding.recyclerView.smoothScrollToPosition(position + 1)
                Handler(Looper.getMainLooper()).postDelayed(
                    Runnable {
                        val selectedList = ArrayList<ResultFuntime>()
                        reelFullViewAdapter.reelList.forEach {
                            if (it.user_id == funtime.user_id) {
                                selectedList.add(it)
                            }
                        }
                        selectedList.forEach {
                            reelFullViewAdapter.reelList.remove(it)
                        }
                        reelFullViewAdapter.blockUserFromList(position)
                    },
                    500
                )
            }
        })
    }

    fun deleteReel(funtime: ResultFuntime) {
        val hashmap = HashMap<String, String>()
        hashmap.put("id", funtime.id.toString())
        hashmap.put("is_delete", "1")
        hashmap.put("text", "")
        //hashmap.put("comment_status", "true")
        networkViewModel.funtimUpdate(prefManager?.access_token.toString(), hashmap)
        networkViewModel.funtimeUpdateLiveData.observe(this, Observer {
            it.let {
                if (it?.status == true) {
                    val position = reelFullViewAdapter.reelList.indexOfFirst { it.id == funtime.id }
                    Log.d("a;lksdasd", position.toString())
                    binding.recyclerView.smoothScrollToPosition(position + 1)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { reelFullViewAdapter.removeReelById(funtime.id.toString()) },
                        200
                    )
                }
            }
        })
    }

    override fun onClickOnRemoveReel(resultFuntime: ResultFuntime) {
        deleteReel(resultFuntime)
    }

    override fun onClickOnLikeButtonReel(resultFuntime: ResultFuntime) {
        likeApiHit(resultFuntime)
    }

    override fun onClickOnEditReel(resultFuntime: ResultFuntime) {
        startActivity(
            IntentHelper.getCreateFuntimePostScreen(this)!!.putExtra("isEdit", true)
                .putExtra("data", resultFuntime)
        )
    }

    var blockLisuUser = ArrayList<ResultFuntime>()
    override fun onClickOnBlockUser(resultFuntime: ResultFuntime) {
        blockLisuUser.clear()
        blockLisuUser.add(resultFuntime)
        hitBlockApi(resultFuntime)
    }

    override fun downloadThisFuntime(resultFuntime: ResultFuntime) {
        try {
            val uri =
                Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/drawable/android_logo_white")
            val inputStream = contentResolver.openInputStream(uri)
            masked = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/Stalmate/FunTimes/"
            if (!File(masked + "stalmate.png").exists()) {
                File(masked).mkdirs()
                Handler(Looper.getMainLooper()).post {
                    val outputStream: OutputStream = FileOutputStream(masked + "stalmate.png")
                    val buf = ByteArray(1024)
                    var len: Int
                    while ((inputStream?.read(buf).also { len = (it ?: 0) } ?: 0) > 0) {
                        outputStream.write(buf, 0, len)
                    }
                    outputStream.flush()
                    inputStream?.close()
                    outputStream.close()
                }
            }
            val fileName = resultFuntime.file.splitToSequence(".com/")
                .toList()[1]//.substringBeforeLast(".").toString()
            val filePath = "/Stalmate/FunTimes/$fileName"
            val input =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + filePath
            val output =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/Stalmate/FunTimes/temp_$fileName"
            if (File(input).exists()) {
                //If file exist in directory
                customDownloadDialog.dismiss()
                showToast("Already Downloaded!")
            } else {
                customDownloadDialog.show()
                //If file not exist in directory
                downloadFile(
                    url = resultFuntime.file,
                    filePath = filePath,
                    input = input,
                    output = output
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private lateinit var customDownloadDialog: CustomDownloadDialog
    private var downloadManager: DownloadManager? = null
    private var downloadId: Long = 0

    private fun downloadFile(
        url: String,
        filePath: String,
        input: String,
        output: String
    ) {
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(true)
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle("Downloading")
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription("Downloading ${filePath.substringBeforeLast(".").toString()}")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            filePath
        )
        //Enqueue a new download and same the referenceId
        downloadManager?.let { downloadManager ->
            downloadId = downloadManager.enqueue(request)
            Thread(Runnable {
                val handler = Handler(this.mainLooper)
                do {
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    var status = 0
                    if (cursor.moveToFirst()) {
                        val bytesDownloaded =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        val progress = ((bytesDownloaded * 100L) / bytesTotal)
                        handler.post {
                            if (progress < 100.0) {
                                customDownloadDialog.setMessage("$progress%")
                            } else {
                                customDownloadDialog.setMessage("👍")
                                ffmpegWatermark(input = input, output = output)
                            }
                        }
                    }
                    cursor.close()
                } while (status != DownloadManager.STATUS_SUCCESSFUL)
            }).start()
        }
    }

    private fun ffmpegWatermark(
        input: String,
        output: String
    ) {
        val asyncTask =
            FFmpegAsyncTask("-i $input -i ${masked + "stalmate.png"} -filter_complex 'overlay=10:main_h-overlay_h-10' $output",
                object : FFmpegAsyncTask.OnTaskCompleted {
                    override fun onTaskCompleted(isSuccess: Boolean) {
                        runOnUiThread {
                            saveVideoToInternalStorage(input, output)
                        }
                    }
                })
        asyncTask.execute()
    }

    private fun saveVideoToInternalStorage(
        input: String,
        output: String
    ) {
        try {
            val currentFile: File = File(output)
            val newFile: File = File(input)
            if (currentFile.exists()) {
                Handler(Looper.getMainLooper()).post {
                    val inputStream: InputStream = FileInputStream(currentFile)
                    val outputStream: OutputStream = FileOutputStream(newFile)
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                    outputStream.flush()
                    inputStream.close()
                    outputStream.close()

                    currentFile.delete()
                }
                customDownloadDialog.dismiss()
                Toast.makeText(
                    this.applicationContext,
                    "Downloaded",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Video has failed for downloading!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
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
}