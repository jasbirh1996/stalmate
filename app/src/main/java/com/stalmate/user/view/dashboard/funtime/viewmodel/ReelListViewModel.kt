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
import com.stalmate.user.view.dashboard.funtime.ResultFuntime
import java.io.Serializable


class ReelListViewModel(state: SavedStateHandle) : ViewModel() {
    private val state: SavedStateHandle
    var reelListLiveData = MutableLiveData<FuntimeModel>()
    var reelListObject: FuntimeModel = FuntimeModel(ArrayList())
    fun addToList(user: ResultFuntime) {
        if (!reelListObject.reelList.any {
                user.id== it.id
            }){
            reelListObject.reelList.add(user)
            reelListLiveData.postValue(reelListObject)
        }
    }

    

    fun clearList() {
        reelListObject.reelList.clear()
        reelListLiveData.postValue(reelListObject)
    }




    fun removeFromList(user: ResultFuntime) {
        //  reelList.add(user)
        reelListObject.reelList.remove(user)
        reelListLiveData.postValue(reelListObject)
    }




    fun getreelList(): MutableLiveData<FuntimeModel> {
        return reelListLiveData
    }

    



    init {
        this.state = state
        reelListLiveData = state.getLiveData("Default Message")
    }
}


data class FuntimeModel(
    var reelList: ArrayList<ResultFuntime>,
):Serializable



