package com.stalmate.user.view.dashboard.funtime

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentReelPlayBinding
import com.stalmate.user.utilities.TimesAgo2


class FragmentReelPlay(var videoLists : ResultFuntime) : BaseFragment(), Player.Listener {

    var exoplayer: ExoPlayer? = null
    private lateinit var binding : FragmentReelPlayBinding
    var playerView: PlayerView? = null
    var animationRunning = false
    var handler: Handler? = null
    var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.bind<FragmentReelPlayBinding>(inflater.inflate(R.layout.fragment_reel_play, container, false))!!

        initializePlayer()

        playerView = binding.pvExoplayer


        binding.pvExoplayer.setOnTouchListener( object : OnTouchListener {
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
        })

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    fun initializePlayer() {

        binding.tvUserName.text= videoLists.first_name+ " " +videoLists.last_name
        Glide.with(this).load(videoLists.profile_img).placeholder(R.drawable.profileplaceholder).into(binding.imgUserProfile)

        binding.like.text = videoLists.like_count.toString()
        binding.comment.text = videoLists.comment_count.toString()
        binding.share.text = videoLists.share_count.toString()
        binding.tvStatusDescription.text = videoLists.text

        binding.like.setOnClickListener {
            likeApiHit()
        }

        val timesAg = TimesAgo2.covertTimeToText(videoLists.Created_date, true)
        binding.tvStoryPostTime.text = timesAg


        if (videoLists.file_type==".mp4") {
            exoplayer = ExoPlayer.Builder(requireContext()).build()
            binding.pvExoplayer.player = exoplayer
            val uri: MediaItem = MediaItem.fromUri(videoLists.file)
            exoplayer!!.addMediaItem(uri)
            exoplayer!!.prepare()
            exoplayer!!.play()
            exoplayer!!.repeatMode = Player.REPEAT_MODE_ALL
            exoplayer!!.playWhenReady = true
        }else if (videoLists.file_type==".png"){
            binding.shapeableImageView.visibility = View.VISIBLE
            binding.pvExoplayer.visibility = View.GONE
            Glide.with(this).load(videoLists.file).placeholder(R.drawable.profileplaceholder).into(binding.imgUserProfile)
        }
    }

    private fun likeApiHit() {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", videoLists.id)
        networkViewModel.funtimeLiveLikeUnlikeData(hashmap)
        networkViewModel.funtimeLiveLikeUnlikeData.observe(viewLifecycleOwner){
            if (it!!.message=="Liked") {
                binding.like.text = it.like_count.toString()
                binding.likeIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_funtime_post_like_fill))
            }else{
                binding.like.text = it.like_count.toString()
            }
        }
    }

    var isVisibleToUser = false

    override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)

        isVisibleToUser = visible

        Handler(Looper.getMainLooper()).postDelayed({
            if (exoplayer != null && visible) {
                setPlayer(isVisibleToUser)
                // updateVideoView();
            }
        }, 200)
    }


    fun setPlayer(isVisibleToUser: Boolean) {
        if (exoplayer != null) {
            if (exoplayer != null) {
                if (isVisibleToUser) {
                    exoplayer!!.playWhenReady = true
                } else {
                    exoplayer!!.playWhenReady = false
                    binding.pvExoplayer.findViewById<View>(com.google.android.exoplayer2.R.id.exo_play).alpha = 1f
                }
            }
        }
    }

    fun mainMenuVisibility(isvisible: Boolean) {
        if (exoplayer != null && isvisible) {
            exoplayer!!.playWhenReady = true
        } else if (exoplayer != null && !isvisible) {
            exoplayer!!.playWhenReady = false
            binding.pvExoplayer.findViewById<View>(com.google.android.exoplayer2.R.id.exo_play).alpha = 1f
        }
    }


    override fun onPause() {
        super.onPause()
        if (exoplayer != null) {
            exoplayer!!.playWhenReady = false
            binding.pvExoplayer.alpha = 1f
            mainMenuVisibility(false)
        }
    }


    override fun onStop() {
        super.onStop()
        if (exoplayer != null) {
            exoplayer!!.playWhenReady = false
            binding.pvExoplayer.alpha = 1f
        }
    }

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

    override fun onResume() {
        super.onResume()
        if (exoplayer != null) {
            exoplayer!!.playWhenReady = true
            binding.pvExoplayer.alpha = 1f
        }
    }
}