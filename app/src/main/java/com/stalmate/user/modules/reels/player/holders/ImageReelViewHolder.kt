package com.stalmate.user.modules.reels.player.holders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.tabs.TabLayout
import com.stalmate.user.databinding.HorizontalItemReelBinding
import com.stalmate.user.modules.reels.player.ImageAdapter.Companion.TOTAL_IMAGES


/**
 * Create By Neha Kushwah
 */
class ImageReelViewHolder(root: View) : ReelViewHolder(root) {
    lateinit var recyclerViewImages: RecyclerView;

    constructor(binding: HorizontalItemReelBinding) : this(binding.root) {
        recyclerViewImages =
            binding.recyclerViewImages;

        /** Keep the item center aligned**/
        val snapHelper: SnapHelper = PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewImages);


        /**
         * Add dots (fixed size for now)
         */
        for (i in 0 until TOTAL_IMAGES) {
            binding.dots.addTab(binding.dots.newTab())
        }

        recyclerViewImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val itemPosition: Int =
                    (recyclerViewImages.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                try {
                    val tab: TabLayout.Tab? = binding.dots.getTabAt(itemPosition)
                    if (tab != null) tab.select()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

}