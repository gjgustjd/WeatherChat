package com.miso.misoweather.Acitivity.getnickname

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@ActivityRetainedScoped
class SelectNicknameViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MisoRepository

    @MutableResponseLiveData
    @Inject
    lateinit var nicknameResponseDto: MutableLiveData<Response<*>?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var smallScaleRegion: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var bigScaleRegion: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var middleScaleRegion: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var accessToken: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var misoToken: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var socialId: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var socialType: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var registerResultString: MutableLiveData<String?>

    lateinit var loginRequestDto: LoginRequestDto
    lateinit var signUpRequestDto: SignUpRequestDto
    lateinit var defaultRegionId: String

    fun setupSocialId() {
        socialId.value = repository.getPreference("socialId")
    }

    fun setupSocialType() {
        socialType.value = repository.getPreference("socialType")
    }

    fun setupMisoToken() {
        misoToken.value = repository.getPreference("misoToken")
    }

    fun setupAccessToken() {
        accessToken.value = repository.getPreference("accessToken")
    }

    fun setupBigScaleRegion() {
        bigScaleRegion.value = repository.getPreference("BigScaleRegion")
    }

    fun setupMiddleScaleRegion() {
        middleScaleRegion.value = repository.getPreference("MidScaleRegion")
    }

    fun setupSmallScaleRegion() {
        smallScaleRegion.value = repository.getPreference("SmallScaleRegion")
    }

    fun updatePreferences() {
        setupSocialType()
        setupSocialId()
        setupMisoToken()
        setupSmallScaleRegion()
        setupMiddleScaleRegion()
        setupBigScaleRegion()
        setupAccessToken()
    }

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
                issueMisoToken(loginRequestDto)
            },
            { call, response ->
                if (response.errorBody()!!.source().toString().contains("UNAUTHORIZED") &&
                    isResetedToken == false
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
                repository.addPreferencePair("accessToken", token.accessToken)
                repository.savePreferences()
                updatePreferences()
                registerMember(signUpRequestDto, socialType.value!!, true)
            }
        }
    }

    fun removeRegionPref() {
        repository.removePreference("BigScaleRegion")
        repository.removePreference("MidScaleRegion")
        repository.removePreference("SmallScaleRegion")
    }

    fun issueMisoToken(
        loginRequestDto: LoginRequestDto
    ) {
        repository.issueMisoToken(
            loginRequestDto,
            accessToken.value!!,
            { call, response ->
                try {
                    Log.i("결과", "성공")
                    val headers = response.headers()
                    val serverToken = headers.get("servertoken")
                    repository.addPreferencePair("misoToken", serverToken!!)
                    repository.addPreferencePair("defaultRegionId", defaultRegionId)
                    repository.savePreferences()
                    updatePreferences()
                    if (!misoToken.value.isNullOrBlank()) {
                        registerResultString.value = "OK"
                    } else
                        registerResultString.value = "Failed"
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
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