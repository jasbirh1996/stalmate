package com.stalmate.user.view.dashboard.funtime

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentReelUsedAudioBinding
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_FILE
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_ID
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_NAME
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker
import com.stalmate.user.utilities.ValidationHelper
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class FragmentReelUsedAudio(var reelData: ResultFuntime) : BaseFragment(){
    private var playingAudio = false
    var currentPos=0
    var runnable: Runnable? = null
    var handler: Handler? = null
     var mediaPlayer:MediaPlayer?=null
    lateinit var adapter:ReelVideosByAudioAdapter
    lateinit var binding:FragmentReelUsedAudioBinding
    var wasPlaying=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_reel_used_audio, container, false)
        binding=DataBindingUtil.bind<FragmentReelUsedAudioBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler= Handler()
        binding.tvArtistName.text=reelData.artist_name
        binding.tvMusicName.text=reelData.sound_name
        adapter=ReelVideosByAudioAdapter(requireContext())
        binding.rvList.layoutManager=GridLayoutManager(context,3)
        binding.rvList.adapter=adapter

        binding.ivplayPauseButton.setOnClickListener {
            if (mediaPlayer!!.isPlaying){
               // pause()
                binding.ivplayPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_music_play_icon))
                mediaPlayer!!.pause()
            }else{
                binding.ivplayPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_postvideo_pause_icon))
                mediaPlayer!!.start()
               // playSong()
            }
        }
        setUPPlayer()
        getReelsListApiByMusic()



        binding.buttonUseAudio.setOnClickListener {
            if (reelData.sound_id!=null){
                onSongSelected(reelData.sound_id!!,reelData.sound_file,reelData.sound_name)
            }


        }

    }


    fun setUPPlayer(){
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        try {
            mediaPlayer!!.setDataSource(reelData.sound_file)
            mediaPlayer!!.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer!!.setOnPreparedListener { mp: MediaPlayer ->
            binding.ivplayPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_postvideo_pause_icon))
            binding.seekbar.max = mp.duration
            binding.tvAudioDuration.setText(convertDurationMillis(mp.duration))
            mediaPlayer!!.start()
            playingAudio = true
            updateSeekbar.run()

        }

        mediaPlayer!!.setOnBufferingUpdateListener { mp: MediaPlayer, percent: Int ->
            val ratio = percent / 100.0
            val bufferingLevel = (mp.duration * ratio).toInt()
            binding.seekbar.setSecondaryProgress(bufferingLevel)
        }



        binding.seekbar.setOnSeekBarChangeListener(seekBarChangeListener)
    }

    fun calculateTime(seconds: Long) {
        val day = TimeUnit.SECONDS.toDays(seconds).toInt()
        val hours: Long = TimeUnit.SECONDS.toHours(seconds) - day * 24
        val minute: Long = TimeUnit.SECONDS.toMinutes(seconds) -
                TimeUnit.SECONDS.toHours(seconds) * 60
        val second: Long = TimeUnit.SECONDS.toSeconds(seconds) -
                TimeUnit.SECONDS.toMinutes(seconds) * 60
        println(
            "Day " + day + " Hour " + hours + " Minute " + minute +
                    " Seconds " + second
        )

        binding.tvAudioDuration.setText(minute.toInt().toString()+":"+second.toString())
    }

    fun convertDurationMillis(getDurationInMillis: Int): String? {
        val convertHours = String.format(
            "%02d",
            TimeUnit.MILLISECONDS.toHours(getDurationInMillis.toLong())
        )
        val convertMinutes = String.format(
            "%02d",
            TimeUnit.MILLISECONDS.toMinutes(getDurationInMillis.toLong()) -
                    TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            getDurationInMillis.toLong()
                        )
                    )
        ) //I needed to add this part.
        val convertSeconds = String.format(
            "%02d",
            TimeUnit.MILLISECONDS.toSeconds(getDurationInMillis.toLong()) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            getDurationInMillis.toLong()
                        )
                    )
        )
        return "$convertMinutes:$convertSeconds"
    }

    private val seekBarChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    seekBar.setProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }


    private var updateSeekbar: Runnable = object : Runnable {
        override fun run() {
            try {

               currentPos = mediaPlayer!!.currentPosition
                Log.d("aksdasda", currentPos.toString())
                binding.seekbar.progress = currentPos as Int

            } finally {
                handler!!.postDelayed(this, 1000)
            }
        }
    }


    private fun getReelsListApiByMusic() {
        var hashMap=HashMap<String,String>()
        hashMap.put("page","1")
        if (!ValidationHelper.isNull(reelData.sound_id)){
            hashMap.put("sound_id",reelData.sound_id!!)
        }

        networkViewModel.get_song_funtime_list(hashMap)
        networkViewModel.reelVideosByAudioLiveData.observe(viewLifecycleOwner) {

            it.let {

                    adapter.addToList(it!!.results)

            }
        }
    }


    override fun onDestroy() {

        stopRepeatingTask()
        super.onDestroy()
    }

    override fun onPause() {
        if (mediaPlayer!=null){
            stopRepeatingTask()
            mediaPlayer!!.pause()
        }
        super.onPause()
    }


    fun stopRepeatingTask() {
        handler!!.removeCallbacks(updateSeekbar)



    }



    fun downloadSelectedSong(song: Song) {

        val songs: File = File(requireActivity().filesDir, "songs")
        if (!songs.exists() && !songs.mkdirs()) {
            Log.w(VideoSpeedWorker.TAG, "Could not create directory at $songs")
        }
        val audio = File(songs, song.title)
        if (audio.exists()) {
            closeWithSelection(song, Uri.fromFile(audio))
            return
        }
        Log.d("song_file====>>>>>", song.audio)
        val input: Data = Data.Builder()
            .putString(FileDownloadWorker.KEY_URL, song.audio)
            .putString(FileDownloadWorker.KEY_PATH, audio.absolutePath)
            .build()
        val request: WorkRequest = OneTimeWorkRequest.Builder(FileDownloadWorker::class.java)
            .setInputData(input)
            .build()
        val wm = WorkManager.getInstance(requireContext())
        wm.enqueue(request)
        wm.getWorkInfoByIdLiveData(request.id)
            .observe(viewLifecycleOwner) { info: WorkInfo ->
                val ended = (info.state == WorkInfo.State.CANCELLED
                        || info.state == WorkInfo.State.FAILED)
                if (info.state == WorkInfo.State.SUCCEEDED) {

                    closeWithSelection(song, Uri.fromFile(audio.absoluteFile))
                } else if (ended) {

                }
            }
    }



    private fun closeWithSelection(song: Song, file: Uri) {

        startActivity(IntentHelper.getCreateReelsScreen(context)!!.putExtra(EXTRA_SONG_ID,song.id).putExtra(EXTRA_SONG_NAME,song.title).putExtra(EXTRA_SONG_FILE,File(file.path).absolutePath).putExtra("type","image"))
    }

     fun onSongSelected(songId:String,songUrl:String,songTitle:String) {
        var downloadableSong= Song()
        downloadableSong.id=songId
        downloadableSong.audio=songUrl
        downloadableSong.title=songTitle
        downloadSelectedSong(downloadableSong)
    }

}