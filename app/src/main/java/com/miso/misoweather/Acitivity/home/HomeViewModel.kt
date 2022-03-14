package com.miso.misoweather.Acitivity.home

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception

class HomeViewModel(private val repository: MisoRepository) : ViewModel() {
    val memberInfoResponse: MutableLiveData<Response<MemberInfoResponseDto>?> = MutableLiveData()
    val forecastBriefResponse: MutableLiveData<Any?> =
        MutableLiveData()
    val commentListResponse: MutableLiveData<Response<CommentListResponseDto>?> = MutableLiveData()
    val surveyResultResponse: MutableLiveData<Response<SurveyResultResponseDto>?> =
        MutableLiveData()
    val isSurveyed: MutableLiveData<String?> = MutableLiveData()
    val lastSurveyedDate: MutableLiveData<String?> = MutableLiveData()
    val defaultRegionId: MutableLiveData<String?> = MutableLiveData()
    val misoToken: MutableLiveData<String?> = MutableLiveData()
    val bigScale: MutableLiveData<String?> = MutableLiveData()
    val midScale: MutableLiveData<String?> = MutableLiveData()
    val smallScale: MutableLiveData<String?> = MutableLiveData()
    val logoutResponseString: MutableLiveData<String?> = MutableLiveData()

    fun updateProperties() {
        setupBigScale()
        setupMidScale()
        setupSmallScale()
        setupDefaultRegionId()
        setupDefaultRegionId()
        setupSurveyed()
        setupMisoToken()
        setupLastSurveyedDate()
    }

    fun setupSurveyed() {
        isSurveyed.value = repository.getPreference("isSurveyed")
    }

    fun setupLastSurveyedDate() {
        lastSurveyedDate.value = repository.getPreference("LastSurveyedDate")
    }

    fun setupDefaultRegionId() {
        defaultRegionId.value = repository.getPreference("defaultRegionId")
    }

    fun setupMisoToken() {
        misoToken.value = repository.getPreference("misoToken")
    }

    fun setupBigScale() {
        bigScale.value = repository.getPreference("BigScaleRegion")
    }

    fun setupMidScale() {
        midScale.value = repository.getPreference("MidScaleRegion")
    }

    fun setupSmallScale() {
        smallScale.value = repository.getPreference("SmallScaleRegion")
    }

    fun getUserInfo(serverToken: String) {
        repository.getUserInfo(
            serverToken,
            { call, response ->
                memberInfoResponse.value = response
                val memberInfoResponseDto = response.body()!!
                var memberInfo = memberInfoResponseDto.data
                repository.addPreferencePair(
                    "defaultRegionId",
                    memberInfoResponseDto.data.regionId.toString()
                )
                repository.addPreferencePair("emoji", memberInfo.emoji)
                repository.addPreferencePair("nickname", memberInfo.nickname)
                repository.savePreferences()
                updateProperties()
            },
            { call, response ->
                memberInfoResponse.value = response
            },
            { call, throwable ->
                memberInfoResponse.value = null
            })
    }

    fun loadWeatherInfo(regionId: Int) {
        repository.loadWeatherInfo(
            regionId,
            { call, response ->
                getBriefForecast(regionId)
            },
            { call, response ->
                forecastBriefResponse.value = response.message()
            },
            { call, t ->
                forecastBriefResponse.value = null
            }
        )
    }

    fun getBriefForecast(regionId: Int) {
        repository.getBriefForecast(
            regionId,
            { call, response ->
                var region = response.body()!!.data.region
                repository.addPreferencePair("BigScaleRegion", region.bigScale)
                repository.addPreferencePair(
                    "MidScaleRegion",
                    if (region.midScale.equals("선택 안 함")) "전체" else region.midScale
                )
                repository.addPreferencePair(
                    "SmallScaleRegion",
                    if (region.smallScale.equals("선택 안 함")) "전체" else region.smallScale
                )
                repository.savePreferences()
                updateProperties()
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

    fun getSurveyResult(shortBigScale: String) {
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
                repository.removePreference("accessToken", "socialId", "socialType", "misoToken")
                repository.savePreferences()
//                updateProperties()
                logoutResponseString.value = "OK"
            }
        }
    }
}
