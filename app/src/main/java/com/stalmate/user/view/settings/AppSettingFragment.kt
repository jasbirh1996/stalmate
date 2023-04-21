package com.stalmate.user.view.settings

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.stalmate.user.BuildConfig
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentAppSettingBinding
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.SpinnerUtil.setSpinner
import com.stalmate.user.view.authentication.ActivityAuthentication
import com.stalmate.user.view.dashboard.Chat.FragmentNotification
import com.stalmate.user.view.dashboard.funtime.ActivityReportListing
import com.stalmate.user.view.dashboard.funtime.ActivityReportUser
import ly.img.android.pesdk.kotlin_extension.IntentHelper
import ly.img.android.pesdk.ui.utils.IntentUtils


class AppSettingFragment : BaseFragment() {
    private var _binding: FragmentAppSettingBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_app_setting, container, false)

        _binding = FragmentAppSettingBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initControl()
        initViews()
    }

    private fun initControl() {
        binding.toolbar.topAppBar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.tvhead.text = "General Settings"
        binding.constLogout.setOnClickListener {
            PrefManager.getInstance(this.requireContext())?.keyIsLoggedIn = false
            requireActivity().startActivity(
                Intent(
                    context,
                    ActivityAuthentication::class.java
                ).putExtra("screen", "login")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            (context as ActivitySettings).finishAffinity()
        }
        networkViewModel.languageLiveData(HashMap())
        networkViewModel.languageLiveData.observe(requireActivity()) {
            it.let {
                binding.spinnerLanguage.setSpinner(
                    listFromServer = ArrayList(it?.results?.map { it.name }),
                    listFromResources = 0,
                    setSelection = (it?.results?.map { it.name }?.indexOf(
                        PrefManager.getInstance(App.getInstance())?.getStringValue(key = "language")
                    ) ?: 0),
                    onItemSelectedListener = { pos ->
                        PrefManager.getInstance(App.getInstance())
                            ?.setStringValue(
                                key = "language",
                                value = ArrayList(it?.results?.map { it.name })[pos]
                            )
                        networkViewModel.updateLanguageAndCountry(
                            access_token = prefManager?.access_token.toString(),
                            country = PrefManager.getInstance(App.getInstance())
                                ?.getStringValue(key = "country").toString(),
                            language = PrefManager.getInstance(App.getInstance())
                                ?.getStringValue(key = "language").toString()
                        )
                    })
            }
        }
        binding.tvCountryAppSeting.setDefaultCountryUsingNameCode(
            PrefManager.getInstance(App.getInstance())?.getStringValue(key = "country").toString()
        )
        binding.tvCountryAppSeting.resetToDefaultCountry()
        binding.tvCountryAppSeting.setOnCountryChangeListener {
            PrefManager.getInstance(App.getInstance())
                ?.setStringValue(
                    key = "country",
                    value = binding.tvCountryAppSeting.selectedCountryNameCode
                )
            networkViewModel.updateLanguageAndCountry(
                access_token = prefManager?.access_token.toString(),
                country = PrefManager.getInstance(App.getInstance())
                    ?.getStringValue(key = "country").toString(),
                language = PrefManager.getInstance(App.getInstance())
                    ?.getStringValue(key = "language").toString()
            )
        }
        networkViewModel.updateLanguageAndCountryResponse.observe(this.viewLifecycleOwner) {
            Toast.makeText(this.requireContext(), "Updated!", Toast.LENGTH_SHORT).show()
        }
        binding.constDeleteMyAccount.setOnClickListener {
            findNavController().navigate(R.id.action_appSettingFragment_to_deleteMyAccountFragment)
        }
        binding.constCountry.setOnClickListener {
            binding.tvCountryAppSeting.launchCountrySelectionDialog()
        }
    }

    private fun initViews() {
        Glide.with(requireActivity())
            .load(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.profile_img1)
            .placeholder(R.drawable.user_placeholder).circleCrop().into(binding.userProfileImage)
        binding.tvUserName.setText(
            PrefManager.getInstance(requireContext())?.userProfileDetail?.results?.first_name + PrefManager.getInstance(
                requireContext()
            )?.userProfileDetail?.results?.last_name
        )
        binding.tvAbout.setText(PrefManager.getInstance(requireContext())!!.userProfileDetail.results.city)

        val list = ArrayList<AppSettingMenuModel>()
        list.add(AppSettingMenuModel("Version"))
        list.add(AppSettingMenuModel("Rate App"))
        list.add(AppSettingMenuModel("Report Problem"))
        list.add(AppSettingMenuModel("Notification"))
        list.add(AppSettingMenuModel("Share App"))
        binding.rvListAppSeting.adapter = AppSettingAdapter(list) {
            when (it) {
                "Version" -> {}
                "Rate App" -> {
                    rateApp(this.requireContext())
                }
                "Report Problem" -> {
                    startActivity(Intent(this.requireContext(), ActivityReportListing::class.java))
                }
                "Notification" -> {
                    startActivity(Intent(requireActivity(), FragmentNotification::class.java))
                }
                "Share App" -> {
                    shareApp(this.requireContext())
                }
            }
        }
    }

    fun rateApp(context: Context) {
        val goToMarket = rateIntentForUrl("market://details", context)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                rateIntentForUrl("http://play.google.com/store/apps/details", context)
            )
        }
    }

    private fun rateIntentForUrl(url: String, context: Context): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(String.format("%s?id=%s", url, context.packageName))
        )

        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK

        if (Build.VERSION.SDK_INT >= 21) {
            flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            //noinspection deprecation
            flags = flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        }
        intent.addFlags(flags)
        return intent
    }

    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                shareMessage + "https://play.google.com/store/apps/details?_id=" + BuildConfig.APPLICATION_ID
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share Stalmate with your friends"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareSocial(context: Context, propertyId: String, propertyType: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var shareMessage = "Let me recommend you this property\n"
            shareMessage += "http://www.status.com/properties?id=${propertyId}&type=${propertyType}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, "Share Property"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}