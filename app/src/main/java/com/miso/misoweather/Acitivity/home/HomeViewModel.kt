package com.miso.misoweather.Acitivity.home

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception

class HomeViewModel() : ViewModel() {
    val memberInfoResponse: MutableLiveData<Response<MemberInfoResponseDto>?> = MutableLiveData()
    val forecastBriefResponse: MutableLiveData<Response<ForecastBriefResponseDto>?> =
        MutableLiveData()
    val commentListResponse: MutableLiveData<Response<CommentListResponseDto>?> = MutableLiveData()
    val surveyResultResponse: MutableLiveData<Response<SurveyResultResponseDto>?> =
        MutableLiveData()

    fun getUserInfo(serverToken: String) {
        MisoRepository.getUserInfo(
            serverToken,
            { call, response ->
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
        MisoRepository.getBriefForecast(
            regionId,
            { call, response ->
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
        MisoRepository.getCommentList(
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
        MisoRepository.getSurveyResults(
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
}
