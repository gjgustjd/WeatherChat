package com.miso.misoweather.Acitivity.getnickname

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameData
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.MisoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class SelectNickNameActivity : MisoActivity() {
    lateinit var binding: ActivitySelectNicknameBinding
    lateinit var txt_get_new_nick: TextView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var viewModel: SelectNicknameViewModel
    var generalResponseDto = GeneralResponseDto("", "", null)
    var nickName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    fun initializeViews() {
        viewModel = SelectNicknameViewModel(MisoRepository(this))
        txt_get_new_nick = binding.txtGetNewNickname
        txt_get_new_nick.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        txt_get_new_nick.setOnClickListener() {
            getNickname()
        }
        btn_back.setOnClickListener()
        {
            doBack()
        }
        btn_next.setOnClickListener()
        {
            registerMember()
        }
        getNickname()
    }

    override fun doBack() {
        var intent: Intent?
        if (getPreference("SmallScaleRegion").isNullOrBlank())
            intent = Intent(this, SelectTownActivity::class.java)
        else
            intent = Intent(this, SelectAreaActivity::class.java)

        startActivity(intent)
        transferToBack()
        finish()
    }

    fun registerMember(isResetedToken: Boolean = false) {
        fun inCaseFailedRegister() {
            Toast.makeText(this@SelectNickNameActivity, "회원가입에 실패하였습니다.", Toast.LENGTH_LONG)
                .show()
            goToLoginActivity()
        }

        MisoRepository.registerMember(
            getSignUpInfo(),
            getPreference("accessToken")!!,
            { call, response ->
                if (response.body() == null) {
                    Log.i("결과", "실패")
                    if (response.errorBody()!!.source().toString().contains("UNAUTHORIZED") &&
                        isResetedToken == false
                    ) {
                        resetAccessToken()
                    } else {
                        inCaseFailedRegister()
                    }
                } else {
                    Log.i("결과", "성공")
                    issueMisoToken()
                }
            },
            { call, response ->
                inCaseFailedRegister()
            },
            { call, t ->
                Log.i("결과", "실패 : $t")
                inCaseFailedRegister()
            }
        )
    }

    fun resetAccessToken() {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if (error != null) {
                Log.e("resetAccessToken", "로그인 실패", error)
            } else if (token != null) {
                Log.i("resetAccessToken", "로그인 성공 ${token.accessToken}")
                addPreferencePair("accessToken", token.accessToken)
                savePreferences()
                registerMember()
            }
        }
    }

    fun goToLoginActivity() {
        startActivity(Intent(this@SelectNickNameActivity, LoginActivity::class.java))
        transferToBack()
        finish()
    }

    fun issueMisoToken() {
        MisoRepository.issueMisoToken(
            makeLoginRequestDto(),
            getPreference("accessToken")!!,
            { call, response ->
                try {
                    Log.i("결과", "성공")
                    generalResponseDto = response.body()!!
                    var headers = response.headers()
                    var serverToken = headers.get("servertoken")
                    addPreferencePair("misoToken", serverToken!!)
                    addPreferencePair("defaultRegionId", intent.getStringExtra("RegionId")!!)
                    removeRegionPref()
                    savePreferences()
                    if (!getPreference("misoToken").equals("")) {
                        var intent = Intent(this@SelectNickNameActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else
                        Toast.makeText(
                            this@SelectNickNameActivity,
                            "서버 토큰 발행에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                }
            },
            { call, response ->
//                Log.i("issueMisoToken", "실패")
            },
            { call, t ->
//                Log.i("결과", "실패 : $t")
            })
    }

    fun removeRegionPref() {
        removePreference("BigScaleRegion")
        removePreference("MidScaleRegion")
        removePreference("SmallScaleRegion")
    }

    fun makeLoginRequestDto(): LoginRequestDto {
        var loginRequestDto = LoginRequestDto(
            getPreference("socialId"),
            getPreference("socialType")
        )

        return loginRequestDto
    }

    fun getSignUpInfo(): SignUpRequestDto {
        var signUpRequestDto = SignUpRequestDto()
        signUpRequestDto.defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
        signUpRequestDto.emoji = binding.txtEmoji.text.toString()
        signUpRequestDto.nickname = nickName
        signUpRequestDto.socialId = getPreference("socialId")!!
        signUpRequestDto.socialType = getPreference("socialType")!!
        return signUpRequestDto
    }

    fun getNickname() {
        viewModel.getNickname()
        viewModel.nicknameResponseDto.observe(this,{
            if(it==null)
            {
                Toast.makeText(
                    this@SelectNickNameActivity,
                    "닉네임 받기에 실패하였습니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else
            {
                if(it.isSuccessful)
                {
                        Log.i("결과", "성공")
                        Log.i("결과", "닉네임 : ${it.body()?.data?.nickname}")
                        val nicknameResponseDto = it.body()!!
                        nickName = nicknameResponseDto.data.nickname
                        binding.txtGreetingBold.text = "${nickName}님!"
                        binding.txtEmoji.text = "${nicknameResponseDto.data.emoji}"
                }
                else
                {
                    Log.i("결과", "실패")
                    Toast.makeText(
                        this@SelectNickNameActivity,
                        "닉네임 받기에 실패하였습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        })
    }
}