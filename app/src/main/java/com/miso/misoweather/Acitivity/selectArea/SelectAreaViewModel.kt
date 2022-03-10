package com.miso.misoweather.Acitivity.selectArea

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response

class SelectAreaViewModel : ViewModel() {
    val areaRequestResult: MutableLiveData<Response<RegionListResponseDto>?> = MutableLiveData()

    fun getAreaList(
        bigScaleRegion: String,
        midScaleRegion: String
    ) {
        MisoRepository.getArea(
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