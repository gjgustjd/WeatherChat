package com.miso.misoweather.Acitivity.home

import android.util.Log
import androidx.lifecycle.*
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {
    val logoutResponseString by lazy { MutableLiveData<String?>() }
    val forecastBriefResponse by lazy { MutableLiveData<Any?>() }
    val memberInfoResponse by lazy { MutableLiveData<Response<*>?>() }
    val commentListResponse by lazy { MutableLiveData<Response<*>?>() }
    val surveyResultResponse by lazy { MutableLiveData<Response<*>?>() }

    val isSurveyed =
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.IS_SURVEYED)
            .map {
                if (it.isNullOrBlank()) {
                    getSurveyMyAnswer(misoToken)
                    ""
                } else
                    it
            }
            .asLiveData()

    val lastSurveyedDate by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.LAST_SURVEYED_DATE)
    }

    val defaultRegionId by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.DEFAULT_REGION_ID)
            .asLiveData()
    }

    val misoToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
    }

    val bigScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.BIGSCALE_REGION)
            .asLiveData()
    }

    val midScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.MIDSCALE_REGION)
            .asLiveData()
            .map {
                if (it.equals("선택 안 함")) "전체" else it
            }
    }

    val smallScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SMALLSCALE_REGION)
            .map {
                if (midScale.value.equals("선택 안 함") || midScale.value.equals("전체"))
                    ""
                else
                    if (it.equals("선택 안 함"))
                        "전체"
                    else
                        it
            }
            .asLiveData()
    }


    fun getUserInfo() {
        repository.getUserInfo(
            misoToken,
            { call, response ->
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
                memberInfoResponse.value = response
            },
            { call, response ->
                memberInfoResponse.value = response
            },
            { call, throwable ->
                memberInfoResponse.value = null
            })
    }

    fun getBriefForecast(regionId: Int) {
        repository.getBriefForecast(
            regionId,
            { call, response ->
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
                forecastBriefResponse.value = response
            },
            { call, response ->
                forecastBriefResponse.value = response
            },
            { call, t ->
                forecastBriefResponse.value = null
            }
        )
    }

    fun getCommentList(commentId: Int?, size: Int) {
        repository.getCommentList(
            commentId,
            size,
            { call, response ->
                commentListResponse.value = response
            },
            { call, response ->
                commentListResponse.value = response
            },
            { call, throwable -> }
        )
    }

    fun getSurveyResult(shortBigScale: String? = null) {
        repository.getSurveyResults(
            shortBigScale,
            { call, reponse ->
                surveyResultResponse.value = reponse!!
            },
            { call, reponse ->
                surveyResultResponse.value = reponse!!
            },
            { call, t ->

            },
        )
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

    fun getSurveyMyAnswer(token: String) {
        repository.getSurveyMyAnswers(
            token,
            { call, response ->
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
                }
            },
            { call, response ->
                repository.dataStoreManager.savePreference(
                    DataStoreManager.IS_SURVEYED,
                    "false"
                )
            },
            { call, t ->
                repository.dataStoreManager.savePreference(
                    DataStoreManager.IS_SURVEYED,
                    "false"
                )
            }
        )
    }

}
