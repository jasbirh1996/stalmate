package com.stalmate.user.utilities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet

import android.widget.SeekBar





@SuppressLint("AppCompatCustomView")
class StepSeekbar(context: Context?, attrs: AttributeSet?, var mSteps: Int) : SeekBar(context, attrs) {
    var mCurrentStep = 0
    var mStepWidth: Int
    
    fun nextStep() {
        // Animate progress to next step
        val animator = ObjectAnimator.ofInt(
            this,
            "progress",
            mStepWidth * mCurrentStep,
            mStepWidth * ++mCurrentStep
        )
        animator.setDuration(2000)
        animator.start()
    }

    init {
        mStepWidth = 100 / mSteps
    }
}