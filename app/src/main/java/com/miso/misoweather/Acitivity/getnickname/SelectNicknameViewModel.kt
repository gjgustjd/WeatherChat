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
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.MisoRepository
import lombok.Setter
import retrofit2.Response
import java.lang.Exception
import kotlin.math.log

class SelectNicknameViewModel(private val repository: MisoRepository) : ViewModel() {
    val nicknameResponseDto: MutableLiveData<Response<NicknameResponseDto>?> = MutableLiveData()
    val smallScaleRegion: MutableLiveData<String?> = MutableLiveData()
    val bigScaleRegion: MutableLiveData<String?> = MutableLiveData()
    val middleScaleRegion: MutableLiveData<String?> = MutableLiveData()
    val accessToken: MutableLiveData<String?> = MutableLiveData()
    val misoToken: MutableLiveData<String?> = MutableLiveData()
    val socialId: MutableLiveData<String?> = MutableLiveData()
    val socialType: MutableLiveData<String?> = MutableLiveData()
    val registerResultString: MutableLiveData<String?> = MutableLiveData()

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

    fun resetAccessToken() {
        UserApiClient.instance.loginWithKakaoTalk(MisoRepository.context) { token, error ->
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
                    var headers = response.headers()
                    var serverToken = headers.get("servertoken")
                    repository.addPreferencePair("misoToken", serverToken!!)
                    repository.addPreferencePair("defaultRegionId", defaultRegionId)
//                    removeRegionPref()
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