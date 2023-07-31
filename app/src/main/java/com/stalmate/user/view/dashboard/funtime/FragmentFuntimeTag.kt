package com.stalmate.user.view.dashboard.funtime

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.makeramen.roundedimageview.RoundedImageView
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.TaggedUsersAdapter
import com.stalmate.user.databinding.FragmentFuntimeTagBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.adapter.FriendAdapter
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel
import java.io.File

class FragmentFuntimeTag : BaseFragment(), FriendAdapter.Callbackk, TaggedUsersAdapter.Callback {
    private var mPlayer: ExoPlayer? = null
    lateinit var binding: FragmentFuntimeTagBinding
    lateinit var peopleAdapter: TaggedUsersAdapter
    lateinit var tagPeopleViewModel: TagPeopleViewModel
    var mVideo = ""
    private var isImage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentFuntimeTagBinding>(
            inflater.inflate(
                R.layout.fragment_funtime_tag,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!arguments?.getString("mediaUri").isNullOrEmpty()) {
            mVideo = arguments?.getString("mediaUri").toString()
            isImage = arguments?.getBoolean("isImage", false)!!
        }

        tagPeopleViewModel =
            ViewModelProvider(requireActivity()).get(TagPeopleViewModel::class.java)
        peopleAdapter = TaggedUsersAdapter(tagPeopleViewModel, requireContext(), false, this)
        binding.rvPeople.adapter = peopleAdapter
        binding.rvPeople.layoutManager = LinearLayoutManager(requireContext())


        if (!isImage) {
            binding.playerView.visibility = View.VISIBLE
            binding.playerImage.visibility = View.GONE
            mPlayer = ExoPlayer.Builder(requireContext()).build()
            mPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
            val factory = DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))
            val mediaItem: MediaItem = if ((requireActivity() as ActivityFuntimePost).isEdit) {
                //MediaItem.fromUri(Uri.parse((requireActivity() as ActivityFuntimePost).funtime.file))
                MediaItem.fromUri(Uri.fromFile(File(mVideo)))
            } else {
                MediaItem.fromUri(Uri.fromFile(File(mVideo)))
            }
            val source: ProgressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
            binding.playerView.player = mPlayer;
            mPlayer?.prepare(source);
            mPlayer?.playWhenReady = true;
            mPlayer?.play()
        } else {
            binding.playerView.visibility = View.GONE
            binding.playerImage.visibility = View.VISIBLE
            Glide.with(binding.playerImage.context).asBitmap().load(mVideo).addListener(object :RequestListener<Bitmap>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return isFirstResource
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        Palette.from(it).generate { palette ->
                            palette?.let { it1 ->
                                setUpInfoBackgroundColor(
                                    binding.clContainer,
                                    it1
                                )
                            }
                        }
                    }
                    return isFirstResource
                }
            }).into(binding.playerImage)
        }

        binding.layoutAddMoreButton.setOnClickListener {
            /*    setFragmentResultListener(SELECT_USER) { key, bundle ->
                clearFragmentResultListener(requestKey = SELECT_USER)
              *//*  var arrayList=ArrayList<User>()
                arrayList.add(bundle.getSerializable(SELECT_USER) as User)
                peopleAdapter.addToList(arrayList)*//*
            }*/
            if (binding.layoutWhio.visibility == View.GONE) {
                binding.layoutWhio.visibility = View.VISIBLE
            } else {
                binding.layoutWhio.visibility = View.GONE
            }
        }
        binding.layoutWhio.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentFuntimeTag_to_fragmentSingleUserSelector)
        }
        tagPeopleViewModel.getTaggedPeopleList().observe(viewLifecycleOwner, Observer {
            binding.layoutWhio.visibility = View.GONE
            peopleAdapter.submitList(it.taggedPeopleList)
            if (it.taggedPeopleList.size > 0) {
                binding.buttonOk.visibility = View.VISIBLE
                binding.buttonOk.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        })
        binding.ivDone.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivCloseScreen.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }

    override fun onUserSelected(user: User) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isImage) {
            mPlayer?.stop(true)
            mPlayer?.playWhenReady = false
            mPlayer?.release()
            mPlayer = null
        }
    }

    override fun onResume() {
        if (!isImage) {
            if (mPlayer != null) {
                mPlayer?.play()
            }
        }
        super.onResume()
    }

    override fun onPause() {
        if (!isImage) {
            if (mPlayer != null) {
                mPlayer?.release()
            }
        }
        super.onPause()
    }

    private fun setUpInfoBackgroundColor(cl: ConstraintLayout, palette: Palette) {
        val swatch = getMostPopulousSwatch(palette)
        if (swatch != null) {
            val endColor = swatch.rgb
            cl.setBackgroundColor(endColor)
        } else {
            val defaultColor = ContextCompat.getColor(cl.context, R.color.pinklight)
            cl.setBackgroundColor(defaultColor)
        }
    }

    private fun getMostPopulousSwatch(palette: Palette?): Palette.Swatch? {
        var mostPopulous: Palette.Swatch? = null
        if (palette != null) {
            for (swatch in palette.swatches) {
                if (mostPopulous == null || swatch.population > mostPopulous.population) {
                    mostPopulous = swatch
                }
            }
        }
        return mostPopulous
    }
}

const val SELECT_USER = "selectUser"


