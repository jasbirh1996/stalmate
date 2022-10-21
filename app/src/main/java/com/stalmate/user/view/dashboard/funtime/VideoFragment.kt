/*

package com.stalmate.user.view.dashboard.funtime

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.base.BaseFragment

import fr.castorflex.android.verticalviewpager.VerticalViewPager
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors


*/
/**
 * A simple [Fragment] subclass.
 *//*

// this is the main view which is show all  the video in list
class VideosListF : BaseFragment, Player.Listener, View.OnClickListener {
    var sideMenu: LinearLayout? = null
    var videoInfoLayout: LinearLayout? = null
    var menuPager: VerticalViewPager? = null
    var item: ResultFuntime? = null
    var showad = false
    var fragmentContainerId = 0

    constructor(
        showad: Boolean,
        item: ResultFuntime?,
        menuPager: VerticalViewPager?,
        fragmentContainerId: Int
    ) {
        this.showad = showad
        this.item = item
        this.menuPager = menuPager
        this.fragmentContainerId = fragmentContainerId
    }

    constructor() {
        // Required empty public constructor
    }
    override
    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.item_home_layout, container, false)
        context = view!!.context
        initializePlayer()
        initalize_views()
        return view
    }

    var username: TextView? = null
    var descTxt: TextView? = null
    var soundName: TextView? = null
    var skipBtn: TextView? = null
    var varifiedBtn: ImageView? = null
    var duetLayoutUsername: RelativeLayout? = null
    var animateRlt: RelativeLayout? = null
    var mainlayout: RelativeLayout? = null
    var duetOpenVideo: LinearLayout? = null
    var likeLayout: LinearLayout? = null
    var commentLayout: LinearLayout? = null
    var sharedLayout: LinearLayout? = null
    var soundImageLayout: LinearLayout? = null
    var commentImage: ImageView? = null
    var likeTxt: TextView? = null
    var commentTxt: TextView? = null
    var duetUsername: TextView? = null
    var playerView: PlayerView? = null
    var handler: Handler? = null
    var runnable: Runnable? = null
    var animationRunning = false
    var pbar: ProgressBar? = null
    fun initalize_views() {
        sideMenu = view!!.findViewById(R.id.side_menu)
        videoInfoLayout = view!!.findViewById(R.id.video_info_layout)
        mainlayout = view!!.findViewById(R.id.mainlayout)
        playerView = view!!.findViewById(R.id.playerview)
        duetLayoutUsername = view!!.findViewById(R.id.duet_layout_username)
        duetUsername = view!!.findViewById(R.id.duet_username)
        duetOpenVideo = view!!.findViewById(R.id.duet_open_video)
        username = view!!.findViewById(R.id.username)
        userPic = view!!.findViewById(R.id.user_pic)
        thumb_image = view!!.findViewById(R.id.thumb_image)
        soundName = view!!.findViewById(R.id.sound_name)
        soundImage = view!!.findViewById(R.id.sound_image)
        varifiedBtn = view!!.findViewById(R.id.varified_btn)
        likeLayout = view!!.findViewById(R.id.like_layout)
        likeImage = view!!.findViewById(R.id.likebtn)
        likeTxt = view!!.findViewById(R.id.like_txt)
        animateRlt = view!!.findViewById(R.id.animate_rlt)
        skipBtn = view!!.findViewById(R.id.skip_btn)
        descTxt = view!!.findViewById(R.id.desc_txt)
        commentLayout = view!!.findViewById(R.id.comment_layout)
        commentImage = view!!.findViewById(R.id.comment_image)
        commentTxt = view!!.findViewById(R.id.comment_txt)
        soundImageLayout = view!!.findViewById(R.id.sound_image_layout)
        sharedLayout = view!!.findViewById(R.id.shared_layout)
        pbar = view!!.findViewById(R.id.p_bar)
        duetOpenVideo.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        userPic.setOnClickListener(::onClick)
        animateRlt.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        username.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        commentLayout.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        sharedLayout.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        soundImageLayout.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        likeImage.setOnLikeListener(object : OnLikeListener() {
            fun liked(likeButton: LikeButton?) {
                likeVideo(item)
            }

            fun unLiked(likeButton: LikeButton?) {
                likeVideo(item)
            }
        })
        skipBtn.setOnClickListener(View.OnClickListener { v: View ->
            onClick(
                v
            )
        })
        thumb_image.setController(Functions.frescoImageLoad(item.thum, thumb_image, false))
        Handler(Looper.getMainLooper()).postDelayed({ setData() }, 200)
    }

    fun setData() {
        if (view == null && item != null) return else {
            username!!.text = "" + Functions.showUsername("" + item.username)
            userPic.setController(Functions.frescoImageLoad(item.profile_pic, userPic, false))
            if (item.sound_name == null || item.sound_name.equals("") || item.sound_name.equals("null")) {
                soundName!!.text =
                    context!!.getString(R.string.orignal_sound_) + " " + item.username
                item.sound_pic = item.profile_pic
            } else {
                soundName.setText(item.sound_name)
            }
            soundName!!.isSelected = true
            soundImage.setController(Functions.frescoImageLoad(item.sound_pic, soundImage, false))
            descTxt.setText(item.video_description)
            FriendsTagHelper.Creator.create(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.white),
                object : OnFriendsTagClickListener() {
                    fun onFriendsTagClicked(friendsTag: String) {
                        var friendsTag = friendsTag
                        onPause()
                        if (friendsTag.contains("#")) {
                            Log.d(Constants.tag, "Hash $friendsTag")
                            if (friendsTag[0] == '#') {
                                friendsTag = friendsTag.substring(1)
                                openHashtag(friendsTag)
                            }
                        } else if (friendsTag.contains("@")) {
                            Log.d(Constants.tag, "Friends $friendsTag")
                            if (friendsTag[0] == '@') {
                                friendsTag = friendsTag.substring(1)
                                openUserProfile(friendsTag)
                            }
                        }
                    }
                }).handle(descTxt)
            setLikeData()
            if (item.allow_comments != null && item.allow_comments.equalsIgnoreCase("false")) {
                commentLayout!!.visibility = View.GONE
            } else {
                commentLayout!!.visibility = View.VISIBLE
            }
            commentTxt.setText(Functions.getSuffix(item.video_comment_count))
            if (item.verified != null && item.verified.equalsIgnoreCase("1")) {
                varifiedBtn!!.visibility = View.VISIBLE
            } else {
                varifiedBtn!!.visibility = View.GONE
            }
            if (item.duet_video_id != null && !item.duet_video_id.equals("") && !item.duet_video_id.equals(
                    "0"
                )
            ) {
                duetLayoutUsername!!.visibility = View.VISIBLE
                duetUsername.setText(item.duet_username)
            }
            if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false)) {
                animateRlt!!.visibility = View.GONE
            }
            Functions.printLog(Constants.tag, "SetData" + item.video_id)
        }
    }

    fun setLikeData() {
        if (item.liked.equals("1")) {
            likeImage.animate().start()
            likeImage.setLikeDrawable(context!!.resources.getDrawable(R.drawable.ic_heart_gradient))
            likeImage.setLiked(true)
        } else {
            likeImage.setLikeDrawable(context!!.resources.getDrawable(R.drawable.ic_unliked))
            likeImage.setLiked(false)
            likeImage.animate().cancel()
        }
        likeTxt.setText(Functions.getSuffix(item.like_count))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.user_pic -> {
                onPause()
                openProfile(item, false)
            }
            R.id.username -> {
                onPause()
                openProfile(item, false)
            }
            R.id.comment_layout -> if (Functions.checkLoginUser(getActivity())) {
                openComment(item)
            }
            R.id.animate_rlt -> if (Functions.checkLoginUser(getActivity())) {
                animateRlt!!.visibility = View.GONE
                likeVideo(item)
            }
            R.id.shared_layout -> {
                val fragment = VideoActionF(item.video_id, object : FragmentCallBack() {
                    fun onResponce(bundle: Bundle) {
                        if (bundle.getString("action") == "save") {
                            saveVideo(item)
                        } else if (bundle.getString("action") == "duet") {
                            if (Functions.checkLoginUser(getActivity())) {
                                duetVideo(item)
                            }
                        } else if (bundle.getString("action") == "privacy") {
                            onPause()
                            if (Functions.checkLoginUser(getActivity())) {
                                openVideoSetting(item)
                            }
                        } else if (bundle.getString("action") == "delete") {
                            if (Functions.checkLoginUser(getActivity())) {
                                deleteListVideo(item)
                            }
                        } else if (bundle.getString("action") == "favourite") {
                            if (Functions.checkLoginUser(getActivity())) {
                                favouriteVideo(item)
                            }
                        } else if (bundle.getString("action") == "not_intrested") {
                            if (Functions.checkLoginUser(getActivity())) {
                                notInterestVideo(item)
                            }
                        } else if (bundle.getString("action") == "report") {
                            if (Functions.checkLoginUser(getActivity())) {
                                openVideoReport(item)
                            }
                        }
                    }
                })
                val bundle = Bundle()
                bundle.putString("videoId", item.video_id)
                bundle.putString("userId", item.user_id)
                bundle.putString("userName", item.username)
                bundle.putString("userPic", item.profile_pic)
                bundle.putString("fullName", item.first_name.toString() + " " + item.last_name)
                bundle.putSerializable("data", item)
                fragment.setArguments(bundle)
                fragment.show(getChildFragmentManager(), "")
            }
            R.id.sound_image_layout -> {
                takePermissionUtils = PermissionUtils(getActivity(), mPermissionResult)
                if (takePermissionUtils.isCameraRecordingPermissionGranted()) {
                    openSoundByScreen()
                } else {
                    takePermissionUtils.showCameraRecordingPermissionDailog(
                        view!!.context.getString(
                            R.string.we_need_camera_and_recording_permission_for_make_video_on_sound
                        )
                    )
                }
            }
            R.id.duet_open_video -> {
                run {}
                openDuetVideo(item)
            }
            R.id.skip_btn -> hideAd()
        }
    }

    private fun openSoundByScreen() {
        val intent = Intent(view!!.context, VideoSoundA::class.java)
        intent.putExtra("data", item)
        startActivity(intent)
    }

    private fun deleteListVideo(item: HomeModel?) {
        Functions.showLoader(context, false, false)
        Functions.callApiForDeleteVideo(getActivity(), item.video_id, object : APICallBack() {
            fun arrayData(arrayList: ArrayList<*>?) {
                //return data in case of array list
            }

            fun onSuccess(responce: String?) {
                val pagerAdapter = menuPager!!.adapter as ViewPagerStatAdapter
                val bundle = Bundle()
                bundle.putString("action", "deleteVideo")
                bundle.putInt("position", menuPager!!.currentItem)
                fragmentCallBack.onResponce(bundle)
                pagerAdapter.refreshStateSet(true)
                pagerAdapter.removeFragment(menuPager!!.currentItem)
                pagerAdapter.refreshStateSet(false)
            }

            fun onFail(responce: String?) {}
        })
    }

    private fun openVideoSetting(item: HomeModel) {
        val intent = Intent(view!!.context, PrivacyVideoSettingA::class.java)
        intent.putExtra("video_id", item.video_id)
        intent.putExtra("privacy_value", item.privacy_type)
        intent.putExtra("duet_value", item.allow_duet)
        intent.putExtra("comment_value", item.allow_comments)
        intent.putExtra("duet_video_id", item.duet_video_id)
        resultVideoSettingCallback.launch(intent)
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top)
    }

    var resultVideoSettingCallback: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult) {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    if (data!!.getBooleanExtra("isShow", false)) {
                        callApiForSinglevideos()
                    }
                }
            }
        })

    // initlize the player for play video
    private fun initializePlayer() {
        if (exoplayer == null && item != null) {
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                val loadControl: LoadControl = DefaultLoadControl.Builder()
                    .setAllocator(DefaultAllocator(true, 16))
                    .setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024)
                    .setTargetBufferBytes(-1)
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .build()
                val trackSelector = DefaultTrackSelector(context!!)
                try {
                    exoplayer = SimpleExoPlayer.Builder(context!!).setTrackSelector(trackSelector)
                        .setLoadControl(loadControl)
                        .build()
                    val dataSourceFactory: DataSource.Factory =
                        DefaultDataSourceFactory(
                            view!!.context, context!!.getString(R.string.app_name)
                        )
                    val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(item.video_url))
                    exoplayer!!.setThrowsWhenUsingWrongThread(false)
                    exoplayer!!.addMediaSource(videoSource)
                    exoplayer!!.prepare()
                    exoplayer!!.addListener(this@VideosListF)
                    exoplayer!!.repeatMode = Player.REPEAT_MODE_ALL
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val audioAttributes =
                            AudioAttributes.Builder()
                                .setUsage(C.USAGE_MEDIA)
                                .setContentType(C.CONTENT_TYPE_MOVIE)
                                .build()
                        exoplayer!!.setAudioAttributes(audioAttributes, true)
                    }
                } catch (e: Exception) {
                    Log.d(Constants.tag, "Exception audio focus : $e")
                }
                getActivity().runOnUiThread(Runnable {
                    playerView = view!!.findViewById(R.id.playerview)
                    playerView.findViewById<View>(R.id.exo_play).visibility =
                        View.GONE
                    if (exoplayer != null) {
                        playerView.setPlayer(exoplayer)
                    }
                })
            }
        }
    }

    fun setPlayer(isVisibleToUser: Boolean) {
        if (exoplayer != null) {
            if (exoplayer != null) {
                if (isVisibleToUser) {
                    exoplayer!!.playWhenReady = true
                } else {
                    exoplayer!!.playWhenReady = false
                    playerView!!.findViewById<View>(R.id.exo_play).alpha = 1f
                }
            }
            playerView!!.setOnTouchListener(object : OnSwipeTouchListener(context) {
                fun onSwipeLeft() {
                    openProfile(item, true)
                }

                fun onLongClick() {
                    if (isVisibleToUser) {
                        showVideoOption(item)
                    }
                }

                fun onSingleClick() {
                    if (!exoplayer!!.playWhenReady) {
                        exoplayer!!.playWhenReady = true
                        playerView!!.findViewById<View>(R.id.exo_play).alpha = 0f
                        countdownTimer(true)
                    } else {
                        countdownTimer(false)
                        exoplayer!!.playWhenReady = false
                        playerView!!.findViewById<View>(R.id.exo_play).alpha = 1f
                    }
                }

                fun onDoubleClick(e: MotionEvent) {
                    if (!exoplayer!!.playWhenReady) {
                        exoplayer!!.playWhenReady = true
                    }
                    if (Functions.checkLoginUser(getActivity())) {
                        if (!animationRunning) {
                            if (handler != null && runnable != null) {
                                handler!!.removeCallbacks(runnable!!)
                            }
                            handler = Handler(Looper.getMainLooper())
                            runnable = Runnable {
                                if (!item.liked.equalsIgnoreCase("1")) {
                                    likeVideo(item)
                                }
                                showHeartOnDoubleTap(item, mainlayout, e)
                            }
                            handler!!.postDelayed(runnable, 200)
                        }
                    }
                }
            })
            if (item.promote != null && item.promote.equals("1") && showad) {
                item.promote = "0"
                showAd()
            } else {
                hideAd()
            }
        }
    }

    fun updateVideoView() {
        if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false)) {
            Functions.callApiForUpdateView(getActivity(), item.video_id)
        }
        //        callApiForSinglevideos();
    }

    // show a video as a ad
    var isAddAlreadyShow = false
    fun showAd() {
        playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        soundImageLayout!!.animation = null
        sideMenu!!.visibility = View.GONE
        videoInfoLayout!!.visibility = View.GONE
        soundImageLayout!!.visibility = View.GONE
        //        sideMenu.animate().alpha(0).setDuration(400).start();
//        soundImageLayout.setAnimation(null);
//        soundImageLayout.animate().alpha(0).setDuration(400).start();
//        videoInfoLayout.animate().alpha(0).setDuration(400).start();
        skipBtn!!.visibility = View.VISIBLE
        val bundle = Bundle()
        bundle.putString("action", "showad")
        fragmentCallBack.onResponce(bundle)
        countdownTimer(true)
    }

    var countDownTimer: CountDownTimer? = null
    fun countdownTimer(starttimer: Boolean) {
        if (countDownTimer != null) countDownTimer!!.cancel()
        if (view!!.findViewById<View>(R.id.skip_btn).visibility == View.VISIBLE) {
            if (starttimer) {
                countDownTimer = object : CountDownTimer(100000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (exoplayer != null) {
                            if (exoplayer!!.currentPosition > 7000) {
                                hideAd()
                                countdownTimer(false)
                            }
                        }
                    }

                    override fun onFinish() {
                        hideAd()
                    }
                }
                countDownTimer.start()
            }
        }
    }

    // hide the ad of video after some time
    fun hideAd() {
        isAddAlreadyShow = true
        sideMenu!!.visibility = View.VISIBLE
        videoInfoLayout!!.visibility = View.VISIBLE
        soundImageLayout!!.visibility = View.VISIBLE
        //        sideMenu.animate().alpha(1).setDuration(400).start();
//        videoInfoLayout.animate().alpha(1).setDuration(400).start();
//        soundImageLayout.animate().alpha(1).setDuration(400).start();
        val aniRotate = AnimationUtils.loadAnimation(context, R.anim.d_clockwise_rotation)
        soundImageLayout!!.startAnimation(aniRotate)
        skipBtn!!.visibility = View.GONE
        val bundle = Bundle()
        bundle.putString("action", "hidead")
        fragmentCallBack.onResponce(bundle)
    }

    var isVisibleToUser = false
    fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        isVisibleToUser = visible
        Handler(Looper.getMainLooper()).postDelayed({
            if (exoplayer != null && visible) {
                setPlayer(isVisibleToUser)
                updateVideoView()
            }
        }, 200)
    }

    fun mainMenuVisibility(isvisible: Boolean) {
        if (exoplayer != null && isvisible) {
            exoplayer!!.playWhenReady = true
        } else if (exoplayer != null && !isvisible) {
            exoplayer!!.playWhenReady = false
            playerView!!.findViewById<View>(R.id.exo_play).alpha = 1f
        }
    }

    // when we swipe for another video this will relaese the privious player
    var exoplayer: SimpleExoPlayer? = null
    fun releasePriviousPlayer() {
        if (exoplayer != null) {
            exoplayer!!.removeListener(this)
            exoplayer!!.release()
            exoplayer = null
        }
    }

    fun onDestroy() {
        releasePriviousPlayer()
        super.onDestroy()
    }

    private fun openDuetVideo(item: HomeModel) {
        val intent = Intent(view!!.context, WatchVideosA::class.java)
        intent.putExtra("video_id", item.duet_video_id)
        intent.putExtra("position", 0)
        intent.putExtra("pageCount", 0)
        intent.putExtra(
            "userId",
            Functions.getSharedPreference(view!!.context).getString(Variables.U_ID, "")
        )
        intent.putExtra("whereFrom", "IdVideo")
        startActivity(intent)
    }

    // this will open the profile of user which have uploaded the currenlty running video
    private fun openHashtag(tag: String) {
        val intent = Intent(view!!.context, TagedVideosA::class.java)
        intent.putExtra("tag", tag)
        startActivity(intent)
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top)
    }

    // this will open the profile of user which have uploaded the currenlty running video
    private fun openUserProfile(tag: String) {
        val intent = Intent(view!!.context, ProfileA::class.java)
        intent.putExtra("user_name", tag)
        startActivity(intent)
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top)
    }


    var resultCallback: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult) {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    if (data!!.getBooleanExtra("isShow", false)) {
                        callApiForSinglevideos()
                    }
                }
            }
        })

    // show the diolge of video options
    private fun showVideoOption(homeModel: HomeModel?) {
        val alertDialog = Dialog(context!!)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.alert_label_editor)
        alertDialog.window!!.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.d_round_white_background))
        val btn_add_to_fav = alertDialog.findViewById<RelativeLayout>(R.id.btn_add_to_fav)
        val btn_not_insterested = alertDialog.findViewById<RelativeLayout>(R.id.btn_not_insterested)
        val btn_report = alertDialog.findViewById<RelativeLayout>(R.id.btn_report)
        val btnDelete = alertDialog.findViewById<RelativeLayout>(R.id.btnDelete)
        val fav_unfav_txt = alertDialog.findViewById<TextView>(R.id.fav_unfav_txt)
        if (homeModel.favourite != null && homeModel.favourite.equals("1")) fav_unfav_txt.text =
            context!!.getString(R.string.added_to_favourite) else fav_unfav_txt.text =
            context!!.getString(R.string.add_to_favourite)
        if (homeModel.user_id.equalsIgnoreCase(
                Functions.getSharedPreference(context).getString(Variables.U_ID, "")
            )
        ) {
            btn_report.visibility = View.GONE
            btn_not_insterested.visibility = View.GONE
            btnDelete.visibility = View.VISIBLE
        }
        btn_add_to_fav.setOnClickListener {
            alertDialog.dismiss()
            if (Functions.checkLoginUser(getActivity())) {
                favouriteVideo(item)
            }
        }
        btn_not_insterested.setOnClickListener {
            alertDialog.dismiss()
            if (Functions.checkLoginUser(getActivity())) {
                notInterestVideo(item)
            }
        }
        btn_report.setOnClickListener {
            alertDialog.dismiss()
            if (Functions.checkLoginUser(getActivity())) {
                openVideoReport(item)
            }
        }
        btnDelete.setOnClickListener {
            alertDialog.dismiss()
            if (Functions.checkLoginUser(getActivity())) {
                deleteListVideo(item)
            }
        }
        alertDialog.show()
    }

    // this method will be favourite the video
    fun favouriteVideo(item: HomeModel?) {
        val params = JSONObject()
        try {
            params.put("video_id", item.video_id)
            params.put("user_id", Variables.sharedPreferences.getString(Variables.U_ID, ""))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Functions.showLoader(context, false, false)
        VolleyRequest.JsonPostRequest(
            getActivity(),
            ApiLinks.addVideoFavourite,
            params,
            Functions.getHeaders(getActivity()),
            object : Callback() {
                fun onResponce(resp: String?) {
                    Functions.checkStatus(getActivity(), resp)
                    Functions.cancelLoader()
                    try {
                        val jsonObject = JSONObject(resp)
                        val code = jsonObject.optString("code")
                        if (code == "200") {
                            Functions.showToast(
                                context,
                                "Successfully added to your favourite list!"
                            )
                            if (item.favourite != null && item.favourite.equals("0")) item.favourite =
                                "1" else item.favourite = "0"
                            setData()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    // call the api if a user is not intersted the video then the video will not show again to him/her
    fun notInterestVideo(item: HomeModel?) {
        val params = JSONObject()
        try {
            params.put("video_id", item.video_id)
            params.put("user_id", Variables.sharedPreferences.getString(Variables.U_ID, ""))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Functions.showLoader(context, false, false)
        VolleyRequest.JsonPostRequest(
            getActivity(),
            ApiLinks.notInterestedVideo,
            params,
            Functions.getHeaders(getActivity()),
            object : Callback() {
                fun onResponce(resp: String?) {
                    Functions.checkStatus(getActivity(), resp)
                    Functions.cancelLoader()
                    try {
                        val jsonObject = JSONObject(resp)
                        val code = jsonObject.optString("code")
                        if (code == "200") {
                            val pagerAdapter = menuPager!!.adapter as ViewPagerStatAdapter
                            val bundle = Bundle()
                            bundle.putString("action", "removeList")
                            fragmentCallBack.onResponce(bundle)
                            pagerAdapter.refreshStateSet(true)
                            pagerAdapter.removeFragment(menuPager!!.currentItem)
                            pagerAdapter.refreshStateSet(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun openVideoReport(home_model: HomeModel?) {
        onPause()
        val intent = Intent(view!!.context, ReportTypeA::class.java)
        intent.putExtra("video_id", home_model.video_id)
        intent.putExtra("isFrom", false)
        startActivity(intent)
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top)
    }

    // save the video in to local directory
    fun saveVideo(item: HomeModel) {
        val params = JSONObject()
        try {
            params.put("video_id", item.video_id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Functions.showLoader(context, false, false)
        VolleyRequest.JsonPostRequest(
            getActivity(),
            ApiLinks.downloadVideo,
            params,
            Functions.getHeaders(getActivity()),
            object : Callback() {
                fun onResponce(resp: String?) {
                    Functions.checkStatus(getActivity(), resp)
                    Functions.cancelLoader()
                    try {
                        val responce = JSONObject(resp)
                        val code = responce.optString("code")
                        if (code == "200") {
                            val download_url = responce.optString("msg")
                            if (download_url != null) {
                                var downloadDirectory = ""
                                downloadDirectory =
                                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S || Build.VERSION.SDK_INT == Build.VERSION_CODES.R || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                        Functions.getAppFolder(view!!.context)
                                    } else {
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + "/Camera/"
                                    }
                                val file = File(downloadDirectory)
                                if (!file.exists()) {
                                    Log.d(Constants.tag, "Camera directory created again")
                                    file.mkdirs()
                                }
                                Functions.showDeterminentLoader(context, false, false)
                                PRDownloader.initialize(getActivity().getApplicationContext())
                                val prDownloader: DownloadRequest = PRDownloader.download(
                                    Constants.BASE_URL.toString() + download_url,
                                    downloadDirectory,
                                    item.video_id.toString() + ".mp4"
                                )
                                    .build()
                                    .setOnProgressListener(object : OnProgressListener() {
                                        fun onProgress(progress: Progress) {
                                            val prog =
                                                (progress.currentBytes * 100 / progress.totalBytes) as Int
                                            Functions.showLoadingProgress(prog)
                                        }
                                    })
                                val finalDownloadDirectory = downloadDirectory
                                prDownloader.start(object : OnDownloadListener() {
                                    fun onDownloadComplete() {
                                        Functions.cancelDeterminentLoader()
                                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S || Build.VERSION.SDK_INT == Build.VERSION_CODES.R || Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                            downloadAEVideo(
                                                finalDownloadDirectory,
                                                item.video_id.toString() + ".mp4"
                                            )
                                        } else {
                                            deleteWaterMarkeVideo(download_url)
                                            scanFile(finalDownloadDirectory)
                                        }
                                    }

                                    fun onError(error: Error) {
                                        Functions.printLog(
                                            Constants.tag,
                                            "Error : " + error.getConnectionException()
                                        )
                                        Functions.cancelDeterminentLoader()
                                    }
                                })
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun downloadAEVideo(path: String, videoName: String) {
        val valuesvideos: ContentValues
        valuesvideos = ContentValues()
        valuesvideos.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DCIM + File.separator + "Camera"
        )
        valuesvideos.put(MediaStore.MediaColumns.TITLE, videoName)
        valuesvideos.put(MediaStore.MediaColumns.DISPLAY_NAME, videoName)
        valuesvideos.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        valuesvideos.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        valuesvideos.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
        valuesvideos.put(MediaStore.MediaColumns.IS_PENDING, 1)
        val resolver: ContentResolver = getActivity().getContentResolver()
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uriSavedVideo = resolver.insert(collection, valuesvideos)
        val pfd: ParcelFileDescriptor
        try {
            pfd = getActivity().getContentResolver().openFileDescriptor(uriSavedVideo, "w")
            val out = FileOutputStream(pfd.fileDescriptor)
            val imageFile = File(path + videoName)
            val `in` = FileInputStream(imageFile)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            `in`.close()
            pfd.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        valuesvideos.clear()
        valuesvideos.put(MediaStore.MediaColumns.IS_PENDING, 0)
        getActivity().getContentResolver().update(uriSavedVideo, valuesvideos, null, null)
    }

    fun deleteWaterMarkeVideo(video_url: String?) {
        val params = JSONObject()
        try {
            params.put("video_url", video_url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        VolleyRequest.JsonPostRequest(
            getActivity(),
            ApiLinks.deleteWaterMarkVideo,
            params,
            Functions.getHeaders(getActivity()),
            null
        )
    }

    fun scanFile(downloadDirectory: String) {
        MediaScannerConnection.scanFile(
            getActivity(), arrayOf(downloadDirectory + item.video_id.toString() + ".mp4"),
            null
        ) { path, uri -> }
    }

    // download the video for duet with
    fun duetVideo(item: HomeModel) {
        Functions.printLog(Constants.tag, item.video_url)
        if (item.video_url != null) {
            val deletePath: String =
                Functions.getAppFolder(getActivity()) + item.video_id.toString() + ".mp4"
            val deleteFile = File(deletePath)
            if (deleteFile.exists()) {
                openDuetRecording(item)
                return
            }
            Functions.showDeterminentLoader(context, false, false)
            PRDownloader.initialize(getActivity().getApplicationContext())
            val prDownloader: DownloadRequest = PRDownloader.download(
                item.video_url,
                Functions.getAppFolder(getActivity()),
                item.video_id.toString() + ".mp4"
            )
                .build()
                .setOnProgressListener(object : OnProgressListener() {
                    fun onProgress(progress: Progress) {
                        val prog = (progress.currentBytes * 100 / progress.totalBytes) as Int
                        Functions.showLoadingProgress(prog)
                    }
                })
            prDownloader.start(object : OnDownloadListener() {
                fun onDownloadComplete() {
                    Functions.cancelDeterminentLoader()
                    openDuetRecording(item)
                }

                fun onError(error: Error) {
                    Functions.printLog(Constants.tag, "Error : " + error.getConnectionException())
                    Functions.cancelDeterminentLoader()
                }
            })
        }
    }

    fun openDuetRecording(item: HomeModel?) {
        val intent = Intent(getActivity(), VideoRecoderDuetA::class.java)
        intent.putExtra("data", item)
        startActivity(intent)
    }

    // call api for refersh the video details
    private fun callApiForSinglevideos() {
        val parameters = JSONObject()
        try {
            if (Variables.sharedPreferences.getString(Variables.U_ID, null) != null) parameters.put(
                "user_id",
                Variables.sharedPreferences.getString(Variables.U_ID, "0")
            )
            parameters.put("video_id", item.video_id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        VolleyRequest.JsonPostRequest(
            getActivity(),
            ApiLinks.showVideoDetail,
            parameters,
            Functions.getHeaders(getActivity()),
            object : Callback() {
                fun onResponce(resp: String?) {
                    Functions.checkStatus(getActivity(), resp)
                    singalVideoParseData(resp)
                }
            })
    }

    // parse the data for a video
    fun singalVideoParseData(responce: String?) {
        try {
            val jsonObject = JSONObject(responce)
            val code = jsonObject.optString("code")
            if (code == "200") {
                val msg = jsonObject.optJSONObject("msg")
                val video = msg.optJSONObject("Video")
                val user = msg.optJSONObject("User")
                val sound = msg.optJSONObject("Sound")
                val userprivacy = user.optJSONObject("PrivacySetting")
                val userPushNotification = user.optJSONObject("PushNotification")
                item =
                    Functions.parseVideoData(user, sound, video, userprivacy, userPushNotification)
                setData()
            } else {
                Functions.showToast(getActivity(), jsonObject.optString("msg"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onDataSent(yourData: String?) {
        val comment_count: Int = Functions.parseInterger(yourData)
        item.video_comment_count = "" + comment_count
        commentTxt.setText(Functions.getSuffix(item.video_comment_count))
    }

    private val mPermissionResult: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            object : ActivityResultCallback<Map<String?, Boolean?>?> {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onActivityResult(result: Map<String?, Boolean?>) {
                    var allPermissionClear = true
                    val blockPermissionCheck: MutableList<String> = ArrayList()
                    for (key in result.keys) {
                        if (!result[key]!!) {
                            allPermissionClear = false
                            blockPermissionCheck.add(
                                Functions.getPermissionStatus(
                                    getActivity(),
                                    key
                                )
                            )
                        }
                    }
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(
                            view!!.context,
                            view!!.context.getString(R.string.we_need_camera_and_recording_permission_for_make_video_on_sound)
                        )
                    } else if (allPermissionClear) {
                        openSoundByScreen()
                    }
                }
            })

    fun onDetach() {
        super.onDetach()
        mPermissionResult.unregister()
    }

    companion object {
        var videoListCallback: FragmentCallBack? = null
    }
}*/
