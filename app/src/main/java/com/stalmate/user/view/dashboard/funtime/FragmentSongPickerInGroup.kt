package com.stalmate.user.view.dashboard.funtime


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.TaggedUsersAdapter
import com.stalmate.user.databinding.FragmentSongPickerInGroupBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.*
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker.TAG
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.ActivityDashboard
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FragmentSongPickerInGroup : BaseFragment(), FriendAdapter.Callbackk,
    AdapterFunTimeMusic.Callback {
    lateinit var binding: FragmentSongPickerInGroupBinding
    lateinit var adapterFunTimeMusic : AdapterFunTimeMusic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentSongPickerInGroupBinding>(
            inflater.inflate(
                R.layout.fragment_song_picker_in_group,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterFunTimeMusic = AdapterFunTimeMusic(networkViewModel, requireContext(),isBig = false,this)
        binding.rvFavouriteMusic.layoutManager=LinearLayoutManager(requireContext())
        binding.rvTrendingMusic.layoutManager=LinearLayoutManager(requireContext())
        binding.rvTrendingMusic.adapter=adapterFunTimeMusic
        binding.rvFavouriteMusic.adapter=adapterFunTimeMusic
        getMusicListApi()
        /*    peopleAdapter = TaggedUsersAdapter(taggedPeopleViewModel, requireContext())*/


        binding.etSearch.setOnFocusChangeListener { view, b ->
            run {
                findNavController().navigate(R.id.FragmentSongPickerSearch)
            }
        }

        binding.toolbar.back.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }

/*    override fun onDestroy() {
        super.onDestroy()
        mPlayer!!.stop(true)
        mPlayer!!.playWhenReady = false
        mPlayer!!.release()
        mPlayer = null
    }*/


    private fun getMusicListApi() {
        var hashMap=HashMap<String,String>()
        hashMap.put("search","")
        networkViewModel.funtimeMusicLiveData(hashMap)
        networkViewModel.funtimeMusicLiveData.observe(viewLifecycleOwner) {

            it.let {
                adapterFunTimeMusic.submitList(it!!.results)
                Log.d("=============", it!!.results.size.toString())
            }
        }
    }
    fun downloadSelectedSong(song: Song) {

        val songs = File(requireActivity().filesDir, "songs")
        if (!songs.exists() && !songs.mkdirs()) {
            Log.w(
                TAG,
                "Could not create directory at $songs"
            )
        }
        val audio = File(songs, song.title.toString() + ".aac")
        if (audio.exists()) {
            closeWithSelection(song, Uri.fromFile(audio))
            return
        }
        showLoader()
        val input = Data.Builder()
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
                    closeWithSelection(song, Uri.fromFile(audio))
                } else if (ended) {

                }
            }
    }

    private fun closeWithSelection(song: Song, file: Uri) {
        val data = Intent()
        Log.d("klajsdasd",song.id)
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

}