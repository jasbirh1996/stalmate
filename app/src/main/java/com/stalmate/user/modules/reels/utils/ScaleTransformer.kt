package com.stalmate.user.modules.reels.utils

import android.view.View
import com.stalmate.user.modules.reels.utils.GalleryLayoutManager.ItemTransformer

class ScaleTransformer : ItemTransformer {
    private val mOffset = 20
    override fun transformItem(layoutManager: GalleryLayoutManager?, item: View, fraction: Float) {
        item.setPivotX(item.getWidth() / 2.0f)
        item.setPivotY(item.getHeight() / 2.0f)
        val scale: Float

        scale = 1.5f - 0.5f * Math.abs(fraction)
        item.setScaleX(scale)
        item.setScaleY(scale)
        item.setTranslationX(mOffset * fraction)
    }
}