package com.stalmate.user.view.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.FragmentProfilePrivacySettingsBinding
import com.stalmate.user.model.PrivacyUpdateResponse
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.ActivitySettings
import com.stalmate.user.utilities.ErrorUtil
import com.stalmate.user.utilities.SpinnerUtil.setSpinner
import com.stalmate.user.view.profile.BlockedUserAdapter

class ActivityPrivacy : BaseActivity() {
    private lateinit var binding: FragmentProfilePrivacySettingsBinding
    private var privacyUpdateResponse: PrivacyUpdateResponse? = PrivacyUpdateResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentProfilePrivacySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        super.onCreate(savedInstanceState)
        listener()
    }

    override fun onResume() {
        super.onResume()
        showLoader()
        networkViewModel.getPrivacyResponse(prefManager?.access_token.toString())
    }

    private fun listener() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
        networkViewModel.mThrowable.observe(this) {
            it?.let { it1 ->
                dismissLoader()
                Snackbar.make(binding.llPrivacy, it, Snackbar.LENGTH_SHORT).show()
            }
            setPrivacy()
        }
        networkViewModel.privacyGetResponse.observe(this) {
            dismissLoader()
            if (it?.reponse != null) {
                privacyUpdateResponse?.reponse = it.reponse
            }
            setPrivacy()
        }
        networkViewModel.privacyUpdateResponse.observe(this) {
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setPrivacy() {
        privacyUpdateResponse?.reponse?.let {
            binding.apply {
                toggleFindMe.isChecked = (it.allow_others_to_find_me)
                toggleFindMe.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) {
                        it.allow_others_to_find_me = isChecked
                        updatePrivacyResponse()
                    }
                }
                toggleReadReceipts.isChecked = (it.read_receipts)
                toggleReadReceipts.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) {
                        it.read_receipts = isChecked
                        updatePrivacyResponse()
                    }
                }

                rvBlockList.adapter = BlockedUserAdapter(
                    networkViewModel,
                    this@ActivityPrivacy,
                    object : BlockedUserAdapter.Callback {
                        override fun onListEmpty() {
                            llBlockList.visibility = View.GONE
                        }

                        override fun onItemRemove() {

                        }
                    }, prefManager?.access_token.toString()
                )
                if (it.block_contact.isNullOrEmpty()) {
                    llBlockList.visibility = View.GONE
                } else {
                    llBlockList.visibility = View.VISIBLE
                }
                val blockUserList = ArrayList<User>()
                repeat((it.block_contact?.size ?: 0)) { _index ->
                    blockUserList.add(
                        User(
                            id = it.block_contact?.get(_index)?._id.toString(),
                            first_name = it.block_contact?.get(_index)?.first_name.toString(),
                            last_name = it.block_contact?.get(_index)?.last_name.toString(),
                            img = it.block_contact?.get(_index)?.profile_img_1.toString()
                        )
                    )
                }
                (rvBlockList.adapter as BlockedUserAdapter).submitList(blockUserList)

                spinnerProfile.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.profile
                ) { position ->
                    it.profile = position
                    updatePrivacyResponse()
                }
                spinnerLastSeen.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.last_seen
                ) { position ->
                    it.last_seen = position
                    updatePrivacyResponse()
                }
                spinnerProfilePhoto.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.prfile_photo
                ) { position ->
                    it.prfile_photo = position
                    updatePrivacyResponse()
                }
                spinnerAbout.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.about
                ) { position ->
                    it.about = position
                    updatePrivacyResponse()
                }
                spinnerStory.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.story
                ) { position ->
                    it.story = position
                    updatePrivacyResponse()
                }
                spinnerGroup.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.groups
                ) { position ->
                    it.groups = position
                    updatePrivacyResponse()
                }
                spinnerLikePost.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_like_my_post
                ) { position ->
                    it.who_can_like_my_post = position
                    updatePrivacyResponse()
                }
                spinnerPostComments.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_post_comment
                ) { position ->
                    it.who_can_post_comment = position
                    updatePrivacyResponse()
                }
                spinnerSendMeMessage.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_send_me_message
                ) { position ->
                    it.who_can_send_me_message = position
                    updatePrivacyResponse()
                }
                spinnerSeeMyFuturePost.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_see_my_future_post
                ) { position ->
                    it.who_can_see_my_future_post = position
                    updatePrivacyResponse()
                }
                spinnerSeePeoplePageList.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_see_people_page_list
                ) { position ->
                    it.who_can_see_people_page_list = position
                    updatePrivacyResponse()
                }
                spinnerSendMeFriendRequest.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_send_you_friend_request
                ) { position ->
                    it.who_can_send_you_friend_request = position
                    updatePrivacyResponse()
                }
                spinnerLookUpEmailAddress.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_see_email_address
                ) { position ->
                    it.who_can_see_email_address = position
                    updatePrivacyResponse()
                }
                spinnerLookUpPhoneNumber.setSpinner(
                    listFromResources = R.array.profile,
                    setSelection = it.who_can_see_phone_number
                ) { position ->
                    it.who_can_see_phone_number = position
                    updatePrivacyResponse()
                }
            }
        }
    }

    private fun updatePrivacyResponse() {
        networkViewModel.updatePrivacyResponse(
            access_token = prefManager?.access_token.toString(),
            allow_others_to_find_me = (privacyUpdateResponse?.reponse?.allow_others_to_find_me
                ?: false),
            profile = (privacyUpdateResponse?.reponse?.profile ?: 0),
            last_seen = (privacyUpdateResponse?.reponse?.last_seen ?: 0),
            prfile_photo = (privacyUpdateResponse?.reponse?.prfile_photo ?: 0),
            about = (privacyUpdateResponse?.reponse?.about ?: 0),
            read_receipts = (privacyUpdateResponse?.reponse?.read_receipts ?: false),
            story = (privacyUpdateResponse?.reponse?.story ?: 0),
            groups = (privacyUpdateResponse?.reponse?.groups ?: 0),
            block_contact = Gson().toJson((privacyUpdateResponse?.reponse?.block_contact?.map { it._id }
                ?: arrayListOf<String>())),
            who_can_like_my_post = (privacyUpdateResponse?.reponse?.who_can_like_my_post ?: 0),
            who_can_post_comment = (privacyUpdateResponse?.reponse?.who_can_post_comment ?: 0),
            who_can_send_me_message = (privacyUpdateResponse?.reponse?.who_can_send_me_message
                ?: 0),
            who_can_see_my_future_post = (privacyUpdateResponse?.reponse?.who_can_see_my_future_post
                ?: 0),
            who_can_see_people_page_list = (privacyUpdateResponse?.reponse?.who_can_see_people_page_list
                ?: 0),
            who_can_send_you_friend_request = (privacyUpdateResponse?.reponse?.who_can_send_you_friend_request
                ?: 0),
            who_can_see_email_address = (privacyUpdateResponse?.reponse?.who_can_see_email_address
                ?: 0),
            who_can_see_phone_number = (privacyUpdateResponse?.reponse?.who_can_see_phone_number
                ?: 0)
        )
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onClick(viewId: Int, view: View?) {
        TODO("Not yet implemented")
    }
}