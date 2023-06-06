package com.stalmate.user.modules.reels.adapter



import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.intentHelper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.commonadapters.ActualTaggedUserAdapter
import com.stalmate.user.databinding.FragmentTaggedUsersListBinding
import com.stalmate.user.view.dashboard.funtime.TaggedUser


class FragmentBSTaggedUsers(var users:ArrayList<TaggedUser>) :
    BottomSheetDialogFragment(), ActualTaggedUserAdapter.Callback {
    lateinit var binding: FragmentTaggedUsersListBinding
    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_tagged_users_list, null)
        binding = DataBindingUtil.bind<FragmentTaggedUsersListBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        var actualTaggedUseAdapter= ActualTaggedUserAdapter(requireContext(),this)
        actualTaggedUseAdapter.addToList(users)
        binding.rvFriends.adapter=actualTaggedUseAdapter
        binding.rvFriends.layoutManager=LinearLayoutManager(requireContext())


        binding.ivClear.setOnClickListener {
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }




    override fun onUserSelected(user: TaggedUser) {

        startActivity(IntentHelper.getOtherUserProfileScreen(requireContext())!!.putExtra("id",user._id))
        dismiss()
    }




}