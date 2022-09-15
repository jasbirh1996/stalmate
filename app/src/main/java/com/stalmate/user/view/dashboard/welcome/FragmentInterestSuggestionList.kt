package com.stalmate.user.view.dashboard.welcome


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.BubbleGradient
import com.igalata.bubblepicker.model.PickerItem
import com.igalata.bubblepicker.rendering.BubblePicker


import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentInterestSuggestionListBinding
import com.stalmate.user.model.User
import com.stalmate.user.view.adapter.FriendAdapter


class FragmentInterestSuggestionList : BaseFragment(), FriendAdapter.Callbackk{

    lateinit var friendAdapter: FriendAdapter
    private lateinit var picker: BubblePicker
    lateinit var binding: FragmentInterestSuggestionListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentInterestSuggestionListBinding>(
            inflater.inflate(
                R.layout.fragment_interest_suggestion_list,
                container,
                false
            )
        )!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  binding.rvIntrast.adapter=friendAdapter
        networkViewModel.getFriendList("", HashMap())
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })*/

/*
        val titles = resources.getStringArray(R.array.countries)
        val colors = resources.obtainTypedArray(R.array.colors)
        val images = resources.obtainTypedArray(R.array.images)
       // binding.picker.centerImmediately = true

        picker.adapter = object : BubblePickerAdapter {

            override val totalCount = titles.size

            override fun getItem(position: Int): PickerItem {
                return PickerItem().apply {
                    title = titles[position]
                    gradient = BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL)
//                    typeface = mediumTypeface
                    textColor = ContextCompat.getColor(requireContext(), android.R.color.white)
                    backgroundImage = ContextCompat.getDrawable(requireContext(), images.getResourceId(position, 0))
                }
            }
        }

      */
/*  colors.recycle()
        images.recycle()*//*


//        picker!!.bubbleSize = 20
        picker!!.listener = object : BubblePickerListener {
            override fun onBubbleSelected(item: PickerItem) = toast("${item.title} selected")

            override fun onBubbleDeselected(item: PickerItem) = toast("${item.title} deselected")
        }
    }

    override fun onResume() {
        super.onResume()
        picker!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        picker!!.onPause()
    }

    private fun toast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
*/

    }

    override fun onClickOnUpdateFriendRequest(friend: User, status: String) {

    }

    override fun onClickOnProfile(friend: User) {

    }
}