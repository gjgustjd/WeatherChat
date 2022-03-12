package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.Forecast.Detail.ForecastDetailResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherDetailViewModel(private val repository: MisoRepository) : ViewModel() {
    val forecastDetailResponse: MutableLiveData<Response<ForecastDetailResponseDto>?> =
        MutableLiveData()
    val isSurveyed: MutableLiveData<String?> = MutableLiveData()
    val lastSurveyedDate: MutableLiveData<String?> = MutableLiveData()
    val defaultRegionId: MutableLiveData<String?> = MutableLiveData()

    fun updateProperties() {
        setupSurveyed()
        setupLastSurveyedDate()
        setupDefaultRegionId()
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

    fun getForecastDetail(regionId: Int) {
        repository.getDetailForecast(
            regionId,
            { call, response ->
                forecastDetailResponse.value = response
            },
            { call, response ->
                forecastDetailResponse.value = response
            },
            { call, t ->
//                Log.i("결과", "실패 : $t")
            },
        )
    }
}