package com.miso.misoweather.Acitivity.getnickname

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@ActivityRetainedScoped
class SelectNicknameViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {

    @MutableResponseLiveData
    @Inject
    lateinit var nicknameResponseDto: MutableLiveData<Response<*>?>

    val smallScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SMALLSCALE_REGION)
    }

    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    val accessToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.ACCESS_TOKEN)
    }

    val socialId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_ID)
    }

    val socialType by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_TYPE)
    }

    @MutableNullableStringLiveData
    @Inject
    lateinit var registerResultString: MutableLiveData<String?>

    lateinit var loginRequestDto: LoginRequestDto
    lateinit var signUpRequestDto: SignUpRequestDto
    lateinit var defaultRegionId: String

    fun registerMember(
        signUpRequestDto: SignUpRequestDto,
        socialToken: String,
        isResetedToken: Boolean
    ) {
        this.signUpRequestDto = signUpRequestDto
        repository.registerMember(
            signUpRequestDto,
            socialToken,
            { call, response ->
                issueMisoToken(loginRequestDto, defaultRegionId)
            },
            { call, response ->
                if (response.errorBody()!!.source().toString()
                        .contains("UNAUTHORIZED") && !isResetedToken
                ) {
                    resetAccessToken()
                } else {
                    registerResultString.value = "Failed"
                }
            },
            { call, t ->
                registerResultString.value = t.toString()
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

    private fun resetAccessToken() {
        UserApiClient.instance.loginWithKakaoTalk(repository.context) { token, error ->
            if (error != null) {
                Log.e("resetAccessToken", "로그인 실패", error)
            } else if (token != null) {
                Log.i("resetAccessToken", "로그인 성공 ${token.accessToken}")
                repository.dataStoreManager.savePreference(
                    DataStoreManager.ACCESS_TOKEN,
                    token.accessToken
                )
                registerMember(signUpRequestDto, socialType, true)
            }
        }
    }

    fun issueMisoToken(
        loginRequestDto: LoginRequestDto,
        defaultRegionId: String
    ) {
        repository.issueMisoToken(
            loginRequestDto,
            accessToken,
            { call, response ->
                try {
                    Log.i("결과", "성공")
                    val headers = response.headers()
                    val serverToken = headers.get("servertoken")
                    repository.dataStoreManager.apply {
                        savePreference(DataStoreManager.MISO_TOKEN, serverToken!!)
                        savePreference(DataStoreManager.DEFAULT_REGION_ID, defaultRegionId)
                    }
                    if (!serverToken.isNullOrBlank()) {
                        registerResultString.value = "OK"
                    } else
                        registerResultString.value = "Failed"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { call, response ->
                registerResultString.value = "Failed"
            },
            { call, t ->
                registerResultString.value = t.toString()
            })
    }
}