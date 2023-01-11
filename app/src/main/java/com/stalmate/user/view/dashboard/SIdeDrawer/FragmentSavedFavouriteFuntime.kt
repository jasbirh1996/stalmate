package com.stalmate.user.view.dashboard.SIdeDrawer

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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.work.*
import com.google.gson.Gson
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment

import com.stalmate.user.databinding.FragmentSavedFavouriteFuntimeBinding
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_COVER
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_FILE
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_ID
import com.stalmate.user.modules.reels.activity.EXTRA_SONG_NAME
import com.stalmate.user.modules.reels.model.Song
import com.stalmate.user.modules.reels.workers.FileDownloadWorker
import com.stalmate.user.modules.reels.workers.VideoSpeedWorker
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.ReelVideosByAudioAdapter
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.view.dashboard.funtime.TaggedUser
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class FragmentSavedFavouriteFuntime : BaseFragment(),
    ReelVideosByAudioAdapter.Callback {
    var runnable: Runnable? = null
    var handler: Handler? = null
    lateinit var adapter: ReelVideosByAudioAdapter
    lateinit var binding: FragmentSavedFavouriteFuntimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_saved_favourite_funtime, container, false)
        binding = DataBindingUtil.bind<FragmentSavedFavouriteFuntimeBinding>(view)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler()
        binding.toolbar.tvhead.text="Posts"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        adapter = ReelVideosByAudioAdapter(requireContext(), this,false)
        binding.rvList.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        binding.rvList.adapter = adapter
        getSavedVideoData()
    }



    fun getSavedVideoData() {
        var hashmap = HashMap<String, String>()
        hashmap.put("limit", "20")
        networkViewModel.getSavedFuntimReels(hashmap).observe(viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it!!.results)
            }
        })
    }


    override fun onClickOnReel(reel: ResultFuntime) {
        var bundle = Bundle()
        var taghhedUser = TaggedUser("", "", "", "")
        reel.tag_user.add(taghhedUser)
        reel.isSave="Yes"
        bundle.putParcelable("data", reel)
        Log.d("lkajsdlasd", Gson().toJson(reel))
        startActivity(IntentHelper.getFullViewReelActivity(context)!!.putExtra("data", reel))
    }

}