package com.miso.misoweather.Acitivity.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.AccessTokenInfo
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {
    val socialId by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SOCIAL_ID).asLiveData()
    }

    val socialType by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.SOCIAL_TYPE).asLiveData()
    }

    val accessToken by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.ACCESS_TOKEN).asLiveData()
    }

    suspend fun checkRegistered() =
        viewModelScope.async {
            repository.checkRegistered(socialId.value!!, socialType.value!!).isSuccessful
        }.await()

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

    fun issueMisoToken(action: (response: Response<GeneralResponseDto>) -> Unit) {
        viewModelScope.launch {
            val response = repository.issueMisoToken(
                makeLoginRequestDto(),
                accessToken.value!!
            )
            if (response.isSuccessful) {
                val headers = response.headers()
                val serverToken = headers.get("servertoken")!!
                repository.dataStoreManager.savePreference(
                    DataStoreManager.MISO_TOKEN,
                    serverToken
                )
            } else {
                response.errorBody()?.let {
                    repository.dataStoreManager.removePreference(DataStoreManager.MISO_TOKEN)
                }
            }

            action(response)
        }
    }
}