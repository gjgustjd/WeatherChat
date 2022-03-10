package com.miso.misoweather.Acitivity.getnickname

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response

class SelectNicknameViewModel : ViewModel() {
    val nicknameResponseDto : MutableLiveData<Response<NicknameResponseDto>?> = MutableLiveData()

    fun getNickname() {
        MisoRepository.getNickname(
            { call, response ->
                nicknameResponseDto.value = response
            },
            { call, response ->
                nicknameResponseDto.value = response
            },
            { call, t ->
                nicknameResponseDto.value = null
            },
        )
    }
}