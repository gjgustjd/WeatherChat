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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.R
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.login.LoginActivity
import com.miso.misoweather.dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.model.dto.LoginRequestDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MyPageActivity : MisoActivity() {
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var binding: com.miso.misoweather.databinding.ActivityMypageBinding
    lateinit var versionString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mypage)
        versionString = this.packageManager.getPackageInfo(this.packageName, 0).versionName
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
    }

    fun showVersionDialog() {
        val dialog = GeneralConfirmDialog(
            this,
            null,
            "버전 ${versionString}\n\n" + "\uD83D\uDC65만든이\n" +
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

    fun showUnRegisterDialog() {
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

    fun showLogoutDialog() {
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
                    goToLoginActivity()

                    if (it.isSuccessful) {
                        Log.i("unregister", "성공")
                    } else {
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