package com.miso.misoweather.Acitivity.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Acitivity.mypage.MyPageActivity
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
    lateinit var weatherLayout: ConstraintLayout
    lateinit var txtNickName: TextView
    lateinit var txtEmoji: TextView
    lateinit var txtLocation: TextView
    lateinit var txtWeatherEmoji: TextView
    lateinit var txtWeatherDegree: TextView
    lateinit var btnShowWeatherDetail: ImageButton
    lateinit var btnProfile: ImageButton
    lateinit var btngoToSurvey: ImageButton
    lateinit var recyclerChat: RecyclerView
    lateinit var recyclerChatAdapter: RecyclerChatsAdapter
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
        weatherLayout = binding.weatherLayout
        txtNickName = binding.txtNickname
        txtEmoji = binding.txtEmoji
        txtLocation = binding.txtLocation
        txtWeatherDegree = binding.txtDegree
        txtWeatherEmoji = binding.txtWeatherEmoji
        btnShowWeatherDetail = binding.imgbtnShowWeather
        btnProfile = binding.imgbtnProfile
        btngoToSurvey = binding.imgbtnSurvey
        btngoToSurvey.setOnClickListener()
        {
            var intent= Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity","Home")
            startActivity(intent)
            transferToNext()
            finish()
        }
        weatherLayout.setOnClickListener()
        {
            startActivity(Intent(this, WeatherDetailActivity::class.java))
            transferToNext()
            finish()
        }
        btnProfile.setOnClickListener()
        {
            startActivity(Intent(this, MyPageActivity::class.java))
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
                    var region = forecastBriefResponseDto.data.region
                    txtLocation.text =
                        region.bigScale + " " + region.midScale + " " + region.smallScale
                    txtWeatherEmoji.setText(forecast.sky)
                    txtWeatherDegree.setText(forecast.temperature + "˚")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ForecastBriefResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }

    fun getCommentList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetCommentList = api.getCommentList(null, 4)

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

    fun setRecyclerChats() {
        recyclerChatAdapter = RecyclerChatsAdapter(this, commentListResponseDto.data.commentList)
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
                    var memberInfo = memberInfoResponseDto.data
                    txtNickName.setText(memberInfo.nickname + "님!")
                    txtEmoji.setText(memberInfo.emoji)
                    addPreferencePair(
                        "defaultRegionId",
                        this@HomeActivity.memberInfoResponseDto.data.regionId.toString()
                    )
                    addPreferencePair("emoji", memberInfo.emoji)
                    addPreferencePair("nickname", memberInfo.nickname)
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