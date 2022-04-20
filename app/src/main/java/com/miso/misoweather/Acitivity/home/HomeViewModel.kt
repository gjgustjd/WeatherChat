package com.miso.misoweather.Acitivity.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {

    @MutableNullableStringLiveData
    @Inject
    lateinit var isSurveyed: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var lastSurveyedDate: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var defaultRegionId: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var misoToken: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var bigScale: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var midScale: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var smallScale: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var logoutResponseString: MutableLiveData<String?>

    @MutableBooleanLiveData
    @Inject
    lateinit var isWeatherLoaded: MutableLiveData<Boolean>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var forecastBriefResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var dailyForecastResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var hourlyForecastResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var currentAirResponse: MutableLiveData<Any?>

    @MutableResponseLiveData
    @Inject
    lateinit var memberInfoResponse: MutableLiveData<Response<*>?>

    @MutableResponseLiveData
    @Inject
    lateinit var commentListResponse: MutableLiveData<Response<*>?>

    @MutableResponseLiveData
    @Inject
    lateinit var surveyResultResponse: MutableLiveData<Response<*>?>

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
        var isSurveyedString = repository.getPreference("isSurveyed")

        if (isSurveyedString.isNullOrBlank())
            getSurveyMyAnswer()
        else
            isSurveyed.value = isSurveyedString
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
        var region = repository.getPreference("MidScaleRegion")
        midScale.value = if (region.equals("선택 안 함")) "전체" else region
    }

    fun setupSmallScale() {
        var Midregion = repository.getPreference("MidScaleRegion")
        var Smallregion = repository.getPreference("SmallScaleRegion")
        smallScale.value =
            if (Midregion.equals("선택 안 함") || Midregion.equals("전체"))
                ""
            else
                if (Smallregion.equals("선택 안 함"))
                    "전체"
                else
                    Smallregion
    }

    fun getUserInfo(serverToken: String) {
        repository.getUserInfo(
            serverToken,
            { call, response ->
                val memberInfoResponseDto = response.body()!!
                val memberInfo = memberInfoResponseDto.data
                repository.apply {
                    addPreferencePair(
                        "defaultRegionId",
                        memberInfoResponseDto.data.regionId.toString()
                    )
                    addPreferencePair("emoji", memberInfo.emoji)
                    addPreferencePair("nickname", memberInfo.nickname)
                    savePreferences()
                }
                updateProperties()
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
                repository.apply {
                    addPreferencePair("BigScaleRegion", region.bigScale)
                    addPreferencePair(
                        "MidScaleRegion",
                        if (region.midScale.equals("선택 안 함")) "전체" else region.midScale
                    )
                    addPreferencePair(
                        "SmallScaleRegion",
                        if (region.smallScale.equals("선택 안 함")) "전체" else region.smallScale
                    )
                    savePreferences()
                }
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
                repository.apply {
                    removePreference("accessToken", "socialId", "socialType", "misoToken")
                    savePreferences()
                }
                logoutResponseString.value = "OK"
            }
        }
    }

    fun getSurveyMyAnswer() {
        repository.getSurveyMyAnswers(
            repository.getPreference("misoToken")!!,
            { call, response ->
                if (response.isSuccessful) {
                    if (response.body()!!.data.responseList.filter { it.answered == true }.size > 0)
                        isSurveyed.value = "surveyed"
                    else
                        isSurveyed.value = "false"
                }
            },
            { call, response ->
                isSurveyed.value = "false"
            },
            { call, t ->
                isSurveyed.value = "false"
            }
        )
    }

}
