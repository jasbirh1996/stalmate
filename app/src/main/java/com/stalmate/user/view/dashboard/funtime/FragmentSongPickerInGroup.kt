package com.stalmate.user.view.dashboard.funtime


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.stalmate.user.BuildConfig
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSongPickerInGroupBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_COVER
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_FILE
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_ID
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_NAME
import com.stalmate.user.modules.reels.audioVideoTrimmer.utils.FileUtils
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker.TAG
import com.stalmate.user.view.adapter.FriendAdapter
import java.io.*


class FragmentSongPickerInGroup : BaseFragment(), FriendAdapter.Callbackk,
    AdapterFunTimeMusic.Callback {
    lateinit var binding: FragmentSongPickerInGroupBinding
    lateinit var adapterFunTimeMusic: AdapterFunTimeMusic
    private val PICK_AUDIO_REQUEST_CODE = 99
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1
    var musicFile: String = ""
    val PICK_FILE = 99
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        adapterFunTimeMusic =
            AdapterFunTimeMusic(networkViewModel, requireContext(), isBig = false, this)
        binding.rvFavouriteMusic.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTrendingMusic.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTrendingMusic.adapter = adapterFunTimeMusic
        binding.rvFavouriteMusic.adapter = adapterFunTimeMusic
        binding.toolbar.toolBarEndText.text = "Add"
        binding.toolbar.toolBarEndText.visibility = View.VISIBLE
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
        binding.toolbar.toolBarEndText.setOnClickListener {
            songPick()
            //songPick1()
        }

    }

    /*private fun songPick1() {
        val pictureIntent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: Exception) {
            }
            if (photoFile != null) {
                *//*  Uri photoURI = FileProvider.getUriForFile(getActivity(), getPackageName()+".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);*//*
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext().applicationContext,
                    BuildConfig.APPLICATION_ID + ".fileprovider", photoFile
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultCallbackForCamera.launch(pictureIntent)
            }
        }
    }*/

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


    private fun songPick() {
        val i = Intent()
        i.type = "audio/*"
        i.action = Intent.ACTION_GET_CONTENT
        audioPick.launch(i)
        Log.d("clicked", "clicked")
    }

    private var audioPick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val selectedImageUri = result.data?.data!!
                // perform your logic with the selected image Uri
                Log.d("clicked", selectedImageUri.toString())
                val paths = fileFromContentUri(requireContext(), selectedImageUri)

            }
        }


    fun fileFromContentUri(context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivityResult", "onActivityResult")
        var filePath: String? = ""
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data!!.data
            val wholeID = DocumentsContract.getDocumentId(uri)
            val id = wholeID.split(":").toTypedArray()[1]
            val column = arrayOf(MediaStore.MediaColumns.DATA)
            val sel = MediaStore.Audio.Media._ID + "=?"
            val cursor = requireContext().contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )
            Log.d("Cursor123", cursor?.count.toString())
            Log.d("Cursor count", cursor.toString())
            Log.d("id123", id)
            Log.d("column123", column.toString())

            if (cursor != null && cursor.count > 0) {
                val columnIndex = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
                Log.d("jcbjscb", "Chosen path = $filePath")
            } else {
                Log.d("Cursor", "Cursor is empty or null")
            }

            if (!filePath.isNullOrEmpty()) {
                Log.d("jcbjscb", "Chosen path = $filePath")
                musicFile = filePath
                Log.d("jcbjscb", "Chosen path = $musicFile")
            } else {
                Log.d("jcbjscb", "File path is empty or null")
            }
        }

        val selectedAudioUri = data?.data
        if (selectedAudioUri != null) {
            val path = getRealPathFromURI(requireContext(), selectedAudioUri)
            // Use the path as needed
            println("path: $path")
        }
    }*/

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Audio.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "getRealPathFromURI Exception : $e")
            ""
        } finally {
            cursor?.close()
        }
    }

    private fun getMusicListApi() {
        var hashMap = HashMap<String, String>()

        networkViewModel.funtimeMusicLiveData(hashMap)
        networkViewModel.funtimeMusicLiveData.observe(viewLifecycleOwner) {

            it.let {
                adapterFunTimeMusic.submitList(it!!.results)
                Log.d("=============", it!!.results.size.toString())
            }
        }
    }


    private fun saveUnsaveMusic(song: ResultMusic) {
        var hashmap = java.util.HashMap<String, String>()
        hashmap.put("sound_id", song.id)
        networkViewModel.saveUnsaveMusic(hashmap)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it.let {
                    adapterFunTimeMusic.updateSaveStatusList(song)
                }


            })
    }

    fun downloadSelectedSong(song: Song) {

        val songs = File(requireActivity().filesDir, "songs")
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
        Log.d("klajsdasd", song.id)
        data.putExtra(EXTRA_SONG_ID, song.id)
        data.putExtra(EXTRA_SONG_NAME, song.title)
        data.putExtra(EXTRA_SONG_FILE, file)
        data.putExtra(EXTRA_SONG_COVER, song.cover)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, data)
        requireActivity().finish()
    }

    override fun onSongSelected(song: ResultMusic) {
        var downloadableSong = Song()
        downloadableSong.id = song.id
        downloadableSong.audio = song.sound_file
        downloadableSong.title = song.sound_name
        downloadableSong.cover = song.image
        downloadSelectedSong(downloadableSong)
    }

    override fun onClickOnFavouriteMusicButton(song: ResultMusic) {
        saveUnsaveMusic(song)
    }

}