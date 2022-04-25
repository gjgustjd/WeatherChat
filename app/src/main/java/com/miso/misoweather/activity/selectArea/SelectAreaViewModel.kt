package com.miso.misoweather.activity.selectArea

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.miso.misoweather.model.dto.GeneralResponseDto
import com.miso.misoweather.model.dto.Region
import com.miso.misoweather.model.dto.regionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SelectAreaViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {
    val smallScaleRegion by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SMALLSCALE_REGION)
            .asLiveData()
    }

    val midScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MIDSCALE_REGION)
    }

    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    val misoToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
    }

    suspend fun updateRegion(
        selectedRegion: Region,
        regionId: Int,
        action: (response: Response<GeneralResponseDto>) -> Unit
    ) {
        val response = repository.updateRegion(
            misoToken,
            regionId
        )
        if (response.isSuccessful) {
            try {
                Log.i("changeRegion", "성공")
                addRegionPreferences(selectedRegion)
                repository.dataStoreManager.savePreference(
                    DataStoreManager.DEFAULT_REGION_ID, regionId.toString()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        action(response)
    }

    suspend fun getAreaList(
        bigScaleRegion: String,
        midScaleRegion: String,
        action: (response: Response<RegionListResponseDto>) -> Unit
    ) = action(repository.getArea(bigScaleRegion, midScaleRegion))

    fun addRegionPreferences(selectedRegion: Region) {
        val mid = selectedRegion.midScale
        val big = selectedRegion.bigScale
        val small = selectedRegion.smallScale
        repository.dataStoreManager.apply {
            savePreference(DataStoreManager.BIGSCALE_REGION, big)
            savePreference(DataStoreManager.MIDSCALE_REGION, mid)
            savePreference(DataStoreManager.SMALLSCALE_REGION, small)
        }
    }

}