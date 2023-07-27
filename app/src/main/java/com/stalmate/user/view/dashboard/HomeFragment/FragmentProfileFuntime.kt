package com.stalmate.user.view.dashboard.HomeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentProfileFuntimeBinding
import com.stalmate.user.view.dashboard.funtime.ReelVideosByAudioAdapter
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

class FragmentProfileFuntime : BaseFragment(), ReelVideosByAudioAdapter.Callback {
    lateinit var adapter: ReelVideosByAudioAdapter
    lateinit var binding: FragmentProfileFuntimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentProfileFuntimeBinding>(
            inflater.inflate(
                R.layout.fragment_profile_funtime,
                container,
                false
            )
        )!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ReelVideosByAudioAdapter(requireContext(), this, true)
        binding.rvList.layoutManager = GridLayoutManager(context, 3)
        binding.rvList.adapter = adapter
        Log.d("akljsdasd", "aljsdasd")
        getReelsListApiByMusic()

    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun getReelsListApiByMusic() {
        val hashMap = HashMap<String, String>()
        hashMap.put("page", "1")
        hashMap.put("id_user", prefManager?._id.toString())
        hashMap.put("fun_id", "")
        hashMap.put("limit", "50")
        hashMap.put("is_video", "2")
        networkViewModel.funtimeLiveData(prefManager?.access_token.toString(), hashMap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(
                    if (arguments?.getBoolean("isVideos") == false)
                        it.results.filter {
                            it.isImage()
                        }
                    else
                        it.results.filter {
                            (!it.isImage())
                        }
                )
                binding.root.requestLayout()
            }
        }
    }

    override fun onClickOnReel(reel: ResultFuntime) {
        IntentHelper.getFullViewReelActivity(context).apply {
            this?.putExtra("data", reel)
            this?.putExtra("showMyVideos", true)
        }?.let { startActivity(it) }
    }
}