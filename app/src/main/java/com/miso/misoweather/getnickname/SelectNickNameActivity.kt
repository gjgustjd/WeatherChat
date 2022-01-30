package com.miso.misoweather.getnickname

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
import com.miso.misoweather.home.HomeActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.selectArea.SelectAreaActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

class SelectNickNameActivity : MisoActivity() {
    lateinit var binding: ActivitySelectNicknameBinding
    lateinit var txt_get_new_nick: TextView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    var nicknameResponseDto = NicknameResponseDto(Data("", ""), "", "")
    var generalResponseDto = GeneralResponseDto("","")
    var nickName:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()

    }

    fun initializeViews() {
        txt_get_new_nick = binding.txtGetNewNickname
        txt_get_new_nick.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        txt_get_new_nick.setOnClickListener() {
            getNickname()
        }
        btn_back.setOnClickListener()
        {
            startActivity(Intent(this, SelectAreaActivity::class.java))
            transferToBack()
            finish()
        }
        btn_next.setOnClickListener()
        {
            registerMember()
        }
        getNickname()
    }

    fun registerMember(){
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)

        val callRegisterMember = api.registerMember(getSignUpInfo(),prefs.getString("accessToken","")!!)
        callRegisterMember.enqueue(object : Callback<GeneralResponseDto> {
            override fun onResponse(
                call: Call<GeneralResponseDto>,
                response: Response<GeneralResponseDto>
            ) {
                Log.i("결과", "성공")
                issueMisoToken()
            }

            override fun onFailure(call: Call<GeneralResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
    fun issueMisoToken()
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callReIssueMisoToken = api.reIssueMisoToken(makeLoginRequestDto(),prefs.getString("accessToken","")!!)


        callReIssueMisoToken.enqueue(object : Callback<GeneralResponseDto> {
            override fun onResponse(
                call: Call<GeneralResponseDto>,
                response: Response<GeneralResponseDto>
            ) {
                Log.i("결과", "성공")
                generalResponseDto = response.body()!!
                var headers = response.headers()
                var serverToken = headers.get("servertoken")
                prefs.edit().putString("misoToken",serverToken).apply()
                var misoToken = prefs.getString("misoToken","")
                if(!misoToken.equals(""))
                {
                    var intent = Intent(this@SelectNickNameActivity,HomeActivity::class.java)
                    startActivity(intent)
                }
                else
                    Toast.makeText(this@SelectNickNameActivity,"서버 토큰 발행에 실패하였습니다.",Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<GeneralResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
    fun makeLoginRequestDto():LoginRequestDto
    {
       var loginRequestDto = LoginRequestDto(
           prefs.getString("socialId","")?.toInt(),
           prefs.getString("socialType",""))

        return loginRequestDto
    }

    fun getSignUpInfo():SignUpRequestDto
    {
        var signUpRequestDto = SignUpRequestDto()
        signUpRequestDto.defaultRegionId=prefs.getString("defaultRegionId","")!!
        signUpRequestDto.emoji=binding.txtEmoji.text.toString()
        signUpRequestDto.nickname=nickName
        signUpRequestDto.socialId=prefs.getString("socialId","")!!
        signUpRequestDto.socialType=prefs.getString("socialType","")!!
        return signUpRequestDto
    }

    fun getNickname() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callGetNickName = api.getNickname()

        callGetNickName.enqueue(object : Callback<NicknameResponseDto> {
            override fun onResponse(
                call: Call<NicknameResponseDto>,
                response: Response<NicknameResponseDto>
            ) {
                Log.i("결과", "성공")
                Log.i("결과", "닉네임 : ${response.body()?.data?.nickname}")
                nicknameResponseDto = response.body()!!
                nickName = nicknameResponseDto.data.nickname
                binding.txtGreetingBold.text = "${nickName}님!"
                binding.txtEmoji.text = "${nicknameResponseDto.data.emoji}"
            }

            override fun onFailure(call: Call<NicknameResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
}