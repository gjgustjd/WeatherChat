package com.miso.misoweather.Acitivity.weatherdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {

    val defaultRegionId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.DEFAULT_REGION_ID)
    }

    val bigScale by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    val midScale by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MIDSCALE_REGION)
    }

    val smallScale by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SMALLSCALE_REGION)
    }

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


    fun setupWeatherData() {
        getBriefForecast(defaultRegionId.toInt())
        getHourlyForecast(defaultRegionId.toInt())
        getCurrentAir(defaultRegionId.toInt())
        getDailyForecast()
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

    fun getDailyForecast() {
        repository.getDailyForecast(
            defaultRegionId.toInt(),
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

    fun getHourlyForecast(regionId: Int? = defaultRegionId.toInt()) {
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

    fun getCurrentAir(regionId: Int? = defaultRegionId.toInt()) {
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