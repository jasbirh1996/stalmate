package com.stalmate.user.view.dashboard.funtime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
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
//    var activityDashboard : ActivityDashboard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var isEdit=false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        if (requireActivity().intent.getStringExtra(ActivityFilter.EXTRA_VIDEO)!=null){
            mVideo = requireActivity().intent.getStringExtra(ActivityFilter.EXTRA_VIDEO)!!
        }







        tagPeopleViewModel= ViewModelProvider(requireActivity()).get(TagPeopleViewModel::class.java)
        peopleAdapter = TaggedUsersAdapter(tagPeopleViewModel, requireContext(),false,this)
        binding.rvPeople.adapter = peopleAdapter
        binding.rvPeople.layoutManager = LinearLayoutManager(requireContext())
        Log.d("asdasdgkn","okpppoo")
        mPlayer = ExoPlayer.Builder(requireContext()).build()
        mPlayer!!.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        val factory = DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))
        val mediaItem: MediaItem
        if ((requireActivity() as ActivityFuntimePost).isEdit){
             mediaItem = MediaItem.fromUri(Uri.parse((requireActivity() as ActivityFuntimePost).funtime.file))
        }else{
            mediaItem = MediaItem.fromUri(Uri.fromFile(File(mVideo!!)))
        }





        val source: ProgressiveMediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)




        binding.playerView.player=mPlayer;
        mPlayer!!.prepare(source);
        mPlayer!!.playWhenReady = true;
        mPlayer!!.play()

        binding.layoutAddMoreButton.setOnClickListener {
        /*    setFragmentResultListener(SELECT_USER) { key, bundle ->
                clearFragmentResultListener(requestKey = SELECT_USER)
              *//*  var arrayList=ArrayList<User>()
                arrayList.add(bundle.getSerializable(SELECT_USER) as User)
                peopleAdapter.addToList(arrayList)*//*
            }*/
            if (binding.layoutWhio.visibility==View.GONE){
                binding.layoutWhio.visibility=View.VISIBLE
            }else{
                binding.layoutWhio.visibility=View.GONE
            }

        }



        binding.layoutWhio.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentFuntimeTag_to_fragmentSingleUserSelector)
        }

        tagPeopleViewModel.getTaggedPeopleList().observe(viewLifecycleOwner, Observer {
            binding.layoutWhio.visibility=View.GONE
            peopleAdapter.submitList(it.taggedPeopleList)
            if (it.taggedPeopleList.size>0){
                binding.buttonOk.visibility=View.VISIBLE
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
    override fun onDestroy() {
        super.onDestroy()
        mPlayer!!.stop(true)
        mPlayer!!.playWhenReady = false
        mPlayer!!.release()
        mPlayer = null
    }



    override fun onUserSelected(user: User) {

    }
    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onResume() {
        if (mPlayer!=null){
            mPlayer!!.play()
        }
        super.onResume()
    }


    override fun onPause() {
        if (mPlayer!=null){
            mPlayer!!.release()
        }
        super.onPause()
    }




}

const val SELECT_USER="selectUser"


