package com.miso.misoweather.home

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class HomeActivity : MisoActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var apiResponseWithData: MemberInfoResponseDto
    lateinit var txtNickName:TextView
    lateinit var txtEmoji:TextView
    lateinit var txtLocation:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getUserInfo()
    }

    fun initializeViews() {
        txtNickName = binding.txtNickname
        txtEmoji = binding.txtEmoji
        txtLocation = binding.txtLocation
    }

    fun getUserInfo()
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetUserInfo = api.getUserInfo(getPreference("misoToken")!!)

        callgetUserInfo.enqueue(object : Callback<MemberInfoResponseDto> {
            override fun onResponse(
                call: Call<MemberInfoResponseDto>,
                response: Response<MemberInfoResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    apiResponseWithData = response.body()!!
                    var memberInfoResponseDto = apiResponseWithData.data as MemberInfoDto
                    txtNickName.setText(memberInfoResponseDto.nickname+"님!")
                    txtEmoji.setText(memberInfoResponseDto.emoji)
                    txtLocation.setText(memberInfoResponseDto.regionName)
                    addPreferencePair("defaultRegionId",apiResponseWithData.data.regionId.toString())
                    savePreferences()
                }catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<MemberInfoResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
}