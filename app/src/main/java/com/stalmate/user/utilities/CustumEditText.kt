package com.stalmate.user.utilities

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

object CustumEditText {
  fun setup(textInputLayout:TextInputLayout, textInputEditText:EditText){
      textInputEditText.setOnFocusChangeListener { _, hasFocus->
          run {
              val color = if (hasFocus) Color.parseColor("#0F52BA") else Color.GRAY
              textInputEditText.compoundDrawableTintList=ColorStateList.valueOf(color)
          }

      }
  }
}