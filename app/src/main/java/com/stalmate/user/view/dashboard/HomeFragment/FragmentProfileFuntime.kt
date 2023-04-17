package com.stalmate.user.view.dashboard.HomeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentProfileFuntimeBinding
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import com.stalmate.user.view.dashboard.funtime.ReelVideosByAudioAdapter
import com.stalmate.user.view.dashboard.funtime.ResultFuntime

class FragmentProfileFuntime :BaseFragment(), ReelVideosByAudioAdapter.Callback {
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
        adapter=ReelVideosByAudioAdapter(requireContext(),this,true)
        binding.rvList.layoutManager= GridLayoutManager(context,3)
        binding.rvList.adapter=adapter
        Log.d("akljsdasd","aljsdasd")
        getReelsListApiByMusic()

    }
    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun getReelsListApiByMusic() {
        var hashMap=HashMap<String,String>()
        hashMap.put("page", "1")
        hashMap.put("limit", "5")
        hashMap.put("id_user", prefManager?.id.toString())
        hashMap.put("fun_id", "")



        networkViewModel.funtimeLiveData(hashMap)
        networkViewModel.funtimeLiveData.observe(viewLifecycleOwner) {
            it.let {
                adapter.addToList(it!!.results)
                binding.root.requestLayout()

            }
        }
    }

    override fun onClickOnReel(reel: ResultFuntime) {
        var bundle= Bundle()
        bundle.putParcelable("data",reel)
        startActivity(IntentHelper.getFullViewReelActivity(context)!!.putExtra("data",reel).putExtra("showMyVideos",true))
    }


}