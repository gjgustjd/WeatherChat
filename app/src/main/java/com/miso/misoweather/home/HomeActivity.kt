package com.miso.misoweather.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.weatherdetail.WeatherDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class HomeActivity : MisoActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var memberInfoResponseDto: MemberInfoResponseDto
    lateinit var forecastBriefResponseDto: ForecastBriefResponseDto
    lateinit var commentListResponseDto: CommentListResponseDto
    lateinit var txtNickName: TextView
    lateinit var txtEmoji: TextView
    lateinit var txtLocation: TextView
    lateinit var txtWeatherEmoji: TextView
    lateinit var txtWeatherDegree: TextView
    lateinit var btnShowWeatherDetail: ImageButton
    lateinit var recyclerChat: RecyclerView
    lateinit var recyclerChatAdapter:RecyclerChatsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getUserInfo()
        getBriefForecast()
        getCommentList()
    }

    fun initializeViews() {
        txtNickName = binding.txtNickname
        txtEmoji = binding.txtEmoji
        txtLocation = binding.txtLocation
        txtWeatherDegree = binding.txtDegree
        txtWeatherEmoji = binding.txtWeatherImoji
        btnShowWeatherDetail = binding.imgbtnShowWeather
        btnShowWeatherDetail.setOnClickListener()
        {
            startActivity(Intent(this, WeatherDetailActivity::class.java))
            transferToNext()
            finish()
        }
        recyclerChat = binding.recyclerChats
    }

    fun getBriefForecast() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetBriefForecast = api.getBriefForecast(getPreference("defaultRegionId")!!.toInt())

        callgetBriefForecast.enqueue(object : Callback<ForecastBriefResponseDto> {
            override fun onResponse(
                call: Call<ForecastBriefResponseDto>,
                response: Response<ForecastBriefResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    forecastBriefResponseDto = response.body()!!
                    var forecast = forecastBriefResponseDto.data.forecast
                    txtWeatherEmoji.setText(forecast.sky)
                    txtWeatherDegree.setText(forecast.temperature)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ForecastBriefResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
    fun getCommentList()
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetCommentList = api.getCommentList(null,4)

        callgetCommentList.enqueue(object : Callback<CommentListResponseDto> {
            override fun onResponse(
                call: Call<CommentListResponseDto>,
                response: Response<CommentListResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    commentListResponseDto = response.body()!!
                    setRecyclerChats()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommentListResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }

    fun setRecyclerChats(){
        recyclerChatAdapter = RecyclerChatsAdapter(this,commentListResponseDto.data.commentList)
        recyclerChat.adapter = recyclerChatAdapter
        recyclerChat.layoutManager = LinearLayoutManager(this)
    }
    fun getUserInfo() {
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
                    memberInfoResponseDto = response.body()!!
                    var memberInfoResponseDto = memberInfoResponseDto.data as MemberInfoDto
                    txtNickName.setText(memberInfoResponseDto.nickname + "님!")
                    txtEmoji.setText(memberInfoResponseDto.emoji)
                    txtLocation.setText(memberInfoResponseDto.regionName)
                    addPreferencePair(
                        "defaultRegionId",
                        this@HomeActivity.memberInfoResponseDto.data.regionId.toString()
                    )
                    savePreferences()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<MemberInfoResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
}