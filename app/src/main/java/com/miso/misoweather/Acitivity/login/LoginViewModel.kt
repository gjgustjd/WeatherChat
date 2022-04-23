package com.miso.misoweather.Acitivity.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.AccessTokenInfo
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {
    val checkRegistered by lazy { MutableLiveData<Boolean?>() }
    val issueMisoTokenResponse by lazy { MutableLiveData<Any?>() }

    val socialId by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SOCIAL_ID).asLiveData()
    }

    val socialType by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SOCIAL_TYPE).asLiveData()
    }

    val accessToken by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.ACCESS_TOKEN).asLiveData()
    }


    fun checkRegistered() {
        repository.checkRegistered(
            socialId.value!!,
            socialType.value!!,
            { call, response ->
                checkRegistered.value = true
            },
            { call, response ->
                checkRegistered.value = false
            },
            { call, throwable ->
                checkRegistered.value = false
            })
    }

    fun makeLoginRequestDto(): LoginRequestDto {
        return LoginRequestDto(
            socialId.value!!,
            socialType.value!!,
        )
    }

    fun saveTokenInfo(token: OAuthToken, tokenInfo: AccessTokenInfo) {
        repository.dataStoreManager.apply {
            savePreference(DataStoreManager.ACCESS_TOKEN, token.accessToken)
            savePreference(DataStoreManager.SOCIAL_ID, tokenInfo.id.toString())
            savePreference(DataStoreManager.SOCIAL_TYPE, "kakao")
        }
    }

    fun issueMisoToken() {
        repository.issueMisoToken(
            makeLoginRequestDto(),
            accessToken.value!!,
            { call, response ->
                val headers = response.headers()
                val serverToken = headers.get("servertoken")!!
                repository.dataStoreManager.savePreference(
                    DataStoreManager.MISO_TOKEN,
                    serverToken
                )
                issueMisoTokenResponse.value = response
            },
            { call, response ->
                issueMisoTokenResponse.value = response
            },
            { call, t ->
                issueMisoTokenResponse.value = t
                repository.dataStoreManager.removePreference(DataStoreManager.MISO_TOKEN)
            })
    }
}