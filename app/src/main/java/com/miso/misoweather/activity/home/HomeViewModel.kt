package com.miso.misoweather.activity.home

import android.util.Log
import androidx.lifecycle.*
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.dto.commentList.Comment
import com.miso.misoweather.model.dto.commentList.CommentListResponseDto
import com.miso.misoweather.model.dto.forecast.brief.ForecastBriefResponseDto
import com.miso.misoweather.model.dto.memberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.dto.surveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MisoRepository
) : ViewModel() {
    val misoToken =
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.MISO_TOKEN)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
            )
    val logoutResponseString by lazy { MutableLiveData<String?>() }
    val todaySurveyResultResponseDto by lazy { MutableLiveData<SurveyResultResponseDto>() }
    val commentList by lazy { MutableLiveData<List<Comment>>() }
    val isSurveyed =
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.IS_SURVEYED)
            .map {
                if (it.isNullOrBlank()) {
                    getSurveyMyAnswer(misoToken.value, null)
                    ""
                } else
                    it
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ""
            )

    val lastSurveyedDate by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.LAST_SURVEYED_DATE)
    }

    val defaultRegionId by lazy {
        viewModelScope.async {
            val valueFlow =
                repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.DEFAULT_REGION_ID)
            valueFlow
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = valueFlow.first()
                )
        }
    }


    val bigScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.BIGSCALE_REGION)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
            )
    }

    val midScale =
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.MIDSCALE_REGION)
            .map {
                if (it.equals("?????? ??? ???")) "??????" else it
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ""
            )

    val smallScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SMALLSCALE_REGION)
            .mapLatest {
                if (midScale.value.equals("?????? ??? ???") || midScale.value.equals("??????"))
                    ""
                else
                    if (it.equals("?????? ??? ???"))
                        "??????"
                    else
                        it
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ""
            )
    }

    val location: StateFlow<String> =
        flow {
            emit("${bigScale.value} ${midScale.value} ${smallScale.value}")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = "${bigScale.value} ${midScale.value} ${smallScale.value}"
        )

    val nickname: MutableStateFlow<String> = MutableStateFlow("")
    val emoji: MutableStateFlow<String> = MutableStateFlow("")
    val weatherEmoji: MutableStateFlow<String> = MutableStateFlow("")
    val weatherDegree: MutableStateFlow<String> = MutableStateFlow("")

    suspend fun getUserInfo(action: (response: Response<MemberInfoResponseDto>) -> Unit) {
        val response = repository.getUserInfo(misoToken.value)
        if (response.isSuccessful) {
            val memberInfoResponseDto = response.body()!!
            val memberInfo = memberInfoResponseDto.data
            repository.dataStoreManager.apply {
                savePreference(
                    DataStoreManager.DEFAULT_REGION_ID,
                    memberInfoResponseDto.data.regionId.toString()
                )
                savePreference(DataStoreManager.EMOJI, memberInfo.emoji)
                savePreference(DataStoreManager.NICKNAME, memberInfo.nickname)
            }
            nickname.value = "${memberInfo.regionName}??? ${memberInfo.nickname}???!"
            emoji.value = memberInfo.emoji
        }
        action(response)
    }

    suspend fun getBriefForecast(
        action: (response: Response<ForecastBriefResponseDto>) -> Unit
    ) {
        val response = repository.getBriefForecast(defaultRegionId.await().value.toInt())
        if (response.isSuccessful) {
            val region = response.body()!!.data.region
            repository.dataStoreManager.apply {
                savePreference(DataStoreManager.BIGSCALE_REGION, region.bigScale)
                savePreference(
                    DataStoreManager.MIDSCALE_REGION,
                    if (region.midScale.equals("?????? ??? ???")) "??????" else region.midScale
                )
                savePreference(
                    DataStoreManager.SMALLSCALE_REGION,
                    if (region.smallScale.equals("?????? ??? ???")) "??????" else region.smallScale
                )
            }
            weatherEmoji.value = response.body()!!.data.weather
            weatherDegree.value = CommonUtil.toIntString(response.body()!!.data.temperature)
        }
        action(response)
    }

    suspend fun getCommentList(
        commentId: Int?,
        size: Int,
        action: ((response: Response<CommentListResponseDto>) -> Unit)? = null
    ) {
        val response = repository.getCommentList(commentId, size)
        commentList.value = response.body()!!.data.commentList
        action?.let { action(response) }
    }

    suspend fun getSurveyResult(
        shortBigScale: String? = null,
        action: (response: Response<SurveyResultResponseDto>) -> Unit
    ) {
        val response = repository.getSurveyResults(shortBigScale)
        todaySurveyResultResponseDto.value = response.body()
        action(response)
    }

    fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakaoLogout", "???????????? ??????. SDK?????? ?????? ?????????", error)
                logoutResponseString.value = error.toString()
            } else {
                Log.i("kakaoLogout", "???????????? ??????. SDK?????? ?????? ?????????")
                repository.dataStoreManager.removePreferences(
                    DataStoreManager.ACCESS_TOKEN,
                    DataStoreManager.SOCIAL_ID,
                    DataStoreManager.SOCIAL_TYPE,
                    DataStoreManager.MISO_TOKEN
                )
                logoutResponseString.value = "OK"
            }
        }
    }

    suspend fun getSurveyMyAnswer(token: String, action: ((response: Response<*>) -> Unit)?) {
        val response = repository.getSurveyMyAnswers(token)
        if (response.isSuccessful) {
            if (response.body()!!.data.responseList.any { it.answered })
                repository.dataStoreManager.savePreference(
                    DataStoreManager.IS_SURVEYED,
                    "surveyed"
                )
            else
                repository.dataStoreManager.savePreference(
                    DataStoreManager.IS_SURVEYED,
                    "false"
                )
        } else {
            repository.dataStoreManager.savePreference(
                DataStoreManager.IS_SURVEYED,
                "false"
            )
        }

        if (action != null) {
            action(response)
        }
    }
}
