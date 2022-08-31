package com.stalmate.user.view.myStory

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentQuickModeBinding
import com.stalmate.user.databinding.FragmentSchaduleQuickModeBinding
import com.stalmate.user.model.WeekDaysModel
import com.stalmate.user.view.adapter.FriendAdapter
import java.util.*
import kotlin.collections.ArrayList

class FragmentSchaduleQuickMode : Fragment() {

    private lateinit var binding : FragmentSchaduleQuickModeBinding
    lateinit var weekDaysAdapter: WeekDaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_schadule_quick_mode, container, false)

        binding = DataBindingUtil.bind<FragmentSchaduleQuickModeBinding>(view)!!


        val mTimePicker: TimePickerDialog
        val mTimePickerEnd: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)


        /*Start Time Picker*/
        mTimePicker = TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.startTime.setText(String.format("%d", hourOfDay))
            }
        }, hour, minute, false)

        binding.startTime.setOnClickListener { v ->
            mTimePicker.show()
        }

        /*End Time Picker*/
        mTimePickerEnd = TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.endTime.setText(String.format("%d", hourOfDay))
            }
        }, hour, minute, false)

        binding.endTime.setOnClickListener { v ->
            mTimePicker.show()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

        /*Week Dsays Adapter setUp*/

        AdapterSet()


    }

    private fun AdapterSet() {

        // ArrayList of class ItemsViewModel
        val data = ArrayList<WeekDaysModel>()
        data.add(WeekDaysModel("M", "1"))
        data.add(WeekDaysModel("T", "2"))
        data.add(WeekDaysModel("W", "3"))
        data.add(WeekDaysModel("T", "4"))
        data.add(WeekDaysModel("F", "5"))
        data.add(WeekDaysModel("S", "6"))
        data.add(WeekDaysModel("S", "7"))

        weekDaysAdapter = WeekDaysAdapter(data)

        binding.rvWeekDays.adapter = weekDaysAdapter

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun toolbarSetUp() {

        binding.toolbar.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text =  getString(R.string.Schedulequite_mode)
        binding.toolbar.menuChat.visibility = View.VISIBLE
        binding.toolbar.menuChat.setImageDrawable(getResources().getDrawable(R.drawable.ic_quitemode_tick))

    }


}