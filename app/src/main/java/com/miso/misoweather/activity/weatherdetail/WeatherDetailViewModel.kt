package com.miso.misoweather.activity.weatherdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miso.misoweather.model.dto.forecast.brief.ForecastBriefResponseDto
import com.miso.misoweather.model.dto.forecast.currentAir.CurrentAirResponseDto
import com.miso.misoweather.model.dto.forecast.daily.DailyForecastResponseDto
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecastResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.dto.forecast.brief.ForecastBriefData
import com.miso.misoweather.model.dto.forecast.currentAir.CurrentAirData
import com.miso.misoweather.model.dto.forecast.daily.DailyForecastData
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecastData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {
    val defaultRegionId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.DEFAULT_REGION_ID)
    }

    val bigScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.BIGSCALE_REGION)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
            )
    }

    val midScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.MIDSCALE_REGION)
            .map { if (it.equals("선택 안 함")) "전체" else it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = repository.dataStoreManager.getPreference(DataStoreManager.MIDSCALE_REGION)
            )
    }

    val smallScale by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SMALLSCALE_REGION)
            .map {
                if (it.equals("전체"))
                    ""
                else
                    if (it.equals("선택 안 함"))
                        "전체"
                    else
                        it
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = repository.dataStoreManager.getPreference(DataStoreManager.SMALLSCALE_REGION)
            )
    }

    val location by lazy {
        flow {
            emit("${bigScale.value} ${midScale.value} ${smallScale.value}")
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = "${bigScale.value} ${midScale.value} ${smallScale.value}"
            )
    }

    val briefForecastData =
        MutableLiveData<ForecastBriefData>()
    val hourlyForecastData =
        MutableLiveData<HourlyForecastData>()
    val dailyForecastData by lazy {
        MutableLiveData<DailyForecastData>()
    }
    val currentAirData by lazy {
        MutableLiveData<CurrentAirData>()
    }

    suspend fun getBriefForecast(
        regionId: Int = defaultRegionId.toInt(),
        action: (response: Response<ForecastBriefResponseDto>) -> Unit
    ) {
        val response = repository.getBriefForecast(regionId)
        if (response.isSuccessful) {
            briefForecastData.value = response.body()!!.data
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
        }

        action(response)
    }

    suspend fun getDailyForecast(action: (response: Response<DailyForecastResponseDto>) -> Unit) {
        val response = repository.getDailyForecast(defaultRegionId.toInt())
        dailyForecastData.value = response.body()!!.data
        action(response)
    }

    suspend fun getHourlyForecast(
        regionId: Int? = defaultRegionId.toInt(),
        action: (response: Response<HourlyForecastResponseDto>) -> Unit
    ) {
        val response = repository.getHourlyForecast(regionId!!.toInt())
        hourlyForecastData.value = response.body()!!.data
        action(response)
    }

    suspend fun getCurrentAir(
        regionId: Int? = defaultRegionId.toInt(),
        action: (response: Response<CurrentAirResponseDto>) -> Unit
    ) {
        val response = repository.getCurrentAir(regionId!!.toInt())
        currentAirData.value = response.body()!!.data
        action(response)
    }
}