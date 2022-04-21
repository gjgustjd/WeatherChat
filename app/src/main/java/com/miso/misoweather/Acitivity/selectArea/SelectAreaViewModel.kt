package com.miso.misoweather.Acitivity.selectArea

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.Region
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

    @MutableResponseLiveData
    @Inject
    lateinit var areaRequestResult: MutableLiveData<Response<*>?>

    @MutableResponseLiveData
    @Inject
    lateinit var updateRegionResponse: MutableLiveData<Response<*>?>

    fun updateRegion(selectedRegion: Region, regionId: Int) {
        repository.updateRegion(
            misoToken,
            regionId,
            { call, response ->
                try {
                    Log.i("changeRegion", "성공")
                    addRegionPreferences(selectedRegion)
                    repository.dataStoreManager.savePreference(
                        DataStoreManager.DEFAULT_REGION_ID, regionId.toString()
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

    fun getAreaList(
        bigScaleRegion: String,
        midScaleRegion: String
    ) {
        repository.getArea(
            bigScaleRegion,
            midScaleRegion,
            { call, response ->
                areaRequestResult.value = response
            },
            { call, response ->
                areaRequestResult.value = response
            },
            { call, t ->
//                Log.i("결과", "실패 : $t")
            },
        )
    }
}