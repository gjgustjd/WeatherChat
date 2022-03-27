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
import java.lang.Exception
import javax.inject.Inject

@ActivityRetainedScoped
class LoginViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MisoRepository

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
        repository.addPreferencePair("accessToken", token.accessToken)
        repository.addPreferencePair("socialId", tokenInfo.id.toString())
        repository.addPreferencePair("socialType", "kakao")
        repository.savePreferences()
        updateProperties()
    }

    fun issueMisoToken() {
        repository.issueMisoToken(
            makeLoginRequestDto(),
            accessToken.value!!,
            { call, response ->
                var headers = response.headers()
                var serverToken = headers.get("servertoken")!!
                repository.addPreferencePair("misoToken", serverToken!!)
                repository.savePreferences()
                issueMisoTokenResponse.value = response
            },
            { call, response ->
                issueMisoTokenResponse.value = response
            },
            { call, t ->
                issueMisoTokenResponse.value = t
                repository.removePreference("misoToken")
                repository.savePreferences()
            })
    }

    fun checkTokenValid() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(repository.context)) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.i("token", "토큰 정보 보기 실패", error)
                    isCheckValid.value = false
                } else if (tokenInfo != null) {
                    Log.i(
                        "token", "토큰 정보 보기 성공" +
                                "\n회원번호:${tokenInfo.id}"
                    )
                    isCheckValid.value = true
                }
            }
        } else
            isCheckValid.value = false
    }

    fun kakaoLoginAvailable(): Boolean {
        return UserApiClient.instance.isKakaoTalkLoginAvailable(repository.context)
    }

    fun loginWithKakaoTalk(context: Context) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                loginWithKakaoTalkResponse.value = "Login Failed"
            } else if (token != null) {
                Log.i("miso", "로그인 성공 ${token.accessToken}")
                try {
                    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                        if (error != null) {
                            loginWithKakaoTalkResponse.value = error
                        } else if (tokenInfo != null) {
                            Log.i("token", "토큰 정보 보기 성공" + "\n회원번호:${tokenInfo.id}")
                            loginWithKakaoTalkResponse.value = "OK"
                            saveTokenInfo(token, tokenInfo)
                        }
                    }
                } catch (e: Exception) {
                    Log.i("kakaoLogin", e.stackTraceToString())
                    loginWithKakaoTalkResponse.value = error
                }
            }
        }
    }
}