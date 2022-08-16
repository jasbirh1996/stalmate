package com.stalmate.user.view.dashboard.welcome


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.baoyachi.stepview.HorizontalStepView
import com.baoyachi.stepview.bean.StepBean

import com.kienht.bubblepicker.BubblePickerListener
import com.kienht.bubblepicker.adapter.BubblePickerAdapter
import com.kienht.bubblepicker.model.BubbleGradient
import com.kienht.bubblepicker.model.PickerItem
import com.kienht.bubblepicker.rendering.BubblePicker
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentInterestSuggestionListBinding
import com.stalmate.user.model.Friend
import com.stalmate.user.view.adapter.FriendAdapter


class FragmentInterestSuggestionList : BaseFragment(), FriendAdapter.Callbackk {
    lateinit var friendAdapter: FriendAdapter
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

/*        friendAdapter = FriendAdapter(networkViewModel, requireContext(), this)

        binding.rvUsers.adapter=friendAdapter
        binding.rvUsers.layoutManager= LinearLayoutManager(context)
        networkViewModel.getFriendList("", HashMap())
        networkViewModel.friendLiveData.observe(viewLifecycleOwner, Observer {
            it.let {
                friendAdapter.submitList(it!!.results)
            }
        })*/


        val titles = resources.getStringArray(R.array.countries)
        val colors = resources.obtainTypedArray(R.array.colors)
        val images = resources.obtainTypedArray(R.array.images)
        binding.picker.centerImmediately = true

/*        Handler().postDelayed({
            binding.picker = BubblePicker(this, null)
            binding.picker!!.adapter = object : BubblePickerAdapter {
                override val totalCount = titles.size

                override fun getItem(position: Int): PickerItem {
                    return PickerItem().apply {
                        title = titles[position]
                        gradient = BubbleGradient(colors.getColor((position * 2) % 8, 0),
                            colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL)
                        imgUrl = "http://sohanews.sohacdn.com/2018/4/11/hat9-1523392964439195574255.jpg"
                    }
                }
            }

            picker!!.bubbleSize = 10
            picker!!.listener = this@AsyncActivity

            setContentView(picker)

        }, 3000)
        colors.recycle()
        images.recycle()*/


        binding.picker.listener = object : BubblePickerListener {
            override fun onBubbleDeselected(item: PickerItem) {

            }

            override fun onBubbleSelected(item: PickerItem) {

            }

        }


        val stepsBeanList: MutableList<StepBean> = ArrayList()
        val stepBean0 = StepBean("接单", 1)
        val stepBean1 = StepBean("打包", 1)
        val stepBean2 = StepBean("出发", 1)
        val stepBean3 = StepBean("送单", 0)
        val stepBean4 = StepBean("完成", -1)
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        stepsBeanList.add(stepBean4)


        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            binding.stepview.setStepViewTexts(stepsBeanList) //总步骤
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        android.R.color.white
                    )
                )
                .setStepsViewIndicatorUnCompletedLineColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorPrimary
                    )
                )
                .setStepViewComplectedTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        android.R.color.white
                    )
                )
                .setStepViewUnComplectedTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorPrimary
                    )
                )
                .setStepsViewIndicatorCompleteIcon(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.view
                    )
                )
                .setStepsViewIndicatorDefaultIcon(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.view
                    )
                )
                .setStepsViewIndicatorAttentionIcon(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.view
                    )
                )

        }, 250)


    }

    override fun onResume() {
        super.onResume()
        binding.picker.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.picker.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.picker.resources
    }


    override fun onClickOnUpdateFriendRequest(friend: Friend, status: String) {

    }
}