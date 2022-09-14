package com.stalmate.user.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.slatmate.user.model.CommonModelResponse
import com.stalmate.user.base.App
import com.stalmate.user.model.*
import com.stalmate.user.networking.ApiInterface
import com.stalmate.user.view.photoalbum.ModelAlbumCreateResponse
import com.stalmate.user.view.photoalbum.ModelPhotoResponse
import com.stalmate.user.view.singlesearch.ModelSearch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Part

open class AppViewModel : ViewModel() {

    var apiInterface = ApiInterface.init(App.getInstance())

    fun <T : Any> getResult(data: MutableLiveData<T?>, call: Call<T>) {
        call.enqueue(object : retrofit2.Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                Log.d("asdasdas", "spfoksdf")
                data.value = response.body()
                Log.d("akjsdasd", Gson().toJson(response.body()))

            }

            override fun onFailure(call: Call<T?>, t: Throwable) {
                data.value = null
            }
        })
    }




    var feedLiveData: LiveData<ModelFeed?> = MutableLiveData<ModelFeed?>()
    fun getFeedList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFeed?>()
        feedLiveData = temp
        getResult(temp, apiInterface.getFeedList())
    }


    var languageLiveData: LiveData<ModelLanguageResponse?> =
        MutableLiveData<ModelLanguageResponse?>()

    fun languageLiveData(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLanguageResponse?>()
        languageLiveData = temp
        getResult(temp, apiInterface.getLanguageList())
    }

    var searchLiveData: LiveData<ModelSearch?> = MutableLiveData<ModelSearch?>()

    fun searchLiveData(map: HashMap<String, String>,  search: String) {
        val temp = MutableLiveData<ModelSearch?>()
        searchLiveData = temp
        getResult(temp, apiInterface.setSearch(search))
    }
    var searchBranchLiveData: LiveData<ModelSearch?> = MutableLiveData<ModelSearch?>()

    fun searchBranchLiveData(map: HashMap<String, String>,  search: String) {
        val temp = MutableLiveData<ModelSearch?>()
        searchBranchLiveData = temp
        getResult(temp, apiInterface.setSearchBranch(search))
    }

    var photoLiveData: LiveData<ModelPhotoResponse?> = MutableLiveData<ModelPhotoResponse?>()
    fun photoLiveData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelPhotoResponse?>()
        photoLiveData = temp
        getResult(temp, apiInterface.getPhotoList(map))
    }


    var friendLiveData: MutableLiveData<ModelFriend?> = MutableLiveData<ModelFriend?>()
    fun getFriendList(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFriend?>()
        friendLiveData = temp
        getResult(temp, apiInterface.getFriendList(map))
    }


    var updateFriendRequestLiveData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun updateFriendRequest(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        updateFriendRequestLiveData = temp
        getResult(temp, apiInterface.updateFriendRequest(map))
    }

    var removeUserFromSuggestionLiveData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun removeUserFromSuggestion(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        removeUserFromSuggestionLiveData = temp
        getResult(temp, apiInterface.removeUserFromSuggestions(map))
    }




    var sendFriendRequestLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun sendFriendRequest(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        sendFriendRequestLiveData = temp
        getResult(temp, apiInterface.sendFriendRequest(map))
    }

    var followRequestLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun sendFollowRequest(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        followRequestLiveData = temp
        getResult(temp, apiInterface.requestBeFollower(map))
    }

    var categoryLiveData: LiveData<ModelCategory?> = MutableLiveData<ModelCategory?>()
    fun getCategoryList(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelCategory?>()
        categoryLiveData = temp
        getResult(temp, apiInterface.getCategorList())
    }

    var registerData: LiveData<ModelLoginResponse?> = MutableLiveData<ModelLoginResponse?>()
    fun registration(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLoginResponse?>()
        registerData = temp
        getResult(temp, apiInterface.setSignupDetails(map))

    }


    var loginData: LiveData<ModelLoginResponse?> = MutableLiveData<ModelLoginResponse?>()
    fun login(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLoginResponse?>()
        loginData = temp
        getResult(temp, apiInterface.setLoginDetails(map))
    }

    var educationData: LiveData<ModelCommonAddEducationAndProfessionResponse?> = MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()
    fun educationData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()
        educationData = temp
        getResult(temp, apiInterface.setEducationAddDetails(map))
    }

    var addUpdateProfessionLiveData: LiveData<ModelCommonAddEducationAndProfessionResponse?> = MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()
    fun addUpdateProfessionData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()
        addUpdateProfessionLiveData = temp
        getResult(temp, apiInterface.setProfessionAddDetails(map))
    }

    var createAlbumData: LiveData<ModelAlbumCreateResponse?> = MutableLiveData<ModelAlbumCreateResponse?>()
    fun createAlbum(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelAlbumCreateResponse?>()
        createAlbumData = temp
        getResult(temp, apiInterface.setCreateAlbumDetails(map))
    }




    var otpVerifyData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun otpVerify(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        otpVerifyData = temp
        getResult(temp, apiInterface.setOtpVerify(map))
    }
    var blockListLiveData: LiveData<ModelBlockedUser?> = MutableLiveData<ModelBlockedUser?>()
    fun getBlockList(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelBlockedUser?>()
        blockListLiveData = temp
        getResult(temp, apiInterface.getBlockedList(map))
    }






    var otpVerifyRegistarionData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()

    fun otpVerifyRegistration(map: HashMap<String, String>, email: String, otp: String) {
        val temp = MutableLiveData<CommonModelResponse?>()
        otpVerifyRegistarionData = temp
        getResult(temp, apiInterface.setOtpVerifyRegistration(email, otp))
    }

    var profileLiveData: MutableLiveData<ModelUser?> = MutableLiveData<ModelUser?>()
    fun getProfileData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelUser?>()
        profileLiveData = temp
        getResult(temp, apiInterface.setProfileDetails())
    }


    var otherUserProfileLiveData: MutableLiveData<ModelUser?> = MutableLiveData<ModelUser?>()
    fun getOtherUserProfileData(map: HashMap<String, String>, user_id: String) {
        val temp = MutableLiveData<ModelUser?>()
        otherUserProfileLiveData = temp
        getResult(temp, apiInterface.getOtherUserProfileDetails(user_id))
    }








    var UpdateProfileLiveData: LiveData<CommonModelResponse?> =

        MutableLiveData<CommonModelResponse?>()











    var globalSearchLiveData: MutableLiveData<ModelGlobalSearch?> = MutableLiveData<ModelGlobalSearch?>()
    fun getGlobalSearch(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelGlobalSearch?>()
        globalSearchLiveData = temp
        getResult(temp, apiInterface.getGlobalSearch(map))
    }











    fun etsProfileApi(
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("about") about: RequestBody,
       /* @Part("number") number: RequestBody,*/
        @Part("dob") dob: RequestBody,
        @Part("marital_status") marital_status: RequestBody,
        @Part("home_town") home_town: RequestBody,
        @Part("city") city: RequestBody,
        @Part("url") url: RequestBody,
        @Part("company") company: RequestBody,
        @Part("gender") gender: RequestBody,


        ) {
        val temp = MutableLiveData<CommonModelResponse?>()
        UpdateProfileLiveData = temp

        getResult(
            temp, apiInterface.updateUserProfile(
                first_name,
                last_name,
                about,
                /*number,*/
                dob,
                marital_status,
                url,
                company,
                gender,
                city,
                home_town,
                )
        )

    }


    fun etsProfileApi(
        @Part file_Profile_Image: MultipartBody.Part? = null,
    ) {
        val temp = MutableLiveData<CommonModelResponse?>()
        UpdateProfileLiveData = temp

        getResult(
            temp, apiInterface.updateUserProfileImage(
                file_Profile_Image!!
            )
        )
    }


    var blockData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun block(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        blockData = temp
        getResult(temp, apiInterface.setBlock(map))
    }


    var albumLiveData: LiveData<ModelAlbumsResponse?> = MutableLiveData<ModelAlbumsResponse?>()

    fun albumLiveDatas(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelAlbumsResponse?>()
        albumLiveData = temp
        getResult(temp, apiInterface.getAlbumList())
    }



}


/* open fun etProfileApi(
   first_name: String?,
   last_name: String?,
   email: String?,
   dob: String?,
   about: String?,
   home_town: String?,
   city: String?,
   number: String?,
   marital_status: String?,
   url: String?,
   company: String?,
   gender: String?,
   file_Profile_Image : File,
   file_Cover_image :  File ,
   callback: Callback<CommonModelResponse>?
) {
   val firstname = RequestBody.create("text/plain".toMediaTypeOrNull(), first_name!!)
   val lastname = RequestBody.create("text/plain".toMediaTypeOrNull(), last_name!!)
   val dobs = RequestBody.create("text/plain".toMediaTypeOrNull(), dob!!)
   val about = RequestBody.create("text/plain".toMediaTypeOrNull(), about!!)
   val homeTown = RequestBody.create("text/plain".toMediaTypeOrNull(), home_town!!)
   val citys = RequestBody.create("text/plain".toMediaTypeOrNull(), city!!)
   val numbers = RequestBody.create("text/plain".toMediaTypeOrNull(), number!!)
   val marriageStatus = RequestBody.create("text/plain".toMediaTypeOrNull(), marital_status!!)
   val urls = RequestBody.create("text/plain".toMediaTypeOrNull(), url!!)
   val companys = RequestBody.create("text/plain".toMediaTypeOrNull(), company!!)
   val genders = RequestBody.create("text/plain".toMediaTypeOrNull(), gender!!)
   val thumbnailBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file_Profile_Image)
   val thumbnailBody1: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file_Cover_image)
   val profile_image1: MultipartBody.Part? = createFormData("image", file_Profile_Image.getName(), thumbnailBody)
   val cover_image1: MultipartBody.Part? = createFormData("image", file_Cover_image.getName(), thumbnailBody1)
   val temp = MutableLiveData<CommonModelResponse?>()
   UpdateProfileLiveData = temp
   val call: Call<CommonModelResponse> =  apiInterface.updateUserProfile(
       firstname, lastname, about, numbers, dobs, marriageStatus, urls, companys, genders,
       citys, homeTown,profile_image1,cover_image1)
   call.enqueue(callback)
}*/

}

 */