package com.miso.misoweather.Acitivity.selectTown

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception

class SelectTownViewModel : ViewModel() {
    val townRequestResult: MutableLiveData<Response<RegionListResponseDto>?> = MutableLiveData()

    fun getTownList(bigScaleRegion: String) {
        MisoRepository.getCity(
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