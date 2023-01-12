package com.stalmate.user.view.dashboard.SIdeDrawer


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSavedFavouriteBinding
import com.stalmate.user.view.adapter.SavedGridMusicAdapter
import com.stalmate.user.view.adapter.SavedGridVideoAdapter
import com.stalmate.user.view.adapter.SearchedUserAdapter

class FragmentSavedFavourite : BaseFragment() {
    lateinit var userAdapter: SearchedUserAdapter
    lateinit var binding: FragmentSavedFavouriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        /*     if (requireArguments().getString("dataSearch") != null) {
                 searchData = requireArguments().getString("dataSearch").toString()
             }
      */
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentSavedFavouriteBinding>(
            inflater.inflate(
                R.layout.fragment_saved_favourite,
                container,
                false
            )
        )!!
        return binding.root
    }
lateinit var musicAdapter: SavedGridMusicAdapter
    lateinit var videoAdapter: SavedGridVideoAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicAdapter= SavedGridMusicAdapter(requireContext())
        videoAdapter= SavedGridVideoAdapter(requireContext())

        binding.rvReelList.adapter=videoAdapter
        binding.rvMusicList.adapter=musicAdapter
        binding.toolbar.tvhead.text="Saved/Favourite"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
        getSavedVideoData()





    }


    fun getSavedMusicData() {

        var hashmap = HashMap<String, String>()
        hashmap.put("limit", "4")
        networkViewModel.getSavedFuntimMusic(hashmap).observe(viewLifecycleOwner, Observer {
            it.let {
                if (it!!.results.size==0){
                    binding.tvNoAudio.visibility=View.VISIBLE
                }

                if (it!!.results.size==1){
                    binding.rvMusicList.layoutManager=GridLayoutManager(requireContext(),1)
                }
                if (it!!.results.size==2){


                    binding.rvMusicList.visibility=View.GONE
                    binding.layoutForTwoAudio.visibility=View.VISIBLE
                    Glide.with(requireActivity()).load(it.results[0].image).into(binding.ivAudioOne)
                    Glide.with(requireActivity()).load(it.results[1].image).into(binding.ivAudioTwo)

                    binding.layoutForTwoAudio.setOnTouchListener(object : View.OnTouchListener {
                        override
                        fun onTouch(v: View?, event: MotionEvent): Boolean {
                            Log.d("asdasda","asdasd")
                            if (event.action==MotionEvent.ACTION_UP)
                                onClickOnMusic()
                            return false
                        }
                    })


                    binding.layoutForTwoAudio.setOnClickListener {
                        onClickOnMusic()
                    }
                }

                if (it!!.results.size==3){
                    binding.rvMusicList.layoutManager=GridLayoutManager(requireContext(),2)
                }

                if (it!!.results.size>3){
                    binding.rvMusicList.layoutManager=GridLayoutManager(requireContext(),2)
                }





                musicAdapter.submitList(it!!.results)



                binding.rvMusicList.setOnTouchListener(object : View.OnTouchListener {
                    override
                    fun onTouch(v: View?, event: MotionEvent): Boolean {
                        Log.d("asdasda","asdasd")
                        if (event.action==MotionEvent.ACTION_UP)
                        onClickOnMusic()
                        return false
                    }
                })


                binding.layoutForTwoVideo.setOnClickListener {
                    onClickOnMusic()
                }

                if (videoAdapter.list.size==0 && musicAdapter.list.size==0){
                    binding.gridlayuot.visibility=View.GONE
                    Log.d("klajsdasd",";asldkasd")
                    binding.tvNoData.visibility=View.VISIBLE
                }


            }
        })
    }



    fun getSavedVideoData() {
        var hashmap = HashMap<String, String>()
        hashmap.put("limit", "4")
        networkViewModel.getSavedFuntimReels(hashmap).observe(viewLifecycleOwner, Observer {
            it.let {

                if (it!!.results.size==0){
                    binding.tvNoReel.visibility=View.VISIBLE
                }

                if (it!!.results.size==1){
                    binding.rvReelList.layoutManager=GridLayoutManager(requireContext(),1)
                }
                if (it!!.results.size==2){
                    binding.rvReelList.visibility=View.GONE
                    binding.layoutForTwoVideo.visibility=View.VISIBLE



                    val requestOptions = RequestOptions()
                    Glide.with(requireContext())
                        .load(it!!.results[0].file)
                        .apply(requestOptions)
                        .thumbnail(Glide.with(requireContext()).load(it!!.results[0].file))
                        .into(binding.ivVideoOne);


                    Glide.with(requireContext())
                        .load(it!!.results[1].file)
                        .apply(requestOptions)
                        .thumbnail(Glide.with(requireContext()).load(it!!.results[1].file))
                        .into(binding.ivVideoTwo);

                    binding.layoutForTwoVideo.setOnTouchListener(object : View.OnTouchListener {
                        override
                        fun onTouch(v: View?, event: MotionEvent): Boolean {
                            Log.d("asdasda","asdasd")
                            if (event.action==MotionEvent.ACTION_UP)
                                onClickOnVideo()
                            return false
                        }
                    })

                    binding.layoutForTwoVideo.setOnClickListener {
                        onClickOnVideo()
                    }

                }

                if (it!!.results.size==3){
                    binding.rvReelList.layoutManager=GridLayoutManager(requireContext(),2)
                }

                if (it!!.results.size>3){
                    binding.rvReelList.layoutManager=GridLayoutManager(requireContext(),2)
                }


                videoAdapter.submitList(it!!.results)


                binding.rvReelList.setOnTouchListener(object : View.OnTouchListener {
                    override
                    fun onTouch(v: View?, event: MotionEvent): Boolean {
                        Log.d("asdasda","asdasd")
                        if (event.action==MotionEvent.ACTION_UP)
                        onClickOnVideo()
                        return false
                    }
                })



                Log.d(";laskdasd",videoAdapter.list.size.toString())


                getSavedMusicData()

            }
        })
    }


     fun onClickOnVideo() {
            findNavController().navigate(R.id.action_fragmentsavefavourite_to_fragmentsavefavouritereel)
    }
     fun onClickOnMusic() {
        findNavController().navigate(R.id.action_fragmentsavefavourite_to_fragmentsavefavouritemusic)
    }
}