package com.miso.misoweather.Acitivity.selectArea

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@ActivityRetainedScoped
class SelectAreaViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MisoRepository

    @MutableNullableStringLiveData
    @Inject
    lateinit var smallScaleRegion: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var midScaleRegion: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var bigScaleRegion: MutableLiveData<String?>

    @MutableResponseLiveData
    @Inject
    lateinit var areaRequestResult: MutableLiveData<Response<*>?>

    @MutableResponseLiveData
    @Inject
    lateinit var updateRegionResponse: MutableLiveData<Response<*>?>

    fun setupSmallScaleRegion() {
        smallScaleRegion.value = repository.getPreference("SmallScaleRegion")
    }

    fun setupMidScaleRegion() {
        midScaleRegion.value = repository.getPreference("MidScaleRegion")
    }

    fun setupBigScaleRegion() {
        bigScaleRegion.value = repository.getPreference("BigScaleRegion")
    }

    fun updateProperties() {
        setupBigScaleRegion()
        setupMidScaleRegion()
        setupSmallScaleRegion()
    }

    fun updateRegion(selectedRegion: Region, regionId: Int) {
        repository.updateRegion(
            repository.getPreference("misoToken")!!,
            regionId,
            { call, response ->
                try {
                    Log.i("changeRegion", "성공")
                    addRegionPreferences(selectedRegion)
                    repository.addPreferencePair(
                        "defaultRegionId",
                        regionId.toString()
                    )
                    repository.savePreferences()
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
        var midScaleRegion = selectedRegion.midScale
        var bigScaleRegion = selectedRegion.bigScale
        var smallScaleRegion = selectedRegion.smallScale
        repository.addPreferencePair("BigScaleRegion", bigScaleRegion)
        repository.addPreferencePair("MidScaleRegion", midScaleRegion)
        repository.addPreferencePair("SmallScaleRegion", smallScaleRegion)
        repository.savePreferences()
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