package com.stalmate.user.utilities

import android.os.Bundle
import android.view.View
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.PopupLayoutBinding

class CommonImageActivity : BaseActivity() {
    private lateinit var binding: PopupLayoutBinding

    override fun onClick(viewId: Int, view: View?) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = PopupLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        super.onCreate(savedInstanceState)
        showFullImage()
        binding.popUpImage.setOnClickListener {
            onBackPressed()
        }
        binding.layoutPopupImage.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showFullImage() {
        val img = intent.getSerializableExtra("picture")

        ImageLoaderHelperGlide.setGlide(
            this,
            binding.popUpImage,
            img.toString(),
            R.drawable.user_placeholder
        )
    }
}