package com.miso.misoweather.Acitivity.weatherdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityWeatherMainBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.model.DTO.Forecast.Detail.ForecastDetailResponseDto
import com.miso.misoweather.model.DTO.Forecast.ForecastDetailInfo
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.TransportManager
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
    lateinit var viewModel: WeatherDetailViewModel
    lateinit var forecastdetailInfo: ForecastDetailInfo
    lateinit var region: Region
    lateinit var chatLayout: ConstraintLayout
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
        viewModel = WeatherDetailViewModel()
        chatLayout = binding.chatLayout
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
            doBack()
        }
        chatLayout.setOnClickListener()
        {
            goToChatMainActivity()
        }
        recyclerWeatherOnTIme = binding.recylcerWeatherOnTIme
        recyclerForecast = binding.recyclerForecast
    }

    override fun doBack() {
        startActivity(Intent(this, HomeActivity::class.java))
        transferToBack()
        finish()
    }

    fun goToChatMainActivity() {
        var isSurveyed = getPreference("isSurveyed")
        var lastSurveyedDate = getPreference("LastSurveyedDate") ?: ""
        var currentDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
        if (!isSurveyed.equals("true") || !lastSurveyedDate.equals(currentDate)) {
            var intent = Intent(this, SelectSurveyAnswerActivity::class.java)
            intent.putExtra("isFirstSurvey", true)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        } else {
            var intent = Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity", "Weather")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }

    fun getForecastDetail() {
        viewModel.getForecastDetail( getPreference("defaultRegionId")!!.toInt())
        viewModel.forecastDetailResponse.observe(this,  {
           if(it == null)
           {

           }
            else
           {
               if(it.isSuccessful)
               {
                   try {
                       Log.i("결과", "성공")
                       forecastdetailInfo = it.body()!!.data.forecastInfo
                       region = it.body()!!.data.region
                       setupWeatherOnTimeRecycler(it.body()!!)
                       setForecastInfo()
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }
               }
               else
               {

               }
           }
        })
    }

    fun setForecastInfo() {
        val midScaleString = if (region.midScale.equals("선택 안 함")) "전체" else region.midScale
        val smallScaleString =
            if (midScaleString.equals("전체"))
                ""
            else
                if (region.smallScale.equals("선택 안 함"))
                    "전체"
                else
                    region.smallScale
        txtLocation.text = region.bigScale + " " + midScaleString + " " + smallScaleString
        txtMinDegree.text = forecastdetailInfo.temperatureMin.split(".")[0] + "˚"
        txtMaxDegree.text = forecastdetailInfo.temperatureMax.split(".")[0] + "˚"
        txtEmojiRain.text = forecastdetailInfo.rainSnow
        txtDegreeRain.text = forecastdetailInfo.rainSnowPossibility + "%"
        txtDegreeRainOnHour.text = forecastdetailInfo.rainSnowValue
        txtDegreeWind.text = getWindDegree(forecastdetailInfo.windSpeed)
        txtEmojiWind.text = forecastdetailInfo.windSpeed
        val forecastOnCurrentHour = weatherOnTimeAdapter.getForecastOnHour(
            ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("HH"))
                .toInt()
        )
        txtDegree.text = forecastOnCurrentHour.temperature + "˚"
        txtWeatherEmoji.text = forecastOnCurrentHour.sky
    }

    fun getWindDegree(emoji: String): String {
        val degrees: Array<String> = resources.getStringArray(R.array.wind_degree)
        val emojies: Array<String> = resources.getStringArray(R.array.wind_emoji)

        return degrees.get(emojies.indexOf(emoji))
    }


    fun setupWeatherOnTimeRecycler(forecastDetailResponseDto:ForecastDetailResponseDto) {
        weatherOnTimeAdapter =
            RecyclerForecastOnTimeAdapter(this, forecastDetailResponseDto.data.forecastByTime)
        recyclerWeatherOnTIme.adapter = weatherOnTimeAdapter
        recyclerWeatherOnTIme.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }


}