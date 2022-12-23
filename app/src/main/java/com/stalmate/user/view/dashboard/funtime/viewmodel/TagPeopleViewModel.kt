package com.stalmate.user.view.dashboard.funtime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.otaliastudios.opengl.core.use
import com.slatmate.user.model.CommonModelResponse
import com.stalmate.user.model.Education
import com.stalmate.user.model.Profession
import com.stalmate.user.model.User
import java.io.Serializable


class TagPeopleViewModel(state: SavedStateHandle) : ViewModel() {
    private val state: SavedStateHandle
    var tagModelLiveData = MutableLiveData<TagModel>()
    var taggedModelObject: TagModel = TagModel(ArrayList(),ArrayList())
    fun addToList(user: User) {
        if (!taggedModelObject.taggedPeopleList.any {
                user.id== it.id
            }){
            taggedModelObject.taggedPeopleList.add(user)
            tagModelLiveData.postValue(taggedModelObject)
        }
    }


    fun addToSpecificList(user: User) {
        if (!taggedModelObject.specifFriendsList.any {
                user.id== it.id
            }){
            taggedModelObject.specifFriendsList.add(user)
            tagModelLiveData.postValue(taggedModelObject)
        }
    }

    fun clearList() {
        taggedModelObject.taggedPeopleList.clear()
        tagModelLiveData.postValue(taggedModelObject)
    }
    fun clearSpecificFriendList() {
        taggedModelObject.specifFriendsList.clear()
        tagModelLiveData.postValue(taggedModelObject)
    }



    fun removeFromList(user: User) {
        //  taggedPeopleList.add(user)
        taggedModelObject.taggedPeopleList.remove(user)
        tagModelLiveData.postValue(taggedModelObject)
    }

    fun removeSpecificFriendFromList(user: User) {
        //  taggedPeopleList.add(user)
        taggedModelObject.specifFriendsList.remove(user)
        tagModelLiveData.postValue(taggedModelObject)
    }


    fun getTaggedPeopleList(): MutableLiveData<TagModel> {
        return tagModelLiveData
    }

    fun getSpecifFriendList(): MutableLiveData<TagModel> {
        return tagModelLiveData
    }

    fun setPolicy(policy:String){
        taggedModelObject.policy=policy
        tagModelLiveData.postValue(taggedModelObject)
    }
    fun getPolicy(): MutableLiveData<TagModel> {
       return tagModelLiveData
    }



    init {
        this.state = state
        tagModelLiveData = state.getLiveData("Default Message")
    }
}


data class TagModel(
    var taggedPeopleList: ArrayList<User>,
    var specifFriendsList: ArrayList<User>,
    var policy: String="",
    ):Serializable




