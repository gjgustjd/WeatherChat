package com.miso.misoweather.Acitivity.selectTown

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SelectTownViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {
    val townRequestResult by lazy { MutableLiveData<Response<*>?>() }
    val updateRegionResponse by lazy { MutableLiveData<Response<*>?>() }

    val midScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MIDSCALE_REGION)
    }

    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    val misoToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
    }

    fun updateSmallScaleRegion(region: String) {
        repository.dataStoreManager.savePreference(DataStoreManager.SMALLSCALE_REGION, region)
    }

    fun addRegionPreferences(selectedRegion: Region) {
        val midScaleRegion = selectedRegion.midScale
        val bigScaleRegion = selectedRegion.bigScale

        repository.dataStoreManager.apply {
            savePreference(DataStoreManager.BIGSCALE_REGION, bigScaleRegion)
            savePreference(DataStoreManager.MIDSCALE_REGION, midScaleRegion)
        }
    }

    fun updateRegion(selectedRegion: Region, regionId: Int) {
        repository.updateRegion(
            misoToken,
            regionId,
            { call, response ->
                try {
                    Log.i("changeRegion", "성공")
                    addRegionPreferences(selectedRegion)
                    repository.dataStoreManager.savePreference(
                        DataStoreManager.DEFAULT_REGION_ID,
                        regionId.toString()
                    )
                    updateRegionResponse.value = response
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { call, response ->
                updateRegionResponse.value = response
            },
            { call, t ->
//                Log.i("changeRegion", "실패")
            },
        )
    }

    fun getTownList(bigScaleRegion: String) {
        repository.getCity(
            bigScaleRegion,
            { call, response ->
                townRequestResult.value = response
            },
            { call, response ->
                townRequestResult.value = response
            },
            { call, t ->
                townRequestResult.value = null
            }
        )
    }
}