package com.miso.misoweather.Acitivity.selectTown

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SelectTownViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {

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
    lateinit var townRequestResult: MutableLiveData<Response<*>?>

    @MutableResponseLiveData
    @Inject
    lateinit var updateRegionResponse: MutableLiveData<Response<*>?>


    fun updateSmallScaleRegion(region: String) {
        repository.apply {
            addPreferencePair("SmallScaleRegion", region)
            savePreferences()
        }
    }

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

    fun addRegionPreferences(selectedRegion: Region) {
        var midScaleRegion = selectedRegion.midScale
        var bigScaleRegion = selectedRegion.bigScale
        repository.apply {
            addPreferencePair("BigScaleRegion", bigScaleRegion)
            addPreferencePair("MidScaleRegion", midScaleRegion)
            savePreferences()
        }
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