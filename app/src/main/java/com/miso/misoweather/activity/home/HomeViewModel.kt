package com.miso.misoweather.activity.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.common.CommonUtil
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MisoRepository
) : ViewModel() {
    val logoutResponseString by lazy { MutableLiveData<String?>() }
    val todaySurveyResultResponseDto by lazy { MutableLiveData<SurveyResultResponseDto>() }

    val isSurveyed =
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.IS_SURVEYED)
            .map {
                if (it.isNullOrBlank()) {
                    getSurveyMyAnswer(misoToken, null)
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

    val misoToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
    }

    val bigScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.BIGSCALE_REGION)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ""
            )
    }

    val midScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.MIDSCALE_REGION)
            .map {
                if (it.equals("선택 안 함")) "전체" else it
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ""
            )
    }

    val smallScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SMALLSCALE_REGION)
            .mapLatest {
                if (midScale.value.equals("선택 안 함") || midScale.value.equals("전체"))
                    ""
                else
                    if (it.equals("선택 안 함"))
                        "전체"
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
        val response = repository.getUserInfo(misoToken)
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
            nickname.value = "${memberInfo.regionName}의 ${memberInfo.nickname}님!"
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
                    if (region.midScale.equals("선택 안 함")) "전체" else region.midScale
                )
                savePreference(
                    DataStoreManager.SMALLSCALE_REGION,
                    if (region.smallScale.equals("선택 안 함")) "전체" else region.smallScale
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
        action: (response: Response<CommentListResponseDto>) -> Unit
    ) = action(repository.getCommentList(commentId, size))

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
                Log.e("kakaoLogout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                logoutResponseString.value = error.toString()
            } else {
                Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨")
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
