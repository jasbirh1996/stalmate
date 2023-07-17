package com.stalmate.user.view.dashboard.funtime


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.google.android.exoplayer2.ExoPlayer
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.TaggedUsersAdapter
import com.stalmate.user.databinding.FragmentSongPickerSearchBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_COVER
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_FILE
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_ID
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_NAME
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker.TAG
import com.stalmate.user.view.adapter.FriendAdapter
import java.io.File
import java.util.*


class FragmentSongPickerSearch : BaseFragment(), FriendAdapter.Callbackk,
    AdapterFunTimeMusic.Callback {
    private var mPlayer: ExoPlayer? = null
    lateinit var binding: FragmentSongPickerSearchBinding
    lateinit var peopleAdapter: TaggedUsersAdapter
    lateinit var adapterFunTimeMusic : AdapterFunTimeMusic
    var searchData=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentSongPickerSearchBinding>(
            inflater.inflate(
                R.layout.fragment_song_picker_search,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterFunTimeMusic = AdapterFunTimeMusic(networkViewModel, requireContext(),isBig = false,this)
        binding.rvTrendingMusic.layoutManager= LinearLayoutManager(requireContext())
        binding.rvTrendingMusic.adapter=adapterFunTimeMusic
        getMusicListApi()
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    searchData = p0.toString()
                    Handler(Looper.myLooper()!!).post {
                        getMusicListApi()
                    }

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.toolbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }

    private fun getMusicListApi() {
        var hashmap = HashMap<String, String>()
        hashmap.put("search", searchData)
        networkViewModel.funtimeMusicLiveData(hashmap)
        networkViewModel.funtimeMusicLiveData.observe(viewLifecycleOwner) {

            it.let {
                adapterFunTimeMusic.submitList(it!!.results)
                Log.d("=============", it!!.results?.size.toString())
            }
        }
    }

    fun downloadSelectedSong(song: Song) {
        val songs: File = File(requireActivity().filesDir, "songs")
        if (!songs.exists() && !songs.mkdirs()) {
            Log.w(TAG, "Could not create directory at $songs")
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
            .observe(this) { info: WorkInfo ->
                val ended = (info.state == WorkInfo.State.CANCELLED
                        || info.state == WorkInfo.State.FAILED)
                if (info.state == WorkInfo.State.SUCCEEDED) {
                    dismissLoader()
                    closeWithSelection(song, Uri.fromFile(audio.absoluteFile))
                } else if (ended) {
                  dismissLoader()
                }
            }
    }



    private fun closeWithSelection(song: Song, file: Uri) {
        val data = Intent()
        data.putExtra(EXTRA_SONG_ID, song.id)
        data.putExtra(EXTRA_SONG_NAME, song.title)
        data.putExtra(EXTRA_SONG_FILE, file)
        data.putExtra(EXTRA_SONG_COVER, song.cover)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, data)
        requireActivity().finish()
    }

    override fun onSongSelected(song: ResultMusic) {
        var downloadableSong=Song()
        downloadableSong.id=song.id
        downloadableSong.audio=song.sound_file
        downloadableSong.title=song.sound_name
        downloadableSong.cover=song.image
        downloadSelectedSong(downloadableSong)
    }

    override fun onClickOnFavouriteMusicButton(song: ResultMusic) {
        saveUnsaveMusic(song)
    }



    private fun saveUnsaveMusic(song: ResultMusic) {
        var hashmap = java.util.HashMap<String, String>()
        hashmap.put("sound_id",song.id)
        networkViewModel.saveUnsaveMusic(hashmap).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.let {
                adapterFunTimeMusic.updateSaveStatusList(song)
            }


        })
    }

}