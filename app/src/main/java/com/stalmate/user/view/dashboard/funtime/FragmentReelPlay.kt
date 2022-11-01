package com.stalmate.user.view.dashboard.funtime

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.c2m.storyviewer.utils.OnSwipeTouchListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.*
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentReelPlayBinding
import com.stalmate.user.utilities.TimesAgo2
import fr.castorflex.android.verticalviewpager.VerticalViewPager
import java.util.concurrent.Executors


class FragmentReelPlay : BaseFragment, Player.Listener {
    var menuPager: VerticalViewPager? = null
    var item: ResultFuntime? = null
    private var exoplayer: ExoPlayer? = null
    private lateinit var binding: FragmentReelPlayBinding
    var animationRunning = false
    var handler: Handler? = null
    var runnable: Runnable? = null

    constructor(
        item: ResultFuntime?,
        menuPager: VerticalViewPager?,
    ) {
        this.item = item
        this.menuPager = menuPager

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.bind<FragmentReelPlayBinding>(
            inflater.inflate(
                R.layout.fragment_reel_play,
                container,
                false
            )
        )!!
        Handler(Looper.getMainLooper()).post {
            initializePlayer()
        }
        setData()


/*        binding.playerView.setOnTouchListener( object : OnTouchListener {
            private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (!exoplayer!!.playWhenReady) {
                        exoplayer!!.playWhenReady = true
                    }
                    if (!animationRunning) {
                        if (handler != null && runnable != null) {
                            handler!!.removeCallbacks(runnable!!)
                        }
                        handler = Handler(Looper.getMainLooper())
                        runnable = Runnable {

                        }
                        handler!!.postDelayed(runnable!!, 200)
                    }
                    return super.onDoubleTap(e)
                }

                override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                    if (exoplayer != null) {
                        exoplayer!!.playWhenReady = false
                    }
                    Log.d("onSingleTapConfirmed", "onSingleTap")
                    return false
                }
            })

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(event)
                return true
            }
        })*/

        return binding.root
    }


    private fun likeApiHit() {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", item!!.id)
        networkViewModel.funtimeLiveLikeUnlikeData(hashmap)
        networkViewModel.funtimeLiveLikeUnlikeData.observe(viewLifecycleOwner) {
            if (it!!.message == "Liked") {
                binding.like.text = it.like_count.toString()
                binding.likeIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_funtime_post_like_fill))
            } else {
                binding.like.text = it.like_count.toString()
            }
        }
    }

    fun setData() {

        binding.tvUserName.text = item!!.first_name + " " + item!!.last_name
        Glide.with(this).load(item!!.profile_img).placeholder(R.drawable.profileplaceholder)
            .into(binding.imgUserProfile)

        binding.like.text = item!!.like_count.toString()
        binding.comment.text = item!!.comment_count.toString()
        binding.share.text = item!!.share_count.toString()
        binding.tvStatusDescription.text = item!!.text

        binding.like.setOnClickListener {
            likeApiHit()
        }

        /* val timesAg = TimesAgo2.covertTimeToText(item!!.Created_date, true)*/
        binding.tvStoryPostTime.text = item!!.Created_date


    }


    // initlize the player for play video
    private fun initializePlayer() {
        Log.d("alskdasd", "aosdasddfgdfg")
        if (exoplayer == null && item != null) {
            Log.d("alskdasd", "aosdasfgjhd")
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {

            }

            val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setAllocator(DefaultAllocator(true, 16))
                .setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()
            val trackSelector = DefaultTrackSelector(requireContext())
            try {
                exoplayer = ExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .build()
                val dataSourceFactory: DataSource.Factory =
                    DefaultDataSourceFactory(
                        requireView().context, requireContext().getString(R.string.app_name)
                    )
                Log.d("alskdasd", "aosdasd")
                val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(item!!.file))
                //  exoplayer!!.setThrowsWhenUsingWrongThread(false)
                exoplayer!!.addMediaSource(videoSource)
                Log.d("alskdasd", "aosdasd")
                exoplayer!!.prepare()
                exoplayer!!.addListener(this@FragmentReelPlay)
                exoplayer!!.repeatMode = Player.REPEAT_MODE_ALL
                /*   val audioAttributes = AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build()
                exoplayer!!.setAudioAttributes(audioAttributes, true)*/
            } catch (e: Exception) {
                Log.d("Constants.tag", "Exception audio focus : $e")
            }

            requireActivity().runOnUiThread(Runnable {
                Log.d("jkasdas", "aklsdlaskjdfgkjlas")
                binding.playerView.findViewById<View>(com.google.android.exoplayer2.R.id.exo_play)
                    .setVisibility(View.GONE)
                if (exoplayer != null) {
                    Log.d("jkasdas", "aklsdlaskjdlas")
                    binding.playerView.player = exoplayer
                }
            })


        }
    }

    fun setPlayer(isVisibleToUser: Boolean) {
        if (exoplayer != null) {
            if (exoplayer != null) {
                if (isVisibleToUser) {
                    exoplayer!!.playWhenReady = true
                } else {
                    exoplayer!!.playWhenReady = false
                    binding.playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_play)
                        .setAlpha(1f)
                }
            }
            binding.playerView.setOnTouchListener(object : OnSwipeTouchListener(requireActivity()) {

                override
                fun onLongClick() {
                    if (isVisibleToUser) {
                        // showVideoOption(item)
                    }
                }

                fun onSingleClick() {
                    if (!exoplayer!!.playWhenReady) {
                        exoplayer!!.playWhenReady = true
                        binding.playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_play)
                            .setAlpha(0f)
                        //  countdownTimer(true)
                    } else {
                        //  countdownTimer(false)
                        exoplayer!!.playWhenReady = false
                        binding.playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_play)
                            .setAlpha(1f)
                    }
                }

                fun onDoubleClick(e: MotionEvent?) {
                    if (!exoplayer!!.playWhenReady) {
                        exoplayer!!.playWhenReady = true
                    }
                }
            })
            /*    if (item.promote != null && item.promote.equals("1") && showad) {
                item.promote = "0"
                showAd()
            } else {
                hideAd()
            }*/
        }
    }


    var isVisibleToUser = false
    override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        isVisibleToUser = visible
        Handler(Looper.getMainLooper()).postDelayed({
            if (exoplayer != null && visible) {
                setPlayer(isVisibleToUser)
                //updateVideoView()
            }
        }, 200)
    }


    fun mainMenuVisibility(isvisible: Boolean) {

        if (exoplayer != null && isvisible) {
            exoplayer!!.playWhenReady = true
        } else if (exoplayer != null && !isvisible) {
            exoplayer!!.playWhenReady = false
            binding.playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_play)
                .setAlpha(1f)
        }
    }

    // when we swipe for another video this will relaese the privious player
    fun releasePriviousPlayer() {
        if (exoplayer != null) {
            exoplayer!!.removeListener(this)
            exoplayer!!.release()
            exoplayer = null
        }
    }

    override fun onDestroy() {
        releasePriviousPlayer()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (exoplayer != null) {
            exoplayer!!.playWhenReady = false
            binding.playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_play)
                .setAlpha(1f)
        }
    }


    override fun onStop() {
        super.onStop()
        if (exoplayer != null) {
            exoplayer!!.playWhenReady = false
            binding.playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_play)
                .setAlpha(1f)
        }
    }
}