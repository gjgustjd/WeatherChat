package com.miso.misoweather.Acitivity.weatherdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class WeatherDetailViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MisoRepository
    val forecastBriefResponse: MutableLiveData<Any?> =
        MutableLiveData()
    val dailyForecastResponse: MutableLiveData<Any?> =
        MutableLiveData()
    val hourlyForecastResponse: MutableLiveData<Any?> =
        MutableLiveData()
    val currentAirResponse: MutableLiveData<Any?> =
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
        setupWeatherData()
    }

    fun setupWeatherData() {
        getBriefForecast(defaultRegionId.value!!.toInt())
        getDailyForecast(defaultRegionId.value!!.toInt())
        getHourlyForecast(defaultRegionId.value!!.toInt())
        getCurrentAir(defaultRegionId.value!!.toInt())
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

    fun getDailyForecast(regionId: Int? = defaultRegionId.value!!.toInt()) {
        repository.getDailyForecast(
            regionId!!,
            { call, response ->
                dailyForecastResponse.value = response
            },
            { call, response ->
                dailyForecastResponse.value = response
            },
            { call, t ->
                dailyForecastResponse.value = t
            },
        )
    }

    fun getHourlyForecast(regionId: Int? = defaultRegionId.value!!.toInt()) {
        repository.getHourlyForecast(
            regionId!!,
            { call, response ->
                hourlyForecastResponse.value = response
            },
            { call, response ->
                hourlyForecastResponse.value = response
            },
            { call, t ->
                hourlyForecastResponse.value = t
            },
        )
    }

    fun getCurrentAir(regionId: Int? = defaultRegionId.value!!.toInt()) {
        repository.getCurrentAir(
            regionId!!,
            { call, response ->
                currentAirResponse.value = response
            },
            { call, response ->
                currentAirResponse.value = response
            },
            { call, t ->
                currentAirResponse.value = t
            },
        )
    }
}