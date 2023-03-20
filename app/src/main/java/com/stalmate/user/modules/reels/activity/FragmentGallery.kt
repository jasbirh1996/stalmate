package com.stalmate.user.modules.reels.activity


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stalmate.user.R
import com.stalmate.user.databinding.FragmentGalleryBinding
import com.stalmate.user.modules.reels.adapter.GalleryAdapter
import com.stalmate.user.modules.reels.adapter.GalleryItem

class FragmentGallery(
    var list: Array<Bitmap?>,
    var typeMedia: IntArray,
    var arrPath: Array<String?>
) : BottomSheetDialogFragment(), GalleryAdapter.Callbackk {
    private var galleryPickerListener: GalleryPickerListener? = null
    lateinit var binding: FragmentGalleryBinding

    interface GalleryPickerListener {
        fun onItemPicked(item: GalleryItem)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
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
        val contentView = View.inflate(context, R.layout.fragment_gallery, null)
        dialog.setContentView(contentView)
        binding = DataBindingUtil.bind<FragmentGalleryBinding>(contentView)!!
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }



        binding.txtClose.setOnClickListener {
            dismiss()
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        val gridLayoutManager = GridLayoutManager(activity, 3)
        binding.rvList.layoutManager = gridLayoutManager
        val emojiAdapter = GalleryAdapter(requireContext(), this)
        val data = ArrayList<GalleryItem>()
        for (i in list.indices) {
            val galleryItem = GalleryItem(image = list[i], typeMedia[i], arrPath[i]!!)
            data.add(galleryItem)
        }
        emojiAdapter.submitList(data)
        binding.rvList.adapter = emojiAdapter
        binding.rvList.setHasFixedSize(true)
        binding.rvList.setItemViewCacheSize(list.size)
    }

    fun setEmojiListener(galleryPickerListener: GalleryPickerListener?) {
        this.galleryPickerListener = galleryPickerListener
    }


    override fun onItemSelected(item: GalleryItem) {
        galleryPickerListener?.onItemPicked(item)
        dismiss()
    }
}