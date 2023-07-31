package com.stalmate.user.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.slatmate.user.model.CommonModelResponse
import com.slatmate.user.model.FunTimeAddResponse
import com.slatmate.user.model.ProfileImagesUpdated
import com.stalmate.user.base.App
import com.stalmate.user.model.*
import com.stalmate.user.networking.ApiInterface
import com.stalmate.user.utilities.ErrorBean
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.view.dashboard.Friend.categorymodel.AddCategoryModel
import com.stalmate.user.view.dashboard.Friend.categorymodel.ModelCategoryResponse
import com.stalmate.user.view.dashboard.funtime.ModelFuntimeLikeResponse
import com.stalmate.user.view.dashboard.funtime.ModelFuntimeResponse
import com.stalmate.user.view.dashboard.funtime.ModelMusicListResponse
import com.stalmate.user.view.photoalbum.ModelAlbumCreateResponse
import com.stalmate.user.view.photoalbum.ModelPhotoResponse
import com.stalmate.user.view.photoalbum.imageshowindex.ModelPhotoIndexDataResponse
import com.stalmate.user.view.singlesearch.ModelSearch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Part

open class AppViewModel : ViewModel() {

    var apiInterface = ApiInterface.init(App.getInstance())
    val mThrowable = MutableLiveData<String?>()
    fun <T : Any> getResult(data: MutableLiveData<T?>, call: Call<T>) {
        call.enqueue(object : retrofit2.Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                if (response.code() in 200..299) {
                    data.value = response.body()
                } else {
                    mThrowable.value = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorBean::class.java
                    ).message
                }
            }

            override fun onFailure(call: Call<T?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    var feedLiveData: LiveData<ModelFeed?> = MutableLiveData<ModelFeed?>()
    fun getFeedList(token: String = "", map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFeed?>()
        feedLiveData = temp
        getResult(temp, apiInterface.getFeedList(access_token = token))
    }

    var languageLiveData: LiveData<ModelLanguageResponse?> =
        MutableLiveData<ModelLanguageResponse?>()

    fun languageLiveData(map: HashMap<String, String>, access_token: String = "") {
        val temp = MutableLiveData<ModelLanguageResponse?>()
        languageLiveData = temp
        getResult(temp, apiInterface.getLanguageList(access_token = access_token))
    }

    var funtimeLiveData: LiveData<ModelFuntimeResponse?> = MutableLiveData<ModelFuntimeResponse?>()

    fun funtimeLiveData(access_token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFuntimeResponse?>()
        funtimeLiveData = temp
        getResult(temp, apiInterface.getFuntimeList(access_token, map))
    }

    var myfuntimeLiveData: LiveData<ModelFuntimeResponse?> =
        MutableLiveData<ModelFuntimeResponse?>()

    fun myfuntimeLiveData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFuntimeResponse?>()
        myfuntimeLiveData = temp
        getResult(temp, apiInterface.getMyFuntimeList(map))
    }


    var createRoomIdLiveData: LiveData<ModelRoom?> = MutableLiveData<ModelRoom?>()

    fun createroomId(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelRoom?>()
        createRoomIdLiveData = temp
        getResult(temp, apiInterface.createroomId(map))
    }


/*
    var funtimeDetailListLiveData: LiveData<ModelFuntimeResponse?> = MutableLiveData<ModelFuntimeResponse?>()

    fun funtimeDetailListLiveData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFuntimeResponse?>()
        funtimeDetailListLiveData = temp
        getResult(temp, apiInterface.getFuntimeDetailListList(map))
    }
*/

    var funtimeLiveLikeUnlikeData: LiveData<ModelFuntimeLikeResponse?> =
        MutableLiveData<ModelFuntimeLikeResponse?>()

    fun funtimeLiveLikeUnlikeData(access_token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFuntimeLikeResponse?>()
        funtimeLiveLikeUnlikeData = temp
        getResult(temp, apiInterface.getFuntimeLikeUnlike(access_token, access_token, map))
    }


    fun saveUnsavePost(map: HashMap<String, String>): LiveData<CommonModelResponse?> {
        val temp = MutableLiveData<CommonModelResponse?>()
        var saveLiveDAta: LiveData<CommonModelResponse?>
        saveLiveDAta = temp
        getResult(temp, apiInterface.saveUnsaveFuntime(map))
        return saveLiveDAta
    }


    fun followUnfollowUser(
        access_token: String,
        map: HashMap<String, String>
    ): LiveData<ModelSuccess?> {
        val temp = MutableLiveData<ModelSuccess?>()
        var saveLiveDAta: LiveData<ModelSuccess?>
        saveLiveDAta = temp
        getResult(temp, apiInterface.requestBeFollower(access_token, access_token, map))
        return saveLiveDAta
    }


    var funtimeMusicLiveData: LiveData<ModelMusicListResponse?> =
        MutableLiveData<ModelMusicListResponse?>()

    fun funtimeMusicLiveData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelMusicListResponse?>()
        funtimeMusicLiveData = temp
        getResult(temp, apiInterface.getFuntimeMusicList(map))
    }


    var categoryFriendLiveData: MutableLiveData<ModelCategoryResponse?> =
        MutableLiveData<ModelCategoryResponse?>()

    fun categoryFriendLiveData() {
        val temp = MutableLiveData<ModelCategoryResponse?>()
        categoryFriendLiveData = temp
        getResult(temp, apiInterface.getCategoryList())
    }

    var reelVideosByAudioLiveData: MutableLiveData<ModelFuntimeResponse?> =
        MutableLiveData<ModelFuntimeResponse?>()

    fun get_song_funtime_list(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFuntimeResponse?>()
        reelVideosByAudioLiveData = temp
        getResult(temp, apiInterface.get_song_funtime_list(map))
    }


    /*var categoryIntrestLiveData: LiveData<ModelIntrestResponse?> = MutableLiveData<ModelIntrestResponse?>()

    fun categoryIntrestLiveData(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelIntrestResponse?>()
        categoryIntrestLiveData = temp
        getResult(temp, apiInterface.getLanguageList())
    }*/


    var searchLiveData: LiveData<ModelSearch?> = MutableLiveData<ModelSearch?>()

    fun searchLiveData(map: HashMap<String, String>, search: String) {
        val temp = MutableLiveData<ModelSearch?>()
        searchLiveData = temp
        getResult(temp, apiInterface.setSearch(search))
    }

    var searchBranchLiveData: LiveData<ModelSearch?> = MutableLiveData<ModelSearch?>()

    fun searchBranchLiveData(map: HashMap<String, String>, search: String) {
        val temp = MutableLiveData<ModelSearch?>()
        searchBranchLiveData = temp
        getResult(temp, apiInterface.setSearchBranch(search))
    }

    var photoLiveData: LiveData<ModelPhotoResponse?> = MutableLiveData<ModelPhotoResponse?>()
    fun getAlbumPhotos(access_token: String,map: HashMap<String, Any>) {
        val temp = MutableLiveData<ModelPhotoResponse?>()
        photoLiveData = temp
        getResult(temp, apiInterface.getPhotoList(access_token,map))
    }


    var photoIndexLiveData: LiveData<ModelPhotoIndexDataResponse?> =
        MutableLiveData<ModelPhotoIndexDataResponse?>()

    fun photoIndexLiveData(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelPhotoIndexDataResponse?>()
        photoIndexLiveData = temp
        getResult(temp, apiInterface.getPhotoIndexList(map))
    }


    var friendLiveData: MutableLiveData<ModelFriend?> = MutableLiveData<ModelFriend?>()
    fun getFriendList(access_token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelFriend?>()
        friendLiveData = temp
        getResult(temp, apiInterface.getFriendListMap(access_token = access_token, map))
    }

    fun getFriendListBody(access_token: String, map: ApiInterface.UsersListResponse) {
        val temp = MutableLiveData<ModelFriend?>()
        friendLiveData = temp
        getResult(temp, apiInterface.getFriendListBody(access_token = access_token, map = map))
    }

    var funtimeUpdateLiveData: MutableLiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun funtimUpdate(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        funtimeUpdateLiveData = temp
        getResult(temp, apiInterface.funtimeUpdate(map))
    }


    var updateFriendRequestLiveData: LiveData<CommonModelResponse?> =
        MutableLiveData<CommonModelResponse?>()

    fun updateFriendRequest(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        updateFriendRequestLiveData = temp
        getResult(temp, apiInterface.updateFriendRequest(map))
    }
    fun removeFriendRequest(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        updateFriendRequestLiveData = temp
        getResult(temp, apiInterface.removeFriendRequest(map))
    }

    var removeUserFromSuggestionLiveData: LiveData<CommonModelResponse?> =
        MutableLiveData<CommonModelResponse?>()

    fun removeUserFromSuggestion(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        removeUserFromSuggestionLiveData = temp
        getResult(temp, apiInterface.removeUserFromSuggestions(map))
    }


    var shareWithFriendLiveData: MutableLiveData<CommonModelResponse?> =
        MutableLiveData<CommonModelResponse?>()

    fun shareWithFriend(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        shareWithFriendLiveData = temp
        getResult(temp, apiInterface.shareWithFriend(map))
    }


    var sendFriendRequestLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun sendFriendRequest(access_token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        sendFriendRequestLiveData = temp
        getResult(
            temp,
            apiInterface.sendFriendRequest(
                access_token = PrefManager.getInstance(App.getInstance())?.userDetail?.results?.access_token.toString(),
                map
            )
        )
    }

    var followRequestLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun sendFollowRequest(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        followRequestLiveData = temp
        getResult(temp, apiInterface.requestBeFollower(token, token, map))
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


    var checkIfOldEmailLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun checkIfOldEmail(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        checkIfOldEmailLiveData = temp
        getResult(temp, apiInterface.checkIfOldEmail(map))

    }


    var checkIfOldUsernameLiveData: MutableLiveData<UserNameValidatedResponse?> =
        MutableLiveData<UserNameValidatedResponse?>()

    fun checkIfOldUsername(user_name: String) {
        getResult(
            checkIfOldUsernameLiveData,
            apiInterface.checkIfOldUserName(user_name = user_name)
        )
    }


    var changeUsernameLiveData: MutableLiveData<UserNameChangeResponse?> =
        MutableLiveData<UserNameChangeResponse?>()

    fun changeUsername(access_token: String, user_name: String) {
        getResult(
            changeUsernameLiveData, apiInterface.changeUserName(
                access_token = access_token,
                user_name = user_name
            )
        )
    }


    var loginData: LiveData<ModelLoginResponse?> = MutableLiveData<ModelLoginResponse?>()
    fun login(map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelLoginResponse?>()
        loginData = temp
        getResult(temp, apiInterface.setLoginDetails(map))
    }

    var numberVerifyData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun numberVerify(access_token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        numberVerifyData = temp
        getResult(temp, apiInterface.setOtpNumberVerify(access_token, map))
    }

    var aboutProfileData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    fun aboutProfileUpdate(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        aboutProfileData = temp
        getResult(temp, apiInterface.setUpdateAbout(map))
    }

    var educationData: LiveData<ModelCommonAddEducationAndProfessionResponse?> =
        MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()

    fun educationData(
        access_token: String,
        map: HashMap<String, String>
    ) {
        val temp = MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()
        educationData = temp
        getResult(
            temp, apiInterface.setEducationAddDetails(
                access_token = access_token, map = map
            )
        )
    }

    var updateFriendCategoryLiveData: LiveData<AddCategoryModel?> =
        MutableLiveData<AddCategoryModel?>()

    fun updateFriendCategoryData(map: HashMap<String, String>) {
        val temp = MutableLiveData<AddCategoryModel?>()
        updateFriendCategoryLiveData = temp
        getResult(temp, apiInterface.setUpdateFriendCategoryDetails(map))
    }

    var addUpdateProfessionLiveData: LiveData<ModelCommonAddEducationAndProfessionResponse?> =
        MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()

    fun addUpdateProfessionData(
        access_token: String,
        map: HashMap<String, String>
    ) {
        val temp = MutableLiveData<ModelCommonAddEducationAndProfessionResponse?>()
        addUpdateProfessionLiveData = temp
        getResult(temp, apiInterface.setProfessionAddDetails(access_token, map))
    }

    var createAlbumData: LiveData<ModelAlbumCreateResponse?> =
        MutableLiveData<ModelAlbumCreateResponse?>()

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


    var repliesLiveData: LiveData<ModelGetComment?> = MutableLiveData<ModelGetComment?>()
    fun getRepliesList(
        access_token: String,
        map: HashMap<String, String>
    ) {
        val temp = MutableLiveData<ModelGetComment?>()
        repliesLiveData = temp
        getResult(temp, apiInterface.getCommentList(access_token, map))
    }

    var commentLiveData: LiveData<ModelGetComment?> = MutableLiveData<ModelGetComment?>()
    fun getCommentList(
        access_token: String,
        map: HashMap<String, String>
    ) {
        val temp = MutableLiveData<ModelGetComment?>()
        commentLiveData = temp
        getResult(temp, apiInterface.getCommentList(access_token, map))
    }


    var addCommentLiveData: LiveData<ModelComment?> = MutableLiveData<ModelComment?>()
    fun addComment(
        access_token: String,
        @Part("funtime_id") funtime_id: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part("id") id: RequestBody,
        @Part("comment_id") comment_id: RequestBody,
        @Part("is_delete") is_delete: RequestBody,
        @Part images: MultipartBody.Part? = null
    ) {
        val temp = MutableLiveData<ModelComment?>()
        addCommentLiveData = temp
        getResult(
            temp, apiInterface.addComment(
                access_token = access_token,
                funtime_id = funtime_id,
                comment = comment,
                id = id,
                comment_id = comment_id,
                is_delete = is_delete,
                images = images
            )
        )
    }

    fun addComment1(
        access_token: String,
        @Part("funtime_id") funtime_id: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part images: MultipartBody.Part? = null
    ) {
        val temp = MutableLiveData<ModelComment?>()
        addCommentLiveData = temp
        getResult(
            temp, apiInterface.addComment1(
                access_token = access_token,
                funtime_id = funtime_id,
                comment = comment,
                images = images
            )
        )
    }

    var likeCommentLiveData: LiveData<ModelSuccess?> = MutableLiveData<ModelSuccess?>()
    fun likeComment(access_token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelSuccess?>()
        likeCommentLiveData = temp
        getResult(temp, apiInterface.likeComment(access_token, access_token, map))
    }


    var otpVerifyRegistarionData: LiveData<CommonModelResponse?> =
        MutableLiveData<CommonModelResponse?>()

    fun otpVerifyRegistration(map: HashMap<String, String>, email: String, otp: String) {
        val temp = MutableLiveData<CommonModelResponse?>()
        otpVerifyRegistarionData = temp
        getResult(temp, apiInterface.setOtpVerifyRegistration(email, otp))
    }

    var profileLiveData: MutableLiveData<ModelUser?> = MutableLiveData<ModelUser?>()
    fun getProfileData(map: HashMap<String, String>, access_token: String) {
        val temp = MutableLiveData<ModelUser?>()
        profileLiveData = temp
        getResult(temp, apiInterface.setProfileDetails(access_token = access_token))
    }


    var otherUserProfileLiveData: MutableLiveData<ModelFriend1?> = MutableLiveData<ModelFriend1?>()
    fun getOtherUserProfileData(access_token: String, user_id: String) {
        val temp = MutableLiveData<ModelFriend1?>()
        otherUserProfileLiveData = temp
        getResult(
            temp,
            apiInterface.getOtherUserProfileDetails(access_token, access_token, user_id)
        )
    }


    var globalSearchLiveData: MutableLiveData<ModelGlobalSearch?> =
        MutableLiveData<ModelGlobalSearch?>()

    fun getGlobalSearch(access_token: String, map: ApiInterface.SearchRequest) {
        val temp = MutableLiveData<ModelGlobalSearch?>()
        globalSearchLiveData = temp
        getResult(temp, apiInterface.getGlobalSearch(access_token, map))
    }


    fun getSavedFuntimMusic(map: HashMap<String, String>): LiveData<ModelMusicListResponse?> {
        val temp = MutableLiveData<ModelMusicListResponse?>()
        var saveLiveDAta: LiveData<ModelMusicListResponse?>
        saveLiveDAta = temp
        getResult(temp, apiInterface.getSavedFuntimMusic(map))
        return saveLiveDAta
    }


    fun getSavedFuntimReels(map: HashMap<String, String>): LiveData<ModelFuntimeResponse?> {
        val temp = MutableLiveData<ModelFuntimeResponse?>()
        var saveLiveDAta: LiveData<ModelFuntimeResponse?>
        saveLiveDAta = temp
        getResult(temp, apiInterface.getSavedFuntimReels(map))
        return saveLiveDAta
    }

    fun saveUnsaveMusic(map: HashMap<String, String>): LiveData<CommonModelResponse?> {
        val temp = MutableLiveData<CommonModelResponse?>()
        var saveLiveDAta: LiveData<CommonModelResponse?>
        saveLiveDAta = temp
        getResult(temp, apiInterface.saveUnsaveMusic(map))
        return saveLiveDAta
    }

    var reportFuntimeLiveData: LiveData<CommonModelResponse?> =
        MutableLiveData<CommonModelResponse?>()

    fun reportFuntime(
        @Part file: MultipartBody.Part? = null,
        @Part("funtime_id") funtimeId: RequestBody,
        @Part("category") category: RequestBody,
        @Part("report_reason") report_reason: RequestBody,
        @Part("detailed_reason") detailed_reason: RequestBody
    ): LiveData<CommonModelResponse?> {
        val temp = MutableLiveData<CommonModelResponse?>()
        reportFuntimeLiveData = temp
        getResult(
            temp,
            apiInterface.reportFuntime(file, funtimeId, category, report_reason, detailed_reason)
        )
        return reportFuntimeLiveData
    }

    fun reportProblem(
        access_token: String,
        @Part report_image: MultipartBody.Part? = null,
        @Part("report_category") report_category: RequestBody,
        @Part("report_reason") report_reason: RequestBody,
        @Part("detailed_reason") detailed_reason: RequestBody
    ): LiveData<CommonModelResponse?> {
        val temp = MutableLiveData<CommonModelResponse?>()
        reportFuntimeLiveData = temp
        getResult(
            temp,
            apiInterface.reportProblem(
                access_token = access_token,
                report_image = report_image,
                report_category = report_category,
                report_reason = report_reason,
                detailed_reason = detailed_reason
            )
        )
        return reportFuntimeLiveData
    }

    var getReportListLiveData = MutableLiveData<ReportsListingResponse?>()

    fun getReportList(access_token: String) {
        getResult(
            getReportListLiveData,
            apiInterface.getReportProblemList(access_token = access_token)
        )
    }


    var UpdateProfileLiveData: LiveData<ProfileImagesUpdated?> =
        MutableLiveData<ProfileImagesUpdated?>()

    fun etsProfileApi1(
        access_token: String,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part("about") about: RequestBody,
        @Part("countrycode") countrycode: RequestBody,
        @Part("number") number: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("marital_status") marital_status: RequestBody,
        @Part("home_town") home_town: RequestBody,
        @Part("city") city: RequestBody,
        @Part("url") url: RequestBody,
        @Part("gender") gender: RequestBody,
    ) {
        val temp = MutableLiveData<ProfileImagesUpdated?>()
        UpdateProfileLiveData = temp

        getResult(
            temp, apiInterface.updateUserProfile(
                access_token = access_token,
                firstName = first_name,
                lastName = last_name,
                about = about,
                countrycode = countrycode,
                number = number,
                dob = dob,
                maritalStatus = marital_status,
                url = url,
                gender = gender,
                city = city,
                home_town = home_town,
            )
        )

    }


    fun etsProfileApi(access_token: String, @Part file_Profile_Image: MultipartBody.Part? = null) {
        val temp = MutableLiveData<ProfileImagesUpdated?>()
        UpdateProfileLiveData = temp
        getResult(
            temp,
            apiInterface.updateUserProfileImage(access_token = access_token, file_Profile_Image!!)
        )
    }


    var UplodedAlbumImageLiveData: LiveData<CommonModelResponse?> =
        MutableLiveData<CommonModelResponse?>()

    fun uploadAlbumImageApi(
        access_token: String,
        @Part album_image: MultipartBody.Part? = null,
        @Part("album_id") albumId: RequestBody,
    ) {
        val temp = MutableLiveData<CommonModelResponse?>()
        UplodedAlbumImageLiveData = temp
        getResult(
            temp,
            apiInterface.addAlbumImage(access_token = access_token, album_image!!, albumId)
        )
    }


    var blockData: LiveData<CommonModelResponse?> = MutableLiveData<CommonModelResponse?>()
    /*fun block(map: HashMap<String, String>) {
        val temp = MutableLiveData<CommonModelResponse?>()
        blockData = temp
        getResult(temp, apiInterface.setBlock(map))
    }*/

    fun block(
        access_token: String,
        _id: String
    ) {
        val temp = MutableLiveData<CommonModelResponse?>()
        blockData = temp
        getResult(temp, apiInterface.setBlock(access_token = access_token, _id = _id))
    }

    var albumLiveData: LiveData<ModelAlbumsResponse?> = MutableLiveData<ModelAlbumsResponse?>()

    fun albumLiveDatas(token: String, map: HashMap<String, String>) {
        val temp = MutableLiveData<ModelAlbumsResponse?>()
        albumLiveData = temp
        getResult(temp, apiInterface.getAlbumList(access_token = token))
    }


    var postReelLiveData: LiveData<FunTimeAddResponse?> = MutableLiveData<FunTimeAddResponse?>()
    fun postReel(
        access_token: String,
        file: MultipartBody.Part? = null,
        thum_icon: MultipartBody.Part? = null,
        cover_image: MultipartBody.Part? = null,
        file_type: RequestBody,
        text: RequestBody,
        tag_id: RequestBody,
        sound_id: RequestBody,
        location: RequestBody,
        privacy: RequestBody,
        privacy_data: RequestBody,
        deviceId: RequestBody,
        deviceToken: RequestBody,
    ) {
        val temp = MutableLiveData<FunTimeAddResponse?>()
        postReelLiveData = temp
        getResult(
            temp,
            apiInterface.postReel(
                Authorization = access_token,
                access_token = access_token,
                file = file,
                thum_icon = thum_icon,
                cover_image = cover_image,
                file_type = file_type,
                text = text,
                tag_id = tag_id,
                sound_id = sound_id,
                location = location,
                privacy = privacy,
                privacy_data = privacy_data,
                deviceId = deviceId,
                deviceToken = deviceToken
            )
        )
    }

    var hashTagsListResponse: LiveData<HashTagsListResponse?> =
        MutableLiveData<HashTagsListResponse?>()
    fun get_hash_tags(access_token: String) {
        val temp = MutableLiveData<HashTagsListResponse?>()
        hashTagsListResponse = temp
        getResult(temp, apiInterface.get_hash_tags(access_token, access_token))
    }

    fun saveAsDraft(
        @Part file: MultipartBody.Part? = null,
        @Part cover_image: MultipartBody.Part? = null,
        @Part("file_type") file_type: RequestBody,
        @Part("text") text: RequestBody,
        @Part("tag_id") tag_id: RequestBody,
        @Part("sound_id") sound_id: RequestBody,
        @Part("location") location: RequestBody,
        @Part("privacy") privacy: RequestBody,
        @Part("privacy_data") privacy_data: RequestBody,
        @Part("deviceId") deviceId: RequestBody,
        @Part("deviceToken") deviceToken: RequestBody,
    ) {
        val temp = MutableLiveData<FunTimeAddResponse?>()
        postReelLiveData = temp
        getResult(
            temp,
            apiInterface.saveAsDraft(
                file = file!!,
                cover_image = cover_image,
                file_type = file_type,
                text = text,
                tag_id = tag_id,
                sound_id = sound_id,
                location = location,
                privacy = privacy,
                privacy_data = privacy_data,
                deviceId = deviceId,
                deviceToken = deviceToken
            )
        )
    }

    var privacyGetResponse = MutableLiveData<PrivacyUpdateResponse?>()
    fun getPrivacyResponse(access_token: String) {
        getResult(
            data = privacyGetResponse,
            call = apiInterface.privacyGet(access_token = access_token)
        )
    }

    var privacyUpdateResponse = MutableLiveData<PrivacyUpdateResponse?>()
    fun updatePrivacyResponse(
        access_token: String,
        allow_others_to_find_me: Boolean,
        profile: Int,
        last_seen: Int,
        prfile_photo: Int,
        about: Int,
        read_receipts: Boolean,
        story: Int,
        groups: Int,
        block_contact: String,
        who_can_like_my_post: Int,
        who_can_post_comment: Int,
        who_can_send_me_message: Int,
        who_can_see_my_future_post: Int,
        who_can_see_people_page_list: Int,
        who_can_send_you_friend_request: Int,
        who_can_see_email_address: Int,
        who_can_see_phone_number: Int
    ) {
        getResult(
            data = privacyUpdateResponse, call = apiInterface.privacyUpdate(
                access_token = access_token,
                allow_others_to_find_me = allow_others_to_find_me,
                profile = profile,
                last_seen = last_seen,
                prfile_photo = prfile_photo,
                about = about,
                read_receipts = read_receipts,
                story = story,
                groups = groups,
                block_contact = block_contact,
                who_can_like_my_post = who_can_like_my_post,
                who_can_post_comment = who_can_post_comment,
                who_can_send_me_message = who_can_send_me_message,
                who_can_see_my_future_post = who_can_see_my_future_post,
                who_can_see_people_page_list = who_can_see_people_page_list,
                who_can_send_you_friend_request = who_can_send_you_friend_request,
                who_can_see_email_address = who_can_see_email_address,
                who_can_see_phone_number = who_can_see_phone_number
            )
        )
    }

    var accountSettingGet = MutableLiveData<AccountSettingGetAndPut?>()
    fun accountSettingGet(access_token: String) {
        getResult(
            accountSettingGet,
            apiInterface.accountSettingsGet(access_token = access_token)
        )
    }

    var accountSettingPut = MutableLiveData<AccountSettingGetAndPut?>()
    fun accountSettingPut(access_token: String, requestBody: AccountSettingGetAndPut.Reponse) {
        getResult(
            accountSettingPut,
            apiInterface.accountSettingsUpdate(
                access_token = access_token,
                requestBody = requestBody
            )
        )
    }

    var changePasswordResponse = MutableLiveData<ChangePasswordResponse?>()
    fun changePassword(
        access_token: String,
        password_old: String,
        password_new: String,
        password_confirm: String
    ) {
        getResult(
            changePasswordResponse, apiInterface.changePasswordApi(
                access_token = access_token,
                password_old = password_old,
                password_new = password_new,
                password_confirm = password_confirm
            )
        )
    }

    var deleteMyAccountResponse = MutableLiveData<ChangePasswordResponse?>()
    var sendOtpResponse = MutableLiveData<OtpReceiveResponse?>()

    fun deleteMyAccount(
        access_token: String,
        email: String,
        otp: String,
        notify_contact: Boolean
    ) {
        getResult(
            deleteMyAccountResponse, apiInterface.deleteMyAccount(
                access_token = access_token,
                email = email,
                otp = otp,
                notify_contact = notify_contact
            )
        )
    }

    fun sendOtp(access_token: String) {
        getResult(sendOtpResponse, apiInterface.sendOtp(access_token = access_token))
    }

    var updateLanguageAndCountryResponse = MutableLiveData<CommonModelResponse?>()
    fun updateLanguageAndCountry(
        access_token: String,
        country: String,
        language: String
    ) {
        getResult(
            updateLanguageAndCountryResponse, apiInterface.updateLanguageAndCountry(
                access_token = access_token,
                country = country,
                language = language
            )
        )
    }
}