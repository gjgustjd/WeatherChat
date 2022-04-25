package com.miso.misoweather.activity.mypage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.login.LoginActivity
import com.miso.misoweather.dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityMypageBinding
import com.miso.misoweather.model.dto.LoginRequestDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MyPageActivity : MisoActivity() {
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var binding: ActivityMypageBinding
    private lateinit var btn_back: ImageButton
    private lateinit var btn_logout: Button
    private lateinit var btn_unregister: Button
    private lateinit var btn_version: Button
    private lateinit var txt_version: TextView
    private lateinit var txt_emoji: TextView
    private lateinit var txt_nickname: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()
        setupObservers()
    }

    private fun getVersionString(): String {
        return this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }

    private fun setupObservers() {
        viewModel.emoji.observe(this)
        {
            txt_emoji.text = it
        }
        viewModel.nickname.observe(this)
        {
            setNickNameText()
        }
        viewModel.bigScaleRegion.observe(this)
        {
            setNickNameText()
        }
    }

    private fun setNickNameText() {
        if (viewModel.nickname.value != null && viewModel.bigScaleRegion.value != null)
            txt_nickname.text =
                "${getBigShortScale(viewModel.bigScaleRegion.value!!)}의 ${viewModel.nickname.value}"
    }


    private fun initializeView() {
        btn_back = binding.imgbtnBack
        btn_logout = binding.btnLogout
        btn_unregister = binding.btnUnregister
        btn_version = binding.btnVersion
        txt_version = binding.txtVersion
        txt_emoji = binding.txtEmoji
        txt_nickname = binding.txtNickname
        txt_version.text = getVersionString()

        btn_version.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                null,
                "버전 ${getVersionString()}\n\n" + "\uD83D\uDC65만든이\n" +
                        "-\uD83E\uDD16안드로이드 개발: 허현성\n" +
                        "-\uD83C\uDF4EiOS 개발: 허지인,강경훈\n" +
                        "-\uD83D\uDCE6서버 개발: 강승연\n" +
                        "-\uD83C\uDFA8UI/UX 디자인: 정한나",
                "확인",
                0.8f,
                0.4f
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
        btn_back.setOnClickListener()
        {
            doBack()
        }
        btn_unregister.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                {
                    unregister()
                },
                "정말로 계정을 삭제할까요? \uD83D\uDE22",
                "삭제"
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
        btn_logout.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                {
                    logout()
                },
                "로그아웃 하시겠습니까? \uD83D\uDD13",
                "로그아웃"
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
    }

    private fun makeLoginRequestDto(): LoginRequestDto {
        val loginRequestDto = LoginRequestDto(
            viewModel.socialId,
            viewModel.socialType
        )
        return loginRequestDto
    }

    override fun doBack() {
        startActivity(Intent(this, HomeActivity::class.java))
        transferToBack()
        finish()
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        transferToBack()
        finish()
    }

    private fun unregister() {
        lifecycleScope.launch {
            viewModel.unRegister(makeLoginRequestDto())
            {
                try {
                    if (it.isSuccessful) {
                        goToLoginActivity()
                        Log.i("unregister", "성공")
                    } else {
                        goToLoginActivity()
                        throw Exception(it.errorBody()!!.source().toString())
                    }
                } catch (e: Exception) {
                    Log.e("unregister", e.stackTraceToString())
                    Log.e("unregister", e.message.toString())
                    Toast.makeText(
                        this@MyPageActivity,
                        "카카오 계정 연결 해제에 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakaoLogout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("", "토큰 정보 보기 실패", error)
                    } else if (tokenInfo != null) {
                        Log.i(
                            "", "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초"
                        )
                    }
                }
            } else {
                Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                removePreference("accessToken", "socialId", "socialType", "misoToken")
                savePreferences()
                goToLoginActivity()
            }
        }
    }

}