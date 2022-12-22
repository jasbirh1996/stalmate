package com.stalmate.user.view.dashboard.funtime.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stalmate.user.model.Comment

class CommentViewModel: ViewModel() {

    private val _commentList = MutableLiveData(mutableListOf<Comment>())
    val commentList: LiveData<MutableList<Comment>>
        get() = _commentList

    fun addComment(shortComment: Comment) {
        val newList = _commentList.value ?: mutableListOf()
        if(shortComment.parentId != null) {
            newList.find { it._id == shortComment.parentId }?.replies?.add(shortComment)
        } else {
            newList.add(shortComment)
        }
        _commentList.value = newList
    }

    fun likeComment(shortComment: Comment) {
        val newList = _commentList.value ?: mutableListOf()
        newList

        if(shortComment.parentId != null) {
            newList.find { it._id == shortComment.parentId }?.isLiked=="Yes"
        } else {
            newList.add(shortComment)
        }
        _commentList.value = newList
    }
}