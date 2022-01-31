package com.miso.misoweather.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.common.VerticalSpaceItemDecoration
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.databinding.ActivitySelectRegionBinding
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.login.LoginActivity
import com.miso.misoweather.model.DTO.ApiResponseWithData.ApiResponseWithData
import com.miso.misoweather.model.DTO.ApiResponseWithMemberInfoResponseDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class HomeActivity : MisoActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var apiResponseWithData: ApiResponseWithMemberInfoResponseDto
    lateinit var txtNickName:TextView
    lateinit var txtEmoji:TextView
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
    }

    fun getUserInfo()
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetUserInfo = api.getUserInfo(getPreference("misoToken")!!)

        callgetUserInfo.enqueue(object : Callback<ApiResponseWithMemberInfoResponseDto> {
            override fun onResponse(
                call: Call<ApiResponseWithMemberInfoResponseDto>,
                response: Response<ApiResponseWithMemberInfoResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    apiResponseWithData = response.body()!!
                    var memberInfoResponseDto = apiResponseWithData.data as MemberInfoResponseDto
                    txtNickName.setText(memberInfoResponseDto.nickname+"님!")
                    txtEmoji.setText(memberInfoResponseDto.emoji)
                    addPreferencePair("defaultRegionId",apiResponseWithData.data.regionId.toString())
                    savePreferences()
                }catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ApiResponseWithMemberInfoResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
}