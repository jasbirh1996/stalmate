package com.stalmate.user.view.dashboard.funtime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.otaliastudios.opengl.core.use
import com.slatmate.user.model.CommonModelResponse
import com.stalmate.user.model.User



class TagPeopleViewModel(state: SavedStateHandle) : ViewModel() {
    private val state: SavedStateHandle
    var taggedPeopleLiveData = MutableLiveData<ArrayList<User>>()
    var taggedPeopleList: ArrayList<User> = ArrayList<User>()
    fun addToList(user: User) {
      //  taggedPeopleList.add(user)
        taggedPeopleList.add(user)
        taggedPeopleLiveData.postValue(taggedPeopleList)
    }

    fun removeFromList(user: User) {
        //  taggedPeopleList.add(user)
        taggedPeopleList.remove(user)
        taggedPeopleLiveData.postValue(taggedPeopleList)
    }


    fun getTaggedPeopleList(): MutableLiveData<ArrayList<User>> {
        return taggedPeopleLiveData
    }


    init {
        this.state = state
        taggedPeopleLiveData = state.getLiveData("Default Message")
    }
}
