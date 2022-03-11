package com.miso.misoweather.Acitivity.getnickname

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.MisoRepository

class SelectNickNameActivity : MisoActivity() {
    lateinit var binding: ActivitySelectNicknameBinding
    lateinit var txt_get_new_nick: TextView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var viewModel: SelectNicknameViewModel
    lateinit var repository: MisoRepository
    var nickName: String = ""
    var accessToken: String = ""
    var misoToken: String = ""
    var smallScaleRegion: String = ""
    var midScaleRegion: String = ""
    var bigScaleRegion: String = ""
    var socialId: String = ""
    var socialType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        initializeProperties()
    }

    fun initializeViews() {
        repository = MisoRepository.getInstance(this)
        viewModel = SelectNicknameViewModel(repository)
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

    fun initializeProperties() {
        viewModel.setupAccessToken()
        viewModel.accessToken.observe(this, {
            accessToken = it!!
        })
        viewModel.setupMisoToken()
        viewModel.misoToken.observe(this, {
            misoToken = it!!
        })
        viewModel.setupBigScaleRegion()
        viewModel.bigScaleRegion.observe(this, {
            bigScaleRegion = it!!
        })
        viewModel.setupMiddleScaleRegion()
        viewModel.middleScaleRegion.observe(this, {
            midScaleRegion = it!!
        })
        viewModel.setupSmallScaleRegion()
        viewModel.smallScaleRegion.observe(this, {
            smallScaleRegion = it!!
        })
        viewModel.setupSocialId()
        viewModel.socialId.observe(this, {
            socialId = it!!
        })
        viewModel.setupSocialType()
        viewModel.socialType.observe(this, {
            socialType = it!!
        })
        viewModel.defaultRegionId = intent.getStringExtra("RegionId")!!
    }

    override fun doBack() {
        var intent: Intent?
        if (smallScaleRegion.isNullOrBlank())
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
        viewModel.registerMember(
            getSignUpInfo(),
            accessToken,
            false
        )
        viewModel.registerResultString.observe(this, {
            if (it.equals("OK")) {
                var intent = Intent(this@SelectNickNameActivity, HomeActivity::class.java)
                startActivity(intent)
            } else {
                inCaseFailedRegister()
            }
        })
    }

    fun goToLoginActivity() {
        startActivity(Intent(this@SelectNickNameActivity, LoginActivity::class.java))
        transferToBack()
        finish()
    }

    fun makeLoginRequestDto(): LoginRequestDto {
        return LoginRequestDto(
            socialId,
            socialType
        )
    }

    fun getSignUpInfo(): SignUpRequestDto {
        var signUpRequestDto = SignUpRequestDto()
        signUpRequestDto.defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
        signUpRequestDto.emoji = binding.txtEmoji.text.toString()
        signUpRequestDto.nickname = nickName
        signUpRequestDto.socialId = socialId
        signUpRequestDto.socialType = socialType
        return signUpRequestDto
    }

    fun getNickname() {
        viewModel.getNickname()
        viewModel.nicknameResponseDto.observe(this, {
            if (it == null) {
                Toast.makeText(
                    this@SelectNickNameActivity,
                    "닉네임 받기에 실패하였습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (it.isSuccessful) {
                    Log.i("결과", "성공")
                    Log.i("결과", "닉네임 : ${it.body()?.data?.nickname}")
                    val nicknameResponseDto = it.body()!!
                    nickName = nicknameResponseDto.data.nickname
                    binding.txtGreetingBold.text = "${nickName}님!"
                    binding.txtEmoji.text = "${nicknameResponseDto.data.emoji}"
                } else {
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