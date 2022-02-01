package com.miso.misoweather.weatherdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.home.HomeActivity
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Detail.ForecastDetailResponseDto
import com.miso.misoweather.model.DTO.Forecast.ForecastDetailInfo
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class WeatherDetailActivity : MisoActivity() {
    lateinit var binding: ActivityWeatherMainBinding
    lateinit var btnBack: ImageButton
    lateinit var forecastDetailResponseDto: ForecastDetailResponseDto
    lateinit var forecastdetailInfo: ForecastDetailInfo
    lateinit var region: Region

    lateinit var txtLocation: TextView
    lateinit var txtWeatherEmoji: TextView
    lateinit var txtDegree: TextView
    lateinit var txtMinDegree: TextView
    lateinit var txtMaxDegree: TextView
    lateinit var btnChat: ImageButton
    lateinit var recyclerWeatherOnTIme: RecyclerView
    lateinit var weatherOnTimeAdapter: RecyclerForecastOnTimeAdapter
    lateinit var txtEmojiRain: TextView
    lateinit var txtDegreeRain: TextView
    lateinit var txtDegreeRainOnHour: TextView
    lateinit var recyclerForecast: RecyclerView
    lateinit var txtEmojiWind: TextView
    lateinit var txtDegreeWind: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getForecastDetail()
    }

    fun initializeViews() {
        txtLocation = binding.txtLocation
        txtWeatherEmoji = binding.txtWeatherEmoji
        txtDegree = binding.txtDegree
        txtMinDegree = binding.txtMinDegree
        txtMaxDegree = binding.txtMaxDegree
        btnChat = binding.imgbtnChat
        txtEmojiRain = binding.txtImogeRain
        txtDegreeRain = binding.txtDegreeRain
        txtDegreeRainOnHour = binding.txtRainDegreeOnHour
        txtEmojiWind = binding.txtEmojiWindSpeed
        txtDegreeWind = binding.txtDegreeWind
        btnBack = binding.imgbtnBack
        btnBack.setOnClickListener()
        {
            startActivity(Intent(this, HomeActivity::class.java))
            transferToBack()
            finish()
        }
        recyclerWeatherOnTIme = binding.recylcerWeatherOnTIme
        recyclerForecast = binding.recyclerForecast
    }

    fun getForecastDetail() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callForecastDetailList =
            api.getDetailForecast(getPreference("defaultRegionId")!!.toInt())

        callForecastDetailList.enqueue(object : Callback<ForecastDetailResponseDto> {
            override fun onResponse(
                call: Call<ForecastDetailResponseDto>,
                response: Response<ForecastDetailResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    forecastDetailResponseDto = response.body()!!
                    forecastdetailInfo = forecastDetailResponseDto.data.forecastInfo
                    region = forecastDetailResponseDto.data.region
                    setupRecyclers()
                    setForecastInfo()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ForecastDetailResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }

    fun setForecastInfo() {
        txtLocation.text = region.bigScale + " " + region.midScale + " " + region.smallScale
        txtMinDegree.text = forecastdetailInfo.temperatureMin.split(".")[0] + "˚"
        txtMaxDegree.text = forecastdetailInfo.temperatureMax.split(".")[0] + "˚"
        txtEmojiRain.text = forecastdetailInfo.rainSnow
        txtDegreeRain.text = forecastdetailInfo.rainSnowPossibility + "%"
        txtDegreeRainOnHour.text = forecastdetailInfo.rainSnowValue
        txtDegreeWind.text = forecastdetailInfo.windSpeedValue
        txtEmojiWind.text = forecastdetailInfo.windSpeed
        val forecastOnCurrentHour = weatherOnTimeAdapter.getForecastOnHour(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("HH")) .toInt())
        txtDegree.text = forecastOnCurrentHour.temperature+ "˚"
        txtWeatherEmoji.text = forecastOnCurrentHour.sky
    }

    fun setupRecyclers() {
        setupWeatherOnTimeRecycler()
    }

    fun setupWeatherOnTimeRecycler() {
        weatherOnTimeAdapter =
            RecyclerForecastOnTimeAdapter(this, forecastDetailResponseDto.data.forecastByTime)
        recyclerWeatherOnTIme.adapter = weatherOnTimeAdapter
        recyclerWeatherOnTIme.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }


}