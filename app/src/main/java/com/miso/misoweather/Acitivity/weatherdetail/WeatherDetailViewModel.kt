package com.miso.misoweather.Acitivity.weatherdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response

class WeatherDetailViewModel(private val repository: MisoRepository) : ViewModel() {
    val forecastDetailResponse: MutableLiveData<Response<DailyForecastResponseDto>?> =
        MutableLiveData()
    val isSurveyed: MutableLiveData<String?> = MutableLiveData()
    val lastSurveyedDate: MutableLiveData<String?> = MutableLiveData()
    val defaultRegionId: MutableLiveData<String?> = MutableLiveData()
    val bigScale: MutableLiveData<String?> = MutableLiveData()
    val midScale: MutableLiveData<String?> = MutableLiveData()
    val smallScale: MutableLiveData<String?> = MutableLiveData()

    fun updateProperties() {
        setupBigScale()
        setupMidScale()
        setupSmallScale()
        setupSurveyed()
        setupLastSurveyedDate()
        setupDefaultRegionId()
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
        repository.getDailyForecast(
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