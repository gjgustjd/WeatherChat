package com.miso.misoweather.activity.getnickname

import android.content.Intent
import android.graphics.Paint
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
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.login.LoginActivity
import com.miso.misoweather.model.dto.*
import com.miso.misoweather.activity.selectArea.SelectAreaActivity
import com.miso.misoweather.activity.selectTown.SelectTownActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectNickNameActivity : MisoActivity() {
    private val viewModel: SelectNicknameViewModel by viewModels()
    private lateinit var binding: ActivitySelectNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_nickname)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        viewModel.defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
        getNickname()
    }

    override fun doBack() {
        val intent: Intent?
        if (viewModel.smallScaleRegion.isBlank())
            intent = Intent(this, SelectTownActivity::class.java)
        else
            intent = Intent(this, SelectAreaActivity::class.java)

        startActivity(intent)
        transferToBack()
        finish()
    }

    fun registerMember() {
        fun inCaseFailedRegister() {
            Toast.makeText(this@SelectNickNameActivity, "회원가입에 실패하였습니다.", Toast.LENGTH_LONG)
                .show()
            goToLoginActivity()
        }

        viewModel.loginRequestDto = makeLoginRequestDto()
        lifecycleScope.launch {
            viewModel.registerMember(
                getSignUpInfo(),
                viewModel.accessToken,
                false
            )
            {
                if (it.isSuccessful) {
                    startActivity(Intent(this@SelectNickNameActivity, HomeActivity::class.java))
                } else {
                    inCaseFailedRegister()
                }
            }
        }
    }

    fun goToLoginActivity() {
        startActivity(Intent(this@SelectNickNameActivity, LoginActivity::class.java))
        transferToBack()
        finish()
    }

    private fun makeLoginRequestDto(): LoginRequestDto {
        return LoginRequestDto(
            viewModel.socialId,
            viewModel.socialType
        )
    }

    private fun getSignUpInfo(): SignUpRequestDto =
        SignUpRequestDto().apply {
            defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
            emoji = binding.txtEmoji.text.toString()
            nickname = viewModel.nickname.value!!
            socialId = viewModel.socialId
            socialType = viewModel.socialType
        }

    fun getNickname() {
        lifecycleScope.launch {
            viewModel.getNickname()
            {
                if (!it.isSuccessful) {
                    Log.i("결과", "실패")
                    Toast.makeText(
                        this@SelectNickNameActivity,
                        "닉네임 받기에 실패하였습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}