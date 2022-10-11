package com.stalmate.user.modules.reels.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.*
import com.stalmate.user.databinding.ActivitySongPickerBinding
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker

import java.io.File



const val EXTRA_SONG_FILE = "song_file"
const val EXTRA_SONG_ID = "song_id"
const val  EXTRA_SONG_NAME = "song_name"
class ActivitySongPicker : AppCompatActivity() {

    private val TAG = "SongPickerActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySongPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun downloadSelectedSong(song: Song) {
        val songs = File(filesDir, "songs")
        if (!songs.exists() && !songs.mkdirs()) {
            Log.w(
              TAG,
                "Could not create directory at $songs"
            )
        }
        val audio = File(songs, song.id.toString() + ".aac")
        if (audio.exists()) {
            closeWithSelection(song, Uri.fromFile(audio))
            return
        }

        val input = Data.Builder()
            .putString(FileDownloadWorker.KEY_URL, song.audio)
            .putString(FileDownloadWorker.KEY_PATH, audio.absolutePath)
            .build()
        val request: WorkRequest = OneTimeWorkRequest.Builder(FileDownloadWorker::class.java)
            .setInputData(input)
            .build()
        val wm = WorkManager.getInstance(this)
        wm.enqueue(request)
        wm.getWorkInfoByIdLiveData(request.id)
            .observe(this) { info: WorkInfo ->
                val ended = (info.state == WorkInfo.State.CANCELLED
                        || info.state == WorkInfo.State.FAILED)
                if (info.state == WorkInfo.State.SUCCEEDED) {

                    closeWithSelection(song, Uri.fromFile(audio))
                } else if (ended) {

                }
            }
    }

    private fun closeWithSelection(song: Song, file: Uri) {
        val data = Intent()
        data.putExtra(EXTRA_SONG_ID, song.id)
        data.putExtra(EXTRA_SONG_NAME, song.title)
        data.putExtra(EXTRA_SONG_FILE, file)
        setResult(RESULT_OK, data)
        finish()
    }


}