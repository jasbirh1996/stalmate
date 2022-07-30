package com.slatmate.user.Home.Dashboard.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.slatmate.user.R
import com.slatmate.user.databinding.ChatcalltabfragmentBinding

class ChatCallTabFragment : Fragment() {

    private lateinit var binding : ChatcalltabfragmentBinding
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.chatcalltabfragment, container, false)
        binding = DataBindingUtil.bind<ChatcalltabfragmentBinding>(view)!!



        /*tab and ViewPager Layout id*/

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById<ViewPager>(R.id.viewPager)

        /*tab layout setUp Data*/

        binding.tabLayout.addTab(tabLayout!!.newTab().setText("Chats"))
        binding.tabLayout.addTab(tabLayout!!.newTab().setText("Calls"))

        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        /*TAb ViewPager Adapter*/

        val adapter = AdapterChatAndCall(requireContext(), requireFragmentManager(), tabLayout!!.tabCount)
        viewPager!!.adapter = adapter


        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager!!.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Common ToolBar SetUp*/
        toolbarSetUp()


    }

    private fun toolbarSetUp() {

        binding.toolbar.backButtonRightText.visibility =View.VISIBLE
        binding.toolbar.backButtonRightText.text =  getString(R.string.chat)
        binding.toolbar.chatNotification.visibility =View.VISIBLE
        binding.toolbar.chatSetting.visibility =View.VISIBLE
        binding.toolbar.menuChat.visibility =View.VISIBLE

    }
}