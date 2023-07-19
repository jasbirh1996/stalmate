package com.stalmate.user.view.dashboard.funtime

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.databinding.FragmentBottomDialogReelsMenuBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.viewmodel.AppViewModel


class BottomDialogFragmentMenuReels(
    var isOtherUserReel: Boolean, var networkViewModel: AppViewModel, var funtime: ResultFuntime,
    var callBack: Callback
) :
    BottomSheetDialogFragment() {
    lateinit var binding: FragmentBottomDialogReelsMenuBinding
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
        val contentView = View.inflate(context, R.layout.fragment_bottom_dialog_reels_menu, null)
        binding = DataBindingUtil.bind<FragmentBottomDialogReelsMenuBinding>(contentView)!!
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))


        if (isOtherUserReel) {
            binding.layoutOtherUserMenu.visibility = View.VISIBLE
            binding.layoutOwnMenu.visibility = View.GONE
        } else {
            binding.layoutOtherUserMenu.visibility = View.GONE
            binding.layoutOwnMenu.visibility = View.VISIBLE
        }


        binding.layoutButtonBlock.setOnClickListener {
            dismiss()
            callBack.onClickOnMenu(4)
        }

        binding.layoutButtonEdit.setOnClickListener {
            dismiss()
            callBack.onClickOnMenu(1)
        }


        binding.layoutButtonDelete.setOnClickListener {
            dismiss()
            callBack.onClickOnMenu(2)
        }

        binding.layoutButtonReport.setOnClickListener {
            dismiss()
            callBack.onClickOnMenu(3)
        }

        binding.layoutButtonSave.setOnClickListener { //save
            saveUnsaveFuntime(funtime)
        }

        binding.layoutFollowAccount.setOnClickListener { //follow
            followUnfollowUer(funtime)
        }

        binding.layoutShareAspost.setOnClickListener {  //shareaspost
            dismiss()
            callBack.onClickOnMenu(7)
        }


        if (funtime.isSave == "Yes") {
            binding.ivSave.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.fun_unsave
                )
            )
            binding.tvSave.text = "Unsave"
        } else {
            binding.ivSave.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.fun_save
                )
            )
            binding.tvSave.text = "Save"
        }

        if (funtime.isFollowing == "Yes") {
            binding.ivFollow.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.fun_unfollow
                )
            )
            binding.tvFollow.text = "Unfollow Account"
        } else {
            binding.ivFollow.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.fun_follow
                )
            )
            binding.tvFollow.text = "Follow Account"
        }


    }


    private fun saveUnsaveFuntime(funtime: ResultFuntime) {
        var hashmap = HashMap<String, String>()
        hashmap.put("funtime_id", funtime.id)
        networkViewModel.saveUnsavePost(hashmap).observe(this) {
            it.let {
                if (it!!.status!!) {
                    if (it.message == "Remove") {
                        binding.ivSave.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.fun_save
                            )
                        )
                        binding.tvSave.text = "Save"
                    } else {
                        binding.ivSave.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.fun_unsave
                            )
                        )
                        binding.tvSave.text = "Unsave"
                    }

                }
            }
        }
    }

    private fun followUnfollowUer(funtime: ResultFuntime) {
        var hashmap = HashMap<String, String>()
        hashmap.put("id_user", funtime.user_id!!)
        networkViewModel.followUnfollowUser(
            PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token.toString(),
            hashmap
        ).observe(this) {
            it.let {
                if (it!!.status) {

                    if (it.message == "Add request successfully") {
                        binding.ivFollow.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.fun_unfollow
                            )
                        )
                        binding.tvFollow.text = "Unfollow Account"
                    } else {
                        binding.ivFollow.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.fun_follow
                            )
                        )
                        binding.tvFollow.text = "Follow Account"
                    }
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    public interface Callback {
        fun onClickOnMenu(typeCode: Int)
    }


}