package com.miso.misoweather.activity.weatherdetail

import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirResponseDto
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastResponseDto
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
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

    suspend fun getBriefForecast(
        regionId: Int = defaultRegionId.toInt(),
        action: (response: Response<ForecastBriefResponseDto>) -> Unit
    ) {
        val response = repository.getBriefForecast(regionId)
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
        }

        action(response)
    }

    suspend fun getDailyForecast(action: (response: Response<DailyForecastResponseDto>) -> Unit) =
        action(repository.getDailyForecast(defaultRegionId.toInt()))

    suspend fun getHourlyForecast(
        regionId: Int? = defaultRegionId.toInt(),
        action: (response: Response<HourlyForecastResponseDto>) -> Unit
    ) = action(repository.getHourlyForecast(regionId!!))

    suspend fun getCurrentAir(
        regionId: Int? = defaultRegionId.toInt(),
        action: (response: Response<CurrentAirResponseDto>) -> Unit
    ) = action(repository.getCurrentAir(regionId!!))
}