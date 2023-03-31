package com.stalmate.user.view.dashboard.HomeFragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.databinding.FragmentDashboardBinding
import com.stalmate.user.view.dashboard.ActivityDashboardNew
import com.stalmate.user.view.dashboard.Chat.FragmentChatCall
import com.stalmate.user.view.dashboard.Friend.FragmentFriend
import com.stalmate.user.view.dashboard.VideoReels.FragmentReels
import com.stalmate.user.view.dashboard.funtime.FragmentFunTime
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import com.stalmate.user.view.profile.FragmentProfile

class FragmentDashboard : BaseFragment(), View.OnClickListener, FragmentHome.Callback,
    FragmentFriend.Callbackk {
    private lateinit var binding: FragmentDashboardBinding
    lateinit var feedAdapter: AdapterFeed


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*   Log.d(TAG, "Fragment back pressed invoked")
                       // Do custom work here

                       // if you want onBackPressed() to be called as normal afterwards
                       if (isEnabled) {
                           isEnabled = false
                           requireActivity().onBackPressed()
                       }*/


                    var currentVisibleFragment =
                        childFragmentManager.findFragmentById(binding.navHostContainer.id)
                    Log.d("alsjkdlasd", currentVisibleFragment.toString())
                    if (currentVisibleFragment is FragmentHome) {

                        if ((requireActivity() as ActivityDashboardNew).drawerLayout.isDrawerOpen(
                                GravityCompat.END
                            )
                        ) {
                            (requireActivity() as ActivityDashboardNew).drawerLayout.closeDrawer(
                                GravityCompat.END
                            )
                        } else {


                            var custumConfirmDialog = CommonConfirmationDialog(
                                requireContext(),
                                "Exit App ?",
                                "Are you Sure you want to Exit",
                                "Yes",
                                "Cancel",
                                object :
                                    CommonConfirmationDialog.Callback {
                                    override fun onDialogResult(isPermissionGranted: Boolean) {
                                        if (isPermissionGranted) {
                                            requireActivity().finish()
                                        }
                                    }
                                })
                            custumConfirmDialog.show()

                        }

                    } else {
                        setUpNavigationBar(2)
                        loadFragment(fragmentHome)
                    }

                }
            }
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        binding = DataBindingUtil.bind<FragmentDashboardBinding>(view)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationBar.tabFuntime.root.setOnClickListener(this)
        binding.navigationBar.tabChat.root.setOnClickListener(this)
        binding.navigationBar.tabHome.root.setOnClickListener(this)
        binding.navigationBar.tabVideos.root.setOnClickListener(this)
        binding.navigationBar.tabSuggestions.root.setOnClickListener(this)

        binding.navigationBar.tabFuntime.tabIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_botm_menu_funtime
            )
        )
        binding.navigationBar.tabChat.tabIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_botm_menu_chat_inactive
            )
        )
        binding.navigationBar.tabHome.tabIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_botm_menu_home_inactive
            )
        )
        binding.navigationBar.tabVideos.tabIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_botm_menu_video_inactive
            )
        )
        binding.navigationBar.tabSuggestions.tabIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_botm_menu_friends_inactive
            )
        )
        setUpNavigationBar(2)
        loadFragment(fragmentHome)
    }

    val fragmentHome: Fragment = FragmentHome(this)
    val fragmentFuntime: Fragment = FragmentFunTime()
    val fragmentChat: Fragment = FragmentChatCall()
    val fragmentReels: Fragment = FragmentReels()
    val fragmentFriends: Fragment = FragmentFriend(this)
    val fragmentProfile: FragmentProfile = FragmentProfile()


    fun setUpNavigationBar(position: Int) {

        when (position) {
            0 -> {
                /*      loadFragment(fragmentFuntime)
                      binding.navigationBar.tabFuntime.tabLayout.background=ContextCompat.getDrawable(requireContext(),0R.drawable.active_tab_background)
                      binding.navigationBar.tabChat.tabLayout.background=ContextCompat.getDrawable(requireContext(),R.drawable.in_active_tab_background)
                      binding.navigationBar.tabHome.tabLayout.background=ContextCompat.getDrawable(requireContext(),R.drawable.in_active_tab_background)
                      binding.navigationBar.tabVideos.tabLayout.background=ContextCompat.getDrawable(requireContext(),R.drawable.in_active_tab_background)
                      binding.navigationBar.tabSuggestions.tabLayout.background=ContextCompat.getDrawable(requireContext(),R.drawable.in_active_tab_background)*/
                (requireActivity() as ActivityDashboardNew).onClickOnFuntime()
            }

            1 -> {
                loadFragment(fragmentChat)

                binding.navigationBar.tabFuntime.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabChat.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.white)
                );
                binding.navigationBar.tabHome.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabVideos.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabSuggestions.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );









                binding.navigationBar.tabFuntime.tabLayout.background = null
                binding.navigationBar.tabChat.tabLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_tab_background)
                binding.navigationBar.tabHome.tabLayout.background = null
                binding.navigationBar.tabVideos.tabLayout.background = null
                binding.navigationBar.tabSuggestions.tabLayout.background = null


            }

            2 -> {
                loadFragment(fragmentHome)
                binding.navigationBar.tabFuntime.tabLayout.background = null
                binding.navigationBar.tabChat.tabLayout.background = null
                binding.navigationBar.tabHome.tabLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_tab_background)
                binding.navigationBar.tabVideos.tabLayout.background = null
                binding.navigationBar.tabSuggestions.tabLayout.background = null





                binding.navigationBar.tabFuntime.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabChat.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabHome.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.white)
                );
                binding.navigationBar.tabVideos.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabSuggestions.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );


            }
            3 -> {
                loadFragment(fragmentReels)
                binding.navigationBar.tabFuntime.tabLayout.background = null
                binding.navigationBar.tabChat.tabLayout.background = null
                binding.navigationBar.tabHome.tabLayout.background = null
                binding.navigationBar.tabVideos.tabLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_tab_background)
                binding.navigationBar.tabSuggestions.tabLayout.background = null




                binding.navigationBar.tabFuntime.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabChat.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabHome.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabVideos.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.white)
                );
                binding.navigationBar.tabSuggestions.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );

            }
            4 -> {
                loadFragment(fragmentFriends)
                binding.navigationBar.tabFuntime.tabLayout.background = null
                binding.navigationBar.tabChat.tabLayout.background = null
                binding.navigationBar.tabHome.tabLayout.background = null
                binding.navigationBar.tabVideos.tabLayout.background = null
                binding.navigationBar.tabSuggestions.tabLayout.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_tab_background)


                binding.navigationBar.tabFuntime.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabChat.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabHome.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabVideos.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.colorPrimary)
                );
                binding.navigationBar.tabSuggestions.tabIcon.setColorFilter(
                    requireContext().getResources().getColor(R.color.white)
                );

            }
        }


    }

    private fun getCurrentVisibleFragment(): Fragment? {
        val navHostFragment = childFragmentManager.primaryNavigationFragment as NavHostFragment?
        val fragmentManager: FragmentManager = navHostFragment!!.childFragmentManager
        val fragment: Fragment = fragmentManager.getPrimaryNavigationFragment()!!
        return if (fragment is Fragment) {
            fragment
        } else null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {


            R.id.tabFuntime -> {
                Log.d("klajsdasd", ";aksldasd")
                setUpNavigationBar(0)
            }

            R.id.tabChat -> {
                setUpNavigationBar(1)
            }

            R.id.tabHome -> {
                setUpNavigationBar(2)
            }

            R.id.tabVideos -> {
                setUpNavigationBar(3)
            }

            R.id.tabSuggestions -> {
                setUpNavigationBar(4)
            }
        }
    }


    private fun loadFragment(fragment: Fragment) {

        val backStateName = fragment.javaClass.name
        val fragmentTag = backStateName
        val manager: FragmentManager = childFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 1)
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(binding.navHostContainer.id, fragment, fragmentTag)
            ft.commit()
        }
    }

    override fun onCLickOnMenuButton() {
        (requireActivity() as ActivityDashboardNew).toggleDrawer()
    }

    override fun onCLickOnProfileButton() {
//        startActivity(IntentHelper.getProfileScreen(this.requireActivity()))
        loadFragment(fragmentProfile)
    }

    override fun onScoll(toHide: Boolean) {
        Log.d("klajsdasd", toHide.toString())
        /*      if (toHide) {
             //     binding.navigationBar.root.setVisibility(View.GONE)
                  binding.navigationBar.root.animate().translationY((binding.navigationBar.root.getHeight() + 60).toFloat())
                      .setInterpolator(LinearInterpolator()).start()
              }else{
                  binding.navigationBar.root.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
               //   binding.navigationBar.root.setVisibility(View.VISIBLE)
              }*/
    }

    override fun onClickBack() {

    }


    public fun isBottomBarShowing(): Boolean {
        return binding.navigationBar.root.visibility == View.VISIBLE
    }


}