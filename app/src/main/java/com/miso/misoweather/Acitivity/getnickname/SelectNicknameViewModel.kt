package com.miso.misoweather.Acitivity.getnickname

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception

class SelectNicknameViewModel(private val repository: MisoRepository) : ViewModel() {
    val nicknameResponseDto: MutableLiveData<Response<NicknameResponseDto>?> = MutableLiveData()
    val registerResponse: MutableLiveData<Response<GeneralResponseDto>?> = MutableLiveData()
    val issueTokenResponse: MutableLiveData<Response<GeneralResponseDto>?> = MutableLiveData()

    fun registerMember(
        signUpRequestDto: SignUpRequestDto,
        socialToken: String
    ) {

        repository.registerMember(
            signUpRequestDto,
            socialToken,
            { call, response ->
                registerResponse.value = response
            },
            { call, response ->
                registerResponse.value = response
            },
            { call, t ->
            }
        )
    }

    fun getNickname() {
        repository.getNickname(
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

    fun issueMisoToken(
        loginRequestDto: LoginRequestDto,
        socialType: String
    ) {
        repository.issueMisoToken(
            loginRequestDto,
            socialType,
            { call, response ->
                issueTokenResponse.value = response
            },
            { call, response ->
                issueTokenResponse.value = response
            },
            { call, t ->
//                Log.i("결과", "실패 : $t")
            })
    }
}