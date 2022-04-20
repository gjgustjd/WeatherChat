package com.miso.misoweather.Acitivity.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import com.miso.misoweather.Module.LiveDataModule.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {

    @MutableNullableStringLiveData
    @Inject
    lateinit var socialId: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var socialType: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var accessToken: MutableLiveData<String?>

    @MutableNullableBooleanLiveData
    @Inject
    lateinit var checkRegistered: MutableLiveData<Boolean?>

    @MutableNullableBooleanLiveData
    @Inject
    lateinit var isCheckValid: MutableLiveData<Boolean?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var issueMisoTokenResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var loginWithKakaoTalkResponse: MutableLiveData<Any?>

    fun updateProperties() {
        setupSocialId()
        setupSocialType()
        setupAccessToken()
    }

    fun setupSocialId() {
        socialId.value = repository.getPreference("socialId")
    }

    fun setupSocialType() {
        socialType.value = repository.getPreference("socialType")
    }

    fun setupAccessToken() {
        accessToken.value = repository.getPreference("accessToken")
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
            socialId.value,
            socialType.value
        )
    }

    fun saveTokenInfo(token: OAuthToken, tokenInfo: AccessTokenInfo) {
        repository.apply {
            addPreferencePair("accessToken", token.accessToken)
            addPreferencePair("socialId", tokenInfo.id.toString())
            addPreferencePair("socialType", "kakao")
            savePreferences()
        }
        updateProperties()
    }

    fun issueMisoToken() {
        repository.issueMisoToken(
            makeLoginRequestDto(),
            accessToken.value!!,
            { call, response ->
                val headers = response.headers()
                val serverToken = headers.get("servertoken")!!
                repository.apply {
                    addPreferencePair("misoToken", serverToken!!)
                    savePreferences()
                }
                issueMisoTokenResponse.value = response
            },
            { call, response ->
                issueMisoTokenResponse.value = response
            },
            { call, t ->
                issueMisoTokenResponse.value = t
                repository.apply {
                    removePreference("misoToken")
                    savePreferences()
                }
            })
    }
}