package com.stalmate.user.view.dashboard.SIdeDrawer

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment

import com.stalmate.user.databinding.FragmentSavedFavouriteFuntimeBinding
import com.stalmate.user.view.dashboard.funtime.ReelVideosByAudioAdapter
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import com.stalmate.user.view.dashboard.funtime.TaggedUser


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
        networkViewModel.getSavedFuntimReels(prefManager?.access_token.toString(),hashmap).observe(viewLifecycleOwner, Observer {
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